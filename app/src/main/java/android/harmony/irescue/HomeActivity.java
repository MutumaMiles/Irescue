package android.harmony.irescue;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.harmony.irescue.database.IRescuePreferences;
import android.harmony.irescue.fragments.FirstAidFragment;
import android.harmony.irescue.fragments.HomeFragment;
import android.harmony.irescue.fragments.NextOfKinFragment;
import android.harmony.irescue.model.Token;
import android.harmony.irescue.model.User;
import android.harmony.irescue.services.SendSmsService;
import android.harmony.irescue.utility.Constants;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private TextView userName,userEmail;
    private DatabaseReference mReference,devicesRef;
    private PowerButtonListener receiver=null;
    public static Intent newInstance(Context context){
        return new Intent(context,HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        receiver=new PowerButtonListener();
        registerReceiver(receiver,intentFilter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alerts");
        mAuth=FirebaseAuth.getInstance();
        mReference= FirebaseDatabase.getInstance().getReference("Users");
        devicesRef=FirebaseDatabase.getInstance().getReference("Devices");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);

        profileImageView=headerView.findViewById(R.id.profile_image);
        userName=headerView.findViewById(R.id.user_name);
        userEmail=headerView.findViewById(R.id.email);

        if(mAuth.getCurrentUser()!=null){
            final String userId=mAuth.getCurrentUser().getUid();
            mReference.child(userId).child("UserDetails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    user.setUserId(userId);
                    showDetails(user);
                    setDeviceToken();
                    loadHomeFragment(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }

    private void setDeviceToken(){
        String token= IRescuePreferences.getDeviceToken(getApplicationContext());
        Token myToken=new Token();
        myToken.setTokenId(token);
        devicesRef.child(mAuth.getCurrentUser().getUid()).setValue(myToken);
    }

    //load home fragment
    private void loadHomeFragment(User user){
        Fragment fragmentHolder=getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        HomeFragment fragment=HomeFragment.newInstance(user);
        FragmentManager fragmentManager=getSupportFragmentManager();
        if(fragmentHolder==null) {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }else {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void showDetails(User user) {
        Constants.setUser(user);
        userName.setText(Constants.getUser().getFirstName().concat(" ").concat(Constants.getUser().getLastName()));
        userEmail.setText(Constants.getUser().getEmail());
        //add glide library
        Glide.with(HomeActivity.this)
                .load(Constants.getUser().getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImageView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment.newInstance(Constants.getUser())).commit();
            // load home fragments
            getSupportActionBar().setTitle("Alerts");
        } else if (id == R.id.police_stations) {
            //load police stations
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=police+station");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } else if (id == R.id.next_of_kins) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, NextOfKinFragment.newInstance()).commit();
            getSupportActionBar().setTitle("Next of Kin");

        } else if (id == R.id.nav_manage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FirstAidFragment.newInstance()).commit();
            getSupportActionBar().setTitle("First Aid");

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if (id == R.id.log_out) {
            mAuth.signOut();
            startActivity(MainActivity.newInstance(this));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
