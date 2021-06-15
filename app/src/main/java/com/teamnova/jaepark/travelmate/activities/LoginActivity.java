package com.teamnova.jaepark.travelmate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    String idInput, pwInput;
    EditText idEditText, pwEditText;
    private CallSharedPreference callSprf;
    private CheckBox autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_login);

        //SharedPreference setting
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();

        //editTEXT
         idEditText = (EditText)findViewById(R.id.activity_login_idEditText);
         pwEditText = (EditText)findViewById(R.id.activity_login_pwEditText);

        //자동로그인 체크박스
        autoLogin = (CheckBox)findViewById(R.id.activity_login_checkBox);

        //일반 로그인 버튼
        Button loginBtn  = (Button)findViewById(R.id.activity_login_loginBtn);
        loginBtn.setOnClickListener(loginOnClickListener);

        //회원가입 버튼
        Button registerBtn = (Button)findViewById(R.id.activity_login_registerBtn);
        registerBtn.setOnClickListener(loginOnClickListener);

        //페이스북 콜백 매니저
        callbackManager = CallbackManager.Factory.create();


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {    //페이스북 로그인 성공시 수행할 작업들
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject fbobject, GraphResponse response) {
                        Log.e("resultFromFb",fbobject.toString());
                        Log.e("Token after login : ", loginResult.getAccessToken().getToken());

                        TWDhttpATask fbLogin  = new TWDhttpATask("facebookLoin", "fbLogin.php", 0 ) {
                            @Override
                            protected void onPostExecute(JSONObject fromServerData) {
                                try {
                                    JSONObject resultJo = new JSONObject(fromServerData.toString());
                                    Log.e("resultJsonObjectString",fromServerData.toString());

                                    String fbLoginResult = resultJo.getString("result");

                                    switch (fbLoginResult){
                                        case("registerSuccess"):{
                                            String userNick = resultJo.get("nickname").toString();
                                            Toast.makeText(LoginActivity.this, userNick + "님 환영합니다!", Toast.LENGTH_SHORT).show();


                                            callSprf.savePreferences("memberInfo", "id", fbobject.get("id").toString());
                                            callSprf.savePreferences("memberInfo", "token", loginResult.getAccessToken().getToken());

                                            startActivity(new Intent(LoginActivity.this, com.teamnova.jaepark.travelmate.activities.MenuActivity.class));
                                            finish();   //finish는 생명주기 중 onPause 와 onStop을 건너뛰고 바로 onDestroy로 가서 실행하는 것
//                                            String userBirth = resultJo.get("birth").toString();
//                                            Toast.makeText(LoginActivity.this, userBirth, Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                        case("registerError"):{
                                            Toast.makeText(LoginActivity.this, "등록문제가 생겨 페이스북 로그인을 할 수 없습니다", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                        case("memberLogin"):{
                                            String userNick = resultJo.get("nickname").toString();
                                            Toast.makeText(LoginActivity.this, userNick + "님 반갑습니다!", Toast.LENGTH_SHORT).show();

                                            //AccessToken ac =  AccessToken.getCurrentAccessToken();
                                            String at = loginResult.getAccessToken().getToken();
                                            Log.e("accessToken - ", at);

                                            callSprf = new CallSharedPreference();
                                            callSprf.mContext = getApplicationContext();
                                            callSprf.savePreferences("memberInfo", "id", fbobject.get("id").toString());
                                            callSprf.savePreferences("memberInfo", "token", loginResult.getAccessToken().getToken());

                                            startActivity(new Intent(LoginActivity.this, com.teamnova.jaepark.travelmate.activities.MenuActivity.class));
                                            finish();
                                        }
                                        break;
                                    };



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                         fbLogin.execute(fbobject);

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,age_range");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
            }
        });




    }


    View.OnClickListener loginOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //회원가입 버튼 클릭
                case(R.id.activity_login_registerBtn):{
                    Intent intent = new Intent(LoginActivity.this, com.teamnova.jaepark.travelmate.activities.Signin.TermsActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

                //로그인 버튼 클릭
                case (R.id.activity_login_loginBtn):{
                    //Toast.makeText(LoginActivity.this, "로그인 클릭", Toast.LENGTH_SHORT).show();
                    idInput = idEditText.getText().toString();
                    pwInput = pwEditText.getText().toString();
                    Log.e("id, pw input : ", idInput + " / " + pwInput);


                    final JSONObject loginInfo = new JSONObject();  //기입한 아이디와 비번을 서버에 보내기 위해 JSONObject에 넣음
                    try {
                        loginInfo.put("id", idInput);
                        loginInfo.put("password", pwInput);
                        Log.e("id, pw input : ", idInput + " / " + pwInput);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TWDhttpATask loginTWD = new TWDhttpATask("normalLogin", "login.php", 0) {
                        @Override
                        protected void onPostExecute(JSONObject fromServerData) {
                            try {
                                JSONObject JoFromServer = new JSONObject(fromServerData.toString());
                                Log.e("resultJsonObjectString",JoFromServer.toString());

                                switch (JoFromServer.getString("result")){
                                    case ("noId"):{
                                        Toast.makeText(LoginActivity.this, "회원이 존재하지 않거나 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                                    }break;
                                    case ("loginSuccess"):{
                                        String id = JoFromServer.getString("id");
                                        String nickname = JoFromServer.getString("nickname");
                                        Log.e("check", id + "/" + nickname);

                                        //로그인 성공시 sharedPreference에 id 와 nickname 저장
                                        callSprf.savePreferences("memberInfo", "id", id);
                                        callSprf.savePreferences("memberInfo", "nickname", nickname);
//                                        Toast.makeText(LoginActivity.this, callSprf.getPreferences("memberInfo", "id"), Toast.LENGTH_SHORT).show();


                                        //자동로그인 체크박스 체크 여부를 확인하여 유저가 체크한 경우 sharedPreference에 autoLogin여부 저장
                                        if(autoLogin.isChecked() == true){
                                            callSprf.savePreferences("configuration", "autoLogin", "true");
                                        }


                                        Toast.makeText(LoginActivity.this, nickname + "님 환영합니다", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(LoginActivity.this, com.teamnova.jaepark.travelmate.activities.MenuActivity.class));
                                        finish();

                                    }break;
                                    case ("loginFail"):{
                                        Toast.makeText(LoginActivity.this, "회원이 존재하지 않거나 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                                    }break;
                                    case ("dbError"):{
                                        Toast.makeText(LoginActivity.this, "죄송합니다. 시스템 문제로 로그인 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    loginTWD.execute(loginInfo);    //로그인 체크 실시
                }
                break;
                //

            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //페북 로그인 관련 메소드
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                android.app.AlertDialog.Builder ExitDialog = new android.app.AlertDialog.Builder(LoginActivity.this);

                ExitDialog.setTitle("확인을 누르시면 앱 종료됩니다.");          // 제목
                ExitDialog.setMessage("앱을 종료하시겠습니까?");  // 메세지
                ExitDialog.setCancelable(false);                    // 취소가능 설정

                ExitDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override   // 확인 버튼 클릭할 경우
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(LoginActivity.this, "Have a Wonderful Day!", Toast.LENGTH_LONG).show();

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
