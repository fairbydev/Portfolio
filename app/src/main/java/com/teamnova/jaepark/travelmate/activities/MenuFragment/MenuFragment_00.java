package com.teamnova.jaepark.travelmate.activities.MenuFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.TravelMenu.CreateTravel;
import com.teamnova.jaepark.travelmate.activities.MenuActivity;
import com.teamnova.jaepark.travelmate.activities.TravelMenu.TravelDetailActivity;
import com.teamnova.jaepark.travelmate.activities.functionalClass.ListViewAdapter_travelList;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;



    //여행리스트 프래그먼트 클래스
public class MenuFragment_00 extends Fragment implements DatePickerDialog.OnDateSetListener, AbsListView.OnScrollListener{

    private CallSharedPreference callSprf;


    private LayoutInflater mInflater;

    ImageView createTravelImgView;

    public static ListViewAdapter_travelList travelListViewAdapter;
    public ListView travelListView;
    public  ArrayList<String> listViewItemList = new ArrayList<>();
    JSONObject requestCode;

    //검색조건
    String filterWord, filterPlace, filterStartDate, filterEndDate, filterMaxPerson, filterGender, filterCost, filterHighAge, filterLowAge;
    //정렬조건
    String sortByPopularity, sortByRegistration;

    //검색 에딧텍스트
    EditText searchET;

    //세부필터 이미지뷰
    ImageView filterImgView;

    //여행일정 선택 리니어 레이아웃
    LinearLayout travelDateLL;

    //여행일정 선택 리니어 레이아웃에 속하는 텍스트뷰
    TextView travelDateTV;

    //여행일정 선택 초기화 하는 이미지뷰
    ImageView travelDateInitImgView;

    //정렬 조건 리니어 레이아웃
    LinearLayout travelSortLL;

    //정렬조건 텍스트 레이아웃
    TextView travelSortTV;

    //리스트뷰 페이징 변수
    private boolean mLockListView;  //페이징 락(Lock)
    private int pagingCount = 0; //몇번째 페이지 요청인지 체크,저장하는 변수
    boolean allResultPrinted = false;  //페이징 락(Lock)
    LinearLayout mFooterView;


