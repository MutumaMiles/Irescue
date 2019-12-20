package android.harmony.irescue.utility;

import android.harmony.irescue.model.KinModel;
import android.harmony.irescue.model.User;
import android.location.Location;

import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

public class Constants {
    private final static String USERS_COLLECTION="Users";
    private final static String USER_DETAILS="UserDetails";
    private final static String NEXT_OF_KIN="NextOfKins";
    private final static String PERSONAL="personal";
    private final static String KINS="kins";
    private static User sUser;
    private static String countyName;
    public static String DEVELOPER_KEY="AIzaSyAp4IZZOjHwY5X-FhL3uQDH7Vn3aUZNfz8";
    private static List<KinModel> mKinModels;
    private static Location mLastKownLocation;

    public static String getDeveloperKey() {
        return DEVELOPER_KEY;
    }

    public static void setDeveloperKey(String developerKey) {
        DEVELOPER_KEY = developerKey;
    }

    public static List<KinModel> getmKinModels() {
        return mKinModels;
    }

    public static void setmKinModels(List<KinModel> mKinModels) {
        Constants.mKinModels = mKinModels;
    }

    public static Location getmLastKownLocation() {
        return mLastKownLocation;
    }

    public static void setmLastKownLocation(Location mLastKownLocation) {
        Constants.mLastKownLocation = mLastKownLocation;
    }

    public static List<KinModel> getKinModels() {
        return mKinModels;
    }

    public static void setKinModels(List<KinModel> kinModels) {
        mKinModels = kinModels;
    }

    public static String getCountyName() {
        return countyName;
    }

    public static void setCountyName(String countyName) {
        Constants.countyName = countyName;
    }

    public static User getUser() {
        return sUser;
    }

    public static void setUser(User user) {
        sUser = user;
    }

    public static String getKINS() {
        return KINS;
    }

    public static String getPERSONAL() {
        return PERSONAL;
    }

    public static String getUsersCollection() {
        return USERS_COLLECTION;
    }

    public static String getUserDetails() {
        return USER_DETAILS;
    }

    public static String getNextOfKin() {
        return NEXT_OF_KIN;
    }
    public static FirebaseFirestoreSettings getSettings(){
        FirebaseFirestoreSettings settings=new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        return settings;
    }
}
