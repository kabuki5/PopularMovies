package com.benavides.ramon.popularmovies.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Adapter for viewpager container detail fragment
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private android.support.v4.app.Fragment[] data;

    public SectionsPagerAdapter(FragmentManager fm, android.support.v4.app.Fragment[] fragments) {
        super(fm);
        data = fragments;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return data[position];
    }

    @Override
    public int getCount() {
        // Show total pages.
        return data.length;
    }

    public void setData(Fragment[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mData[position].getTitle();
//        }
}