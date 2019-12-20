package android.harmony.irescue.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.harmony.irescue.PowerButtonListener;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class StartUpService extends Service {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private PowerButtonListener receiver=null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("miles registered service");
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        receiver=new PowerButtonListener();
        registerReceiver(receiver,intentFilter);
        System.out.println("miles broadcast reciver");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
