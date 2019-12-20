package android.harmony.irescue.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.harmony.irescue.R;
import android.harmony.irescue.adapters.AlertsAdapter;
import android.harmony.irescue.database.IRescuePreferences;
import android.harmony.irescue.model.AlertModel;
import android.harmony.irescue.model.LocationUpdate;
import android.harmony.irescue.model.User;
import android.harmony.irescue.utility.Constants;
import android.harmony.irescue.utility.DividerDecoration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean mGpsEnabled = false;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;

    private RecyclerView mAlertsRecyclerView;
    private LinearLayout errorLayout;
    private LinearLayout progressLayout;
    private Button retryButton;    private ImageView incidentImage;

    private String myLocation, myCounty = "";
    private FloatingActionButton btnShow;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mReference,usersLocationRef;
    private static final String USER_VALUE = "USER_VALUE";
    private User mUser;
    private static final int REQUEST_IMAGE_CAPTURE=24;
    private Uri imageUri=null;
    private StorageReference incidentImagesReferences;
    private String mCurrentPhotoPath;
    private static Context mContext;
    private GoogleApiClient mGoogleApiClient;

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_VALUE, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mReference= FirebaseDatabase.getInstance().getReference("Alerts");
        usersLocationRef=FirebaseDatabase.getInstance().getReference("Users_Location");
        incidentImagesReferences= FirebaseStorage.getInstance().getReference("Incident_Images");
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage(getActivity() /* FragmentActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addConnectionCallbacks(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
        requestPermission();
        statusCheck();
        getDeviceLocation();
        mContext=getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        mAlertsRecyclerView = view.findViewById(R.id.alerts_recycler_view);
        mAlertsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAlertsRecyclerView.addItemDecoration(new DividerDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        mUser = (User) getArguments().getSerializable(USER_VALUE);
        errorLayout = view.findViewById(R.id.error_layout);
        errorLayout.setVisibility(View.GONE);
        retryButton = view.findViewById(R.id.retry);
        progressLayout = view.findViewById(R.id.progress_layout);
        btnShow = view.findViewById(R.id.fab);
        mAlertsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    btnShow.hide();
                } else {
                    btnShow.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAlertMessage();
            }
        });
        hideBtnShow();
        return view;
    }

    private void showAddAlertMessage() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_alert_message_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("post", null)
                .setNegativeButton("Cancel", null)
                .create();
        final EditText alertMessageEditField = view.findViewById(R.id.alert_message_edit_text);
        TextView myLocationTextView = view.findViewById(R.id.my_location);
        myLocationTextView.setText("You are here "+myLocation);
        incidentImage=view.findViewById(R.id.incident_image);
        Button btnAddImage=view.findViewById(R.id.add_image);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  dispatchTakePictureIntent();
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),REQUEST_IMAGE_CAPTURE);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface InterfaceDialog) {
                Button btnAdd = dialog.getButton(InterfaceDialog.BUTTON_POSITIVE);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postAlertMessage(alertMessageEditField,dialog);
                    }
                });
            }
        });
        dialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        imageUri=contentUri;
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
    private void postAlertMessage(EditText alertMessageEditField, final AlertDialog popDialog) {
        boolean hasError = false;
        if (alertMessageEditField.getText().toString().equals("")) {
            hasError = true;
            alertMessageEditField.setError("Required Field");
        }
        if(imageUri==null){
            hasError=true;
            Toast.makeText(mContext, "Image Required ", Toast.LENGTH_SHORT).show();
        }
        if (!hasError) {
            //post alert message to firestore
            long time=System.currentTimeMillis();
            final AlertModel alertModel = new AlertModel();
            alertModel.setAlertMessage(alertMessageEditField.getText().toString());
            alertModel.setLat(mLastKnownLocation.getLatitude());
            alertModel.setLng(mLastKnownLocation.getLongitude());
            alertModel.setUser(mUser);
            alertModel.setTime(time);
            alertModel.setLocation(myLocation);
            final ProgressDialog dialog=new ProgressDialog(getActivity());
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            final String[] imageUrl = {null};
            imageUrl[0]="image";
            String imageName=String.valueOf(System.currentTimeMillis());
            if(imageUri!=null) {
                incidentImagesReferences.child("uploads").child(imageName).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            imageUrl[0] =task.getResult().getDownloadUrl().toString();
                            uploadAlert(popDialog, alertModel, dialog, imageUrl[0]);
                        }else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }else{
                uploadAlert(popDialog, alertModel, dialog, imageUrl[0]);
            }

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void uploadAlert(final AlertDialog popDialog, AlertModel alertModel, final ProgressDialog dialog, String imageUrl) {
        alertModel.setImageUrl(imageUrl);
        mReference.child(myCounty).push()
                .setValue(alertModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                popDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Error uploading "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showBtnShow() {
        btnShow.setVisibility(View.VISIBLE);
    }

    private void hideBtnShow() {
        btnShow.setVisibility(View.INVISIBLE);
    }

    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }

    private void showProgressLayout() {
        progressLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressLayout() {
        progressLayout.setVisibility(View.GONE);
    }

    private void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            EnableGPSAutoMatically();
        } else {
            mGpsEnabled = true;
        }
    }

    private void requestPermission() {
        String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(getActivity(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getActivity(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, 24);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, 24);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 24:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                //initialize map
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                mGpsEnabled = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            incidentImage.setImageURI(imageUri);
            imageUri=data.getData();
            incidentImage.setImageURI(imageUri);
            //placeHolder.setVisibility(View.GONE);
        }
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient fusedProvider = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedProvider.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //  Logic to handle location object
                            mLastKnownLocation = location;
                            Constants.setmLastKownLocation(mLastKnownLocation);
                            new GeoCode().execute();
                            System.out.println("miles1 all clear");
                        } else {
                            System.out.println("miles2 errror");

                            hideProgressLayout();
                            showErrorLayout();
                            retryButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hideErrorLayout();
                                    showProgressLayout();
                                    getDeviceLocation();

                                }
                            });
                            // Toast.makeText(getActivity(), "An error occured when getting location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("miles 3"+e.getMessage());
            }
        });
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            mGpsEnabled = true;

                            // Get the current location of the device and set the position of the map.
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            mGpsEnabled = false;
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                status.startResolutionForResult(getActivity(), 1000);
                                mGpsEnabled = true;

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(getActivity(), "Could not find settings", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class GeoCode extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
                Address address = addressList.get(0);
                return address.getFeatureName() + "\n" + address.getAdminArea();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("miles1" + e.getMessage());
                return "error loading your location";
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            if (s.equals("error loading your location")) {
                System.out.println("miles4 error here");

                hideProgressLayout();
                showErrorLayout();
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideErrorLayout();
                        showProgressLayout();
                        getDeviceLocation();
                    }
                });
            } else {
                //get my current county
                String[] items = s.split("\n");
                myCounty = items[1];
                Constants.setCountyName(myCounty);
                myLocation = s.replace("\n"," ");
                hideProgressLayout();
                //load alerts from firestore
                showBtnShow();
                loadAlerts();
                System.out.println("miles5 all good");

                //update my location
                updateMyLocation();
            }
        }
    }
    private void updateMyLocation(){
        LocationUpdate update=new LocationUpdate();
        update.setLat(mLastKnownLocation.getLatitude());
        update.setLng(mLastKnownLocation.getLongitude());
        update.setDeviceToken(IRescuePreferences.getDeviceToken(getActivity()));
        usersLocationRef.child(myCounty).child(Constants.getUser().getUserId()).setValue(update);
    }
    private void loadAlerts() {
        final List<AlertModel> alertModelList=new ArrayList<>();
        mReference.child(myCounty).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    AlertModel model=data.getValue(AlertModel.class);
                    model.setAlertId(data.getKey());
                    alertModelList.add(model);
                }
                mAlertsRecyclerView.setAdapter(new AlertsAdapter(alertModelList,mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),getActivity()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
