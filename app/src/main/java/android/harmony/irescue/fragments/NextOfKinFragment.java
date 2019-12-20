package android.harmony.irescue.fragments;

import android.harmony.irescue.R;
import android.harmony.irescue.adapters.KinsAdpater;
import android.harmony.irescue.model.KinModel;
import android.harmony.irescue.utility.Constants;
import android.harmony.irescue.utility.DividerDecoration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NextOfKinFragment extends Fragment {
    private FirebaseFirestore mFirestore;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView;
    private LinearLayout progressLayout,errorLayout;

    public static Fragment newInstance(){
        return new NextOfKinFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReference= FirebaseDatabase.getInstance().getReference("Users").child(Constants.getUser().getUserId()).child("NextOfkins");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.kins_fragment,container,false);
        progressLayout=view.findViewById(R.id.progress_layout);
        errorLayout=view.findViewById(R.id.error_layout);
        TextView errorTextView=view.findViewById(R.id.error_text_view);
        errorTextView.setText("Error getting your kins");
        mRecyclerView=view.findViewById(R.id.kins_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        showProgressLayout();
        hideErrorLayout();
        Button retryButton=view.findViewById(R.id.retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideErrorLayout();
                showProgressLayout();
                loadKins();
            }
        });
        loadKins();
        return view;
    }

    private void loadKins() {
        final List<KinModel> modelList=new ArrayList<>();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    KinModel model=data.getValue(KinModel.class);
                    model.setId(data.getKey());
                    modelList.add(model);
                }
                hideProgressLayout();
                if(modelList.size()==0){
                    showWErrorLayout();
                }else {
                    mRecyclerView.setAdapter(new KinsAdpater(modelList));
                    Constants.setKinModels(modelList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideErrorLayout(){
        errorLayout.setVisibility(View.INVISIBLE);
    }
    private void showWErrorLayout(){
        errorLayout.setVisibility(View.VISIBLE);
    }
    private void hideProgressLayout(){
        progressLayout.setVisibility(View.INVISIBLE);
    }
    private void showProgressLayout(){
        progressLayout.setVisibility(View.VISIBLE);
    }
}
