package android.harmony.irescue;

import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.model.User;
import android.harmony.irescue.utility.Constants;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage,placeHolder;
    private Uri imageUri=null;
    private StorageReference mReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private User user;

    public static Intent newInstance(Context context,User user){
        Intent intent=new Intent(context,ProfileActivity.class);
        intent.putExtra("User",user);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.user= (User) getIntent().getSerializableExtra("User");
        System.out.println("miles"+user.getEmail());
        mAuth=FirebaseAuth.getInstance();
        usersReference= FirebaseDatabase.getInstance().getReference("Users");
        mReference= FirebaseStorage.getInstance().getReference("Profile_Images");
        profileImage=findViewById(R.id.profile_image);
        placeHolder=findViewById(R.id.place_holder);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Choose Profile Pic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout chooserImage=findViewById(R.id.load_image_chooser);
        chooserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),24);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                uploadUserDetails();
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadUserDetails() {
        if(imageUri!=null){
            final SpotsDialog waitingDialog = new SpotsDialog(ProfileActivity.this);
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.show();
            mAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        final String userId=mAuth.getCurrentUser().getUid();
                        mReference.child(userId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    String downloadUrl=task.getResult().getDownloadUrl().toString();
                                    user.setImageUrl(downloadUrl);
                                    usersReference.child(userId).child("UserDetails").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            waitingDialog.dismiss();
                                            if(task.isSuccessful()){
                                                startActivity(NextOfKinActivity.newInstace(userId,ProfileActivity.this));
                                            }else {
                                                Toast.makeText(ProfileActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    waitingDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        waitingDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error3 "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==24){
            imageUri=data.getData();
            profileImage.setImageURI(imageUri);
            placeHolder.setVisibility(View.GONE);
        }
    }
}
