package com.example.shareride;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class Account_Fragment extends Fragment {

    private static final String TAG = "Account_Fragment";

    private Button logoutbtn;
    private TextView helpTV;
    private FirebaseAuth mAtuh;

    public Account_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_, container, false);
        initializeWidgets(view);
        initializeFirebaseInstance();
        widgetsListener();
        return view;
    }

    private void initializeWidgets(View view)
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        logoutbtn = (Button) view.findViewById(R.id.logoutButton);
        helpTV = view.findViewById(R.id.help_textview);
    }

    private void initializeFirebaseInstance()
    {
        Log.d(TAG, "initializeFirebaseInstance: initializing firebase instances.");
        mAtuh = FirebaseAuth.getInstance();
    }

    private void widgetsListener()
    {
        Log.d(TAG, "widgetsListener: one of the listener is fired.");
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: logoutbtn pressed.");
                mAtuh.signOut();
                Toast.makeText(getActivity(), "Loged out.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        helpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),AboutUsActivity.class));
            }
        });
    }

}
