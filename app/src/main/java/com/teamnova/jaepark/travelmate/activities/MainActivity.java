package com.teamnova.jaepark.travelmate.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.romainpiel.shimmer.Shimmer;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatClient;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;

public class MainActivity extends AppCompatActivity {

    private CallSharedPreference callSprf;
    AccessToken currentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        com.romainpiel.shimmer.ShimmerTextView label = (com.romainpiel.shimmer.ShimmerTextView)findViewById(R.id.activity_main_label);
        com.romainpiel.shimmer.ShimmerTextView nova = (com.romainpiel.shimmer.ShimmerTextView)findViewById(R.id.activity_main_TeamNovaTV);

        Shimmer ab= new Shimmer();
        ab.start(label);
        ab.start(nova);

        Log.e("workingCheck", "is working");
        //바로 넘어가기
//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();

        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();

        final String savedToken = callSprf.getPreferences("memberInfo", "token");
        currentToken = AccessToken.getCurrentAccessToken();




        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if (currentToken == null || savedToken == null) {    //페이스북 액세스 토큰이 없을 경우 비페이스북 사용 유저의 자동로그인 설정 체크
                    Log.e("facebookAccessToken", "is null");

                    String autoLogin = callSprf.getPreferences("configuration", "autoLogin");

                    if(!autoLogin.equals("true")){   //자동로그인 미설정시 로그인 액티비티에서 아이디/비번 입력
                        Log.e("autoLogin", "status : unset");
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }else if(autoLogin.equals("true")){ //이전에 자동로그인 체크 후 비페이스북 회원이 로그인한 경우
                        Log.e("autoLogin", "true");
                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        finish();
                    }

                }else{    //페이스북 액세스 토큰이 있는 경우 메뉴로 이동
                    Log.e("currentToken", currentToken.toString());
                    Log.e("savedToken", savedToken);

                    Log.e("facebookAccessToken", "is Not null : current token - " + savedToken);
                    Log.e("facebookAccessToken", "is Not null : saved token - " + savedToken);
                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                    finish();
                }

            }
        };

        handler.sendEmptyMessageDelayed(0, 300);


    }
}
