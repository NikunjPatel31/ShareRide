package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class Offer_ride_one_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "Offer_ride_one_Activity";

    private TextView dateTV, timeTV, offeredRideTitleTV, offeredRideTitleDescTV;
    private String time = "";
    private String date = "";

    private LatLng sourceLocation, destinationLocation;

    public void next(View view)
    {
        Log.d(TAG, "next: taking user to next screen.");
        if(validateFields())
        {
            Log.d(TAG, "next: time: "+time);
            Log.d(TAG, "next: Date: "+date);
            Intent intent = new Intent(Offer_ride_one_Activity.this,View_My_Cars_Activity.class);
            intent.putExtra("Source Location",sourceLocation);
            intent.putExtra("Destination Location",destinationLocation);
            intent.putExtra("Time",time);
            intent.putExtra("Date",date);
            intent.putExtra("Activity","select car");
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Fields are empty.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride_one_);

        sourceLocation = getIntent().getExtras().getParcelable("Source Location");
        destinationLocation = getIntent().getExtras().getParcelable("Destination Location");

        initializeWidgets();
        animateWidgets();
        textViewClickListener();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        dateTV = (TextView) findViewById(R.id.date_textview);
        timeTV = (TextView) findViewById(R.id.time_textview);
        offeredRideTitleTV = (TextView) findViewById(R.id.offer_ride_title_textview);
        offeredRideTitleDescTV = (TextView) findViewById(R.id.offer_ride_description_textview);
    }

    private void animateWidgets()
    {
        Log.d(TAG, "animateWidgets: animating widgets.");
        dateTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        timeTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        offeredRideTitleTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
        offeredRideTitleDescTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
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
                //R.style.custom_date_picker_dialog,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "-" + month + "-" + year;
        dateTV.setText(date);
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
        Log.d(TAG, "validateFields: Validating fields.");
        if(date.equals("") || time.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
