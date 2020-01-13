package com.example.shareride;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shareride.Account_Fragment;
import com.example.shareride.Profile_Fragment;

public class PageAdapter extends FragmentPagerAdapter {

    public int itemCount;

    public PageAdapter(FragmentManager fm, int itemCount) {
        super(fm);
        this.itemCount = itemCount;
    }

    @Override
    public Fragment getItem(int position) {

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

    @Override
    public int getCount() {
        return itemCount;
    }
}
