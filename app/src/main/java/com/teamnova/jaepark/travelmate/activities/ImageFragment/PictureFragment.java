package com.teamnova.jaepark.travelmate.activities.ImageFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.EditInfoActivity;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PictureFragment extends Fragment {

    ImageView pictureImageView;

    String mPictureAddr;

    public PictureFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_frag_picture, container, false);

        pictureImageView = (ImageView) view.findViewById(R.id.fragmentPicture_ImageView_travelPicture);

        Glide.with(PictureFragment.this)
                .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + mPictureAddr)
                .thumbnail(0.1f)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .into(pictureImageView);

        //Toast.makeText(getContext(), ""+mPictureAddr, Toast.LENGTH_SHORT).show();



        return view;
    }


    public void setPictureAddr(String picAddr){
        mPictureAddr = picAddr;
    }
}
