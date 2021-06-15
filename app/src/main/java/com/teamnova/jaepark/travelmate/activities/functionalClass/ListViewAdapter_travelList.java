package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.content.Context;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teamnova.jaepark.travelmate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;



public class ListViewAdapter_travelList extends BaseAdapter {

    private String travelTitle;
    private String travelPlace;
    private String travelHit;
    private String travelGender;
    private String travelFromAge;
    private String travelToAge;
    private String travelStartDate;
    private String travelEndDate;
    private String travelPicAddr;
    private String profilePicAddr;
    private String userId;


    private ArrayList<String> listViewItemList = new ArrayList<>();

    public ListViewAdapter_travelList(ArrayList<String> listFromServer) {
        listViewItemList = listFromServer;
    }


    @Override
    public int getCount() { return listViewItemList.size(); }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int pos = position;
        Context context = parent.getContext();

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.travel_listview_item, parent, false);

            holder.placeImageView = (ImageView) convertView.findViewById(R.id.travel_listview_traverPicture_ImageView);
            holder.profileImageView = (ImageView) convertView.findViewById(R.id.travel_listview_profilePicture_ImageView);
            holder.placeTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverPlace_TextView);
            holder.hitTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverHit_TextView);
            holder.titleTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverTitle_TextView);
            holder.genderTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverGender_TextView);
            holder.ageTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverAge_TextView);
            holder.dateTextview = (TextView)convertView.findViewById(R.id.travel_listview_traverDate_TextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //어레이 리스트의 값 JSON으로 파싱하여 화면에 뿌려줌

        String listToJson = listViewItemList.get(pos);

        try {
            JSONObject ja = new JSONObject(listToJson);

            travelTitle = ja.get("title").toString();
            travelPlace = ja.get("place").toString();
            travelHit = ja.get("hit").toString();
            travelGender = ja.get("gender").toString();
            travelFromAge = ja.get("fromAge").toString();
            travelToAge = ja.get("toAge").toString();
            travelStartDate = ja.get("startDate").toString();
            travelEndDate = ja.get("endDate").toString();
            travelPicAddr = ja.get("picture01").toString();
            userId = ja.get("id").toString();
            //profilePicAddr = ja.get("place").toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String startDateCoversion = travelStartDate.substring(0,4) + "년" + travelStartDate.subSequence(4,6) + "월" +travelStartDate.substring(6,8) + "일";
        String endDateCoversion = travelEndDate.substring(0,4) + "년" + travelEndDate.substring(4,6) + "월" +travelEndDate.substring(6,8) + "일";


        holder.titleTextview.setText("[" + travelTitle + "]");
        holder.placeTextview.setText(travelPlace);
        holder.hitTextview.setText(travelHit + "Hit");
        switch (travelGender) {
            case ("male") :{
                holder.genderTextview.setText("여행타입 : 남자끼리");
            }break;
            case ("female") :{
                holder.genderTextview.setText("여행타입 : 여자끼리");
            }break;
            case ("all") :{
                holder.genderTextview.setText("여행타입 : 성별무관");
            }break;
        }
        holder.ageTextview.setText("/ 참가연령 : " + travelFromAge + "세 ~ " + travelToAge + "세" );
        holder.dateTextview.setText("여행기간 : " + startDateCoversion + " ~ " + endDateCoversion);
        //holder.placeImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //장소사진
        if (!travelPicAddr.equals("noPicture")){
            Glide.with(convertView.getContext()).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_" + travelPicAddr)
                                                .thumbnail(0.1f)
//                                                .override(600,300)
//                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                                .skipMemoryCache(true)
                                                .into(holder.placeImageView);
        }else if(travelPicAddr.equals("noPicture")){
            Glide.with(convertView.getContext()).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_basicPic.jpg")
                    .thumbnail(0.1f)
                    .into(holder.placeImageView);
        }


        // 프로필사진 이미지뷰 아직 설정 안함

            Glide.with(convertView.getContext()).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_profile_" + userId + ".jpg")
                    .thumbnail(0.1f)
                    .override(100,100)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.nopicture)
                    .bitmapTransform(new CropCircleTransformation(convertView.getContext()))
                    .into(holder.profileImageView);


        return convertView;
    }

    public static class ViewHolder {
        public ImageView placeImageView;
        public ImageView profileImageView;
        public TextView placeTextview;
        public TextView hitTextview;
        public TextView titleTextview;
        public TextView genderTextview;
        public TextView ageTextview;
        public TextView dateTextview;

    }

    public void update(ArrayList<String> data){
        listViewItemList = data;
        this.notifyDataSetChanged();
        Log.e("update", "CALL" );
    }
}
