package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_My_Cars_Activity extends AppCompatActivity {

    private static final String TAG = "View_My_Cars_Activity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<CarDetails> options;
    private FirebaseRecyclerAdapter<CarDetails, CarDetailsViewHolder> firebaseRecyclerAdapter;

    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__my__cars_);

        initializeWidgets();
        initializeFirebaseInstances();
        initiaizingOption();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initalizingFirebaseRecyclerAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initalizingFirebaseRecyclerAdapter();

    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cars").child(UID);
        storageReference = FirebaseStorage.getInstance().getReference().child("Car_Image");
    }

    private void initiaizingOption()
    {
        Log.d(TAG, "initiaizingOption: initializing firebase recyclerview option.");
        options = new FirebaseRecyclerOptions.Builder<CarDetails>().
                setQuery(databaseReference, CarDetails.class)
                .build();
    }

    private void initalizingFirebaseRecyclerAdapter()
    {
        Log.d(TAG, "initalizingFirebaseRecyclerAdapter: initializing firebase recycler view adapter.");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CarDetails, CarDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarDetailsViewHolder holder, int position, @NonNull CarDetails model) {
                Log.d(TAG, "onBindViewHolder: populating the recycler view.");
                holder.setCarName(model.getCar_Name());
                holder.setVehicleNumber(model.getVehicle_Number());
                holder.setFuel(model.getModel_Year());
                holder.setAirConditioner(model.getAir_Conditioner());
                holder.setCarModelYear(model.getModel_Year());
                holder.setCarImage(getApplicationContext(),model.getCar_Image());
            }

            @NonNull
            @Override
            public CarDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.car_details_row,parent,false);

                return new CarDetailsViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class CarDetailsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public CarDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAirConditioner(String airConditioner)
        {
            TextView airConditionerTV = (TextView) mView.findViewById(R.id.air_conditioner_textview);
            airConditionerTV.setText(airConditioner);
        }

        public void setCarImage(Context context,String carImage)
        {
            CircleImageView carImageIV = (CircleImageView) mView.findViewById(R.id.car_image);
            Picasso.get().load(Uri.parse(carImage)).into(carImageIV);
        }

        public void setCarName(String carName)
        {
            TextView carNameTV = (TextView) mView.findViewById(R.id.car_name_textview);
            carNameTV.setText(carName);;
        }

        public void setFuel(String fuel)
        {
            TextView fuelTV = (TextView) mView.findViewById(R.id.fuel_textview);
            fuelTV.setText(fuel);
        }

        public void setCarModelYear(String carModelYear)
        {
            TextView carModelYearTV = (TextView) mView.findViewById(R.id.model_year_textview);
        }

        public void setVehicleNumber(String vehicleNumber)
        {
            TextView vehicleNumberTV = (TextView) mView.findViewById(R.id.vehicle_number_textview);
            vehicleNumberTV.setText(vehicleNumber);
        }


    }
}
