package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String TAG = "HomeScreenActivity";

    FloatingActionButton offerFAB;
    BottomAppBar bottomAppBar;
    FirebaseAuth mAuth;

    public void add(View view)
    {
        offerFAB.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_click_animation));
        Intent intent = new Intent(HomeScreenActivity.this, Source_Location_Activity.class);
        intent.putExtra("Activity","SourceLocation");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeFirebaseInstance();

        offerFAB = (FloatingActionButton) findViewById(R.id.offer_ride_FAB);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_action_bar);
        setSupportActionBar(bottomAppBar);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, Account_Activity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        offerFAB.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fab_animation));
        bottomAppBar.setAnimation(AnimationUtils.loadAnimation(this,R.anim.bottom_app_bar_animation));
    }

    private void initializeFirebaseInstance()
    {
        Log.d(TAG, "initializeFirebaseInstance: initializing firebase Instances.");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.home:
                Toast.makeText(this, "menu pressed.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.app_bar_search:
                Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
                intent.putExtra("Activity","Search_Ride");
                startActivity(intent);
                break;
            case R.id.app_bar_notification:
                startActivity(new Intent(HomeScreenActivity.this, NotificationActivity.class));
            default:
                Log.d(TAG, "onOptionsItemSelected: default case.");
                return false;
        }
        return true;
    }
}
