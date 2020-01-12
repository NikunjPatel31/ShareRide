package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;

public class HomeScreenActivity extends AppCompatActivity {

    BottomAppBar bottomAppBar;

    public void check(View view)
    {
        startActivity(new Intent(HomeScreenActivity.this,Edit_Car_Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_action_bar);
        setSupportActionBar(bottomAppBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar, menu);
        return true;
    }
}
