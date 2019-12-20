package android.harmony.irescue.fragments;

import android.harmony.irescue.ProfileActivity;
import android.harmony.irescue.R;
import android.harmony.irescue.model.User;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpFragment extends Fragment {

    public static Fragment newInstance(){
        return new SignUpFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.sign_up_layout,container,false);
        final EditText firstName=view.findViewById(R.id.first_name);
        final EditText lastName=view.findViewById(R.id.last_name);
        final EditText email=view.findViewById(R.id.email);
        final EditText phoneNumber=view.findViewById(R.id.phone_number);
        final EditText password=view.findViewById(R.id.password);
        final ImageView togglePassword = view.findViewById(R.id.toggle_password);

        final boolean[] isChecked = {false};
        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked[0]) {
                    // show password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isChecked[0] = true;
                    togglePassword.setImageResource(R.drawable.ic_visibility_black_coloured_18dp);
                } else {
                    // hide password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isChecked[0] = false;
                    togglePassword.setImageResource(R.drawable.ic_visibility_off_coloured_18dp);
                }
            }
        });
        Button signUp=view.findViewById(R.id.sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDetails(firstName,lastName,email,phoneNumber,password);
                //startActivity(NextOfKinActivity.newInstace(getActivity()));
            }
        });
        return view;
    }

    private void validateDetails(EditText firstName, EditText lastName, EditText email, EditText phoneNumber, EditText password) {
        boolean hasError=false;
        if(firstName.getText().toString().equals("")){
            firstName.setError("Please provide your first name");
            hasError=true;
        }
        if(lastName.getText().toString().equals("")){
            lastName.setError("Please provide your last name");
            hasError=true;
        }
        if(email.getText().toString().equals("")){
            email.setError("Please provide your email");
            hasError=true;
        }
        if(phoneNumber.getText().toString().equals("")){
            phoneNumber.setError("Please provide your phone number");
            hasError=true;
        }
        if(password.getText().toString().equals("")){
            password.setError("Please provide your password");
            hasError=true;
        }

        if(!hasError){
            final User user=new User();
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setEmail(email.getText().toString());
            user.setPhoneNumber(phoneNumber.getText().toString());
            user.setPassword(password.getText().toString());
            startActivity(ProfileActivity.newInstance(getActivity(),user));
        }

    }
}
