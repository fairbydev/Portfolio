package com.teamnova.jaepark.travelmate.activities.functionalClass;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;


public class CallSharedPreference {

    public    Context mContext;
    SharedPreferences pref;

    //컨텍스트 에러 방지를 위해서 functionalClass 패키지로 이동하지 않았음

    // 값 불러오기
    public String getPreferences(String SprfName, String KeyName){

        pref = mContext.getSharedPreferences(SprfName, MODE_PRIVATE);
        String value = pref.getString(KeyName, ""); //해당 키값에 대응하는 값이 없을 경우 ""을 반납
        Log.e("getPreferences", KeyName + " : " + value);

        return value;
    }

    public String getPreferencesForSearch(String SprfName, String KeyName){

        pref = mContext.getSharedPreferences(SprfName, MODE_PRIVATE);
        String value = pref.getString(KeyName, "notSet");
        Log.e("getPreferences", KeyName + " : " + value);

        return value;
    }


    // 값 저장하기
    public void savePreferences(String SprfName, String KeyName, String value){
        pref = mContext.getSharedPreferences(SprfName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KeyName, value);
        editor.commit();
        Log.e("savePreferences", KeyName + " : " + value);
    }

    // 값(Key Data) 삭제하기
    public void removePreferences(String SprfName, String KeyName){
        pref = mContext.getSharedPreferences(SprfName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KeyName);
        editor.commit();
        Log.e("removePreferences", KeyName);

    }

    // 전체 값(ALL Data) 삭제하기
    public void removeAllPreferences(String SprfName){
        pref = mContext.getSharedPreferences(SprfName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Log.e("removeAllPreferences", " Completed");

    }


}
