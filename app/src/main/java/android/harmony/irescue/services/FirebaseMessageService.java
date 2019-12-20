package android.harmony.irescue.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.HomeActivity;
import android.harmony.irescue.R;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService  extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("miles new notification");
        String messageTitle=remoteMessage.getNotification().getTitle();
        String messageBody=remoteMessage.getNotification().getBody();
        notificationUtil(getApplicationContext(),messageTitle,messageBody);

    }

    private void notificationUtil(Context context,String title,String message){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        /*
         * This Intent will be triggered when the user clicks the notification. In our case,
         * we want to open Sunshine to the DetailActivity to display the newly updated weather.
         */
        Intent detailIntentForToday = new Intent(context, HomeActivity.class);


        android.support.v4.app.TaskStackBuilder taskStackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(24, notificationBuilder.build());
    }
}