    public MenuFragment_00(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment가 화면에 보여질 때 호출됨

        View view = inflater.inflate(R.layout.layout_frag_0, container, false);

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getContext();

        //여행 등록 버튼
        createTravelImgView = (ImageView)view.findViewById(R.id.fragment00_ImageView_createTravel);
        createTravelImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent((MenuActivity)getActivity(), CreateTravel.class));
            }
        });

        //검색 에딧텍스트
        searchET = (EditText) view.findViewById(R.id.fragment_menu_home_searchEditText);

        //세부필터 이미지뷰
        filterImgView = (ImageView) view.findViewById(R.id.fragment_menu_home_filterImgView);


        //여행일정 선택 리니어 레이아웃
        travelDateLL = (LinearLayout) view.findViewById(R.id.fragment_menu_home_travelDateLL);

        //여행일정 선택 리니어 레이아웃에 속하는 텍스트뷰
        travelDateTV = (TextView) view.findViewById(R.id.fragment_menu_home_travelDateTV);

        //여행일정 선택 초기화 하는 이미지뷰
        travelDateInitImgView = (ImageView) view.findViewById(R.id.fragment_menu_home_deleteDate_ImgView);

        //정렬 조건 리니어 레이아웃
        travelSortLL = (LinearLayout) view.findViewById(R.id.fragment_menu_home_sortLinearLayout);

        //정렬조건 텍스트뷰
        travelSortTV = (TextView) view.findViewById(R.id.fragment_menu_home_sortTextView);


        //리스트뷰 선언
        travelListView = (ListView) view.findViewById(R.id.fragment_menu_home_Listview);

        // 푸터를 등록합니다. setAdapter 이전에 해야 합니다.

        mFooterView  = (LinearLayout) view.findViewById(R.id.listview_task_footer);
        //travelListView.addFooterView(mFooterView);
        mFooterView = (LinearLayout) inflater.inflate(R.layout.travel_listview_footer, null);
        travelListView.addFooterView(mFooterView);

        //mInflater = (LayoutInflater) inflater.getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        //travelListView.addFooterView(mInflater.inflate(R.layout.travel_listview_footer, null));


        // ADAPTER 객체 생성 및 데이터 담기
        travelListViewAdapter = new ListViewAdapter_travelList(listViewItemList);   //로딩한 값을 어뎁터에 전달
        travelListViewAdapter.notifyDataSetChanged();
        travelListViewAdapter.update(listViewItemList);


        // 리스트뷰 참조 및 어댑터 달기
        travelListView.setAdapter(travelListViewAdapter);
        travelListView.setOnScrollListener(this);



        // 이하 이벤트 처리 메소드


        // 제목, 도시 검색 에딧텍스트 이벤트 처리
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Toast.makeText(getContext(), "엔터키 눌렀을 경우.", Toast.LENGTH_SHORT).show();

                    searchWithKeyword();

                    return true;
                }
                return false;
            }

        });


        //세부필터 이미지 이벤트 처리
        filterImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), com.teamnova.jaepark.travelmate.activities.travelFilter.ConditionFilterActivity.class));
                MenuFragment_00.super.onDestroyView();
            }
        });


        //  여행일자 선택 이벤트 처리
        travelDateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MenuFragment_00.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                travelDateInitImgView.setVisibility(View.VISIBLE);
            }
        });

        //여행일자 초기화 이미지 클릭 이벤트 처리
        travelDateInitImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelDateInitImgView.setVisibility(View.GONE);

                //여행기간 초기화
                callSprf.savePreferences("filterCondition", "filterStartDate", "notSet");
                callSprf.savePreferences("filterCondition", "filterEndDate", "notSet");
                callSprf.savePreferences("filterCondition", "StringStartDate", "");
                callSprf.savePreferences("filterCondition", "StringEndDate", "");

                //검색시작
                searchWithKeyword();

            }
        });



        // 리스트 정렬 리니어 레이아웃 클릭 이벤트 처리
        travelSortLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = { "최신등록순으로 보기", "인기순으로 보기"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // 제목셋팅
                alertDialogBuilder.setTitle("여행 정렬방식");
                alertDialogBuilder.setSingleChoiceItems(items, -1,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //Toast.makeText(getContext(), items[id] + " 선택했습니다.", Toast.LENGTH_SHORT).show();

                            if(items[id].equals("인기순으로 보기")){
//                                callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
//                                travelSortTV.setText("인기순");
//                                searchWithKeyword();

                                final CharSequence[] items = {"검색기간(미지정시 전체기간)","오늘", "1주", "1개월", "1년"};
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                                // 제목셋팅
                                alertDialogBuilder.setTitle("여행 정렬방식(인기순)");
                                alertDialogBuilder.setSingleChoiceItems(items, -1,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Toast.makeText(getContext(), items[id] + " 선택했습니다.", Toast.LENGTH_SHORT).show();

                                                if(items[id].equals("검색기간(미지정시 전체기간)")){
                                                    callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
                                                    travelSortTV.setText("인기순");
                                                    searchWithKeyword();
                                                    //Toast.makeText(getContext(), "인기순 정렬 시에 여행기간이 검색기간 내에 모두 포함되어야 목록에 나타납니다.", Toast.LENGTH_SHORT).show();

                                                }else if(items[id].equals("오늘")){

                                                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

                                                    Calendar calendar = new GregorianCalendar();
                                                    String filterStartDate = df.format(calendar.getTime());
                                                    String filterEndDate = df.format(calendar.getTime());


                                                    String startDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";
                                                    String endDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";

                                                    travelDateTV.setText(startDate + " ~ " + endDate);
                                                    travelDateInitImgView.setVisibility(View.VISIBLE);

                                                    callSprf.savePreferences("filterCondition", "dateRangeOption", "exclusive"); //여행기간이 최근 1주안에 완전히 속하지 않으면 검색에서 배제
                                                    callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
                                                    callSprf.savePreferences("filterCondition", "filterStartDate", filterStartDate);
                                                    callSprf.savePreferences("filterCondition", "filterEndDate", filterEndDate);
                                                    callSprf.savePreferences("filterCondition", "StringStartDate", startDate);
                                                    callSprf.savePreferences("filterCondition", "StringEndDate", endDate);

                                                    travelSortTV.setText("인기순(오늘)");
                                                    searchWithKeyword();
                                                    Toast.makeText(getContext(), "인기순 정렬 시에 여행기간이 검색기간 내에 모두 포함되어야 목록에 나타납니다.", Toast.LENGTH_SHORT).show();

                                                }else if(items[id].equals("1주")){
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

                                                    Calendar calendar = new GregorianCalendar();

                                                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                                                    String filterStartDate = df.format(calendar.getTime());
                                                    String startDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";

                                                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                                                    String filterEndDate = df.format(calendar.getTime());
                                                    String endDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";

                                                    travelDateTV.setText(startDate + " ~ " + endDate);
                                                    travelDateInitImgView.setVisibility(View.VISIBLE);

                                                    callSprf.savePreferences("filterCondition", "dateRangeOption", "exclusive"); //여행기간이 최근 1주안에 완전히 속하지 않으면 검색에서 배제
                                                    callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
                                                    callSprf.savePreferences("filterCondition", "filterStartDate", filterStartDate);
                                                    callSprf.savePreferences("filterCondition", "filterEndDate", filterEndDate);
                                                    callSprf.savePreferences("filterCondition", "StringStartDate", startDate);
                                                    callSprf.savePreferences("filterCondition", "StringEndDate", endDate);

                                                    travelSortTV.setText("인기순(1주)");
                                                    searchWithKeyword();
                                                    Toast.makeText(getContext(), "인기순 정렬 시에 여행기간이 검색기간 내에 모두 포함되어야 목록에 나타납니다.", Toast.LENGTH_SHORT).show();

                                                }else if(items[id].equals("1개월")){

                                                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

                                                    Calendar calendar = new GregorianCalendar();

                                                    calendar.add(Calendar.MONTH, -1);
                                                    String filterStartDate = df.format(calendar.getTime());
                                                    String startDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";
                                                    calendar.add(Calendar.MONTH, 1);
                                                    String filterEndDate = df.format(calendar.getTime());
                                                    String endDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";

                                                    travelDateTV.setText(startDate + " ~ " + endDate);
                                                    travelDateInitImgView.setVisibility(View.VISIBLE);

                                                    callSprf.savePreferences("filterCondition", "dateRangeOption", "exclusive"); //여행기간이 최근 1주안에 완전히 속하지 않으면 검색에서 배제
                                                    callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
                                                    callSprf.savePreferences("filterCondition", "filterStartDate", filterStartDate);
                                                    callSprf.savePreferences("filterCondition", "filterEndDate", filterEndDate);
                                                    callSprf.savePreferences("filterCondition", "StringStartDate", startDate);
                                                    callSprf.savePreferences("filterCondition", "StringEndDate", endDate);

                                                    travelSortTV.setText("인기순(1개월)");
                                                    searchWithKeyword();
                                                    Toast.makeText(getContext(), "인기순 정렬 시에 여행기간이 검색기간 내에 모두 포함되어야 목록에 나타납니다.", Toast.LENGTH_SHORT).show();


                                                }else if(items[id].equals("1년")){
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

                                                    Calendar calendar = new GregorianCalendar();

                                                    calendar.add(Calendar.YEAR, -1);
                                                    String filterStartDate = df.format(calendar.getTime());
                                                    String startDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";
                                                    calendar.add(Calendar.YEAR, 1);
                                                    String filterEndDate = df.format(calendar.getTime());
                                                    String endDate = calendar.get(Calendar.YEAR) + "년" + (calendar.get(Calendar.MONTH) + 1) + "월" + calendar.get(Calendar.DAY_OF_MONTH) + "일";

                                                    travelDateTV.setText(startDate + " ~ " + endDate);
                                                    travelDateInitImgView.setVisibility(View.VISIBLE);

                                                    callSprf.savePreferences("filterCondition", "dateRangeOption", "exclusive"); //여행기간이 최근 1주안에 완전히 속하지 않으면 검색에서 배제
                                                    callSprf.savePreferences("filterCondition", "sortingParameter", "hit");
                                                    callSprf.savePreferences("filterCondition", "filterStartDate", filterStartDate);
                                                    callSprf.savePreferences("filterCondition", "filterEndDate", filterEndDate);
                                                    callSprf.savePreferences("filterCondition", "StringStartDate", startDate);
                                                    callSprf.savePreferences("filterCondition", "StringEndDate", endDate);

                                                    travelSortTV.setText("인기순(1년)");
                                                    searchWithKeyword();
                                                    Toast.makeText(getContext(), "인기순 정렬 시에 여행기간이 검색기간 내에 모두 포함되어야 목록에 나타납니다.", Toast.LENGTH_SHORT).show();

                                                }

                                                dialog.dismiss();
                                            }
                                        });

                                // 다이얼로그 생성
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // 다이얼로그 보여주기
                                alertDialog.show();



                            }else if(items[id].equals("최신등록순으로 보기")){
                                callSprf.savePreferences("filterCondition", "sortingParameter", "time");
                                travelSortTV.setText("최신등록순");
                                searchWithKeyword();
                                //Toast.makeText(getContext(), "등록순 정렬 시 여행기간의 일부만 포함되어도 목록에 나타납니다.", Toast.LENGTH_SHORT).show();

                            }

                            dialog.dismiss();
                        }
                    });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기
                alertDialog.show();

            }
        });


        //리스트뷰 온클릭 리스너
        travelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(getContext(), "" + listViewItemList.get(position), Toast.LENGTH_SHORT).show();
