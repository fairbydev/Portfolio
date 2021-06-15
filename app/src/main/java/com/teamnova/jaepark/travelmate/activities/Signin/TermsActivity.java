package com.teamnova.jaepark.travelmate.activities.Signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.teamnova.jaepark.travelmate.R;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //약관 웹뷰
        WebView webView = (WebView)findViewById(R.id.activity_terms_webView);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl("http://poyapo123.cafe24.com/TMphp/terms.php");
            // 캐쉬 끄기
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //약관동의 체크박스
        final CheckBox agreementChceck = (CheckBox)findViewById(R.id.activity_terms_checkBox);
        //확인버튼
        Button confirmBtn = (Button)findViewById(R.id.activity_terms_registerBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(agreementChceck.isChecked() == true) {    // 회원가입 약관 동의 시 가입정보 입력 액티비티로 이동

                    Intent intent = new Intent(TermsActivity.this, com.teamnova.jaepark.travelmate.activities.Signin.registerActivity.class);
                    startActivity(intent);
                    finish();

                }else{  //약관 동의요청 다이얼로그 띄우기
                    Toast.makeText(getApplicationContext(), "회원약관을 읽고 동의하여 주십시오", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:


                Intent intent = new Intent(TermsActivity.this, com.teamnova.jaepark.travelmate.activities.LoginActivity.class);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }
}
