package android.harmony.irescue;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.harmony.irescue.adapters.KinsAdpater;
import android.harmony.irescue.model.KinModel;
import android.harmony.irescue.utility.Constants;
import android.harmony.irescue.viewmodel.KinsModelView;
import android.harmony.irescue.viewmodel.NotesListViewModel;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class NextOfKinActivity extends AppCompatActivity {
    private RecyclerView nextOfKins;
    private KinsAdpater mKinsAdpater;
    private KinsModelView mKinsModelView;
    private List<KinModel> mKinModelsList;
    private NotesListViewModel mViewModel;
    private TextView displayText;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private String userId;

    public static Intent newInstace(String userId,Context context){
        Intent intent=new Intent(context,NextOfKinActivity.class);
        intent.putExtra("userId",userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_of_kin);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Next of Kin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        mReference= FirebaseDatabase.getInstance().getReference("Users");
        nextOfKins=findViewById(R.id.next_of_kins);
        displayText=findViewById(R.id.display_text);
        nextOfKins.setLayoutManager(new LinearLayoutManager(this));
        mKinsAdpater=new KinsAdpater(new ArrayList<KinModel>());
        mKinModelsList=new ArrayList<>();
        nextOfKins.setAdapter(mKinsAdpater);
        userId=getIntent().getStringExtra("userId");
       // mKinsModelView=ViewModelProviders.of(this).get(NextOfKinActivity.class);
        FloatingActionButton btnAdd=findViewById(R.id.add_next_of_kin);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNextOfKin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                if(mKinModelsList.size()>0){
                    saveNextOfKin();
                }else{
                    Toast.makeText(this, "please add more than next of kin", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveNextOfKin(){
        if(mAuth.getCurrentUser()!=null){
            final SpotsDialog dialog=new SpotsDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            for (int i=0;i<mKinModelsList.size();i++){
                final int finalI = i;
                mReference.child(userId).child("NextOfkins").push().setValue(mKinModelsList.get(finalI)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(finalI ==mKinModelsList.size()-1) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                startActivity(HomeActivity.newInstance(NextOfKinActivity.this));
                            } else {
                                Toast.makeText(NextOfKinActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }
    private void addNextOfKin(){
        View view= LayoutInflater.from(this).inflate(R.layout.add_next_of_kin,null);
        final AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Add",null)
                .setNegativeButton("Cancel",null)
                .create();
        final EditText name=view.findViewById(R.id.name);
        final EditText phoneNumber=view.findViewById(R.id.phone_number);
        final EditText relationship=view.findViewById(R.id.relationship);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button addButton=alertDialog.getButton(dialog.BUTTON_POSITIVE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      validateInput(name,phoneNumber,relationship,alertDialog);
                       // Toast.makeText(NextOfKinActivity.this, "hey "+kinModel.getName()+kinModel.getPhoneNummber()+kinModel.getRelationship(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void validateInput(EditText name, EditText phoneNumber, EditText relationship, AlertDialog alertDialog) {
        boolean hasError=false;
        if(name.getText().toString().equals("")){
            name.setError("Required");
            hasError=true;
        }
        if(phoneNumber.getText().toString().equals("")){
            phoneNumber.setError("Required");
            hasError=true;
        }
        if(relationship.getText().toString().equals("")){
            relationship.setError("Required");
            hasError=true;
        }
        if(!hasError){
            KinModel kinModel=new KinModel();
            kinModel.setName(name.getText().toString());
            kinModel.setRelationship(relationship.getText().toString());
            kinModel.setPhoneNummber(phoneNumber.getText().toString());
            mKinModelsList.add(kinModel);
            mKinsAdpater.addKin(mKinModelsList);
            alertDialog.dismiss();
            displayText.setVisibility(View.INVISIBLE);
        }
    }
}
