package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private Spinner genderSpinner;
    private TextView genderSpinnerTV;
    private ArrayList<String> genderArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.background5) ;
        setContentView(R.layout.activity_sign_in);

        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        genderSpinnerTV = (TextView) findViewById(R.id.gender_textview);
        genderArrayList = new ArrayList<>();

        genderArrayList.add("Male");
        genderArrayList.add("Female");

        ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,genderArrayList);
        genderSpinner.setAdapter(genderArrayAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSpinnerTV.setText(genderSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
