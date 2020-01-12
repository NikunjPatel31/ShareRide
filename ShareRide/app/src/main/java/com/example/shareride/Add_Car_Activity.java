package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Add_Car_Activity extends AppCompatActivity {

    private static final String TAG = "Add_Car_Activity";

    Spinner airConditionerSpinner, fuelSpinner;
    TextView airConditionerTV, fuelTV;

    ArrayList<String> airConditionerList, fuelList;
    ArrayAdapter<String> airConfitionerAdapter, fuelAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__car_);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        initializWidgets();
        //ArrayList for spinner.
        airConditionerList = new ArrayList<>();
        fuelList = new ArrayList<>();
        //adding data in ArrayList for airConditioner spinner.
        airConditionerList.add("AC");
        airConditionerList.add("Non - AC");
        //adding data in ArrayList for fuel spinner.
        fuelList.add("Petrol");
        fuelList.add("Diesel");
        fuelList.add("CNG");
        //ArrayAdapter for spinner.
        airConfitionerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,airConditionerList);
        fuelAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,fuelList);
        //setting adapter to the spinner.
        airConditionerSpinner.setAdapter(airConfitionerAdapter);
        fuelSpinner.setAdapter(fuelAdapter);
        //adding ItemSeletedListener to the spinner.
        airConditionerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: Item selected: "+airConditionerSpinner.getSelectedItem().toString());
                airConditionerTV.setText(airConditionerSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: Item selected: "+fuelSpinner.getSelectedItem());
                fuelTV.setText(fuelSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializWidgets()
    {
        Log.d(TAG, "initializWidgets: initializing widgets.");
        airConditionerSpinner = (Spinner) findViewById(R.id.air_conditioner_spinner);
        fuelSpinner = (Spinner) findViewById(R.id.fuel_spinner);
        airConditionerTV = (TextView) findViewById(R.id.air_conditioner_textview);
        fuelTV = (TextView) findViewById(R.id.fuel_textview);
    }
}
