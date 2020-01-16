package com.example.shareride;

import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account_Activity extends AppCompatActivity {

    private static final String TAG = "Account_Activity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabItem profileTI, accountTI;
    private TextView userNameTV;
    private CircleImageView profileImageIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_);

        initializeWidgets();
        initializeFirebaseInstance();
        setViewPagerAdapter();
        TablayoutOnTabSeleteListener();
        setOfflineData();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        profileTI = (TabItem) findViewById(R.id.profile_tab_item);
        accountTI = (TabItem) findViewById(R.id.account_tab_item);
        userNameTV = (TextView) findViewById(R.id.user_name_textview);
        profileImageIV = (CircleImageView) findViewById(R.id.profilePicImageView);
    }

    private void initializeFirebaseInstance()
    {
        Log.d(TAG, "initializeFirebaseInstance: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void setViewPagerAdapter()
    {
        Log.d(TAG, "setViewPagerAdapter: setting viewpager adapter.");
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
    }

    private void TablayoutOnTabSeleteListener()
    {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setOfflineData()
    {
        Log.d(TAG, "setOfflineData: setting prfile image and user name.");
        String UID = mAuth.getUid();
        DatabaseReference mChildDB = databaseReference.child("Users").child(UID);

        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("First Name").getValue().toString();
                String lastName = dataSnapshot.child("Last Name").getValue().toString();
                String userName = firstName + " " + lastName;
                String profilePicPath = dataSnapshot.child("Profile Picture").getValue().toString();
                if(!profilePicPath.equals("null"))
                {
                    Log.d(TAG, "onDataChange: image is there.");
                    Uri imageUri = Uri.parse(profilePicPath);
                    Picasso.get().load(imageUri).into(profileImageIV);
                    //profileImageIV.setImageURI(imageUri);
                    userNameTV.setText(userName);
                }
                else
                {
                    Log.d(TAG, "onDataChange: there is no image.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
