package android.harmony.irescue.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.harmony.irescue.model.KinModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface KinModelDao {

    @Query("SELECT * FROM KinModel")
    LiveData<List<KinModel>> getAllKins();

    @Query("SELECT * FROM KinModel WHERE id=:id")
    KinModel getKinById(String id);

    @Insert(onConflict = REPLACE)
    void insertKin(KinModel model);

    @Delete
    void deleteKin(KinModel model);
}
