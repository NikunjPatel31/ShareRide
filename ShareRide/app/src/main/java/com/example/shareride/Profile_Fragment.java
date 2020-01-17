package com.example.shareride;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Fragment extends Fragment{
    private static final String TAG = "Profile_Fragment";

    TextView editProfileTV, addCarTV, deleteCarTV, viewCarTV;


    public Profile_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);
        initializeWidgets(view);
        clickListener();
        return view;
    }

    private void initializeWidgets(View view)
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        editProfileTV = (TextView) view.findViewById(R.id.edit_details_textview);
        addCarTV = (TextView) view.findViewById(R.id.add_car_textview);
        deleteCarTV = (TextView) view.findViewById(R.id.delete_my_car_textview);
        viewCarTV = (TextView) view.findViewById(R.id.view_my_car_textview);
    }

    private void clickListener()
    {
        editProfileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: editProfileTV pressed.");
                startActivity(new Intent(getActivity(),Edit_Profile_Activity.class));
            }
        });

        addCarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: addCarTV pressed.");
                startActivity(new Intent(getActivity(), Add_Car_Activity.class));
            }
        });

        deleteCarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: deleteCarTV pressed.");
                startActivity(new Intent(getActivity(),View_My_Cars_Activity.class));
            }
        });

        viewCarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: viewCarTV pressed.");
                Toast.makeText(getActivity(), "View Car Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
