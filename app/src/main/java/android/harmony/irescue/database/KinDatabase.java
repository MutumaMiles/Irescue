package android.harmony.irescue.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.harmony.irescue.model.KinModel;

import java.util.List;

public abstract class KinDatabase  extends RoomDatabase {

    private static KinDatabase INSTANCE;


    public static KinDatabase getDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE=Room.databaseBuilder(context.getApplicationContext(),
                    KinDatabase.class,
                    "kin_database")
                    .build();

        }
        return INSTANCE;
    }
    public static void destroyDb(){
        INSTANCE=null;
    }

    public abstract KinModelDao kidsModelDao();

}
