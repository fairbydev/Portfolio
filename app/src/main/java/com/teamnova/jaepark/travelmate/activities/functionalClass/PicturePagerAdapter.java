package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.teamnova.jaepark.travelmate.activities.ImageFragment.PictureFragment;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_00;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_01;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_02;
import com.teamnova.jaepark.travelmate.activities.MenuFragment.MenuFragment_03;

import java.util.ArrayList;

public class PicturePagerAdapter extends FragmentStatePagerAdapter {

    int mCount;
    ArrayList mPicAddrArray;


    public PicturePagerAdapter(FragmentManager fm){
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        Log.e("PicturePagerAdater", "getItem : Call");

        switch (position){
            case 0:
                Log.e("PictureAdapterCase(0) : ", mPicAddrArray.get(0).toString());
                PictureFragment picFrag00 = new PictureFragment();
                picFrag00.setPictureAddr(mPicAddrArray.get(0).toString());
                return picFrag00;
            case 1:
                Log.e("PictureAdapterCase(1) : ", mPicAddrArray.get(1).toString());
                PictureFragment picFrag01 = new PictureFragment();
                picFrag01.setPictureAddr(mPicAddrArray.get(1).toString());
                return picFrag01;
            case 2:
                Log.e("PictureAdapterCase(2) : ", mPicAddrArray.get(2).toString());
                PictureFragment picFrag02 = new PictureFragment();
                picFrag02.setPictureAddr(mPicAddrArray.get(2).toString());
                return picFrag02;
            case 3:
                Log.e("PictureAdapterCase(3) : ", mPicAddrArray.get(3).toString());
                PictureFragment picFrag03 = new PictureFragment();
                picFrag03.setPictureAddr(mPicAddrArray.get(3).toString());
                return picFrag03;
            default:
                Log.e("PictureAdapterCase(defualt) : ", mPicAddrArray.get(0).toString());
                PictureFragment picFrag04 = new PictureFragment();
                picFrag04.setPictureAddr(mPicAddrArray.get(0).toString());
                return picFrag04;
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }   //보여줄 프래그먼트의 총 숫자

    public void setPictureInfo(int count, ArrayList picAddrArray){   //생성할 사진 페이지의 숫자를 세팅
        Log.e("PicturePagerAdapter", "setPictureInfo : Call");
        mCount = count;
        mPicAddrArray = picAddrArray;
        Log.e("PictureAdapterMcount ", mCount + "");
        Log.e("PictureAdaptermPicAddrArray ", mPicAddrArray.get(0).toString());
    }
}