//
//                try {
//                    JSONObject travelInfoJson = new JSONObject(listViewItemList.get(position));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Intent intent = new Intent(getActivity(), TravelDetailActivity.class);
                intent.putExtra("travelinfo", listViewItemList.get(position));
                startActivity(intent);

            }
        });

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

         //Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();


        //검색조건 sharedPreference 체크 및 갱신(세팅이 안되있을 경우 기본값으로 "notSet"설정)
        filterWord = callSprf.getPreferencesForSearch("filterCondition", "filterWord");
        filterPlace = callSprf.getPreferencesForSearch("filterCondition", "filterPlace");
        filterStartDate = callSprf.getPreferencesForSearch("filterCondition", "filterStartDate");
        filterEndDate = callSprf.getPreferencesForSearch("filterCondition", "filterEndDate");
        filterMaxPerson = callSprf.getPreferencesForSearch("filterCondition", "filterMaxPerson");
        filterGender = callSprf.getPreferencesForSearch("filterCondition", "filterGender");
        filterCost = callSprf.getPreferencesForSearch("filterCondition", "filterCost");
        filterHighAge = callSprf.getPreferencesForSearch("filterCondition", "filterHighAge");
        filterLowAge = callSprf.getPreferencesForSearch("filterCondition", "filterLowAge");

        //소팅 기준을 최신순으로 설정(디폴트값)
        callSprf.savePreferences("filterCondition", "sortingParameter", "time");

        //필터 이미지뷰 버튼 색 변경

        if (callSprf.getPreferences("filterCondition", "filterApplied").equals("true")){
            filterImgView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_button_black));
            filterImgView.setImageResource(R.drawable.ic_tune_white_24dp);
            //Toast.makeText(getContext(), "filterApplied true", Toast.LENGTH_SHORT).show();

        }else if(callSprf.getPreferences("filterCondition", "filterApplied").equals("false")){
            filterImgView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_button_white));
            filterImgView.setImageResource(R.drawable.ic_tune_black_24dp);
            //Toast.makeText(getContext(), "filterApplied false", Toast.LENGTH_SHORT).show();
        }





        //case 1. 액티비티 처음 진입시 필터설정 및 여행기간 설정이 안되어 있을 경우 전체를 불러옴
        if (callSprf.getPreferences("filterCondition", "filterApplied").equals("false") &&
                callSprf.getPreferences("filterCondition", "filterStartDate").equals("notSet")
                && callSprf.getPreferences("filterCondition", "filterEndDate").equals("notSet"))
        {

//            Toast.makeText(getContext(), "filterApplied false && Time not Set", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), callSprf.getPreferences("filterCondition", "filterPlace"), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), "리스트뷰 로딩 케이스 1-1", Toast.LENGTH_SHORT).show();
            searchWithKeyword();
        }else if(callSprf.getPreferences("filterCondition", "filterApplied").equals("") &&
                callSprf.getPreferences("filterCondition", "filterStartDate").equals("notSet")
                && callSprf.getPreferences("filterCondition", "filterEndDate").equals("notSet")){

//            Toast.makeText(getContext(), "리스트뷰 로딩 케이스 1-2", Toast.LENGTH_SHORT).show();
            searchWithKeyword();
        }else if(callSprf.getPreferences("filterCondition", "filterApplied").equals("") &&
                callSprf.getPreferences("filterCondition", "filterStartDate").equals("")
                && callSprf.getPreferences("filterCondition", "filterEndDate").equals("")){

//            Toast.makeText(getContext(), "리스트뷰 로딩 케이스 1-3", Toast.LENGTH_SHORT).show();
            searchWithKeyword();
        }




        //case 2-1 . 여행기간을 먼저 설정하고, 세부필터 액티비티에서 설정 후 돌아올 떄 여행기간 및 검색 키워드를 다시 불러와서 검색
        if (callSprf.getPreferences("filterCondition", "filterApplied").equals("true") &&
                !callSprf.getPreferences("filterCondition", "filterStartDate").equals("notSet")
                && !callSprf.getPreferences("filterCondition", "filterEndDate").equals("notSet")){

//            Toast.makeText(getContext(), "filterApplied true && Time Set", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), "리스트뷰 로딩 케이스 2", Toast.LENGTH_SHORT).show();
            travelDateInitImgView.setVisibility(View.VISIBLE);
            searchWithKeyword();
        }


        //case 2-2 . 다른 조건들은 설정되어 있으나 여행기간이 설정되어 있지 않은 경우
        if (callSprf.getPreferences("filterCondition", "filterApplied").equals("true") &&
                callSprf.getPreferences("filterCondition", "filterStartDate").equals("notSet")
                && callSprf.getPreferences("filterCondition", "filterEndDate").equals("notSet")){

//            Toast.makeText(getContext(), "filterApplied true && Time not Set22", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), "리스트뷰 로딩 케이스 3", Toast.LENGTH_SHORT).show();

            searchWithKeyword();
        }





    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callSprf.savePreferences("filterCondition", "filterStartDate", "notSet");
        callSprf.savePreferences("filterCondition", "filterEndDate", "notSet");
        callSprf.removePreferences("filterCondition", "StringEndDate");
        callSprf.removePreferences("filterCondition", "StringStartDate");
