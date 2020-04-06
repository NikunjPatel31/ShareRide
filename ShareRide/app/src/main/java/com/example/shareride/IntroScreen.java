package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class IntroScreen extends AppCompatActivity {

    public static ViewPager viewPager;
    private IntroViewPagerAdapter introViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        viewPager = findViewById(R.id.view_pager);

        introViewPagerAdapter = new IntroViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(introViewPagerAdapter);
    }
}
