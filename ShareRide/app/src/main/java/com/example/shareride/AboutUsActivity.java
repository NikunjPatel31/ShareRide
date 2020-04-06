package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;;

public class AboutUsActivity extends AppCompatActivity {

    private static final String TAG = "AboutUsActivity";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initializeWidgets();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(getApplicationContext(), Account_Activity.class));
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onClick: Exception: "+e.getLocalizedMessage());
                }
            }
        });
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initialize widgets.");
        toolbar = findViewById(R.id.toolbar);
    }
}
