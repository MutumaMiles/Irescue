package android.harmony.irescue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.services.SendSmsService;

public class PowerButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("miles power button clicked");
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            System.out.println("miles power button clicked");
            Intent startIntent=new Intent(context, SendSmsService.class);
            startIntent.putExtra("start_service",true);
            context.startService(startIntent);
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            System.out.println("miles power button clicked");
            Intent startIntent=new Intent(context, SendSmsService.class);
            startIntent.putExtra("start_service",false);
            context.startService(startIntent);
        }
    }
}
