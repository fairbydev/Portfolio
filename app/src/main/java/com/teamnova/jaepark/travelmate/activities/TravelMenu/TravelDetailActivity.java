package com.teamnova.jaepark.travelmate.activities.TravelMenu;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatRoomActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.PicturePagerAdapter;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TravelDetailActivity extends AppCompatActivity {

    //SharedPreference
    private CallSharedPreference callSprf;

    //넘겨받은 여행정보 JSON String
    String mTravelInfo;

    //이미지 뷰페이저
    ViewPager mImgViewpager;

    //Bind the title indicator to the adapter
    CirclePageIndicator mCirclePageIndicator;

    //사진정보 어레이리스트
    ArrayList mPicAddressAL = new ArrayList();

    //해당 여행 디테일을 첫 조회한 시간
    String mHitTime;

    //서버로부터 조회한 여형정보를 담을 여행 정보 변수들(mUserId는 현재 앱사용자가 아닌 해당 여행을 등록한 사용자의 아이디)
    String mSeq, mTitle, mStartDate, mEndDate, mPlace, mMaxPerson, mCost, mGender,  mFromAge, mToAge, mBrief,
            mUserId, mEnterTime, mCreatedTime;

    //현재 해당 기기를 사용하고 있는 앱사용자 아이디
    String mCurrentUserId;

    //여행정보를 표시하는 텍스트뷰
    TextView mTitleTV, mDateTV, mPlaceTV, mMaxPensonTV, mCostTV, mGenderTV, mAgeTV, mBriefingTV;


    //여행정보 수정 및 삭제버튼
    Button mEditButton;

    //채팅 참여하기 버튼
    Button mChatButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

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


            //Hit 중복 체크
            mHitTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());   //디테일 액티비티 진입 시각
            mCurrentUserId = callSprf.getPreferences("memberInfo","id");

            // Hit 중복 체크를 위한 http 통신연결
            String mTaskID = "hitCheck(TRAVEL)";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
            String mStrUrl = "hitCheck.php";
            int mDelay = 0;

            //요청코드 작성
            JSONObject requestCode = new JSONObject();

            requestCode.put("currentUserId", mCurrentUserId);
            requestCode.put("travelSeq", mSeq);
            requestCode.put("hitTime", mHitTime);


            TWDhttpATask hitCheck = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
                @Override
                protected void onPostExecute(JSONObject fromServerData) {
//                    try {
//                        //Log.e("hitCheck", fromServerData.get("result").toString());
////                        JSONObject ja = new JSONObject(fromServerData.toString());
////                        Log.e("hitCheck", ja.get("result").toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            };

            hitCheck.execute(requestCode);

            //사진정보 어레이리스트 생성
            for (int i = 0; i < 4; i++){
                if (!travelInfoJson.get("picture0" + (i+1)).equals("noPicture")){
                    mPicAddressAL.add(travelInfoJson.get("picture0" + (i+1)));
                }
                Log.e("pictureArraySize", mPicAddressAL.size() + "");
            }

            //사용자가 별도의 사진등록을 하지 않았을 경우 디폴트 사진값을 넣어준다
            if (mPicAddressAL.size() == 0){
                mPicAddressAL.add("basicPic.jpg");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        //이미지 뷰페이저
        mImgViewpager = (ViewPager) findViewById(R.id.activity_travelDetail_ImgViewpager);

        //어댑터에 생성할 페이저 페이지 총 갯수 밑 각 사진 주소 세팅
        PicturePagerAdapter adapter = new PicturePagerAdapter(getSupportFragmentManager());
        adapter.setPictureInfo(mPicAddressAL.size(), mPicAddressAL);


        //ViewPager에 Adapter 설정
        mImgViewpager.setAdapter(adapter);


        //Bind the title indicator to the adapter
        mCirclePageIndicator = (CirclePageIndicator)findViewById(R.id.activity_travelDetail_indicator_indicator);
        mCirclePageIndicator.setViewPager(mImgViewpager);

        //여행정보를 표시하는 텍스트뷰 참조
        mTitleTV = (TextView)findViewById(R.id.activity_travelDetail_title_TV);
        mDateTV  = (TextView)findViewById(R.id.activity_travelDetail_date_TV);
        mPlaceTV = (TextView)findViewById(R.id.activity_travelDetail_place_TV);
        mMaxPensonTV  = (TextView)findViewById(R.id.activity_travelDetail_maxPerson_TV);
        mCostTV  = (TextView)findViewById(R.id.activity_travelDetail_cost_TV);
        mGenderTV  = (TextView)findViewById(R.id.activity_travelDetail_gender_TV);
        mAgeTV  = (TextView)findViewById(R.id.activity_travelDetail_age_TV);
        mBriefingTV  = (TextView)findViewById(R.id.activity_travelDetail_briefing_TV);

        //여행정보를 표시하는 텍스트뷰에 서버로부터 받은 정보 세팅
        mTitleTV.setText(mTitle);
        String startYear = mStartDate.substring(0,4);
        String startMonth = mStartDate.substring(4,6);
        String startDay = mStartDate.substring(6,8);
        String startDate = startYear + "." + startMonth + "." + startDay + ".";

        String endYear = mEndDate.substring(0,4);
        String endMonth = mEndDate.substring(4,6);
        String endDay = mEndDate.substring(6,8);
        String endDate = endYear + "." + endMonth + "." + endDay + ".";

        mDateTV.setText("기간 : " + startDate + " ~ " + endDate);
        mPlaceTV.setText("장소 : " + mPlace);

        switch (mMaxPerson){
            case ("2") :{
                mMaxPensonTV.setText("인원 : 단둘이서");
            }break;
            case ("3to5") :{
                mMaxPensonTV.setText("인원 : 3 ~ 5인");
            }break;
            case ("6to10") :{
                mMaxPensonTV.setText("인원 : 6 ~ 10인");
            }break;
            case ("from11") :{
                mMaxPensonTV.setText("인원 : 10인 초과");
            }break;
            default:{
                mMaxPensonTV.setText("인원제한없음");
            }break;
        }

        mCostTV.setText("경비 : " + mCost + "원(KRW) 이상");

        switch (mGender){
            case ("male") :{
                mGenderTV.setText("성별 : 남자끼리");
            }break;
            case ("female") :{
                mGenderTV.setText("성별 : 여자끼리");
            }break;
            case ("all") :{
                mGenderTV.setText("셩별무관");
            }break;
            default:{
                mMaxPensonTV.setText("성별무관");
            }break;
        }

        mAgeTV.setText("연령 : " + mFromAge + "세부터 " + mToAge + "세까지");
        mBriefingTV.setText(mBrief);



        //여행정보 수정 및 삭제버튼 참조
        mEditButton = (Button) findViewById(R.id.activity_travelDetail_edit_Button);
            //Toast.makeText(this, "mCurrentUserId = " + mCurrentUserId + ", mUserId = " + mUserId, Toast.LENGTH_SHORT).show();

        if (!mCurrentUserId.equals(mUserId)){ //현재 보고있는 여행의 등록자가 아니면 수정 및 삭제 버튼이 보이지 않게 함
            mEditButton.setVisibility(View.GONE);        }

        mEditButton.setOnClickListener(new View.OnClickListener() { // 수정 및 삭제 앹기비티로 이동 및 여행 정보 Json String 파일 인텐트로 전송
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditTravel.class);
                intent.putExtra("travelinfo", mTravelInfo);
                startActivity(intent);
            }
        });


        //채팅 참여하기 버튼
        mChatButton =  (Button) findViewById(R.id.activity_travelDetail_chat_Button);
        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //여행 호스트 닉네임 불러오기
                String mTaskID2 = "requestHostNickname";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                String mStrUrl2 = "getHostNickname.php";
                int mDelay2 = 0;

                //host nickname 요청코드 작성
                JSONObject requestHostNickname = new JSONObject();

                try {
                    requestHostNickname.put("hostId", mUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TWDhttpATask getHostNickname = new TWDhttpATask(mTaskID2, mStrUrl2, mDelay2) {
                    @Override
                    protected void onPostExecute(JSONObject fromServerData) {
                        try
                        {
                            Log.d("requestHostNickname", fromServerData.get("result").toString());
                            JSONObject resultJO = new JSONObject(fromServerData.toString());
                            Log.d("requestHostNickname", resultJO.get("result").toString());
                            Log.e("requestHostNickname", "hostNickname = " + resultJO.get("hostNickname").toString());
                            String hostNickname = resultJO.get("hostNickname").toString();

                            JSONObject updatedTravelJO = new JSONObject(mTravelInfo);
                            updatedTravelJO.put("hostNickname", hostNickname);
                            String updatedTravelInfo = updatedTravelJO.toString();

                            Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
//                            intent.putExtra("travelinfo", mTravelInfo);
                            intent.putExtra("travelinfo", updatedTravelInfo);

                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };

                getHostNickname.execute(requestHostNickname);

            }
        });


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) { //back key 이벤트
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:{
                Intent intent = new Intent(TravelDetailActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();   //해당 액티비티를 피니쉬하고 다시 메인액티비티로 이동해야 hit가 업데이트 된 여행 리스트를 갱신하여 표시함
            } break;
        }
        return true;
    }


}
