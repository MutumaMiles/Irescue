package android.harmony.irescue.adapters;

import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.CommentActivity;
import android.harmony.irescue.R;
import android.harmony.irescue.model.AlertModel;
import android.harmony.irescue.model.User;
import android.harmony.irescue.utility.CalculateTime;
import android.harmony.irescue.utility.Constants;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertsViewHolder> {
    private List<AlertModel> modelAlerts;
    private double lat,lng;
    private Context mContext;

    public AlertsAdapter(List<AlertModel> modelAlerts, double lat, double lng, Context context) {
        this.modelAlerts = modelAlerts;
        this.lat=lat;
        this.lng=lng;
        this.mContext=context;
    }

    @NonNull
    @Override
    public AlertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_layout,null);
        return new AlertsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertsViewHolder holder, int position) {
        AlertModel model=modelAlerts.get(position);
        holder.bindAlert(model);

    }

    @Override
    public int getItemCount() {
        return modelAlerts.size();
    }

    public class AlertsViewHolder extends RecyclerView.ViewHolder {
        TextView alertMessage,distance,whoPosted,locationTextView,timeTextView;
        ImageView incidentImageView,comment,policeStations,locations;
        public AlertsViewHolder(View itemView) {
            super(itemView);
            alertMessage=itemView.findViewById(R.id.message);
            distance=itemView.findViewById(R.id.distance);
            whoPosted=itemView.findViewById(R.id.messenger);
            incidentImageView=itemView.findViewById(R.id.incident_image);
            comment=itemView.findViewById(R.id.add_comment);
            policeStations=itemView.findViewById(R.id.view_police_stations);
            locations=itemView.findViewById(R.id.view_distance);
            incidentImageView.setImageResource(R.drawable.ic_phone_black_24dp);
            locationTextView=itemView.findViewById(R.id.location);
            timeTextView=itemView.findViewById(R.id.time);
        }

        private void bindAlert(final AlertModel model){
            Location location=new Location("Point A");
            location.setLatitude(model.getLat());
            location.setLongitude(model.getLng());
            Location location2=new Location("Point B");
            location2.setLatitude(lat);
            location2.setLongitude(lng);

            String distanceTo=String.valueOf(location.distanceTo(location2));

            alertMessage.setText(model.getAlertMessage());
            distance.setText(distanceTo);
            locationTextView.setText(model.getLocation());
            String user="";

            User mUser=model.getUser();
            if(Constants.getUser().getUserId().equals(mUser.getUserId())){
                user="You";
            }else{
                user=mUser.getFirstName().concat(" ").concat(mUser.getLastName());
            }
            whoPosted.setText(user);
            timeTextView.setText(String.valueOf(CalculateTime.calculateTime(model.getTime())).equals("Yesterday") ?"Yesterday":"About "+CalculateTime.calculateTime(model.getTime())+" ago");
            if(model.getImageUrl().equals("image")){
                incidentImageView.setVisibility(View.GONE);
            }else{
                Glide.with(mContext)
                        .load(model.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(incidentImageView);
            }
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(CommentActivity.newInstance(mContext,model.getAlertId()));
                }
            });
            policeStations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:"+model.getLat()+","+model.getLng()+"?q=police+station");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(mapIntent);
                }
            });

            locations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", model.getLat(), model.getLng(), model.getLocation());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
