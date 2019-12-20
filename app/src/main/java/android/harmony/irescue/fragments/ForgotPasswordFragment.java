package android.harmony.irescue.fragments;

import android.harmony.irescue.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class ForgotPasswordFragment extends Fragment {
    private FirebaseAuth mAuth;

    public static Fragment newInstance(){
        return new ForgotPasswordFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.forgot_password_layout,container,false);
        final EditText email=view.findViewById(R.id.email);
        Button send=view.findViewById(R.id.reset_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(email.getText().toString()=="")) {
                    final SpotsDialog dialog = new SpotsDialog(getActivity());
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Link sent to your email", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    email.setError("Please provide your email");
                }
            }
        });
        return view;
    }
}
