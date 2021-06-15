package com.teamnova.jaepark.travelmate.activities.MenuFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.EditInfoActivity;
import com.teamnova.jaepark.travelmate.activities.LoginActivity;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MenuFragment_03 extends Fragment {


    //SharedPreference
    private CallSharedPreference callSprf;


    //프로필 이미지뷰
    ImageView profileImgView;

    //닉네임 텍스트뷰
    TextView nicknameTV;

    //나이,성별 텍스트뷰
    TextView otherInfoTV;

    //정보수정 버튼
    Button editInfo;

    //로그아웃 버튼
    Button mLogoutBtn;

    //내가 등록한 여행 리스트뷰
    ListView myListView;

    String userId = "";

    //개인정보 http 요청 코드
    private JSONObject requestCode;


    //http통신으로  유저정보를 가져와 담을 스트링 변수
    String nickname, birth, age, gender, profilePic = "";



    public MenuFragment_03(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment가 화면에 보여질 때 호출됨

        View view = inflater.inflate(R.layout.layout_frag_3, container, false);

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getContext();

        //프로필 이미지뷰
        profileImgView = (ImageView) view.findViewById(R.id.fragment_menu_mypage_profilePic_ImgView);

        //닉네임 텍스트뷰
        nicknameTV = (TextView) view.findViewById(R.id.fragment_menu_mypage_nicknameTV);

        //나이,성별 텍스트뷰
        otherInfoTV = (TextView) view.findViewById(R.id.fragment_menu_mypage_otherInfoTV);

        //정보수정 버튼
        editInfo = (Button) view.findViewById(R.id.fragment_menu_mypage_infoEditBtn);


        //로그아웃 버튼
        mLogoutBtn = (Button) view.findViewById(R.id.fragment_menu_mypage_logOutBtn);


        //내가 등록한 여행 리스트뷰
//        myListView = (ListView) view.findViewById(R.id.fragment_menu_mypage_myListView);



        //http 통신을 통한 개인정보 수신

        userId = callSprf.getPreferences("memberInfo", "id");

        //요청코드 작성
        requestCode = new JSONObject();

        try {
            requestCode.put("requestCode", "getUserInfo");
            requestCode.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //http통신에 필요한 인자 정의
        final String mTaskID = "getUserInfo";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
        final String mStrUrl = "getUserInfo.php";
        final int mDelay = 0;

        TWDhttpATask getUserInfo = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
            @Override
            protected void onPostExecute(JSONObject fromServerData) {
                try {
                    JSONObject ja = new JSONObject(fromServerData.toString());

                    if (ja.get("result").toString().equals("Success")){
                        //통신을 통해 가져온 정보를 전역변수에 담기

                        nickname = ja.get("nickname").toString();
                        birth = ja.get("birth").toString();
                        gender = ja.get("gender").toString();
                        profilePic = ja.get("profilePic").toString();

                        Log.i("nickname", nickname);
                        Log.i("birth", birth);
                        Log.i("gender", gender);
                        Log.i("profilePic", profilePic);


                        callSprf.savePreferences("memberInfo", "nickname", nickname);
                        callSprf.savePreferences("memberInfo", "birth", birth);
                        callSprf.savePreferences("memberInfo", "gender", gender);
                        callSprf.savePreferences("memberInfo", "profilePic", profilePic);

                        //받아온 정보를 뷰에 뿌려주는 메소드
                        settingView();

                    }else{
                        Log.e("getMemberInfo Fail", fromServerData.get("result").toString());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //통신실행을 시작하는 메소드
        getUserInfo.execute(requestCode);



        //정보수정 버튼 클릭이벤트
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((MenuActivity)getActivity(), EditInfoActivity.class));
                MenuFragment_03.this.onDestroyView();
            }
        });


        //로그아웃 버튼 클릭이벤트
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSprf.removePreferences("memberInfo", "id");
                if (callSprf.getPreferences("memberInfo", "nickname") != null){
                    callSprf.removePreferences("memberInfo", "nickname");
                }
                if (callSprf.getPreferences("memberInfo", "token") != null){
                    callSprf.removePreferences("memberInfo", "token");
                }

                if (LoginManager.getInstance() != null){
                    LoginManager.getInstance().logOut();
                }


                startActivity(new Intent((MenuActivity)getActivity(), LoginActivity.class));
                MenuFragment_03.this.onDestroyView();
            }
        });


        return view;

    }





    private void settingView() {

        //가져온 정보를 뷰에 반영하기

        //닉네임 설정
        nicknameTV.setText(nickname);

        //프로필 사진 받기

        if (profilePic.equals("noPicture")){    //기본사진 등록이 안되어 있을 경우
            Glide.with(getContext())
                    .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_basicPic.jpg")
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .into(profileImgView);
        }else{
            Glide.with(getContext())
                    .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + profilePic)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .into(profileImgView);
        }


        //날짜로 나이 계산하여 설정하기

        int birthYear = Integer.parseInt(birth.substring(0,4));
        int birthDate = Integer.parseInt(birth.substring(4,8));


        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String str_date = df.format(new Date());

        int currentYear = Integer.parseInt(str_date.substring(0,4));
        int currentDate = Integer.parseInt(str_date.substring(4,8));


        age = String.valueOf(currentYear - birthYear - 1);
        if (birthDate <= currentDate){
            age  = String.valueOf(currentYear - birthYear);
        }


        //성별(가입 액티비티 참조)

        if (gender.equals("male")){
            gender = "남성";
        }else if (gender.equals("female")){
            gender = "여성";
        }else if (gender.equals("unknown")){
            gender = "기타성별";
        }



        otherInfoTV.setText(age + ", " + gender);


        Log.e("birthYear", birthYear+"");
        Log.e("currentYear", currentYear+"");
        Log.e("birthDate", birthDate+"");
        Log.e("currentDate", currentDate+"");



    }


    @Override
    public void onResume() {
        super.onResume();





    }


}
