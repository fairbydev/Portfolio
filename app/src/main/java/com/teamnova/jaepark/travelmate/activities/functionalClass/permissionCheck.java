package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.Manifest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class permissionCheck {

    private static final int MY_PERMISSION_CAMERA = 101;  //나중에 체크해야 함

    Context mContext;


    public permissionCheck() {
        Log.e("permissionCheck", "Constructor CALL");
    }

    public permissionCheck(Activity activity){
        Log.e("permissionCheck", "Context CALL");
        this.mContext = activity;
    }

    //나중에는 TEDPERMISSION 라이브러리를 쓰자(권한확인 및 요청 라이브러리)
    public boolean isCheck(String permission){
        switch (permission) {
            case ("camera") : {
                Log.e("Camera permission", "CALL");
                //checkSelfPermission 이미 해당권한을 가지고 있는지 체크하는 메소드
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("알림")
                                .setMessage("저장소 권한이 거부되었습니다 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다")
                                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //((Activity) mContext).finish();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    } else {
                        ActivityCompat.requestPermissions((Activity)mContext, new String []{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
                    }
                } else {
                    return true;
                }
            }
        };

        return false;
    }


}
