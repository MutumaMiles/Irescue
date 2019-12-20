package android.harmony.irescue.services;

import android.app.IntentService;
import android.content.Intent;
import android.harmony.irescue.database.IRescuePreferences;
import android.harmony.irescue.model.KinModel;
import android.harmony.irescue.utility.Constants;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendSmsService extends IntentService{
    private static final long DOUBLE_PRESS_INTERVAL = 250;
    public SendSmsService() {
        super("SENDSMS");
    }
    long lastPress=0;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean screenOn=intent.getBooleanExtra("screen_state",false);
        int count=0;
        System.out.println("hey miles am here");
        long pressTime=System.currentTimeMillis();
        lastPress=IRescuePreferences.getPressTime(getApplicationContext());
        System.out.println("miles count1"+String.valueOf((pressTime)));
        System.out.println("miles count2"+String.valueOf((lastPress)));

        if((pressTime-lastPress)<=DOUBLE_PRESS_INTERVAL){
            for(KinModel model: Constants.getKinModels()) {
                SmsManager smsManager = SmsManager.getDefault();
                StringBuffer smsBody = new StringBuffer();

                smsBody.append("Hi Your "+model.getRelationship()+" just sent you an SOS. He was last seen here => ");
                smsBody.append("http://maps.google.com?q=");
                smsBody.append(Constants.getmLastKownLocation().getLatitude());
                smsBody.append("%2C");
                smsBody.append(Constants.getmLastKownLocation().getLongitude());
                System.out.println("sms miles to "+smsBody.toString());

                 smsManager.sendTextMessage("+254"+model.getPhoneNummber(), null, smsBody.toString(), null, null);
            }
        }
        IRescuePreferences.setPressTime(getApplicationContext(),pressTime);
//        if(screenOn){
//            final long[] lastPress = new long[1];
//            System.out.println("miles screen on");
//        }else{
//            System.out.println("miles screen off");
//        }
    }
}
