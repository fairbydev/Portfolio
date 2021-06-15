package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_00;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_01;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_02;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_03;

/**
 * Created by jaehongpark on 2017. 6. 25..
 */

public class pagerAdapter extends FragmentStatePagerAdapter {

    public pagerAdapter(FragmentManager fm){
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MenuFragment_00();
            case 1:
                return new MenuFragment_01();
            case 2:
                return new MenuFragment_02();
            case 3:
                return new MenuFragment_03();
            default:
                return new MenuFragment_00();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }   //보여줄 프래그먼트의 총 숫자
}
