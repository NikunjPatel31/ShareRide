package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    private LatLng sourceLocation, destinationLocation;
    private String time, date;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__my__cars_);

        initializeWidgets();
        initializeFirebaseInstances();
        initiaizingOption();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try
        {
            progressDialog = CommanClass.progressBar(this,new ProgressDialog(this),"Getting all your car's details");
        }
        catch (Exception e)
        {
            Log.d(TAG, "onCreate: progress Bar Exception: "+e.getLocalizedMessage());
        }
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
        progressDialog.show();
        Log.d(TAG, "initalizingFirebaseRecyclerAdapter: initializing firebase recycler view adapter.");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CarDetails, CarDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarDetailsViewHolder holder, int position, @NonNull CarDetails model) {
                progressDialog.dismiss();
                holder.cardView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.recycler_view_animation));
                Log.d(TAG, "onBindViewHolder: populating the recycler view.");
                final String car_id = getRef(position).getKey();
                holder.setCarName(model.getCar_Name());
                holder.setVehicleNumber(model.getVehicle_Number());
                holder.setFuel(model.getModel_Year());
                holder.setAirConditioner(model.getAir_Conditioner());
                try
                {
                    holder.setCarModelYear(model.getModel_Year());
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onBindViewHolder: image Exception: "+e.getLocalizedMessage());
                }
                holder.setCarImage(getApplicationContext(),model.getCar_Image());

                if(getIntent().getStringExtra("Activity").equals("select car"))
                {
                    holder.editBtn.setVisibility(View.INVISIBLE);
                    holder.deleteBtn.setVisibility(View.INVISIBLE);

                    sourceLocation = getIntent().getExtras().getParcelable("Source Location");
                    destinationLocation = getIntent().getExtras().getParcelable("Destination Location");
                    time = getIntent().getStringExtra("Time");
                    date = getIntent().getStringExtra("Date");

                    holder.selectBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onBindViewHolder: car_id: "+car_id);
                            Intent intent = new Intent(View_My_Cars_Activity.this, Offer_ride_second_Activity.class);
                            intent.putExtra("Source Location",sourceLocation);
                            intent.putExtra("Destination Location",destinationLocation);
                            intent.putExtra("Time",time);
                            intent.putExtra("Date",date);
                            intent.putExtra("Car_id",car_id);
                            startActivity(intent);
                        }
                    });
                }
                else
                {
                    if(getIntent().getStringExtra("Activity").equals("View"))
                    {
                        holder.editBtn.setVisibility(View.VISIBLE);
                        holder.deleteBtn.setVisibility(View.INVISIBLE);
                        holder.selectBtn.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        holder.editBtn.setVisibility(View.INVISIBLE);
                        holder.deleteBtn.setVisibility(View.VISIBLE);
                        holder.selectBtn.setVisibility(View.INVISIBLE);
                    }

                    holder.editBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),Edit_Car_Activity.class);
                            intent.putExtra("Car_id",car_id);
                            startActivity(intent);
                        }
                    });

                    holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.child(car_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(View_My_Cars_Activity.this, "Car deleted successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                    {
                                        Log.d(TAG, "onComplete: task Error: "+task.getException());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
                                }
                            });
                        }
                    });
                }
            }

            @NonNull
            @Override
            public CarDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.car_details_row,parent,false);

                return new CarDetailsViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Log.d(TAG, "onDataChanged: getItemCount: "+getItemCount());
                if(getItemCount() == 0)
                {
                    progressDialog.dismiss();
                    try
                    {
                        dialog();
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onDataChanged: dialog Exception: "+e.getLocalizedMessage());
                    }
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void dialog()
    {
        String message="";
        if(getIntent().getStringExtra("Activity").equals("select car"))
        {
            message = "Sorry, You don't have any cars. Please add a car and then offere the ride.";
        }
        else
        {
            message = "Sorry, You don't have any cars. Please add a car.";
        }
        builder = new AlertDialog.Builder(this);
        builder.setTitle("My Cars")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: button clicked on alert dialog.");
                        if(getIntent().getStringExtra("Activity").equals("select car"))
                        {
                            Intent intent = new Intent(getApplicationContext(),Add_Car_Activity.class);
                            intent.putExtra("Activity","Offered_Ride_Activity");
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), Account_Activity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //finish();
                        }
                    }
                });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    public static class CarDetailsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        Button editBtn, deleteBtn, selectBtn;
        CardView cardView;
        public CarDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            editBtn = (Button) mView.findViewById(R.id.edit_button);
            deleteBtn = (Button) mView.findViewById(R.id.delete_button);
            selectBtn = (Button) mView.findViewById(R.id.select_button);
            cardView = (CardView) mView.findViewById(R.id.inner_cardview);
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
