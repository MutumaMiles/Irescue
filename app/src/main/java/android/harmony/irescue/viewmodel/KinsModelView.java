package android.harmony.irescue.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.harmony.irescue.database.KinDatabase;
import android.harmony.irescue.model.KinModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

public class KinsModelView extends AndroidViewModel{
    private KinDatabase mKinDatabase;
    private LiveData<List<KinModel>> kinsList;

    public KinsModelView(@NonNull Application application) {
        super(application);
        mKinDatabase=KinDatabase.getDatabase(this.getApplication());
        kinsList=mKinDatabase.kidsModelDao().getAllKins();
    }

    public LiveData<List<KinModel>> getKinsList() {
        return kinsList;
    }
    public void deleteKin(KinModel model){
        new AsyncTask<KinModel,Void,Void>(){

            @Override
            protected Void doInBackground(KinModel... kinModels) {
                mKinDatabase.kidsModelDao().deleteKin(kinModels[0]);
                return null;
            }
        }.execute(model);
    }
    public void addKin(KinModel model){
        new AsyncTask<KinModel,Void,Void>(){

            @Override
            protected Void doInBackground(KinModel... kinModels) {
                mKinDatabase.kidsModelDao().insertKin(kinModels[0]);
                return null;
            }
        }.execute(model);
    }


}
