package com.teamnova.jaepark.travelmate.activities.News;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatRoomActivity;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<NewsList> newsListAL = new ArrayList<>();

    public NewsListAdapter(Context context)
    {
        this.context = context;
    }

    public void addNewsList(NewsList newsList)
    {
        newsListAL.add(newsList);
    }

    public void clearList()
    {
        newsListAL.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_newslist_recycler,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

        //타이틀
        String place = newsListAL.get(position).getPlace();
        String hostNickname =  newsListAL.get(position).getHostNickname();
        String travelTitle = place + "(HOST - " + hostNickname + ")";
        ((ViewHolder)holder).titleTV.setText(travelTitle);

        //이미지
        String senderID = newsListAL.get(position).senderID;

        if(!senderID.equals("null"))
        {
            Glide.with(context).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_profile_" + senderID + ".jpg")
                    .thumbnail(0.1f)
                    .override(100,100)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.nopicture)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ViewHolder) holder).propicIV);

            ((ViewHolder)holder).propicIV.setVisibility(View.VISIBLE);
        }
        else
        {
            ((ViewHolder)holder).propicIV.setImageResource(R.drawable.ic_account_circle_black_18dp);
        }


        //senderName
        String senderName = newsListAL.get(position).getSenderName();
        ((ViewHolder)holder).senderNameTV.setText(senderName + "님이 새로 입장하였습니다. 반갑습니다!");

        //travelInfo

        final String travelInfo = newsListAL.get(position).getTravelInfo();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("travelinfo", travelInfo);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount()
    {
        return newsListAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView propicIV;
        public TextView titleTV;
        public TextView serverReceiveTimeTV;
        public TextView talkContentTV;
        public TextView senderNameTV;
        public View view;

        public ViewHolder(final View itemView)
        {
            super(itemView);

            propicIV = itemView.findViewById(R.id.layout_newsList_recycler_propic_IV);
            senderNameTV = itemView.findViewById(R.id.layout_newsList_recycler_senderNick_TV);
            titleTV = itemView.findViewById(R.id.layout_newsList_recycler_title_TV);
            setView(itemView);
        }

        public View getView()
        {
            return view;
        }

        public void setView(View view)
        {
            this.view = view;
        }

    }


}
