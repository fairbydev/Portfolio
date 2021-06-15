package com.teamnova.jaepark.travelmate.activities.Signin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity{

    private Button btnDatePicker, signUpBtn;
    private EditText txtDate;
    private String id,password, passwordConfirm, nickname, birth;
    private int mYear, mMonth, mDay;
    private int sYear, sMonth, sDay;
    private int birthday;
    private String genderSelect = "기탸";
    private EditText emailEditText, passwordEditText, passwordConfirmEditText, nicknameEditText;
    private Spinner genderSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //이매일 에딧텍스트
        emailEditText = (EditText)findViewById(R.id.activity_register_idEditText);
        //비밀번호 입력 에딧텍스트
        passwordEditText = (EditText)findViewById(R.id.activity_register_passwordEditText);
        //비밀번호 확인 에딧텍스트
        passwordConfirmEditText = (EditText)findViewById(R.id.activity_register_passwordConfirmEditText);
        //닉네임 에딧텍스트
        nicknameEditText = (EditText)findViewById(R.id.activity_register_nicknameEditText);

        //성별선택 스피너 생성
        genderSpinner = (Spinner)findViewById(R.id.activity_register_genderSpinnrer);

        //성별 스피너의 어댑터 생성
        ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(genderAdapter);   //어뎁터 세팅

        AdapterView.OnItemSelectedListener categoryListener = new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                genderSelect = genderSpinner.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(), "selected category = " + categorySelect, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        genderSpinner.setOnItemSelectedListener(categoryListener);
        genderSpinner.setSelection(0);    //초기값 설정



        //데이트 피커
        btnDatePicker = (Button) findViewById(R.id.activity_register_dateBtn);
        txtDate = (EditText) findViewById(R.id.activity_register_dateEditText);


        //가입하기 버튼
        signUpBtn = (Button)findViewById(R.id.activity_register_submitBtn);


        //이메일 주의 텍스트뷰 밑줄 처리
        TextView emailCaution = (TextView)findViewById(R.id.activity_register_cautionTv);
        SpannableString content = new SpannableString("성별과 생일은 향후 메이트 검색을 위해 사용됩니다");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        emailCaution.setText(content);

        //데이트 피커 온클릭 함수
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(registerActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(year + "년" + (monthOfYear + 1)  + "월" + dayOfMonth + "일");

                                sYear = year;
                                sMonth = monthOfYear + 1;
                                sDay = dayOfMonth;

                                if((sMonth < 10)&&(sDay < 10)){
                                    birthday = Integer.parseInt(String.valueOf(sYear) + "0" + String.valueOf(sMonth) + "0" + String.valueOf(sDay));
                                    Log.e("date - ", "birthday case1 :" + birthday);
                                }else if((sMonth < 10)&&(sDay > 10)){
                                    birthday = Integer.parseInt(String.valueOf(sYear) + "0" + String.valueOf(sMonth) + String.valueOf(sDay));
                                    Log.e("date - ", "birthday case2 :" + birthday);
                                }else{
                                    birthday = Integer.parseInt(String.valueOf(sYear) + String.valueOf(sMonth) + String.valueOf(sDay));
                                    Log.e("date - ", "birthday case3 :" + birthday);
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력정보 변수 담기(가입하기 버튼을 누르는 시점에 변수에 값 넣기)

                id = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                nickname = nicknameEditText.getText().toString();
                passwordConfirm = passwordConfirmEditText.getText().toString();
                //gender는 genderSelect 로 받음
                birth = String.valueOf(birthday);

                boolean idOk = true;
                boolean passwordOk = true;
                boolean passwordConfirmOk = true;
                boolean nicknameOk = true;
                boolean genderOk = true;
                boolean birthOk = true;

                // 예외처리

                //1. id 예외처리

                if (id.length() == 0){
                    Toast.makeText(registerActivity.this, "아이디를 입력해 주십시오", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                    idOk = false;
                }else if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()){
                    Toast.makeText(registerActivity.this, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                    idOk = false;
                }


                //2. password 예외처리
                final String PASSWORDPATTERN = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$";

                if (password.length() < 8 || password.length() > 21){
                    Toast.makeText(registerActivity.this, "비밀번호는 8자 이상 20자 이내로 만들어주세요", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                    passwordOk = false;
                }else if(!Pattern.matches(PASSWORDPATTERN, passwordEditText.getText().toString())){
                    Toast.makeText(registerActivity.this, "비밀번호는 영문/숫자/특수문자를 혼합하여 만들어주세요.", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                    passwordOk = false;
                }

                //3. password confirm 예외처리

                if (passwordConfirm.length() == 0){
                    Toast.makeText(registerActivity.this, "비밀번호를 다시 한번 입력하세요", Toast.LENGTH_SHORT).show();
                    passwordConfirmEditText.requestFocus();
                    passwordConfirmOk = false;
                }else if(!password.equals(passwordConfirm)){
                    Toast.makeText(registerActivity.this, "위의 비밀번호와 동일하게 입력하세요", Toast.LENGTH_SHORT).show();
                    passwordConfirmEditText.requestFocus();
                    passwordConfirmOk = false;
                }


                //4. 성별 예외처리

                if(genderSelect.equals("선택")){
                    Toast.makeText(registerActivity.this, "성별을 입력해주세요", Toast.LENGTH_SHORT).show();
                    genderOk = false;
                }else if (genderSelect.equals("남")){
                    genderSelect = "male";
                }else if (genderSelect.equals("여")){
                    genderSelect = "female";
                }else if (genderSelect.equals("기타")){
                    genderSelect = "unknown";
                }



                //5. nickname 예외처리

                if (nickname.length() == 0){
                    Toast.makeText(registerActivity.this, "닉네임을 입력해 주십시오", Toast.LENGTH_SHORT).show();
                    nicknameEditText.requestFocus();
                    nicknameOk = false;
                }else if(nickname.length() > 15){
                    Toast.makeText(registerActivity.this, "닉네임은 15자 이내로 입력해 주십시오", Toast.LENGTH_SHORT).show();
                    nicknameEditText.requestFocus();
                    nicknameOk = false;
                }


                //6. 생일 예외처리
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String todate =  formatter.format(new Date());
                int gregorianToday = Integer.parseInt(todate);

                if(birthday >= gregorianToday){     // 선택한 생일이 오늘이거나 미래일 경우
                    Toast.makeText(registerActivity.this, "올바른 생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
                    birthOk = false;
                }else if(String.valueOf(birthday).isEmpty()){   //생일입력을 안한 경우
                    Toast.makeText(registerActivity.this, "생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
                    birthOk = false;
                }else if(String.valueOf(birthday).length()<8){  // 데이터피커에 오류가 있는 경우
                    Toast.makeText(registerActivity.this, "생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
                    birthOk = false;
                }



                if(idOk && passwordOk && passwordConfirmOk && nicknameOk && genderOk && birthOk){

                    final JSONObject registerInfo = new JSONObject();
                    try {
                        registerInfo.put("id", id);
                        registerInfo.put("password", password);
                        registerInfo.put("nickname", nickname);
                        registerInfo.put("gender", genderSelect);
                        registerInfo.put("birth", birth);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(getApplicationContext(), "id = " + id, Toast.LENGTH_LONG).show();

                    // http 통신연결
                    final String mTaskID = "signUp";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                    final String mStrUrl = "signUp.php";
                    final int mDelay = 0;

                    final TWDhttpATask signUpATask = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                        @Override
                        protected void onPostExecute(JSONObject fromServerData) {
                            try {
                                //Log.e("signUpResult", fromServerData.getString(0));
                                JSONObject resultJo = fromServerData;

                                Log.e("signUpResult", resultJo.getString("task") + " : " + resultJo.getString("result"));

                                if(resultJo.getString("result").equals("success")){
                                    Toast.makeText(getApplicationContext(), "회원가입이 성공적으로 이루어졌습니다", Toast.LENGTH_LONG).show();

                                    CallSharedPreference cspf = new CallSharedPreference();
                                    cspf.mContext = getApplicationContext();
                                    cspf.savePreferences("memberInfo", "id", id);



                                    startActivity(new Intent(registerActivity.this, MenuActivity.class));
                                    finish();
                                }else if(resultJo.getString("result").equals("error without duplication")){
                                    Toast.makeText(getApplicationContext(), "문제가 생겨 회원가입을 하지 못하였습니다", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "이미 사용하고 있는 아이디 입니다. 다시 작성해주세요", Toast.LENGTH_LONG).show();
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    signUpATask.execute(registerInfo);



                }




            }   //onClick 마침
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:


                Intent intent = new Intent(registerActivity.this, com.teamnova.jaepark.travelmate.activities.Signin.TermsActivity.class);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }





}
