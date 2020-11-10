package dev.moutamid.earnreal;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private static final String USER_EMAIL = "userEmail";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private NavigationView navigationView;
    //private TextView nav_phone_number;
    private Utils utils = new Utils();
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        if (checkLoginStatus())
            return;

        checkBanStatus();

        //TODO: checkAppUpdateStatus();

        initializeViews();
        toggleDrawer();
        initializeDefaultFragment(savedInstanceState, 0);
        //setUrduSwitchListener();


    }

    private void checkBanStatus() {

        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            if (snapshot.hasChild("isBan")) {
                                if (snapshot.child("isBan").getValue(Boolean.class)) {

                                    utils.showDialog(MainActivity.this,
                                            "",
                                            "Your account is temporarily disabled! Contact Care department (03123456789) for further details.",
                                            "",
                                            "",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            },
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            },
                                            false
                                    );

                                }
                            }

                            // SETTING EMAIL AND PHONE NUMBER IN THE HEADER
                            setHeaderDetails(snapshot.child("fullName").getValue(String.class));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: ", error.toException());
                    }
                });
    }

    private boolean checkLoginStatus() {
        // USER IS NOT SIGNED IN
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // REMOVING ALL ACTIVITIES AND STARTING WELCOME ACTIVITY
            Intent intent = new Intent(MainActivity.this, ActivityWelcome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);

            return true;
        }
        return false;
    }

    /**
     * Initialize all widgets
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_id);
        toolbar.setTitle("Dashboard");
        drawerLayout = findViewById(R.id.drawer_layout_id);
        frameLayout = findViewById(R.id.framelayout_id);
        navigationView = findViewById(R.id.navigationview_id);
        navigationView.setNavigationItemSelectedListener(this);
        //urduSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_urdu_id).getActionView();

    }

    private void setHeaderDetails(String name) {
        View header = navigationView.getHeaderView(0);

        //private SwitchCompat urduSwitch;
        TextView nav_name = (TextView) header.findViewById(R.id.nav_header_user_name_id);
        TextView nav_phone_number = (TextView) header.findViewById(R.id.nav_header_phone_nmbr_id);

        String number = utils.getStoredString(MainActivity.this, USER_EMAIL);
//        String name = utils.getStoredString(MainActivity.this, USER_NUMBER);

        nav_phone_number.setText(number.substring(0, 11));
        nav_name.setText(name);

    }

    private void initializeDefaultFragment(Bundle savedInstanceState, int itemIndex) {
        if (savedInstanceState == null) {
            MenuItem menuItem = navigationView.getMenu().getItem(itemIndex).setChecked(true);
            onNavigationItemSelected(menuItem);
        }
    }

    private void toggleDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //Checks if the navigation drawer is open -- If so, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If drawer is already close -- Do not override original functionality
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard_id:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentDashboard())
                        .commit();
                toolbar.setTitle("Dashboard");
                closeDrawer();
                break;
            case R.id.nav_referrals_id:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentReferrals())
                        .commit();
                toolbar.setTitle("Referral details");
                closeDrawer();
                break;
            case R.id.nav_withdraw_id:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentWithdraw())
                        .commit();
                toolbar.setTitle("Withdraw details");
                closeDrawer();
                break;
            case R.id.nav_privacy_policy_id:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentPrivacyPolicy())
                        .commit();
                toolbar.setTitle("Privacy policy");
                closeDrawer();
                break;
            case R.id.nav_terms_of_services_id:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentTermsOfServices())
                        .commit();
                toolbar.setTitle("Terms of services");
                closeDrawer();
                break;
//           TODO: UN-Comment THIS LINE case R.id.nav_contact_us_id:
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new FragmentContactUs())
//                        .commit();
//                toolbar.setTitle("Contact us");
//                closeDrawer();
//                break;
            case R.id.nav_logout_id:
                closeDrawer();
                showLogoutDialog();
                break;
        }
        return true;
    }

    private void showLogoutDialog() {
        new Utils().showDialog(MainActivity.this, "Are you sure!", "Do you really want to logout?", "No", "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FirebaseAuth.getInstance().signOut();

                // REMOVING ALL ACTIVITIES AND STARTING WELCOME ACTIVITY
                Intent intent = new Intent(MainActivity.this, ActivityWelcome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        }, true);
    }

    /**
     * Checks if the navigation drawer is open - if so, close it
     */
    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /*
      Attach setOnCheckedChangeListener to the urdu switch

    private void setUrduSwitchListener(){
        urduSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    Toast.makeText(MainActivity.this, "Urdu Off", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Urdu On", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/

    /**
     * Iterates through all the items in the navigation menu and deselects them:
     * removes the selection color
     */
    private void deSelectCheckedState() {
        int noOfItems = navigationView.getMenu().size();
        for (int i = 0; i < noOfItems; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }


}
