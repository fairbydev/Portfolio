package com.teamnova.jaepark.travelmate.activities.TravelMenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatClient;
import com.teamnova.jaepark.travelmate.activities.Chat.Message;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;
import com.teamnova.jaepark.travelmate.activities.functionalClass.UploadFile;
import com.teamnova.jaepark.travelmate.activities.functionalClass.permissionCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class CreateTravel extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, RangeBar.OnRangeBarChangeListener {


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

    //Json에 저장할 String 데이터
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

    ImageView takePicImg01, takePicImg02, takePicImg03, takePicImg04;
    ArrayList<String> picPathList, picNameList;
    int currentPickedImgNO; //현재 선택하고자 하는 이미지뷰를 알려주기 위한 변수

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    //sharedPreference
    CallSharedPreference callSprf;

    //test
//    LinearLayout countrySearchLL;
//    ScrollView inputMenu;
//    EditText countryET;
//    Button countrySelectBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_travel);


        //액티비티 좌상단 돌아가기 이미지뷰
        backImgView = (ImageView) findViewById(R.id.activity_createTravel_ImgView_backkey);
        backImgView.setOnClickListener(createOnclickListener);

        // [onCreate 시각 + userID]를 고유 구분값으로 생성하여 사진 이름 앞에 부착
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();
        enterTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        userId =  callSprf.getPreferences("memberInfo", "id");


        //여행제목 입력 에딧텍스트
        travelTitleEditText = (EditText) findViewById(R.id.activity_createTravel_travelTitle_Edittext);

        //여행 날짜 텍스트뷰
        dateTextView = (TextView)findViewById(R.id.activity_createTravel_Date_TextView);
        dateTextView.setOnClickListener(createOnclickListener);

        //여행할 도시
        travelPlaceEditText = (EditText)findViewById(R.id.activity_CreateTravel_place_EditText);

        //여행인원 스피너
        maxPersonSpinner = (Spinner)findViewById(R.id.activity_createTravel_maxPerson_spinner);

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
        maxPersonSpinner.setSelection(0);    //초기값 설정



        //여행경비 에딧텍스트
        travelCostEditText = (EditText)findViewById(R.id.activity_createTravel_travelCost_Edittext);

        //동행 성별 선택 버튼 1,2,3(여, 남, 무관)
        travelGenderBtn1 = (Button)findViewById(R.id.activity_createTravel_gender01_btn);
        travelGenderBtn2 = (Button)findViewById(R.id.activity_createTravel_gender02_btn);
        travelGenderBtn3 = (Button)findViewById(R.id.activity_createTravel_gender03_btn);

        travelGenderBtn1.setOnClickListener(createOnclickListener);
        travelGenderBtn2.setOnClickListener(createOnclickListener);
        travelGenderBtn3.setOnClickListener(createOnclickListener);


        //참여연령 seekbar
        ageBar = (RangeBar) findViewById(R.id.activity_createTravel_ageRange_seekbar);
        ageBar.setOnRangeBarChangeListener(this);

        //참여연령 텍스트뷰
        travelAgeTextView = (TextView)findViewById(R.id.activity_createTravel_ageBar_TextView);

        //여행설명 에딧텍스트
        travelBriefingEditText = (EditText)findViewById(R.id.activity_createTravel_travelBriefing_Edittext);

        //등록하기 버튼
        travelCreateBtn = (Button)findViewById(R.id.activity_createTravel_createBtn);
        travelCreateBtn.setOnClickListener(createOnclickListener);

        //사진찍기 이미지뷰
        takePicImg01 = (ImageView)findViewById(R.id.activity_createTravel_takePic01_ImageView);
        takePicImg02 = (ImageView)findViewById(R.id.activity_createTravel_takePic02_ImageView);
        takePicImg03 = (ImageView)findViewById(R.id.activity_createTravel_takePic03_ImageView);
        takePicImg04 = (ImageView)findViewById(R.id.activity_createTravel_takePic04_ImageView);

        //사진 이미지뷰 온클릭 리스너
        takePicImg01.setOnClickListener(createOnclickListener);
        takePicImg02.setOnClickListener(createOnclickListener);
        takePicImg03.setOnClickListener(createOnclickListener);
        takePicImg04.setOnClickListener(createOnclickListener);

        //사진 경로, 이름 기본값 설정
        picNameList = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            picNameList.add(i, "noPicture");
        }

        picPathList = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            picPathList.add(i, "noPicture");
        }

        //권한 설정
        pmsCheck = new permissionCheck(CreateTravel.this);


    }



    @Override
    protected void onResume() {
        super.onResume();

    }




    //글 등록 전 예외처리
    public void exceptionCheck() throws JSONException {

        // 여행주제 예외처리
        if((travelTitleEditText.getText().toString()).length() == 0 || travelTitleEditText.getText().toString() == null
                || ((travelTitleEditText).getText().toString()).trim().equals("")){
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

        if((travelPlaceEditText.getText().toString()).length() == 0 || travelPlaceEditText.getText().toString() == null
                || ((travelPlaceEditText).getText().toString()).trim().equals("")){
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

        if((travelCostEditText.getText().toString()).length() == 0 || travelCostEditText.getText().toString() == null
                || travelCostEditText.getText().toString().trim().equals("")){
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
                        // 촬영할 사진 저장할 빈공간 생성
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (photoFile != null){
                            Toast.makeText(this, "분기 체크 3", Toast.LENGTH_SHORT).show();
                            photoURI = FileProvider.getUriForFile(this,"com.teamnova.jaepark.travelmate.activities",photoFile);
                            Log.i("photoFile", photoFile.toString());
                            Log.i("photoURI", photoURI.toString());
//                          photoURI는 새로 저장하기 위한 파일의 URI
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


    public File createImageFile() throws IOException {  //이 함수와 매니페스트의 프로바이더, 그리고 xml의 path를 함께 맞도록 설정해야함
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
        //Toast.makeText(this, "새 파일명은 " + imageFileName + " 입니다", Toast.LENGTH_SHORT).show();

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
        if (isAlbum == false){  //크롭할 이미지가 직접 찍은 이미지인 경우
            Log.i("cropImage", "isAlbum == false");
           cropIntent.putExtra("output", photoURI);    //크랍된 이미지를 해당 경로에 저장
        } else if(isAlbum == true){ //크롭할 이미지가 앨범에서 가져온 이미지인 경우
            Log.i("cropImage", "isAlbum == true");
            cropIntent.putExtra("output", albumURI);    //크랍된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }


    //동기화 - 갤러리의 새로고침 ACTION_MEDIA_MOUNTED는 하나의 폴더, FILE은 하나의 파일을 새로 고침할 때 사용함
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
                            break;
                        case (2) :
                            takePicImg02.setImageBitmap(myBitmap);
                            picPathList.set(1, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(1, imageFileName);      //서버에 저장될 파일 이름
                            break;
                        case (3) :
                            takePicImg03.setImageBitmap(myBitmap);
                            picPathList.set(2, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(2, imageFileName);      //서버에 저장될 파일 이름
                            break;
                        case (4) :
                            takePicImg04.setImageBitmap(myBitmap);
                            picPathList.set(3, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
                            picNameList.set(3, imageFileName);      //서버에 저장될 파일 이름
                            break;
                    }

                }
                // 업로드
                //uploadFile(mCurrentPhotoPath);
            }break;

        }

    }


    public void uploadFile(String filePath){
        String url = "http://poyapo123.cafe24.com/TMphp/fileUpload.php";

        UploadFile uploadFile = new UploadFile(CreateTravel.this) {
            @Override
            protected void onPostExecute(String s) {
                // 성공실패에 따른 토스트 작성
                if (s.equals("Success")){
                    //Toast.makeText(CreateTravel.this, "성공적으로 업로드 하였습니다", Toast.LENGTH_SHORT).show();
                    picUploadCheck = true;
                }else if(s.equals("Fail")){
                    Toast.makeText(CreateTravel.this, "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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





    public View.OnClickListener createOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case (R.id.activity_createTravel_ImgView_backkey) : {
                    startActivity(new Intent(CreateTravel.this, MenuActivity.class));
                    finish();
                }break;

                case(R.id.activity_createTravel_Date_TextView):{    //여행일정 텍스트뷰
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            CreateTravel.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }break;

                case (R.id.activity_createTravel_gender01_btn): {   // '여자만'버튼
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "female";
                    genderCheck = true;
                    Log.e("버튼1클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_createTravel_gender02_btn): {   //  '남자만' 버튼
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "male";
                    genderCheck = true;
                    Log.e("버튼2클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_createTravel_gender03_btn): {   // '성별무관' 버튼
                    travelGenderBtn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_yellow));
                    travelGenderBtn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));
                    travelGenderBtn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_button_gray));

                    genderJs = "all";
                    genderCheck = true;
                    Log.e("버튼3클릭 : ",  "클릭됨");
                }break;

                case (R.id.activity_createTravel_createBtn) : {

                    try {

                        exceptionCheck();   /* 에외처리 검사 및 이상이 없으면 Json오브젝트 완성*/


                        if (titleCheck == true && dateCheck == true && placeCheck == true && maxpersonCheck == true
                                && costCheck == true && genderCheck == true && ageCheck == true && briefingCheck == true
                                && pictureCheck  == true && userIdCheck == true){   //외의 예외처리가 모두 되어야만 사진업로드

                            for (int i = 0; i < 4; i++){
                                if (picUploadCheck == true){    //중간에 업로드가 실패하면 뒤의 업로드 실행 안함
                                    if (!picPathList.get(i).equals("noPicture")){
                                        uploadFile(picPathList.get(i));
                                    }
                                }
                            }
                        }else{

                        }

                        if (travelInfo_Json != null && picUploadCheck == true){   // Json 파일 완성 및 사진이 모두 잘 올라갔을떄 http통신 시작
                            //Toast.makeText(getApplicationContext(), "Json완성", Toast.LENGTH_SHORT).show();

                            final String mTaskID = "travelInfo_register";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                            final String mStrUrl = "travelInfo_register.php";
                            final int mDelay = 0;

                            //사진업로드를 먼저하고 성공시 데이터 베이스에 나머지 정보를 입력해야한다

                            TWDhttpATask travelInfoRegister = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                                @Override
                                protected void onPostExecute(JSONObject fromServerData) {
                                    try {
                                        final JSONObject result = new JSONObject(fromServerData.toString());
                                        //Toast.makeText(getApplicationContext(), result.getString("task") + " : " + result.get("result"), Toast.LENGTH_SHORT).show();
                                        if (result.get("result").toString().equals("success")){
                                            // 서버에 해당 여행이 성공적으로 등록된 경우 여행의 인덱스를 가져옴
                                            int travelIndx = result.getInt("seq");

                                            //이하 소켓 통신으로 해당 방 생성 요청(객체와 메시지&읽음 db테이블 생성)
                                            ChatClient client = ChatClient.getInstance();                                                //채팅 소켓 연결
                                            String userID = callSprf.getPreferences("memberInfo", "id");
                                            String user_nickname = callSprf.getPreferences("memberInfo", "nickname");
                                            int user_idx = Integer.parseInt(callSprf.getPreferences("memberInfo", "idx"));

                                            if(!client.isConnected() && !userID.equals("") && !user_nickname.equals(""))
                                            {
                                                client.setInitial(getApplicationContext());
                                                client.connect();
                                            }

                                            String place = travelInfo_Json.getString("place");
                                            String roomID = String.valueOf(travelIndx);
                                            String roomName = place + ", " + user_nickname;

                                            Message createRoomMsg = new Message();
                                            createRoomMsg.setSender_idx(user_idx)
                                                    .setSenderID(userID)
                                                    .setSender_nickname(user_nickname)
                                                    .setRoomID(roomID)
                                                    .setRoom_idx(travelIndx)
                                                    .setRoomName(roomName)
                                                    .setSystemMessage().setSystemType(Message.SystemMessage.ENTER);

                                            client.sendMsg(createRoomMsg);

                                            //결과 알림 밑 액티비티 이동
                                            Toast.makeText(getApplicationContext(), "나의 여행이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(CreateTravel.this, MenuActivity.class));
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "문제가 생겨 여행을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            travelInfoRegister.execute(travelInfo_Json);

                        }else if(!picUploadCheck){
                            Toast.makeText(getApplicationContext(), "사진등록에 문제가 생겨 여행을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }break;

                //이미지뷰 버튼 온클릭 리스너
                case (R.id.activity_createTravel_takePic01_ImageView) : {
                    final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                    currentPickedImgNO = 1;

                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateTravel.this);     // 여기서 this는 Activity의 this

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
                case (R.id.activity_createTravel_takePic02_ImageView) : {
                    
                    if (picPathList.get(0).equals("noPicture")){
                        Toast.makeText(CreateTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 2;

                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTravel.this);     // 여기서 this는 Activity의 this

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

                case (R.id.activity_createTravel_takePic03_ImageView) : {

                    if (picPathList.get(1).equals("noPicture")){
                        Toast.makeText(CreateTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 3;

                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTravel.this);     // 여기서 this는 Activity의 this

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

                case (R.id.activity_createTravel_takePic04_ImageView) : {

                    if (picPathList.get(2).equals("noPicture")){
                        Toast.makeText(CreateTravel.this, "왼쪽부터 순서대로 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                        currentPickedImgNO = 4;

                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTravel.this);     // 여기서 this는 Activity의 this

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
