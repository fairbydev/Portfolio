package com.teamnova.jaepark.travelmate.activities;


import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatClient;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;
import com.teamnova.jaepark.travelmate.activities.functionalClass.pagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity{

    ChatClient client;
    ViewPager pager; //ViewPager 참조변수
    private static ImageView tabImg00,  tabImg01, tabImg02, tabImg03;
    ArrayList<ImageView> imgArr;
    LinearLayout menutabLL;
    //sharedPreference
    CallSharedPreference callSprf;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

       //하단 탭 버튼 객체 참조
        tabImg00 = (ImageView)findViewById(R.id.activity_menuImgView_home);
        tabImg01 = (ImageView)findViewById(R.id.activity_menuImgView_chat);
        tabImg02 = (ImageView)findViewById(R.id.activity_menuImgView_alram);
        tabImg03 = (ImageView)findViewById(R.id.activity_menuImgView_mypage);

        //하단 이미지 탭의 부모 레이아웃
        menutabLL = (LinearLayout)findViewById(R.id.activity_menu_tabLinear);

        //액티비티 생성시 홈 이미지 버튼 검은색 지정
        tabImg00.setImageResource(R.drawable.ic_home_black_24dp);

        //각 탭 이미지별 태그 세팅
        tabImg00.setTag("TabImg00");
        tabImg01.setTag("TabImg01");
        tabImg02.setTag("TabImg02");
        tabImg03.setTag("TabImg03");

        //이미지 온클릭 리스너 세팅
        for (int i = 0; i <4; i++){{
                menutabLL.findViewWithTag("TabImg0" + i).setOnClickListener(imgListener);
        }}


        //ViewPager 객체 참조
        pager = (ViewPager) findViewById(R.id.activity_menu_viewPager);


        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        pagerAdapter adapter = new pagerAdapter(getSupportFragmentManager());


        //ViewPager에 Adapter 설정
        pager.setAdapter(adapter);


        //ViewPager에게 Page의 변경을 인지하는 Listener 세팅.
        //마치 클릭상황을 인지하는 OnClickListener와 같은 역할.
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // 이 부분은 사용자가 손가락으로 Swipe를 하던지 아니면 setCurrentItem을 하던지 해서 페이지를 넘기는 과정에서 호출되는 부분
                // 한페이지 넘어갈때마다 한번 호출되는게 아니라 한페이지 넘어가는 순간에도 순간순간 호출이 됩니다
                // 아래 나올 onPageSelected와 다른점입니다. 드래그 할때마다 계속 호출

            }


            @Override
            public void onPageSelected(int position) {
                //페이지가 변경됬을때 한 번 호출

                //ViewPager는 3개의 View를 가지고 있도록 설계하였으므로.
                //Position도 역시 가장 왼쪽 처음부터(0,1,2 순으로 되어있음)
                //현재 전면에 놓여지는 ViewPager의 Page에 해당하는 Position으로
                //ActionBar의 Tab위치를 변경.
                switch (position){
                    case(0):{
                        tabImg00.setImageResource(R.drawable.ic_home_black_24dp);
                        tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                        tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                        tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                    }break;
                    case(1):{
                        tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                        tabImg01.setImageResource(R.drawable.ic_chat_black_24dp);
                        tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                        tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                    }break;
                    case(2):{
                        tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                        tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                        tabImg02.setImageResource(R.drawable.ic_add_alert_black_24dp);
                        tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                    }break;
                    case(3):{
                        tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                        tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                        tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                        tabImg03.setImageResource(R.drawable.ic_portrait_black_24dp);
                    }break;

                }

            }


            @Override
            public void onPageScrollStateChanged(int state) {   //드래그하려 클릭을 하고 움직이는 시점부터 시작, 스크롤을 하느냐 마느냐 '상태의 변화'에 반응하여 호출

            }

        });


        //인텐트 체크 = 개인프로필 사진 수정 후 개인정보 프래그먼트에 반영하기 위해서 여행리스트 프래그먼트로 이동한 다음 다시 개인정보 프래그먼트로 이동
        Intent intent = getIntent();
        String fragment = intent.getStringExtra("fragment");

        if(intent!=null && fragment != null && fragment.equals("MenuFragment_03")){

            pager.setCurrentItem(0);
            tabImg00.setImageResource(R.drawable.ic_home_black_24dp);
            tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
            tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
            tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);

            pager.setCurrentItem(3);
            tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
            tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
            tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
            tabImg03.setImageResource(R.drawable.ic_portrait_black_24dp);


        }


        // [onCreate 시각 + userID]를 고유 구분값으로 생성하여 사진 이름 앞에 부착
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();
//        Toast.makeText(MenuActivity.this, callSprf.getPreferences("memberInfo", "id"), Toast.LENGTH_SHORT).show();


        //Sender_idx(userSeq)를 수신하는 http통신
        String userID = callSprf.getPreferences("memberInfo", "id");
        String user_idx =  callSprf.getPreferences("memberInfo", "idx");

        if (user_idx == null || user_idx.equals("")){
            final String mTaskID = "getUserIdx";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
            final String mStrUrl = "getUserIdx.php";
            final int mDelay = 0;

            JSONObject userJO = new JSONObject();
            try {
                userJO.put("userId", userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TWDhttpATask getUserIndex = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                @Override
                protected void onPostExecute(JSONObject fromServerData) {
                    try {
                        JSONObject resultJO = new JSONObject(fromServerData.toString());
                        if (resultJO.get("result").toString().equals("Success")) {
                            //통신을 통해 가져온 정보를 전역변수에 담기
                            String user_newIdx = resultJO.get("userIdx").toString();
                            callSprf.savePreferences("memberInfo", "idx", user_newIdx);
                            Log.i("getMemberIndex success", user_newIdx + " = " + fromServerData.get("result").toString());

                            //클라이언트, 소켓연결, 메시지리스너 세팅
                            client = ChatClient.getInstance();
                            client.setInitial(getApplicationContext());
                            client.connect();   //이로써 해당 클라이언트 유저의 alive, receive, sync 스레드가 생성 및 실행
                        } else {
                            Log.e("getMemberIndex Fail", fromServerData.get("result").toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //http 통신실행을 시작하는 메소드
            getUserIndex.execute(userJO);
        }
        else
        {  //유저인덱스를 한번 받아와서 저장한 이후 http통신없이 바로 소켓 연결
            //클라이언트, 소켓연결, 메시지리스너 세팅
            client = ChatClient.getInstance();
            if (!client.isConnected())
            {
                client.setInitial(getApplicationContext());
                client.connect();   //이로써 해당 클라이언트 유저의 alive, receive, sync 스레드가 생성 및 실행
            }
        }


    }


    //액티비티 하단 아이콘 이미지뷰별 온클릭 리스너
    View.OnClickListener imgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case(R.id.activity_menuImgView_home):{
                    pager.setCurrentItem(0);
                    tabImg00.setImageResource(R.drawable.ic_home_black_24dp);
                    tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                    tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                    tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                }break;
                case(R.id.activity_menuImgView_chat):{
                    pager.setCurrentItem(1);
                    tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                    tabImg01.setImageResource(R.drawable.ic_chat_black_24dp);
                    tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                    tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                }break;
                case(R.id.activity_menuImgView_alram):{
                    pager.setCurrentItem(2);
                    tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                    tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                    tabImg02.setImageResource(R.drawable.ic_add_alert_black_24dp);
                    tabImg03.setImageResource(R.drawable.ic_portrait_white_24dp);
                }break;
                case(R.id.activity_menuImgView_mypage):{
                    pager.setCurrentItem(3);
                    tabImg00.setImageResource(R.drawable.ic_home_white_24dp);
                    tabImg01.setImageResource(R.drawable.ic_chat_white_24dp);
                    tabImg02.setImageResource(R.drawable.ic_add_alert_white_24dp);
                    tabImg03.setImageResource(R.drawable.ic_portrait_black_24dp);
                }break;


            }

        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                android.app.AlertDialog.Builder ExitDialog = new android.app.AlertDialog.Builder(MenuActivity.this);

                ExitDialog.setTitle("확인을 누르시면 앱 종료됩니다.");          // 제목
                ExitDialog.setMessage("앱을 종료하시겠습니까?");  // 메세지
                ExitDialog.setCancelable(false);                    // 취소가능 설정

                ExitDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override   // 확인 버튼 클릭할 경우
                    public void onClick(DialogInterface dialog, int which) {

                        moveTaskToBack(true);

                        finish();

                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                });

                ExitDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override   // 취소 버튼 클릭할 경우
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                android.app.AlertDialog exitConfirm = ExitDialog.create();
                exitConfirm.show();


                break;
        }
        return true;
    }



}
