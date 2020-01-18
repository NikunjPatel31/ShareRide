package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String TAG = "HomeScreenActivity";

    BottomAppBar bottomAppBar;
    FirebaseAuth mAuth;

    public void add(View view)
    {
        startActivity(new Intent(HomeScreenActivity.this, Source_Location_Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeFirebaseInstance();



        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_action_bar);
        setSupportActionBar(bottomAppBar);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, Account_Activity.class));
            }
        });
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
        switch (item.getGroupId())
        {
            case R.id.home:
                Toast.makeText(this, "menu pressed.", Toast.LENGTH_SHORT).show();
                break;
                default:
                    return false;
        }
        return true;
    }
}
