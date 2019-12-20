package android.harmony.irescue;

import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.fragments.ForgotPasswordFragment;
import android.harmony.irescue.fragments.SignInFragment;
import android.harmony.irescue.fragments.SignUpFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static Fragment mFragment;
    private FirebaseAuth mAuth;


    public static Intent newInstance(Context context){
        return new Intent(context,MainActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment fragment=SignInFragment.newInstance();
        mAuth=FirebaseAuth.getInstance();
        final FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_from_right,R.anim.slide_to_left,R.anim.slide_from_left,R.anim.slide_to_right).add(R.id.container, fragment).addToBackStack(null).commit();
        setmFragment(fragment);
        ImageView backImageView=findViewById(R.id.back_image_view);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFragment instanceof SignUpFragment){
                    fragmentManager.popBackStack();
                    setmFragment(new SignInFragment());
                }else if(mFragment instanceof ForgotPasswordFragment) {
                    fragmentManager.popBackStack();
                    setmFragment(new SignInFragment());
                }else{
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(HomeActivity.newInstance(this));
            finish();
        }
    }

    public static void setmFragment(Fragment fragment){
        mFragment=fragment;
    }

    @Override
    public void onBackPressed() {
        if(mFragment instanceof SignUpFragment){
            getSupportFragmentManager().popBackStack();
            setmFragment(new SignInFragment());
        }else {
            finish();
        }
    }
}
