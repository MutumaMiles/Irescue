package android.harmony.irescue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.services.StartUpService;

public class StartUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            System.out.println("miles boot coplete");
            context.startService(new Intent(context, StartUpService.class));
        }
    }
}
