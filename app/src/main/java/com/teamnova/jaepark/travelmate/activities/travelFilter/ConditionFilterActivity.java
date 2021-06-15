package com.teamnova.jaepark.travelmate.activities.travelFilter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

public class ConditionFilterActivity extends AppCompatActivity implements RangeBar.OnRangeBarChangeListener {

    CallSharedPreference sprf;

    EditText travelCostEditText, travelPlaceEditText;
    TextView initTextView, travelAgeTextView;
    Button travelGenderBtn1, travelGenderBtn2, travelGenderBtn3, travelCreateBtn;
    RangeBar ageBar;
    Spinner maxPersonSpinner;


    String maxPersonSelect; //여행인원

    int fromAge, toAge = -1; //참여연령 관련 인트

    //sharedPreference에 저장할 스트링
    String placeFilter, maxPersonFilter, costFilter, genderFilter,  fromAgeFilter, toAgeFilter;

    //여행경비 예외처리 여부
    boolean costInputType = true;

    // 개별 조건 체크 여부
    boolean isPlaceChecked, isGenderChecked, isMaxPersonChecked, isCostChecked, isAgeChecked = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_filter);

        sprf = new CallSharedPreference();
        sprf.mContext = getApplicationContext();

        //필터 초기화 텍스트뷰
        initTextView = (TextView)findViewById(R.id.activity_conditionFilter_init_TextView);
        initTextView.setOnClickListener(conditionFilterOnclickListener);

        //여행할 도시
        travelPlaceEditText = (EditText)findViewById(R.id.activity_conditionFilter_place_EditText);

        //여행인원 스피너
        maxPersonSpinner = (Spinner)findViewById(R.id.activity_conditionFilter_maxPerson_spinner);

        //스피너의 어댑터 생성
        ArrayAdapter maxPersonAdapter = ArrayAdapter.createFromResource(this,
                R.array.maxPersonForFilter, R.layout.spinner_item);
        maxPersonAdapter.setDropDownViewResource(R.layout.spinner_item);
        maxPersonSpinner.setAdapter(maxPersonAdapter);   //어뎁터 세팅

        AdapterView.OnItemSelectedListener categoryListener = new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                maxPersonSelect = maxPersonSpinner.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(), "selected category = " + categorySelect, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        maxPersonSpinner.setOnItemSelectedListener(categoryListener);
        maxPersonSpinner.setSelection(0);    //초기값 설정


        //여행경비 에딧텍스트
        travelCostEditText = (EditText)findViewById(R.id.activity_conditionFilter_travelCost_Edittext);

        //동행 성별 선택 버튼 1,2,3(여, 남, 무관)
        travelGenderBtn1 = (Button)findViewById(R.id.activity_conditionFilter_gender01_btn);
        travelGenderBtn2 = (Button)findViewById(R.id.activity_conditionFilter_gender02_btn);
        travelGenderBtn3 = (Button)findViewById(R.id.activity_conditionFilter_gender03_btn);

        travelGenderBtn1.setOnClickListener(conditionFilterOnclickListener);
        travelGenderBtn2.setOnClickListener(conditionFilterOnclickListener);
        travelGenderBtn3.setOnClickListener(conditionFilterOnclickListener);


        //참여연령 seekbar
        ageBar = (RangeBar) findViewById(R.id.activity_conditionFilter_ageRange_seekbar);
        ageBar.setOnRangeBarChangeListener(this);

        //참여연령 텍스트뷰
        travelAgeTextView = (TextView)findViewById(R.id.activity_conditionFilter_ageBar_TextView);

        //선택완료 버튼
        travelCreateBtn = (Button)findViewById(R.id.activity_conditionFilter_createBtn);
        travelCreateBtn.setOnClickListener(conditionFilterOnclickListener);

        //기존 필터 세팅값이 있을 경우 다시 뿌려주기
        filterRestore();

    }



    @Override
    protected void onResume() {
        super.onResume();

    }


    public View.OnClickListener conditionFilterOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {


                case (R.id.activity_conditionFilter_gender01_btn): {   // '여자만'버튼
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderFilter = "female";
                    Log.i("버튼1클릭 : ",  "클릭됨");
                    isGenderChecked = true;
                }break;


                case (R.id.activity_conditionFilter_gender02_btn): {   //  '남자만' 버튼
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderFilter = "male";
                    Log.i("버튼2클릭 : ",  "클릭됨");
                    isGenderChecked = true;
                }break;


                case (R.id.activity_conditionFilter_gender03_btn): {   // '성별무관' 버튼
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderFilter = "all";
                    Log.i("버튼3클릭 : ",  "클릭됨");
                    isGenderChecked = true;
                }break;


                case (R.id.activity_conditionFilter_createBtn) : {  //필터 선택 완료 버튼 이벤트 처리

                    filterCheck();

                    Log.i("Activity_condition_filter - isPlaceChecked : ", String.valueOf(isPlaceChecked));
                    Log.i("Activity_condition_filter - isMaxPersonChecked : ", String.valueOf(isMaxPersonChecked));
                    Log.i("Activity_condition_filter - isGenderChecked : ", String.valueOf(isGenderChecked));
                    Log.i("Activity_condition_filter - isCostChecked : ", String.valueOf(isCostChecked));
                    Log.i("Activity_condition_filter - isAgeChecked : ", String.valueOf(isAgeChecked));

                    if (costInputType){ //경비 항목에 숫자가 아닌 것을 입력하지 않았다는 것을 먼저 확인함

                        boolean[] isFilterChecked  = {isPlaceChecked,isMaxPersonChecked,isGenderChecked,isCostChecked,isAgeChecked};
                        boolean isNoCondition = true;

                        //필터 조건 중 하나라도 정해진 것이 있으면 변화가 있는 것으로 간주함
                        for (int i = 0; i < 5; i++){
                            if (isFilterChecked[i]){
                                isNoCondition = false;
                            }
                        }

                        if (isNoCondition){
                            sprf.savePreferences("filterCondition", "filterApplied", "false");
                            startActivity(new Intent(ConditionFilterActivity.this, MenuActivity.class));
                            Log.i("Activity_condition_filter - ", "검색필터 비설정됨");
                        }else{
                            sprf.savePreferences("filterCondition", "filterApplied", "true");
                            startActivity(new Intent(ConditionFilterActivity.this, MenuActivity.class));
                            Log.i("Activity_condition_filter - ", "검색필터 설정됨");
                        }

                        Log.i("Activity_condition_filter - maxPersonFilter", sprf.getPreferences("filterCondition", "filterMaxPerson"));
                        Log.i("Activity_condition_filter - costFilter", sprf.getPreferences("filterCondition", "filterCost"));
                        Log.i("Activity_condition_filter - genderFilter", sprf.getPreferences("filterCondition", "filterGender"));
                        Log.i("Activity_condition_filter - fromAgeFilter", sprf.getPreferences("filterCondition", "filterLowAge"));
                        Log.i("Activity_condition_filter - toAgeFilter", sprf.getPreferences("filterCondition", "filterHighAge"));
                        finish();
                    }

                }break;


                case (R.id.activity_conditionFilter_init_TextView) : {
                    travelPlaceEditText.setText("");
                    maxPersonSpinner.setSelection(0);
                    travelCostEditText.setText("");
                    ageBar.setRangePinsByIndices(0,72);
                    travelAgeTextView.setText("연령설정");
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    sprf.removePreferences("filterCondition","filterPlace");
                    sprf.removePreferences("filterCondition","filterMaxPerson");
                    sprf.removePreferences("filterCondition","filterCost");
                    sprf.removePreferences("filterCondition","filterGender");
                    sprf.removePreferences("filterCondition","filterLowAge");
                    sprf.removePreferences("filterCondition","filterHighAge");

                    sprf.savePreferences("filterCondition", "filterApplied", "false");

                    isPlaceChecked = false;
                    isGenderChecked = false;
                    isMaxPersonChecked = false;
                    isCostChecked = false;
                    isAgeChecked = false;

                    Toast.makeText(getApplicationContext(), "검색필터가 해제되었습니다", Toast.LENGTH_SHORT).show();

                }


            }
        }
    };





    //글 등록 전 예외처리 밑 변경사항이 저장

    public void filterCheck() {

        //여행할 도시

        if((travelPlaceEditText.getText().toString()).length() == 0 || travelPlaceEditText.getText().toString() == null || ((travelPlaceEditText).getText().toString()).trim().equals("")){
            placeFilter = "notSet";
            sprf.savePreferences("filterCondition", "filterPlace", placeFilter);
            isPlaceChecked = false;
        }else{
            placeFilter = travelPlaceEditText.getText().toString();
            sprf.savePreferences("filterCondition", "filterPlace", placeFilter);
            isPlaceChecked = true;
        }


        //여행인원
        switch (maxPersonSelect) {
            case ("상관없음") :{
                maxPersonFilter = "notSet";
                isMaxPersonChecked = false;
            }break;

            case ("단둘이서(2인)") :{
                maxPersonFilter = "2";
                isMaxPersonChecked = true;
            }break;

            case ("3인 ~ 5인") :{
                maxPersonFilter = "3to5";
                isMaxPersonChecked = true;
            }break;

            case ("6인 ~ 10인") :{
                maxPersonFilter = "6to10";
                isMaxPersonChecked = true;
            }break;

            case ("10인 초과") :{
                maxPersonFilter = "from11";
                isMaxPersonChecked = true;
            }break;

            default: {
                maxPersonFilter = "notSet";
                isMaxPersonChecked = false;
            }

        }
        sprf.savePreferences("filterCondition", "filterMaxPerson", maxPersonFilter);


        //여행경비
        final String COSTPATTERN = "^[0-9]*$";

        if((travelCostEditText.getText().toString()).length() == 0 || travelCostEditText.getText().toString() == null || travelCostEditText.getText().toString().trim().equals("")){
            costFilter = "notSet";
            isCostChecked = false;
            costInputType = true;
        }else if(!Pattern.matches(COSTPATTERN, travelCostEditText.getText().toString())){
            Toast.makeText(this, "예상경비는 숫자만 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
            costInputType = false;
            isCostChecked = false;
            costFilter = "notSet";
        }else{
            costFilter = travelCostEditText.getText().toString();
            isCostChecked = true;
        }
        sprf.savePreferences("filterCondition", "filterCost", costFilter);


        //성별
        if (genderFilter == null || genderFilter.length() == 0){
            genderFilter = "notSet";
            isGenderChecked = false;
        }
        sprf.savePreferences("filterCondition", "filterGender", genderFilter);

        //참여연령
        if (fromAge == -1 || toAge == -1){
            fromAgeFilter = "notSet";
            toAgeFilter = "notSet";
            isAgeChecked = false;
        }else if(fromAge == 18 && toAge == 90){ //기본 세팅값, 최대범위이기 떄문에 사실상 세팅 안한 것과 동일
            fromAgeFilter = "notSet";
            toAgeFilter = "notSet";
            isAgeChecked = false;
        }else{
            fromAgeFilter = String.valueOf(fromAge);
            toAgeFilter = String.valueOf(toAge);
            isAgeChecked = true;
        }
        sprf.savePreferences("filterCondition", "filterLowAge", fromAgeFilter);
        sprf.savePreferences("filterCondition", "filterHighAge", toAgeFilter);

    }


    @Override   // 여행 연령 체크 seekbar 범위가 변할 떄 작동하는 함수
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        fromAge = leftPinIndex+18;
        toAge = rightPinIndex+18;
        Log.e("Setting Age : ",  fromAge + " - " + toAge);

        travelAgeTextView.setText(fromAge+"세 - "+toAge+"세");
    }




    private void filterRestore() {  //기존에 필터에 저장된 값이 있을 경우 액티비티 진입 시 다시 뿌려줌
        Log.e("filterRestore", "call");


        if (sprf.getPreferences("filterCondition", "filterApplied").equals("true")){

            if (sprf.getPreferences("filterCondition", "filterPlace").equals("notSet")){
                travelPlaceEditText.setText("");
                isPlaceChecked = false;
            }else{
                travelPlaceEditText.setText(sprf.getPreferences("filterCondition", "filterPlace"));
                isPlaceChecked = true;
            }

            if (sprf.getPreferences("filterCondition", "filterCost").equals("notSet")){
                travelCostEditText.setText("");
                isCostChecked = false;
            }else{
                travelCostEditText.setText(sprf.getPreferences("filterCondition", "filterCost"));
                isCostChecked = true;
            }

            switch (sprf.getPreferences("filterCondition", "filterMaxPerson")) {
                case ("2") :{
                    maxPersonSpinner.setSelection(1);
                    isMaxPersonChecked = true;
                }break;

                case ("3to5") :{
                    maxPersonSpinner.setSelection(2);
                    isMaxPersonChecked = true;
                }break;

                case ("6to10") :{
                    maxPersonSpinner.setSelection(3);
                    isMaxPersonChecked = true;
                }break;

                case ("from11") :{
                    maxPersonSpinner.setSelection(4);
                    isMaxPersonChecked = true;
                }break;
                default:
                    isMaxPersonChecked = false;

            }


            switch (sprf.getPreferences("filterCondition", "filterGender")){
                case ("female"): {   // '여자만'버튼
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    isGenderChecked = true;
                    genderFilter = "female";
                    Log.i("버튼클릭 : ",  "'여자만' 클릭됨");
                }break;


                case ("male"): {   //  '남자만' 버튼
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    isGenderChecked = true;
                    genderFilter = "male";
                    Log.e("버튼클릭 : ",  "'남자만' 클릭됨");
                }break;


                case ("all"): {   // '성별무관' 버튼
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    isGenderChecked = true;
                    genderFilter = "all";
                    Log.e("버튼클릭 : ",  "'성별무관' 클릭됨");
                }break;

                default:
                    isGenderChecked = false;
            }


//            if (travelAgeTextView.getText().toString().equals("연령설정")) {
//                isAgeChecked = false;
//                Toast.makeText(getApplicationContext(), "연령설정", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(getApplicationContext(), "연령설정 외", Toast.LENGTH_LONG).show();
//                int lowAge = Integer.parseInt(sprf.getPreferences("filterCondition","filterLowAge")) - 18;
//                int highAge = Integer.parseInt(sprf.getPreferences("filterCondition","filterHighAge")) - 18;
//                ageBar.setRangePinsByIndices(lowAge, highAge);
//                isAgeChecked = true;
//            }

            //수정 전 코드(백업용)
            if (!sprf.getPreferences("filterCondition","filterLowAge").equals("notSet") && !sprf.getPreferences("filterCondition","filterHighAge").equals("notSet")){
                int lowAge = Integer.parseInt(sprf.getPreferences("filterCondition","filterLowAge")) - 18;
                int highAge = Integer.parseInt(sprf.getPreferences("filterCondition","filterHighAge")) - 18;
                ageBar.setRangePinsByIndices(lowAge, highAge);
                isAgeChecked = true;
            }else{
                isAgeChecked = false;
            }



        }
    }

}
