package com.teamnova.jaepark.travelmate.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;
import com.teamnova.jaepark.travelmate.activities.functionalClass.UploadFile;
import com.teamnova.jaepark.travelmate.activities.functionalClass.permissionCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditInfoActivity extends AppCompatActivity {

    //SharedPreference
    private CallSharedPreference callSprf;

    //프로필 사진 이미지뷰
    ImageView profilePicImgView;

    // 닉네임 텍스트뷰
    EditText nicknameET;

    // 생일 텍스트뷰
    TextView birthTV;

    // 생일 수정버튼
    Button birthEditBtn;

    //성별 텍스트뷰
    TextView genderTV;

    //성별 수정버튼
    Button genderEditBtn;

    //최종 '수정하기' 버튼
    Button editConfirmBtn;

    //좌상단 뒤로가기 이미지뷰
    ImageView backImgView;


    //닉네임, 생일 성별 스트링 변수
    String nickname, birth, gender, userId= "";

    // 프로필 사진 서버 파일 이름
    String profilePic;

    int mYear, mMonth, mDay;
    String sYear, sMonth, sDay;



    boolean picUploadCheck = true;

    // 사진 업로드 관련 변수
    permissionCheck pmsCheck;

    Uri photoURI, albumURI;
    boolean isAlbum = false;    //Crop시 사진을 직접 찍을 것인지 앨범에서 가져온 것인지 확인하는 플래그
    String mCurrentPhotoPath, imageFileName;

    int currentPickedImgNO; //현재 선택하고자 하는 이미지뷰를 알려주기 위한 변수

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();


        //좌상단 뒤로가기 이미지뷰
        backImgView = (ImageView) findViewById(R.id.activity_EditInfo_backkey_ImgView);
        backImgView.setOnClickListener(editOnClickListener);

        //프로필 사진 이미지뷰
        profilePicImgView = (ImageView) findViewById(R.id.activity_EditInfo_profilePic_ImgView);
        profilePicImgView.setOnClickListener(editOnClickListener);

        // 닉네임 텍스트뷰
        nicknameET = (EditText) findViewById(R.id.activity_EditInfo_nickname_ET);
        nicknameET.setOnClickListener(editOnClickListener);

        // 생일 텍스트뷰
        birthTV = (TextView) findViewById(R.id.activity_EditInfo_birth_TV);
        birthTV.setOnClickListener(editOnClickListener);

        // 생일 수정버튼
        birthEditBtn = (Button) findViewById(R.id.activity_EditInfo_birth_Btn);
        birthEditBtn.setOnClickListener(editOnClickListener);

        //성별 텍스트뷰
        genderTV = (TextView) findViewById(R.id.activity_EditInfo_gender_TV);
        genderTV.setOnClickListener(editOnClickListener);

        //성별 수정버튼
        genderEditBtn = (Button) findViewById(R.id.activity_EditInfo_gender_Btn);
        genderEditBtn.setOnClickListener(editOnClickListener);

        //최종 '수정하기' 버튼
        editConfirmBtn = (Button) findViewById(R.id.activity_EditInfo_editConfirm_Btn);
        editConfirmBtn.setOnClickListener(editOnClickListener);


        //기존 초기값 설정

        nickname = callSprf.getPreferences("memberInfo", "nickname");
        birth = callSprf.getPreferences("memberInfo", "birth");
        String birthYear = birth.substring(0, 4);
        String birthMonth = birth.substring(4, 6);
        String birthDay = birth.substring(6, 8);
        gender = callSprf.getPreferences("memberInfo", "gender");


        //초기값 뿌려주기
        nicknameET.setText(nickname);

        birthTV.setText(birthYear + "년" + birthMonth + "월" + birthDay + "일");

        if (gender.equals("male")) {
            genderTV.setText("남성");
        } else if (gender.equals("여성")) {
            genderTV.setText(gender);
        } else if (gender.equals("unknown")) {
            genderTV.setText("기타 성별");
        }


        //http 통신을 통한 개인정보 수신

        userId = callSprf.getPreferences("memberInfo", "id");

        //요청코드 작성
        JSONObject requestCode = new JSONObject();

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

                    if (ja.get("result").toString().equals("Success")) {
                        //통신을 통해 가져온 정보를 전역변수에 담기

                        profilePic = ja.get("profilePic").toString();

                        //받아온 정보를 뷰에 뿌려주는 메소드
                        settingView();

                    } else {
                        Log.e("getMemberInfo Fail", fromServerData.get("result").toString());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //통신실행을 시작하는 메소드
        getUserInfo.execute(requestCode);

        //권한 설정
        pmsCheck = new permissionCheck(EditInfoActivity.this);

    }




    private void settingView() {

        //프로필 사진을 글라이드로 불러오기

        if (profilePic.equals("noPicture")){    //기본사진 등록이 안되어 있을 경우
            Glide.with(this)
                    .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_basicPic.jpg")
                    .bitmapTransform(new CropCircleTransformation(this))
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(profilePicImgView);
        }else{
            Glide.with(this)
                    .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + profilePic)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .into(profilePicImgView);
        }



    }



    View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){


                case(R.id.activity_EditInfo_birth_Btn) : {    //생일 수정 버튼

                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(EditInfoActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    sYear = String.valueOf(year);

                                    if ((monthOfYear + 1) < 10){
                                        sMonth = "0" + (monthOfYear + 1);
                                    }else{
                                        sMonth = "" + (monthOfYear + 1);
                                    }

                                    if (dayOfMonth < 10){
                                        sDay = "0" + dayOfMonth;
                                    }else{
                                        sDay = dayOfMonth + "";
                                    }

                                    birthTV.setText(sYear + "년" + sMonth  + "월" + sDay+ "일");
                                    birth = sYear + sMonth + sDay;


                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();

                }break;

                case(R.id.activity_EditInfo_gender_Btn) : {    //성별 수정 버튼
                    final CharSequence[] items = { "여성", "남성", "그외"};
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditInfoActivity.this);

                    // 제목셋팅
                    alertDialogBuilder.setTitle("성별선택");
                    alertDialogBuilder.setSingleChoiceItems(items, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    if(items[id].equals("남성")){
                                        genderTV.setText("남성");
                                        gender = "male";
                                        Toast.makeText(getApplicationContext(), items[id] + " 선택했습니다.", Toast.LENGTH_SHORT).show();

                                    }else if(items[id].equals("여성")){
                                        genderTV.setText("여성");
                                        gender = "female";
                                        Toast.makeText(getApplicationContext(), items[id] + " 선택했습니다.", Toast.LENGTH_SHORT).show();

                                    }else if(items[id].equals("그외")){
                                        genderTV.setText("그외");
                                        gender = "unknown";
                                        Toast.makeText(getApplicationContext(), items[id] + " 선택했습니다.", Toast.LENGTH_SHORT).show();

                                    }

                                    dialog.dismiss();
                                }
                            });

                    // 다이얼로그 생성
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // 다이얼로그 보여주기
                    alertDialog.show();

                }break;

                case(R.id.activity_EditInfo_editConfirm_Btn) : {    //최종 수정 버튼

                    nickname = nicknameET.getText().toString();
                    userId = callSprf.getPreferences("memberInfo", "id");

                    final String mTaskID = "editInfo";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                    final String mStrUrl = "editInfo.php";
                    final int mDelay = 0;


                    JSONObject editInfo_Json = new JSONObject();

                    try {
                        editInfo_Json.put("requestCode", "editInfo");
                        editInfo_Json.put("nickname", nickname);
                        editInfo_Json.put("birth", birth);
                        editInfo_Json.put("gender", gender);
                        editInfo_Json.put("userId", userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //사진업로드를 먼저하고 성공시 데이터 베이스에 나머지 정보를 입력해야한다

                    TWDhttpATask editInfoTask = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                        @Override
                        protected void onPostExecute(JSONObject fromServerData) {
                            try {
                                JSONObject result = new JSONObject(fromServerData.toString());
                                //Toast.makeText(getApplicationContext(), result.getString("task") + " : " + result.get("result"), Toast.LENGTH_SHORT).show();
                                if (result.get("result").equals("success")){
                                    Toast.makeText(getApplicationContext(), "나의 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(EditInfoActivity.this, MenuActivity.class);
                                    intent.putExtra("fragment", "MenuFragment_03");
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "문제가 생겨 정보를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    editInfoTask.execute(editInfo_Json);


                }break;


                case (R.id.activity_EditInfo_profilePic_ImgView) :{ //프로필 이미지 클릭시 수정
                    final CharSequence[] items = {"사진찍기", "앨범에서 가져오기"};
                    currentPickedImgNO = 1;

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoActivity.this);     // 여기서 this는 Activity의 this

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


                case (R.id.activity_EditInfo_backkey_ImgView) : {   //좌상단 뒤로가기 버튼
                    Intent intent = new Intent(EditInfoActivity.this, MenuActivity.class);
                    intent.putExtra("fragment", "MenuFragment_03");
                    startActivity(intent);
                    finish();
                }break;


            }

        }
    };




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:{
                Intent intent = new Intent(EditInfoActivity.this, MenuActivity.class);
                intent.putExtra("fragment", "MenuFragment_03");
                startActivity(intent);
                finish();
            } break;
        }
        return true;
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
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "profile_"+ userId + ".jpg";
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
//                Toast.makeText(this, "크랍이미지", Toast.LENGTH_SHORT).show();
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

//                //비트맵 썸네일 세팅
//                if(imgFile.exists()){
//                    Log.i("imgFile", "CALL");
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//
//                            profilePicImgView.setImageBitmap(myBitmap);
////                            picPathList.set(0, mCurrentPhotoPath);  //업로드 하기위한 파일 경로
////                            picNameList.set(0, imageFileName);      //서버에 저장될 파일 이름
//
//
//                }
                // 업로드
                uploadFile(mCurrentPhotoPath);
            }break;

        }

    }


    public void uploadFile(String filePath){
        String url = "http://poyapo123.cafe24.com/TMphp/fileUpload.php";

        UploadFile uploadFile = new UploadFile(EditInfoActivity.this) {
            @Override
            protected void onPostExecute(String s) {
                // 성공실패에 따른 토스트 작성
                if (s.equals("Success")){
                    //Toast.makeText(CreateTravel.this, "성공적으로 업로드 하였습니다", Toast.LENGTH_SHORT).show();
                    picUploadCheck = true;

                    //회원정보에 프로필 사진 파일 이름 등록하기
                    profilePicPathRegister();

                    Glide.with(EditInfoActivity.this)
                            .load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_profile_" + userId + ".jpg")
                            .bitmapTransform(new CropCircleTransformation(EditInfoActivity.this))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .into(profilePicImgView);

                }else if(s.equals("Fail")){
                    Toast.makeText(EditInfoActivity.this, "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    picUploadCheck = false;
                }

            }
        };
        uploadFile.setPath(filePath);
        uploadFile.execute(url);
    }

    private void profilePicPathRegister() {

        userId = callSprf.getPreferences("memberInfo", "id");


        final String mTaskID = "profilePicPathRegister";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
        final String mStrUrl = "profilePicPathRegister.php";
        final int mDelay = 0;



        JSONObject editInfo_Json = new JSONObject();

        try {
            editInfo_Json.put("requestCode", "profilePicPathRegister");

            editInfo_Json.put("userId", userId);
            editInfo_Json.put("profilePicName", "profile_" + userId + ".jpg");

        } catch (JSONException e) {
            e.printStackTrace();
        }



        //사진업로드를 먼저하고 성공시 데이터 베이스에 나머지 정보를 입력해야한다

        TWDhttpATask editInfoTask = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
            @Override
            protected void onPostExecute(JSONObject fromServerData) {
                try {
                    JSONObject result = new JSONObject(fromServerData.toString());
                    //Toast.makeText(getApplicationContext(), result.getString("task") + " : " + result.get("result"), Toast.LENGTH_SHORT).show();
                    if (result.get("result").equals("success")){
                        Toast.makeText(getApplicationContext(), "사진정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(), "문제가 생겨 사진정보를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        editInfoTask.execute(editInfo_Json);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }



}
