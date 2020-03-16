package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "SearchActivity";
    private EditText sourceLocationET, destinationLocationET;
    private TextView dateTV, timeTV;
    private String date, time, sourceLocation, destinationLocation = null;
    public void search(View view)
    {
        Log.d(TAG, "search: search button pressed.");
        if(validateFields())
        {
            Intent intent = new Intent(SearchActivity.this, SearchRideResultActivity.class);
            intent.putExtra("Source_Location",sourceLocation);
            intent.putExtra("Destination_Location",destinationLocation);
            intent.putExtra("Date",date);
            intent.putExtra("Time",time);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeWigets();
        textViewClickListener();
    }
    private void initializeWigets()
    {
        Log.d(TAG, "initializeWigets: initializing widgets.");
        sourceLocationET = (EditText) findViewById(R.id.source_location_edittext);
        destinationLocationET = (EditText) findViewById(R.id.destination_location_edittext);
        dateTV = (TextView) findViewById(R.id.date_textview);
        timeTV = (TextView) findViewById(R.id.time_textview);
    }
    private void textViewClickListener()
    {
        Log.d(TAG, "textViewClickListener: textView click listener.");
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickedDialog();
            }
        });
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }
    private void showDatePickedDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private void showTimePicker()
    {
        int hours, min;
        Log.d(TAG, "showTimePicker: showing time picker.");
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                min = Calendar.getInstance().get(Calendar.MINUTE),
                android.text.format.DateFormat.is24HourFormat(this)
        );
        timePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "-" + month + "-" + year;
        dateTV.setText(date);
        Log.d(TAG, "onDateSet: date: "+month);
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;

        if(hourOfDay > 12)
        {
            int tem = hourOfDay - 12;
            time = tem + ":" + minute + " PM";
            Log.d(TAG, "onTimeSet: time: "+tem);
        }
        else
        {
            time = hourOfDay + ":" + minute + " AM";
        }
        timeTV.setText(time);
    }
    private boolean validateFields()
    {
        Log.d(TAG, "validateFields: validating fields.");
        sourceLocation = sourceLocationET.getText().toString();
        destinationLocation = destinationLocationET.getText().toString();
        Log.d(TAG, "validateFields: sourceLocation: "+TextUtils.isEmpty(date));

        if(!((TextUtils.isEmpty(sourceLocation)
                && TextUtils.isEmpty(destinationLocation)
                && TextUtils.isEmpty(date)
                && TextUtils.isEmpty(time))))
        {
            Log.d(TAG, "validateFields: fields are validated.");
            return true;
        }
        else
        {
            Log.d(TAG, "validateFields: fields are empty.");
            Toast.makeText(this, "Required fields are empty.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "validateFields: sourceLocation: "+sourceLocation);
            Log.d(TAG, "validateFields: destinationLocation: "+destinationLocation);
            Log.d(TAG, "validateFields: date: "+date);
            Log.d(TAG, "validateFields: time: "+time);
            return false;
        }
    }
}