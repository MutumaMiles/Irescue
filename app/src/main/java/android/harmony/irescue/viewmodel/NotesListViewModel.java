package android.harmony.irescue.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

public class NotesListViewModel extends AndroidViewModel {

//    private LiveData<List<NoteModel>> noteList;
//    private NoteDatabase mNoteDatabase;

    public NotesListViewModel(@NonNull Application application) {
        super(application);
//        mNoteDatabase=NoteDatabase.getDatabase(this.getApplication());
//        noteList=mNoteDatabase.notesModelDao().getAllNotes();
    }



}