//        Toast.makeText(getContext(), "onDestroy", Toast.LENGTH_SHORT).show();

    }




    @Override   //여행일자 세팅 및 적용
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        int firstIntDate = (year * 10000) + ((monthOfYear + 1) * 100) + dayOfMonth;
        int secondIntDate = (yearEnd * 10000) + ((monthOfYearEnd + 1) * 100) + dayOfMonthEnd;
        String startDate, endDate;

//        if(firstIntDate < secondIntDate || firstIntDate == secondIntDate){
//            startDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
//            endDate = yearEnd + "/" + (monthOfYearEnd + 1) + "/" + dayOfMonthEnd;
//            filterStartDate = String.valueOf(firstIntDate);
//            filterEndDate = String.valueOf(secondIntDate);
//        }else{
//            startDate = yearEnd + "/" + (monthOfYearEnd + 1) + "/" + dayOfMonthEnd;
//            endDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
//            filterStartDate = String.valueOf(secondIntDate);
//            filterEndDate = String.valueOf(firstIntDate);
//        }

        if(firstIntDate < secondIntDate || firstIntDate == secondIntDate){
            startDate = year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일";
            endDate = yearEnd + "년" + (monthOfYearEnd + 1) + "월" + dayOfMonthEnd + "일";
            filterStartDate = String.valueOf(firstIntDate);
            filterEndDate = String.valueOf(secondIntDate);
        }else{
            startDate = yearEnd + "년" + (monthOfYearEnd + 1) + "월" + dayOfMonthEnd + "일";
            endDate = year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일";
            filterStartDate = String.valueOf(secondIntDate);
            filterEndDate = String.valueOf(firstIntDate);
        }

        travelDateTV.setText(startDate + " ~ " + endDate);

        callSprf.savePreferences("filterCondition", "filterStartDate", filterStartDate);
        callSprf.savePreferences("filterCondition", "filterEndDate", filterEndDate);
        callSprf.savePreferences("filterCondition", "StringStartDate", startDate);
        callSprf.savePreferences("filterCondition", "StringEndDate", endDate);

        //새로운 리스트를 불러오는 작업이므로 기존 리스트뷰의 페이징을 촐기화
        pagingCount = 0;


        // http 통신연결
        final String mTaskID = "getTravelListByCondition(TIME)";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
        final String mStrUrl = "getTravelListByTimeCenteredCondition.php";
        final int mDelay = 0;

        //요청코드 작성
        requestCode = new JSONObject();
        try {
            requestCode.put("requestCode", "getTravelLisByCondition");
            requestCode.put("filterStartDate", filterStartDate);
            requestCode.put("filterEndDate", filterEndDate);

            filterWord = searchET.getText().toString();

            //각 필터항목별로 값이 존재할 때만 리퀘스트코드에 넣어서 전송
            if (filterWord != null && !filterWord.trim().equals("") && !filterWord.equals("notSet")){
                requestCode.put("filterWord", filterWord);
            }
            if (filterPlace != null && !filterPlace.trim().equals("") && !filterPlace.equals("notSet")){
                requestCode.put("filterPlace", filterPlace);
            }
            if (filterHighAge != null && !filterHighAge.trim().equals("") && !filterHighAge.equals("notSet")){
                requestCode.put("filterHighAge", filterHighAge);
            }
            if (filterLowAge != null && !filterLowAge.trim().equals("") && !filterLowAge.equals("notSet")){
                requestCode.put("filterLowAge", filterLowAge);
            }
            if (filterCost != null && !filterCost.trim().equals("") && !filterCost.equals("notSet")){
                requestCode.put("filterCost", filterCost);
            }
            if (filterGender != null && !filterGender.trim().equals("") && !filterGender.equals("notSet")){
                requestCode.put("filterGender", filterGender);
            }
            if (filterMaxPerson != null && !filterMaxPerson.trim().equals("") && !filterMaxPerson.equals("notSet")){
                requestCode.put("filterMaxPerson", filterMaxPerson);
            }

            //페이징 관련 변수
            requestCode.put("limit", "5");
            if (pagingCount > 0){
                requestCode.put("offset", String.valueOf((pagingCount-1)*5));
            }else{
                requestCode.put("offset","0");
            }
            allResultPrinted = false;

            //정렬 관련 변수
            requestCode.put("sortingParameter", callSprf.getPreferences("filterCondition", "sortingParameter"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        TWDhttpATask timeFilterSearch = new TWDhttpATask(mTaskID,mStrUrl,mDelay) {
            @Override
            protected void onPostExecute(JSONObject fromServerData) {

                try {
                    if (fromServerData.get("result").toString().equals("[]")){
                        listViewItemList.clear();
                        travelListViewAdapter.update(listViewItemList);
                        travelListViewAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "현재 검색 조건에 해당하는 여행이 없습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        JSONArray ja = new JSONArray(fromServerData.get("result").toString());
                        listViewItemList.clear();
                        for (int i = 0; i < ja.length(); i++){
                            listViewItemList.add(ja.get(i).toString());
                            Log.e("listViewItemList"+"("+i+")", ja.get(i).toString());
                        }
                        travelListViewAdapter.update(listViewItemList);
                        travelListViewAdapter.notifyDataSetChanged();
                        travelSortTV.setText("최신등록순");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        timeFilterSearch.execute(requestCode);

    }



    private void searchWithKeyword() {

        Log.e("searchWithKeyword", "CALL");

        //새로운 리스트를 불러오는 작업이므로 기존 리스트뷰의 페이징을 촐기화
        pagingCount = 0;

        // http 통신연결
        final String mTaskID = "getTravelListByCondition(KEYWORD)";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
        final String mStrUrl = "getTravelListByWordCenteredCondition.php";
        final int mDelay = 0;

        //요청코드 작성
        requestCode = new JSONObject();
        try {
            requestCode.put("requestCode", "getTravelLisByCondition");

            filterWord = searchET.getText().toString();
            filterStartDate = callSprf.getPreferences("filterCondition", "filterStartDate");
            filterEndDate = callSprf.getPreferences("filterCondition", "filterEndDate");

            String startDate = callSprf.getPreferences("filterCondition", "StringStartDate");
            String endDate = callSprf.getPreferences("filterCondition", "StringEndDate");

            if (startDate.length() == 0){
                travelDateTV.setText("여행 일정 선택");
            }else{
                travelDateTV.setText(startDate + " ~ " + endDate);
            }


//            Toast.makeText(getContext(), filterPlace, Toast.LENGTH_SHORT).show();

            //각 필터항목별로 값이 존재할 때만 리퀘스트코드에 넣어서 전송
            if (filterStartDate != null && !filterStartDate.trim().equals("") && !filterStartDate.equals("notSet")){
                requestCode.put("filterStartDate", filterStartDate);
            }
            if (filterEndDate != null && !filterEndDate.trim().equals("") && !filterEndDate.equals("notSet")){
                requestCode.put("filterEndDate", filterEndDate);
            }
            if (filterWord != null && !filterWord.trim().equals("") && !filterWord.equals("notSet")){
                requestCode.put("filterWord", filterWord);
            }
            if (filterPlace != null && !filterPlace.trim().equals("") && !filterPlace.equals("notSet")){
                requestCode.put("filterPlace", filterPlace);
            }
            if (filterHighAge != null && !filterHighAge.trim().equals("") && !filterHighAge.equals("notSet")){
                requestCode.put("filterHighAge", filterHighAge);
            }
            if (filterLowAge != null && !filterLowAge.trim().equals("") && !filterLowAge.equals("notSet")){
                requestCode.put("filterLowAge", filterLowAge);
            }
            if (filterCost != null && !filterCost.trim().equals("") && !filterCost.equals("notSet")){
                requestCode.put("filterCost", filterCost);
            }
            if (filterGender != null && !filterGender.trim().equals("") && !filterGender.equals("notSet")){
                requestCode.put("filterGender", filterGender);
            }
            if (filterMaxPerson != null && !filterMaxPerson.trim().equals("") && !filterMaxPerson.equals("notSet")){
                requestCode.put("filterMaxPerson", filterMaxPerson);
            }

            if (filterStartDate != null && !filterStartDate.trim().equals("") && !filterStartDate.equals("notSet")){
                requestCode.put("filterStartDate", filterStartDate);
            }
            if (filterEndDate != null && !filterEndDate.trim().equals("") && !filterEndDate.equals("notSet")){
                requestCode.put("filterEndDate", filterEndDate);
            }



            //페이징 관련 변수
            requestCode.put("limit", "5");
            if (pagingCount > 0){
                requestCode.put("offset", String.valueOf((pagingCount-1)*5));
            }else{
                requestCode.put("offset","0");
            }
            allResultPrinted = false;

            //정렬 관련 변수
            requestCode.put("sortingParameter", callSprf.getPreferences("filterCondition", "sortingParameter"));




            //인기순 정렬시 기간 외 여행을 제외하기 위한 코드
            if (callSprf.getPreferences("filterCondition", "sortingParameter").equals("hit") && callSprf.getPreferences("filterCondition", "dateRangeOption") != null){
                requestCode.put("dateRangeOption", "exclusive");
                //callSprf.removePreferences("filterCondition", "dateRangeOption");
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        TWDhttpATask keywordFilterSearch = new TWDhttpATask(mTaskID,mStrUrl,mDelay) {
            @Override
            protected void onPostExecute(JSONObject fromServerData) {

                try {
                    if (fromServerData.get("result").toString().equals("[]")){
                        listViewItemList.clear();
                        travelListViewAdapter.update(listViewItemList);
                        travelListViewAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "조건에 해당하는 모든 여행을 가져왔습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        JSONArray ja = new JSONArray(fromServerData.get("result").toString());
                        listViewItemList.clear();
                        for (int i = 0; i < ja.length(); i++){
                            listViewItemList.add(ja.get(i).toString());
                            Log.e("listViewItemList"+"("+i+")", ja.get(i).toString());
                        }
                        travelListViewAdapter.update(listViewItemList);
                        travelListViewAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        keywordFilterSearch.execute(requestCode);
    }



    //여행 리스트뷰 페이징 처리 부분

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0
                && mLockListView == false && allResultPrinted == false)
        {
            Log.i("여행 리스트뷰", "Loading next items");
            addItems();
          //Toast.makeText(getContext(), "페이징 시작", Toast.LENGTH_SHORT).show();

        }
    }


    private void addItems()
    {
        // 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
        mLockListView = true;

        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {

                // http 통신연결
                final String mTaskID = "getTravelListByCondition(KEYWORD_AddITEMS)";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
                final String mStrUrl = "getTravelListByWordCenteredCondition.php";
                final int mDelay = 0;

                //요청코드 작성
                requestCode = new JSONObject();
                try {
                    requestCode.put("requestCode", "getTravelLisByCondition");

                    filterWord = searchET.getText().toString();
                    filterStartDate = callSprf.getPreferences("filterCondition", "filterStartDate");
                    filterEndDate = callSprf.getPreferences("filterCondition", "filterEndDate");

                    String startDate = callSprf.getPreferences("filterCondition", "StringStartDate");
                    String endDate = callSprf.getPreferences("filterCondition", "StringEndDate");

                    if (startDate.length() == 0){
                        travelDateTV.setText("여행 일정 선택");
                    }else{
                        travelDateTV.setText(startDate + " ~ " + endDate);
                    }


//            Toast.makeText(getContext(), filterPlace, Toast.LENGTH_SHORT).show();

                    //각 필터항목별로 값이 존재할 때만 리퀘스트코드에 넣어서 전송
                    if (filterStartDate != null && !filterStartDate.trim().equals("") && !filterStartDate.equals("notSet")){
                        requestCode.put("filterStartDate", filterStartDate);
                    }
                    if (filterEndDate != null && !filterEndDate.trim().equals("") && !filterEndDate.equals("notSet")){
                        requestCode.put("filterEndDate", filterEndDate);
                    }
                    if (filterWord != null && !filterWord.trim().equals("") && !filterWord.equals("notSet")){
                        requestCode.put("filterWord", filterWord);
                    }
                    if (filterPlace != null && !filterPlace.trim().equals("") && !filterPlace.equals("notSet")){
                        requestCode.put("filterPlace", filterPlace);
                    }
                    if (filterHighAge != null && !filterHighAge.trim().equals("") && !filterHighAge.equals("notSet")){
                        requestCode.put("filterHighAge", filterHighAge);
                    }
                    if (filterLowAge != null && !filterLowAge.trim().equals("") && !filterLowAge.equals("notSet")){
                        requestCode.put("filterLowAge", filterLowAge);
                    }
                    if (filterCost != null && !filterCost.trim().equals("") && !filterCost.equals("notSet")){
                        requestCode.put("filterCost", filterCost);
                    }
                    if (filterGender != null && !filterGender.trim().equals("") && !filterGender.equals("notSet")){
                        requestCode.put("filterGender", filterGender);
                    }
                    if (filterMaxPerson != null && !filterMaxPerson.trim().equals("") && !filterMaxPerson.equals("notSet")){
                        requestCode.put("filterMaxPerson", filterMaxPerson);
                    }

                    if (filterStartDate != null && !filterStartDate.trim().equals("") && !filterStartDate.equals("notSet")){
                        requestCode.put("filterStartDate", filterStartDate);
                    }
                    if (filterEndDate != null && !filterEndDate.trim().equals("") && !filterEndDate.equals("notSet")){
                        requestCode.put("filterEndDate", filterEndDate);
                    }

                    //정렬 관련 변수
                    requestCode.put("sortingParameter", callSprf.getPreferences("filterCondition", "sortingParameter"));


                    //인기순 정렬시 기간 외 여행을 제외하기 위한 코드
                    requestCode.put("dateRangeOption", "exclusive");
//                    if (callSprf.getPreferences("filterCondition", "sortingParameter").equals("hit") && callSprf.getPreferences("filterCondition", "dateRangeOption") != null){
//                        requestCode.put("dateRangeOption", "exclusive");
//                        //callSprf.removePreferences("filterCondition", "dateRangeOption");
//                    }


                    //페이징 카운트
                    pagingCount += 1;
                    requestCode.put("limit", "5");
                    requestCode.put("offset", String.valueOf((pagingCount)*5));


                    TWDhttpATask keywordFilterSearch = new TWDhttpATask(mTaskID,mStrUrl,mDelay) {
                        @Override
                        protected void onPostExecute(JSONObject fromServerData) {

                            try {
                                if (fromServerData.get("result").toString().equals("[]")){
                                    //listViewItemList.clear();
                                    travelListViewAdapter.update(listViewItemList);
                                    travelListViewAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "조건에 해당하는 모든 여행을 가져왔습니다", Toast.LENGTH_SHORT).show();
                                    //페이징 락 풀기
                                    mLockListView = false;
                                    //더이상 불러올 결과가 없을 경우 && 전체 결과가 5개 이하라서 계속 하여 페이징 요청을 하는 것을 막기위한 변수
                                    allResultPrinted = true;
                                    //푸터 안보이기
                                    mFooterView.setVisibility(View.GONE);
                                }else{
                                    JSONArray ja = new JSONArray(fromServerData.get("result").toString());
                                    //listViewItemList.clear();
                                    for (int i = 0; i < ja.length(); i++){
                                        listViewItemList.add(ja.get(i).toString());
                                        Log.e("listViewItemList"+"("+i+")", ja.get(i).toString());
                                    }
                                    travelListViewAdapter.update(listViewItemList);
                                    travelListViewAdapter.notifyDataSetChanged();

                                    mFooterView.setVisibility(View.VISIBLE);

                                    //페이징 락 풀기
                                    mLockListView = false;
                                }
//                                Toast.makeText(getContext(), "페이징 완료1-" + pagingCount, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    };

                    keywordFilterSearch.execute(requestCode);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };


        // 속도의 딜레이를 구현하기 위한 꼼수
        Handler handler = new Handler();
        handler.postDelayed(run, 500);

    }








}
