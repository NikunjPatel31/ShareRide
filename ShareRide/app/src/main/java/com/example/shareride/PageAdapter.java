package com.example.shareride;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shareride.Account_Fragment;
import com.example.shareride.Profile_Fragment;

public class PageAdapter extends FragmentPagerAdapter {

    public int itemCount;
    public String Activity;

    public PageAdapter(FragmentManager fm, int itemCount, String Activity) {
        super(fm);
        this.itemCount = itemCount;
        this.Activity = Activity;
    }

    @Override
    public Fragment getItem(int position) {

        if(Activity.equals("Account"))
        {
            switch (position)
            {
                case 0:
                    return new Profile_Fragment();
                case 1:
                    return new Account_Fragment();
                default:
                    return null;
            }
        }
        else if(Activity.equals("Notification"))
        {
            switch (position)
            {
                case 0:
                    return new Notification_Rider_Fragment();
                case 1:
                    return new Notification_Passenger_Fragment();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return itemCount;
    }
}
