package android.harmony.irescue.services;

import android.harmony.irescue.database.IRescuePreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenId= FirebaseInstanceId.getInstance().getToken();

        IRescuePreferences.setDeviceToken(getApplicationContext(),tokenId);
    }
}
