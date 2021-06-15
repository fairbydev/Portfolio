package com.teamnova.jaepark.travelmate.activities.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.teamnova.jaepark.travelmate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int ME = 0;
    final int OTHERS = 1;
    final int ENTER_ALARM = 2;
    final int EXIT_ALARM = 3;
    Context context;
    ArrayList<Chat> dialog = new ArrayList<Chat>();

    public ChatAdapter(Context context)
    {
        this.context = context;
    }

    public void addChat(Chat chat)
    {
        dialog.add(chat);
    }

    public void clearChat()
    {
        dialog.clear();
    }

    public void logChat()
    {
        for(Chat chat : dialog)
        {
            if(chat.getContent() != null)
                Log.d("chat data (content)", chat.getContent());

            if(chat.getImgUri() != null)
                Log.d("chat data (imgUri)", chat.getImgUri());

            if(chat.getUserID() != null)
                Log.d("chat data (userID)", chat.getUserID());

            if(chat.getUserName() != null)
                Log.d("chat data (username)", chat.getUserName());

            if(chat.getTimestamp() != null)
                Log.d("chat data (time)", chat.getTimestamp()+"");

            Log.d("chat data (cli_msg_idx)", chat.getClient_msg_idx()+"");
            Log.d("chat data (ser_msg_idx)", chat.getServer_msg_idx()+"");
            Log.d("chat data (un_read_cnt)", chat.getUnread_users()+"");
        }
    }

    public Chat findChatByClientMsgIdx(int client_msg_idx)
    {
        for(Chat chat : dialog)
        {
            if(chat.getClient_msg_idx() == client_msg_idx)
            {
                return chat;
            }
        }

        return null;
    }

    public Chat findChatByServerMsgIdx(int server_msg_idx)
    {
        for(Chat chat : dialog)
        {
            if(chat.getServer_msg_idx() == server_msg_idx)
            {
                return chat;
            }
        }

        return null;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == OTHERS)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_others,parent,false);
            ViewHolder_You holder = new ViewHolder_You(view);
            return holder;
        }

        else if(viewType == ME)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_me,parent,false);
            ViewHolder_ME holder = new ViewHolder_ME(view);
            return holder;
        }

        else if(viewType == ENTER_ALARM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_system,parent,false);
            ViewHolder_ENTER_ALARM holder = new ViewHolder_ENTER_ALARM(view);
            return holder;
        }

        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_system,parent,false);
            ViewHolder_EXIT_ALARM holder = new ViewHolder_EXIT_ALARM(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        switch (dialog.get(position).getViewType())
        {
            case OTHERS:

                //이름
                ((ViewHolder_You)holder).userNameTV.setText(dialog.get(position).getUserName());
//                Toast.makeText(context, "talker name : " + dialog.get(position).getUserName(), Toast.LENGTH_SHORT).show();

                //내용
                try {
                    JSONObject contentObject = new JSONObject(dialog.get(position).getContent());
                    String talk = contentObject.optString("talk", "전송실패");
                    ((ViewHolder_You)holder).contentTV.setText(talk);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //날짜
                Timestamp timestamp = dialog.get(position).getTimestamp();
                String strDate = String.format("%02d:%02d",timestamp.getHours(), timestamp.getMinutes());
                ((ViewHolder_You)holder).dateTV.setText(strDate);

                //이미지
                String userID = dialog.get(position).userID;
//                if (position > 0){
//                    if (userID.equals(dialog.get(position-1)))
//                    {
//                        userID = "";
//                    }
//                }

                if(!userID.equals("null"))
                {
                    Glide.with(context).load("http://poyapo123.cafe24.com/TMphp/newImage/thumbnail_profile_" + dialog.get(position).getUserID() + ".jpg")
                            .thumbnail(0.1f)
                            .override(100,100)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.nopicture)
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(((ViewHolder_You) holder).profileIV);

                    ((ViewHolder_You)holder).profileIV.setVisibility(View.VISIBLE);
                }
                else
                {
                    ((ViewHolder_You)holder).profileIV.setImageResource(R.drawable.ic_account_circle_black_18dp);
                    ((ViewHolder_You)holder).profileIV.setVisibility(View.VISIBLE);
                }

                //읽은 수
                int unread_users = dialog.get(position).getUnread_users();
                //Log.e("chat position : "+position+ " un_read_count", ""+un_read_Message Doesn't exist);

                if(unread_users != 0)
                {
                    ((ViewHolder_You)holder).readTV.setVisibility(View.VISIBLE);
                    ((ViewHolder_You)holder).readTV.setText(unread_users+"");
                }

                else
                {
                    ((ViewHolder_You)holder).readTV.setVisibility(View.GONE);
                }

                break;

            case ME:

                //내용
//                ((ViewHolder_ME)holder).contentTV.setText(dialog.get(position).getContent());
                try {

                    JSONObject contentObject = new JSONObject(dialog.get(position).getContent());
//                    Log.i("chat contentObject : ", dialog.get(position).getContent());
                    String talk = contentObject.optString("talk", "전송실패");
                    ((ViewHolder_ME)holder).contentTV.setText(talk);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //날짜
                timestamp = dialog.get(position).getTimestamp();
                strDate = String.format("%02d:%02d",timestamp.getHours(), timestamp.getMinutes());
                ((ViewHolder_ME)holder).dateTV.setText(strDate);

                //읽은 수
                int server_msg_idx = dialog.get(position).getServer_msg_idx();
                unread_users = dialog.get(position).getUnread_users();
                Log.e("chat ME position : ("+position+ ") unread_users", ""+unread_users);

                if(server_msg_idx != 0)
                {
//                    ((ViewHolder_ME)holder).progressBar.setVisibility(View.GONE);
                    if(unread_users != 0)
                    {
                        ((ViewHolder_ME)holder).readTV.setVisibility(View.VISIBLE);
                        ((ViewHolder_ME)holder).readTV.setText(unread_users+"");
                    }
                    else
                    {
                        ((ViewHolder_ME)holder).readTV.setVisibility(View.GONE);
                    }
                }
                else
                {
//                    ((ViewHolder_ME)holder).progressBar.setVisibility(View.VISIBLE);
                    ((ViewHolder_ME)holder).readTV.setVisibility(View.GONE);
                }

                break;

            case ENTER_ALARM:

                ((ViewHolder_ENTER_ALARM)holder).enterAlarmTV.setText("<" + dialog.get(position).getUserName()+" 님께서 입장하였습니다>");
                break;

            case EXIT_ALARM:

                ((ViewHolder_EXIT_ALARM)holder).exitAlarmTV.setText("<" + dialog.get(position).getUserName()+" 님께서 퇴장하였습니다>");
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        int viewType = dialog.get(position).getViewType();
        return viewType;
    }

    @Override
    public int getItemCount()
    {
        return dialog.size();
    }

    class ViewHolder_You extends RecyclerView.ViewHolder {

        public ImageView profileIV;
        public TextView userNameTV;
        public TextView contentTV;
        public TextView dateTV;
        public TextView readTV;
        public View view;

        public ViewHolder_You(View itemView)
        {
            super(itemView);

            profileIV = itemView.findViewById(R.id.chat_recycler_others_propic_IV);
            userNameTV = itemView.findViewById(R.id.chat_recycler_others_nickname_TV);
            contentTV =  itemView.findViewById(R.id.chat_recycler_othersMsg_TV);
            dateTV = itemView.findViewById(R.id.chat_recycler_others_timestamp_TV);
            readTV = itemView.findViewById(R.id.chat_recycler_others_unread_TV);
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

    class ViewHolder_ME extends RecyclerView.ViewHolder {

        public TextView contentTV;
        public TextView dateTV;
        public TextView readTV;
//        public ProgressBar progressBar;
        public View view;

        public ViewHolder_ME(View itemView)
        {
            super(itemView);

//            progressBar = itemView.findViewById(R.id.recycler);
            contentTV = itemView.findViewById(R.id.recycler_item_me_Msg_TV);
            dateTV = itemView.findViewById(R.id.recycler_item_me_timestamp_TV);
            readTV = itemView.findViewById(R.id.recycler_item_me_unread_TV);
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

    class ViewHolder_ENTER_ALARM extends RecyclerView.ViewHolder {

        public TextView enterAlarmTV;
        public View view;

        public ViewHolder_ENTER_ALARM(View itemView)
        {
            super(itemView);

            //progressBar = (ProgressBar) itemView.findViewById(R.id.Item_Chat_ProgressBar);
            enterAlarmTV = itemView.findViewById(R.id.chat_recycler_item_system_msg_TV);
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

    class ViewHolder_EXIT_ALARM extends RecyclerView.ViewHolder {

        public TextView exitAlarmTV;
        public View view;

        public ViewHolder_EXIT_ALARM(View itemView)
        {
            super(itemView);

            //progressBar = (ProgressBar) itemView.findViewById(R.id.Item_Chat_ProgressBar);
            exitAlarmTV = itemView.findViewById(R.id.chat_recycler_item_system_msg_TV);
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

//    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
//
//        File fileCacheItem = new File(strFilePath);
//        OutputStream out = null;
//
//        try
//        {
//            fileCacheItem.createNewFile();
//            out = new FileOutputStream(fileCacheItem);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                out.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

}
