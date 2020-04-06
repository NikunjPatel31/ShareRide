package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabItem riderTI, passengerTI;
    private CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initializeWidgets();
        setViewPagerAdapter();
        tablayoutOnTabSeleteListener();
        if (!CommanClass.isNetworkAvailable(this))
        {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "No internet is available", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing Widgets.");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        riderTI = (TabItem) findViewById(R.id.rider_tabItem);
        passengerTI = (TabItem) findViewById(R.id.passenger_tabItem);
        rootLayout = findViewById(R.id.root_layout);
    }
    private void setViewPagerAdapter()
    {
        Log.d(TAG, "setViewPagerAdapter: setting viewpager adapter.");
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),"Notification");
        viewPager.setAdapter(pageAdapter);
    }
    private void tablayoutOnTabSeleteListener()
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
}
