package android.harmony.irescue.fragments;

import android.content.Intent;
import android.harmony.irescue.HomeActivity;
import android.harmony.irescue.MainActivity;
import android.harmony.irescue.R;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class SignInFragment extends Fragment {
    private FirebaseAuth mAuth;

    public static Fragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);
        final EditText email = view.findViewById(R.id.email);
        final EditText password = view.findViewById(R.id.password);
        TextView forgotPassword = view.findViewById(R.id.forgot_password);
        TextView signUp = view.findViewById(R.id.sign_up_text_view);
        Button btnSignUp = view.findViewById(R.id.sign_in);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setmFragment(ForgotPasswordFragment.newInstance());
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right).replace(R.id.container, ForgotPasswordFragment.newInstance()).addToBackStack(null).commit();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDetails(email, password);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // loadLocation();
                MainActivity.setmFragment(SignUpFragment.newInstance());
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right).replace(R.id.container, SignUpFragment.newInstance()).addToBackStack(null).commit();

            }
        });
        final boolean[] isChecked = {false};

        final ImageView togglePassword = view.findViewById(R.id.toggle_password);
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
        return view;
    }

    private void loadLocation(){
        // Search for restaurants nearby
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=police+station");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
// Search for restaurants in San Francisco
//        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=restaurants");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);
    }

    private void validateDetails(EditText email, EditText password) {
        boolean hasError = false;

        if (email.getText().toString().equals("")) {
            email.setError("phone number is required");
            hasError = true;
        }
        if (password.getText().toString().equals("")) {
            password.setError("password is required");
            hasError = true;
        }
        if (!hasError) {
            final SpotsDialog dialog = new SpotsDialog(getActivity());
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        startActivity(HomeActivity.newInstance(getActivity()));
                    }else{
                        Toast.makeText(getActivity(), "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
