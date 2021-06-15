package com.teamnova.jaepark.travelmate.activities.TravelMenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;
import com.teamnova.jaepark.travelmate.activities.functionalClass.UploadFile;
import com.teamnova.jaepark.travelmate.activities.functionalClass.permissionCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditTravel extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, RangeBar.OnRangeBarChangeListener {

    //넘겨받은 여행정보 JSON String
    String mTravelInfo;

    //서버로부터 조회한 여형정보를 담을 여행 정보 변수들(mUserId는 현재 앱사용자가 아닌 해당 여행을 등록한 사용자의 아이디)
    String mSeq, mTitle, mStartDate, mEndDate, mPlace, mMaxPerson, mCost, mGender,  mFromAge, mToAge, mBrief,
            mUserId, mEnterTime, mCreatedTime;

    //사진정보 어레이리스트
    ArrayList mPicAddressAL = new ArrayList();

    //삭제하기 버튼
    Button mDeleteBtn;

    //기타 뷰
    EditText travelTitleEditText, travelCostEditText, travelBriefingEditText, travelPlaceEditText;
    TextView dateTextView, travelAgeTextView;
    Button travelGenderBtn1, travelGenderBtn2, travelGenderBtn3, travelCreateBtn;
    RangeBar ageBar;
    Spinner maxPersonSpinner;

    //액티비티 좌상단 돌아가기 이미지뷰
    ImageView backImgView;

    String startDate;   //여행 시작일
    String endDate;     //여행종료일

    String maxPersonSelect; //여행인원

    int firstIntDate, secondIntDate;  //여행일자 관련 인트
    int fromAge, toAge = -1; //참여연령 관련 인트

    //Json에 저장할 스트링
    String titleJs, startDateJs, endDateJs, placeJs, maxPersonJs, costJs, genderJs,  fromAgeJs, toAgeJs, briefJs,
            userId, enterTime, createdTime;
    //사진이 어떤글에 속해있는지를 구분해주는 고유키
    //String enterTime, createdTime;   // 등록메뉴에 들어온 시각
    //picture01,picture02,picture03,picture04 키값도 Json에 들어감


    //예외처리 체크항목
    boolean titleCheck, dateCheck, placeCheck, maxpersonCheck, costCheck, genderCheck, ageCheck, briefingCheck, pictureCheck, userIdCheck = false;

    JSONObject travelInfo_Json;

    boolean picUploadCheck = true;

    // 사진 업로드 관련 변수
    permissionCheck pmsCheck;

    Uri photoURI, albumURI;
    boolean isAlbum = false;    //Crop시 사진을 직접 찍을 것인지 앨범에서 가져온 것인지 확인하는 플래그
    String mCurrentPhotoPath, imageFileName;


    //사진 이미지뷰
    ImageView takePicImg01, takePicImg02, takePicImg03, takePicImg04;
    ArrayList<String> picPathList, picNameList;
    int currentPickedImgNO; //현재 선택하고자 하는 이미지뷰를 알려주기 위한 변수

    //기존 사진이 변경되었는지를 알려주는 어레이리스트
    boolean [] isPicChanged;

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    //sharedPreference
    CallSharedPreference callSprf;


    //삭제 다이얼로그
    android.app.AlertDialog.Builder ExitDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_travel);



        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();


        //인텐트 체크 = 여행정보 받기
        Intent intent = getIntent();
        mTravelInfo = intent.getStringExtra("travelinfo");
        //Toast.makeText(this, "travelInfoString : " + mTravelInfo, Toast.LENGTH_SHORT).show();
        Log.e("travelInfo", mTravelInfo);

        try {
            JSONObject travelInfoJson = new JSONObject(mTravelInfo);


            //서버로부터 조회한 여형정보를 담을 여행 정보 변수들
            mSeq = travelInfoJson.get("seq").toString();
            mTitle = travelInfoJson.get("title").toString();
            mStartDate = travelInfoJson.get("startDate").toString();
            mEndDate = travelInfoJson.get("endDate").toString();
            mPlace = travelInfoJson.get("place").toString();
            mMaxPerson = travelInfoJson.get("maxPerson").toString();
            mCost = travelInfoJson.get("cost").toString();
            mGender = travelInfoJson.get("gender").toString();
            mFromAge = travelInfoJson.get("fromAge").toString();
            mToAge = travelInfoJson.get("toAge").toString();
            mBrief = travelInfoJson.get("briefing").toString();
            mUserId = travelInfoJson.get("id").toString();
            mEnterTime = travelInfoJson.get("enterTime").toString();
            mCreatedTime = travelInfoJson.get("createdTime").toString();


            //사진정보 어레이리스트 생성
            for (int i = 0; i < 4; i++){
                mPicAddressAL.add(travelInfoJson.get("picture0" + (i+1)));
                Log.e("pictureArraySize", mPicAddressAL.size() + "");
            }

            //사진변경 여부를 false로 초기화
            isPicChanged = new boolean[4];
            Arrays.fill(isPicChanged, false);


        } catch (JSONException e) {
            e.printStackTrace();
        }



        //삭제하기 버튼
        mDeleteBtn = (Button) findViewById(R.id.activity_editTravel_delete_Btn);
        mDeleteBtn.setOnClickListener(editOnclickListener);


        //액티비티 좌상단 돌아가기 이미지뷰
        backImgView = (ImageView) findViewById(R.id.activity_editTravel_backkey_ImgView);
        backImgView.setOnClickListener(editOnclickListener);

        // [onCreate 시각 + userID]를 고유 구분값으로 생성하여 사진 이름 앞에 부착
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();
        enterTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        userId =  callSprf.getPreferences("memberInfo", "id");


        //여행제목 입력 에딧텍스트
        travelTitleEditText = (EditText) findViewById(R.id.activity_editTravel_travelTitle_ET);
        travelTitleEditText.setText(mTitle);


        //여행 날짜 텍스트뷰
        dateTextView = (TextView)findViewById(R.id.activity_editTravel_Date_TV);
        dateTextView.setOnClickListener(editOnclickListener);

        String startYear = mStartDate.substring(0,4);
        String startMonth = mStartDate.substring(4,6);
        String startDay = mStartDate.substring(6,8);
        String startDate = startYear + "." + startMonth + "." + startDay + ".";

        String endYear = mEndDate.substring(0,4);
        String endMonth = mEndDate.substring(4,6);
        String endDay = mEndDate.substring(6,8);
        String endDate = endYear + "." + endMonth + "." + endDay + ".";

        dateTextView.setText(startDate + " ~ " + endDate);
        startDateJs = mStartDate;
        endDateJs = mEndDate;


        //여행할 도시
        travelPlaceEditText = (EditText)findViewById(R.id.activity_editTravel_place_ET);
        travelPlaceEditText.setText(mPlace);

        //여행인원 스피너
        maxPersonSpinner = (Spinner)findViewById(R.id.activity_editTravel_maxPerson_spinner);

        //스피너의 어댑터 생성
        ArrayAdapter maxPersonAdapter = ArrayAdapter.createFromResource(this,
                R.array.maxPerson, R.layout.spinner_item);
        maxPersonAdapter.setDropDownViewResource(R.layout.spinner_item);
        maxPersonSpinner.setAdapter(maxPersonAdapter);   //어뎁터 세팅

        AdapterView.OnItemSelectedListener categoryListener = new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                maxPersonSelect = maxPersonSpinner.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(), "selected category = " + maxPersonSelect, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        maxPersonSpinner.setOnItemSelectedListener(categoryListener);


        //초기값 설정

        switch (mMaxPerson){
            case ("2") :{
                maxPersonSpinner.setSelection(0);
                maxPersonJs = mMaxPerson;
            }break;
            case ("3to5") :{
                maxPersonSpinner.setSelection(1);
                maxPersonJs = mMaxPerson;
            }break;
            case ("6to10") :{
                maxPersonSpinner.setSelection(2);
                maxPersonJs = mMaxPerson;
            }break;
            case ("from11") :{
                maxPersonSpinner.setSelection(3);
                maxPersonJs = mMaxPerson;
            }break;
            default:{
                maxPersonSpinner.setSelection(0);
                maxPersonJs = mMaxPerson;
            }break;
        }



        //여행경비 에딧텍스트
        travelCostEditText = (EditText)findViewById(R.id.activity_editTravel_travelCost_ET);
        travelCostEditText.setText(mCost);

        //동행 성별 선택 버튼 1,2,3(여, 남, 무관)
        travelGenderBtn1 = (Button)findViewById(R.id.activity_editTravel_gender01_btn);
        travelGenderBtn2 = (Button)findViewById(R.id.activity_editTravel_gender02_btn);
        travelGenderBtn3 = (Button)findViewById(R.id.activity_editTravel_gender03_btn);

        travelGenderBtn1.setOnClickListener(editOnclickListener);
        travelGenderBtn2.setOnClickListener(editOnclickListener);
        travelGenderBtn3.setOnClickListener(editOnclickListener);

        genderJs = mGender;
        genderCheck = true;

        switch (mGender){   //초기값 설정
            case ("female") :{
                travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
            }break;
            case ("male") :{
                travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));            }break;
            case ("all") :{
                travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));            }break;
            default:{
                travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));            }break;
        }


        //참여연령 seekbar
        ageBar = (RangeBar) findViewById(R.id.activity_editTravel_ageRange_Seekbar);
        ageBar.setOnRangeBarChangeListener(this);

        //참여연령 텍스트뷰
        travelAgeTextView = (TextView)findViewById(R.id.activity_editTravel_ageBar_TV);
        ageBar.setRangePinsByIndices(Integer.parseInt(mFromAge) - 18, Integer.parseInt(mToAge) - 18);


        //여행설명 에딧텍스트
        travelBriefingEditText = (EditText)findViewById(R.id.activity_editTravel_travelBriefing_ET);
        travelBriefingEditText.setText(mBrief);

        //등록하기 버튼
        travelCreateBtn = (Button)findViewById(R.id.activity_editTravel_editInfo_Btn);
        travelCreateBtn.setOnClickListener(editOnclickListener);

        //사진찍기 이미지뷰
        takePicImg01 = (ImageView)findViewById(R.id.activity_editTravel_takePic01_ImageView);
        takePicImg02 = (ImageView)findViewById(R.id.activity_editTravel_takePic02_ImageView);
        takePicImg03 = (ImageView)findViewById(R.id.activity_editTravel_takePic03_ImageView);
        takePicImg04 = (ImageView)findViewById(R.id.activity_editTravel_takePic04_ImageView);

        //기존사진 가져와서 뿌려주기
        if (mPicAddressAL.size() != 0){
            for (int i = 0; i < mPicAddressAL.size(); i ++){
                if (!mPicAddressAL.get(i).equals("noPicture")){
                    switch (i)  {
                        case (0) : {
                            Glide.with(this).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + mPicAddressAL.get(i))
                                    .thumbnail(0.1f)
                                    .override(100,100)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.nopicture)
                                    .into(takePicImg01);
                        }break;
                        case (1) : {
                            Glide.with(this).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + mPicAddressAL.get(i))
                                    .thumbnail(0.1f)
                                    .override(100,100)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.nopicture)
                                    .into(takePicImg02);
                        }break;
                        case (2) : {
                            Glide.with(this).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + mPicAddressAL.get(i))
                                    .thumbnail(0.1f)
                                    .override(100,100)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.nopicture)
                                    .into(takePicImg03);
                        }break;
                        case (3) : {
                            Glide.with(this).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + mPicAddressAL.get(i))
                                    .thumbnail(0.1f)
                                    .override(100,100)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.nopicture)
                                    .into(takePicImg04);
                        }break;
                    };


                }
            }
        }




        //사진 이미지뷰 온클릭 리스너
        takePicImg01.setOnClickListener(editOnclickListener);
        takePicImg02.setOnClickListener(editOnclickListener);
        takePicImg03.setOnClickListener(editOnclickListener);
        takePicImg04.setOnClickListener(editOnclickListener);

        //사진 경로, 이름 기본값 설정
        picNameList = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            if (mPicAddressAL.get(i).toString() != null){   //기존 사진데이터가 있을 경우 사진 파일명을 가져옴
                picNameList.add(i, mPicAddressAL.get(i).toString());
            }else{
                picNameList.add(i, "noPicture");
            }
        }

        picPathList = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            picPathList.add(i, "noPicture");
        }


        //권한 설정
        pmsCheck = new permissionCheck(EditTravel.this);

    }





    //글 등록 전 예외처리
    public void exceptionCheck() throws JSONException {

        // 여행주제 예외처리
        if((travelTitleEditText.getText().toString()).length() == 0 || travelTitleEditText.getText().toString() == null || ((travelTitleEditText).getText().toString()).trim().equals("")){
            Toast.makeText(this, "여행제목을 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            titleJs = travelTitleEditText.getText().toString();
            titleCheck = true;
        }

        // 여행기간 예외처리
        if (startDateJs == null || endDateJs == null || startDateJs.length() == 0 || endDateJs.length() == 0){
            Toast.makeText(this, "여행날짜를 선택해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            dateCheck = true;
        }

        //여행할 도시

        if((travelPlaceEditText.getText().toString()).length() == 0 || travelPlaceEditText.getText().toString() == null || ((travelPlaceEditText).getText().toString()).trim().equals("")){
            Toast.makeText(this, "여행할 도시명을 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            placeJs = travelPlaceEditText.getText().toString();
            placeCheck = true;
        }


        //여행인원
        switch (maxPersonSelect) {
            case ("단둘이서(2인)") :{
                maxPersonJs = "2";
                maxpersonCheck = true;
            }break;

            case ("3인 ~ 5인") :{
                maxPersonJs = "3to5";
                maxpersonCheck = true;
            }break;

            case ("6인 ~ 10인") :{
                maxPersonJs = "6to10";
                maxpersonCheck = true;
            }break;

            case ("10인 초과") :{
                maxPersonJs = "from11";
                maxpersonCheck = true;
            }break;

            default: {
                Toast.makeText(this, "여행인원을 선택해주시기 바랍니다", Toast.LENGTH_SHORT).show();
            }

        }

        //여행경비
        final String COSTPATTERN = "^[0-9]*$";

        if((travelCostEditText.getText().toString()).length() == 0 || travelCostEditText.getText().toString() == null || travelCostEditText.getText().toString().trim().equals("")){
            Toast.makeText(this, "예상경비를 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else if(!Pattern.matches(COSTPATTERN, travelCostEditText.getText().toString())){
            Toast.makeText(this, "예상경비는 숫자만 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            costJs = travelCostEditText.getText().toString();
            costCheck = true;
        }

        //성별
        if (genderJs == null || genderJs.length() == 0 || genderCheck != true){
            Toast.makeText(this, "함께할 메이트의 성별을 선택해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }

        //참여연령
        if (fromAge == -1 || toAge == -1){
            Toast.makeText(this, "참여연령을 설정해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            fromAgeJs = String.valueOf(fromAge);
            toAgeJs = String.valueOf(toAge);
            ageCheck = true;
        }

        //여행 설명
        if((travelBriefingEditText.getText().toString()).length() == 0 || travelBriefingEditText.getText().toString() == null){
            Toast.makeText(this, "여행계획을 입력해주시기 바랍니다", Toast.LENGTH_SHORT).show();
        }else{
            briefJs = travelBriefingEditText.getText().toString();
            briefingCheck = true;
        }


        //사진 설명
        pictureCheck = true;


        //유저 아이디 불러오기
        if(userId.length() == 0 || userId == null){
            Toast.makeText(this, "정상적 로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }else{
            userIdCheck = true;
        }



        if (titleCheck == true && dateCheck == true && placeCheck == true && maxpersonCheck == true
                && costCheck == true && genderCheck == true && ageCheck == true && briefingCheck == true
                && pictureCheck  == true && userIdCheck == true){

            createdTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            travelInfo_Json = new JSONObject();

            travelInfo_Json.put("seq", mSeq);
            travelInfo_Json.put("userId", userId);
            travelInfo_Json.put("title", titleJs);
            travelInfo_Json.put("startDate", startDateJs);
            travelInfo_Json.put("endDate", endDateJs);
            travelInfo_Json.put("maxPerson", maxPersonJs);
            travelInfo_Json.put("place", placeJs);
            travelInfo_Json.put("cost", costJs);
            travelInfo_Json.put("gender", genderJs);
            travelInfo_Json.put("fromAge", fromAgeJs);
            travelInfo_Json.put("toAge", toAgeJs);
            travelInfo_Json.put("briefing", briefJs);

            travelInfo_Json.put("enterTime", enterTime);
            travelInfo_Json.put("createdTime", createdTime);

            travelInfo_Json.put("picture01", picNameList.get(0));    //사진이 없을시 "noPictue"가 기본값(String)
            travelInfo_Json.put("picture02", picNameList.get(1));
            travelInfo_Json.put("picture03", picNameList.get(2));
            travelInfo_Json.put("picture04", picNameList.get(3));

        }

    }



    @Override   //여행기간 datePicker
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        firstIntDate = (year * 10000) + ((monthOfYear + 1) * 100) + dayOfMonth;
        secondIntDate = (yearEnd * 10000) + ((monthOfYearEnd + 1) * 100) + dayOfMonthEnd;

        if(firstIntDate < secondIntDate || firstIntDate == secondIntDate){
            startDate = year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일";
            endDate = yearEnd + "년" + (monthOfYearEnd + 1) + "월" + dayOfMonthEnd + "일";
            startDateJs = String.valueOf(firstIntDate);
            endDateJs = String.valueOf(secondIntDate);
        }else{
            startDate = yearEnd + "년" + (monthOfYearEnd + 1) + "월" + dayOfMonthEnd + "일";
            endDate = year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일";
            startDateJs = String.valueOf(secondIntDate);
            endDateJs = String.valueOf(firstIntDate);
        }

        dateTextView.setText(startDate + " ~ " + endDate);

    }


    @Override   // 여행 연령 체크 seekbar
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
        fromAge = leftPinIndex+18;
        toAge = rightPinIndex+18;
        Log.e("Setting Age : ",  fromAge + " - " + toAge);

        travelAgeTextView.setText(fromAge+"세 - "+toAge+"세");

    }



    //사진 업로드 관련 메소드

    public void captureCamera(){
        Log.i("captureCamera", "CALL");

        if (pmsCheck.isCheck("camera")){

            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null){
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null){
                        photoURI = FileProvider.getUriForFile(this, "com.teamnova.jaepark.travelmate.activities", photoFile);
                        Log.i("photoFile", photoFile.toString());
                        Log.i("photoURI", photoURI.toString());
                        //photoURI는 새로 저장하기 위한 파일의 URI
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                    }
                } else {
                    Toast.makeText(this, "외장메모리 미 지원하여 해당 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "저장소 권한 설정에 문제가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void getAlbum(){
        //앨범호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);

    }


    public File createImageFile() throws IOException {
        Log.i("createImageFile", "CALL");


        //저장폴더 생성
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TravelMate/";
        File file = new File(dirPath);
        if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인
            file.mkdirs();


        //이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = userId + enterTime + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TravelMate/" + imageFileName);

        //파일 저장
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        Log.i("mCurrentPhotoPath", mCurrentPhotoPath);

        return  storageDir;

    }


    public void cropImage(){
        Log.i("cropImage", "CALL");
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        //사진촬영 후 크롭기능을 위한 코드
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        cropIntent.setDataAndType(photoURI, "image/*");
//        cropIntent.putExtra("outputX", 200);    //crop한 이미지의 x축 크기
//        cropIntent.putExtra("outputY", 200);    //crop한 이미지의 y축 크기
//        cropIntent.putExtra("aspectX", 1);      //crop한 이미지의 x축 비율
//        cropIntent.putExtra("aspectX", 1);      //crop한 이미지의 y축 비율
        cropIntent.putExtra("scale", true);


        //저장
        if (isAlbum == false){
            Log.i("cropImage", "isAlbum == false");
            cropIntent.putExtra("output", photoURI);    //크랍된 이미지를 해당 경로에 저장
        } else if(isAlbum == true){
            Log.i("cropImage", "isAlbum == true");
            cropIntent.putExtra("output", albumURI);    //크랍된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }


    //동기화 - 갤러리의 새로고침 ACTION_MEDIA_MOUNTED는 하나의 폴더 FILE은 하나의 파일을 새로 고침할 때 사용함
    public void galleryAddPic(){
        Log.i("galleryAddPic", "CALL");
        Intent mediaScannIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScannIntent.setData(contentUri);
        sendBroadcast(mediaScannIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "CALL");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (REQUEST_TAKE_PHOTO) : {
                isAlbum = false;
                galleryAddPic();
                Toast.makeText(this, "크랍이미지", Toast.LENGTH_SHORT).show();
                cropImage();
                Log.i("onActivityResult : ", "REQUEST_TAKE_PHOTO - mCurrentPhotoPath : " + mCurrentPhotoPath);
            }break;

            case (REQUEST_TAKE_ALBUM) : {
                if (data == null){  //사진 선택 안하고 그냥 앱으로 돌아갈 경우
                    break;
                }else{
                    isAlbum = true;
                    File albumFile = null;
                    try {
                        albumFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (albumFile != null){
                        albumURI = Uri.fromFile(albumFile);
                    }
                    photoURI = data.getData();
                    cropImage();
                    galleryAddPic();
                    Log.i("photoURI", photoURI.toString());
                    Log.i("albumURI", albumURI.toString());
                }
            }break;

            case (REQUEST_IMAGE_CROP) : {
                galleryAddPic();
                File imgFile = new  File(mCurrentPhotoPath);

                //비트맵 썸네일 세팅
                if(imgFile.exists()){
                    Log.i("imgFile", "CALL");
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    switch (currentPickedImgNO) {
                        case (1) :
                            takePicImg01.setImageBitmap(myBitmap);
                            picPathList.set(0, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(0, imageFileName);      //서버에 저장될 파일 이름
                            isPicChanged[0] = true; //사진 변경 또는 추가됨
                            break;
                        case (2) :
                            takePicImg02.setImageBitmap(myBitmap);
                            picPathList.set(1, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(1, imageFileName);      //서버에 저장될 파일 이름
                            isPicChanged[1] = true; //사진 변경 또는 추가됨
                            break;
                        case (3) :
                            takePicImg03.setImageBitmap(myBitmap);
                            picPathList.set(2, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(2, imageFileName);      //서버에 저장될 파일 이름
                            isPicChanged[2] = true; //사진 변경 또는 추가됨
                            break;
                        case (4) :
                            takePicImg04.setImageBitmap(myBitmap);
                            picPathList.set(3, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(3, imageFileName);      //서버에 저장될 파일 이름
                            isPicChanged[3] = true; //사진 변경 또는 추가됨
                            break;
                    }

                }
                // 업로드
                uploadFile(mCurrentPhotoPath);
            }break;

        }

    }


    public void uploadFile(String filePath){
        String url = "http://poyapo123.cafe24.com/TMphp/fileUpload.php";

        UploadFile uploadFile = new UploadFile(EditTravel.this) {
            @Override
            protected void onPostExecute(String s) {
                // 성공실패에 따른 토스트 작성
                if (s.equals("Success")){
                    //Toast.makeText(EditTravel.this, "성공적으로 업로드 하였습니다", Toast.LENGTH_SHORT).show();
                    picUploadCheck = true;
                }else if(s.equals("Fail")){
                    Toast.makeText(EditTravel.this, "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    picUploadCheck = false;
                }

            }
        };
        uploadFile.setPath(filePath);
        uploadFile.execute(url);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }





    public View.OnClickListener editOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case (R.id.activity_editTravel_backkey_ImgView) : {
                    Intent intent = new Intent(getApplicationContext(), TravelDetailActivity.class);
                    intent.putExtra("travelinfo", mTravelInfo);
                    startActivity(intent);
                    finish();
                }break;


                case (R.id.activity_editTravel_delete_Btn) : {

                    ExitDialog = new android.app.AlertDialog.Builder(EditTravel.this);

                    ExitDialog.setTitle("확인을 누르시면 여행정보가 삭제됩니다.");          // 제목
                    ExitDialog.setMessage("삭제하시겠습니까?");  // 메세지
                    ExitDialog.setCancelable(false);                    // 취소가능 설정

                    ExitDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override   // 확인 버튼 클릭할 경우
                        public void onClick(DialogInterface dialog, int which) {


                            JSONObject seqInfo_Json = new JSONObject();

                            try {
                                seqInfo_Json.put("seq", mSeq);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            final String mTaskID = "travelInfo_delete";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                            final String mStrUrl = "travelInfo_delete.php";
                            final int mDelay = 0;

                            TWDhttpATask travelInfoDelete = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                                @Override
                                protected void onPostExecute(JSONObject fromServerData) {
                                    try {
                                        JSONObject result = new JSONObject(fromServerData.toString());
                                        //Toast.makeText(getApplicationContext(), result.getString("task") + " : " + result.get("result"), Toast.LENGTH_SHORT).show();
                                        if (result.get("result").equals("success")){
                                            Toast.makeText(getApplicationContext(), "여행정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditTravel.this, MenuActivity.class));
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "문제가 생겨 정보를 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            travelInfoDelete.execute(seqInfo_Json);
                        }
                    });

                    ExitDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override   // 취소 버튼 클릭할 경우
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });


                    android.app.AlertDialog deleteConfirm = ExitDialog.create();
                    deleteConfirm.show();

                }break;


                case(R.id.activity_editTravel_Date_TV):{    //여행일정 텍스트뷰
                    if (ExitDialog != null){   //다이얼로그 중복 방지용
                        ExitDialog = null;
                    }


                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            EditTravel.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");


                }break;

                case (R.id.activity_editTravel_gender01_btn): {   // '여자만'버튼
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "female";
                    genderCheck = true;
                    Log.e("버튼1클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_editTravel_gender02_btn): {   //  '남자만' 버튼
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "male";
                    genderCheck = true;
                    Log.e("버튼2클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_editTravel_gender03_btn): {   // '성별무관' 버튼
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "all";
                    genderCheck = true;
                    Log.e("버튼3클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_editTravel_editInfo_Btn) : {

                    try {

                        exceptionCheck();   /* 에외처리 검사 및 이상이 없으면 Json오브젝트 완성*/


                        if (titleCheck == true && dateCheck == true && placeCheck == true && maxpersonCheck == true
                                && costCheck == true && genderCheck == true && ageCheck == true && briefingCheck == true
                                && pictureCheck  == true && userIdCheck == true){   //외의 예외처리가 모두 되어야만 사진업로드

                            for (int i = 0; i < 4; i++){
                                if (picUploadCheck == true && isPicChanged[i] == true){    //중간에 업로드가 실패하면 뒤의 업로드 실행 안함, 수정 변경이 된 경우에만 업로드 메소드 딜행
                                    if (!picPathList.get(i).equals("noPicture")){
                                        uploadFile(picPathList.get(i));
                                    }
                                }
                            }
                        }else{

                        }

                        if (travelInfo_Json != null && picUploadCheck == true){   // Json 파일 완성 및 사진이 모두 잘 올라갔을떄 http통신 시작
                            //Toast.makeText(getApplicationContext(), "Json완성", Toast.LENGTH_SHORT).show();

                            final String mTaskID = "travelInfo_edit";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                            final String mStrUrl = "travelInfo_edit.php";
                            final int mDelay = 0;

                            //사진업로드를 먼저하고 성공시 데이터 베이스에 나머지 정보를 입력해야한다

                            TWDhttpATask travelInfoEdit = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                                @Override
                                protected void onPostExecute(JSONObject fromServerData) {
                                    try {
                                        JSONObject result = new JSONObject(fromServerData.toString());
                                        //Toast.makeText(getApplicationContext(), result.getString("task") + " : " + result.get("result"), Toast.LENGTH_SHORT).show();
                                        if (result.get("result").equals("success")){
                                            Toast.makeText(getApplicationContext(), "여행정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditTravel.this, MenuActivity.class));
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "문제가 생겨 정보를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };

                            travelInfoEdit.execute(travelInfo_Json);

                        }else if(!picUploadCheck){
                            Toast.makeText(getApplicationContext(), "사진등록에 문제가 생겨 정보를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }break;

                //이미지뷰 버튼 온클릭 리스너
                case (R.id.activity_editTravel_takePic01_ImageView) : {
                    final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                    currentPickedImgNO = 1;

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditTravel.this);     // 여기서 this는 Activity의 this

                    // 여기서 부터는 알림창의 속성 설정
                    builder.setTitle("사진등록")        // 제목 설정
                            .setCancelable(true)
                            .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                                public void onClick(DialogInterface dialog, int index){
                                    if (index == 0){
                                        captureCamera();
                                    }else if(index == 1){
                                        getAlbum();
                                    }
                                }
                            });

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기

                }break;

                // 이미지 등록
                case (R.id.activity_editTravel_takePic02_ImageView) : {

                    if (picPathList.get(0).equals("noPicture") &&  mPicAddressAL.size() < 1){
                        Toast.makeText(EditTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 2;

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditTravel.this);     // 여기서 this는 Activity의 this

                        // 여기서 부터는 알림창의 속성 설정
                        builder.setTitle("사진등록")        // 제목 설정
                                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                                    public void onClick(DialogInterface dialog, int index){
                                        if (index == 0){
                                            captureCamera();
                                        }else if(index == 1){
                                            getAlbum();
                                        }
                                    }
                                });

                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                        dialog.show();    // 알림창 띄우기
                    }


                }break;

                case (R.id.activity_editTravel_takePic03_ImageView) : {

                    if (picPathList.get(1).equals("noPicture") && mPicAddressAL.size() < 2){
                        Toast.makeText(EditTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 3;

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditTravel.this);     // 여기서 this는 Activity의 this

                        // 여기서 부터는 알림창의 속성 설정
                        builder.setTitle("사진등록")        // 제목 설정
                                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                                    public void onClick(DialogInterface dialog, int index){
                                        if (index == 0){
                                            captureCamera();
                                        }else if(index == 1){
                                            getAlbum();
                                        }
                                    }
                                });

                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                        dialog.show();    // 알림창 띄우기
                    }
                }break;

                case (R.id.activity_editTravel_takePic04_ImageView) : {

                    if (picPathList.get(2).equals("noPicture") &&  mPicAddressAL.size() < 3){
                        Toast.makeText(EditTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 4;

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditTravel.this);     // 여기서 this는 Activity의 this

                        // 여기서 부터는 알림창의 속성 설정
                        builder.setTitle("사진등록")        // 제목 설정
                                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                                    public void onClick(DialogInterface dialog, int index){
                                        if (index == 0){
                                            captureCamera();
                                        }else if(index == 1){
                                            getAlbum();
                                        }
                                    }
                                });

                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                        dialog.show();    // 알림창 띄우기
                    }
                }break;



            }
        }
    };







}
