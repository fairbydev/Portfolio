package com.teamnova.jaepark.travelmate.activities.ChatList;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ChatList> chatListAL = new ArrayList<>();

    public ChatListAdapter(Context context)
    {
        this.context = context;
    }

    public void addChatlist(ChatList chatList)
    {
        chatListAL.add(chatList);
    }

    public void clearList()
    {
        chatListAL.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_chatlist_recycler,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

        //타이틀
        String place = chatListAL.get(position).getPlace();
        String hostNickname =  chatListAL.get(position).getHostNickname();
        String travelTitle = place + "(HOST - " + hostNickname + ")";
        ((ViewHolder)holder).titleTV.setText(travelTitle);


        //날짜
        Timestamp timestamp = chatListAL.get(position).getServerReceiveTime();
        String strDate = String.format("%02d:%02d",timestamp.getHours(), timestamp.getMinutes());
        ((ViewHolder)holder).serverReceiveTimeTV.setText(strDate);

        //이미지
        String senderID = chatListAL.get(position).senderID;

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
        String senderName = chatListAL.get(position).getSenderName();
        ((ViewHolder)holder).senderNameTV.setText(senderName);

        //talkContent
        String talkContent = chatListAL.get(position).getTalkContent();
        ((ViewHolder)holder).talkContentTV.setText(talkContent);

        //travelInfo

        final String travelInfo = chatListAL.get(position).getTravelInfo();

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
        return chatListAL.size();
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

            propicIV = itemView.findViewById(R.id.layout_chatlist_recycler_propic_IV);
            senderNameTV = itemView.findViewById(R.id.layout_chatlist_recycler_senderNick_TV);
            talkContentTV =  itemView.findViewById(R.id.layout_chatlist_recycler_talkContent_TV);
            serverReceiveTimeTV = itemView.findViewById(R.id.layout_chatlist_recycler_serverReceiveTime_TV);
            titleTV = itemView.findViewById(R.id.layout_chatlist_recycler_title_TV);
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
