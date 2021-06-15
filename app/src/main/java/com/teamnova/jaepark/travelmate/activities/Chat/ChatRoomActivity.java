package com.teamnova.jaepark.travelmate.activities.Chat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.TWDhttpATask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity
implements
OnRecieveMessageListener, View.OnClickListener//,OnFileListener
{


    private CallSharedPreference callSprf;


    //백키(돌아가기) IV
    ImageView backKeyIV;

    //RoomName TV
    TextView roomNameTV;

    //채팅멤버 보기 IV
    ImageView chatMemberListIV;

    //채팅 RecyclerView
    RecyclerView chatRV;
    ChatAdapter chatAdapter;
    LinearLayoutManager dialogLayout;

    //채팅입력 ET
    EditText msgInputET;

    //채팅메시지 발신 TV
    TextView sendMsgTV;

    //넘겨받은 여행정보 JSON String
    String mTravelInfo;

    //서버로부터 조회한 여형정보를 담을 여행 정보 변수들(mUserId는 현재 앱사용자가 아닌 해당 여행을 등록한 사용자의 아이디)
    String mSeq, mTitle, mStartDate, mEndDate, mPlace, mMaxPerson, mCost, mGender,  mFromAge, mToAge, mBrief,
            mHostId, mEnterTime, mCreatedTime, mHostNickname;

    //소켓
    ChatClient client;

    //방 정보
    public static String roomID;
    public String roomName;
    public int room_idx;

    //사용자 정보
    String userID;
    String nickname;
    int userIdx;

    public static boolean isRunning = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getApplicationContext();

        //인텐트 체크 = 여행정보 받기
        Intent intent = getIntent();
        mTravelInfo = intent.getStringExtra("travelinfo");
        //Toast.makeText(this, "travelInfoString : " + mTravelInfo, Toast.LENGTH_SHORT).show();
        Log.i("ChatRoomActivity - travelInfo", mTravelInfo);
        JSONObject travelInfoJson = null;

        try
        {
            travelInfoJson = new JSONObject(mTravelInfo);

            //여행 정보 변수들
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
            mHostId = travelInfoJson.get("id").toString();
            mEnterTime = travelInfoJson.get("enterTime").toString();
            mCreatedTime = travelInfoJson.get("createdTime").toString();

            mHostNickname = travelInfoJson.get("hostNickname").toString();

            nickname = callSprf.getPreferences("memberInfo", "nickname");
            userID = callSprf.getPreferences("memberInfo", "id");
            userIdx = Integer.parseInt(callSprf.getPreferences("memberInfo", "idx"));
            roomID = mSeq;
            roomName = mPlace + ", " + mHostNickname;
            room_idx = Integer.parseInt(mSeq);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        // 여행정보를 roomID와 함께 저장
        saveTravelInfoWithRoomID(roomID, mTravelInfo);


        //백키(돌아가기) IV
         backKeyIV = findViewById(R.id.activity_chat_room_backKey_Img);

        //RoomName TV
         roomNameTV = findViewById(R.id.activity_chat_room_roomName_TV);
         roomNameTV.setText(roomName);

        //채팅멤버 보기 IV
         chatMemberListIV = findViewById(R.id.activity_chat_room_memberListCheck_Img);

        //채팅 RecyclerView
         chatRV = findViewById(R.id.activity_chat_room_contentList_RecyclerView);
         chatAdapter = new ChatAdapter(this);
         chatRV.setAdapter(chatAdapter);
         dialogLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
         chatRV.setLayoutManager(dialogLayout);

        //채팅입력 ET
         msgInputET = findViewById(R.id.activity_chat_room_InputMsg_ET);

        //채팅 발신 TV
         sendMsgTV = findViewById(R.id.activity_chat_room_sendMsg_TV);
         sendMsgTV.setOnClickListener(this);


         //자신이 만든 방에 재입장 요청 처리 되지 않게 입장이 되어있는지 먼저 체크

        // Hit 중복 체크를 위한 http 통신연결
        final String mTaskID = "RoomMembershipCheck";   //로그 출력 시 어떤 Task의 메시지인지 확인하기 위한 ID
        final String mStrUrl = "checkRoomMembership.php";
        final int mDelay = 0;

        //요청코드 작성
        JSONObject requestCode = new JSONObject();

        try {
            requestCode.put("roomID", roomID);
            requestCode.put("memberIdx", userIdx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TWDhttpATask isIntheRoom  = new TWDhttpATask(mTaskID, mStrUrl, mDelay) {
            @Override
            protected void onPostExecute(JSONObject fromServerData) {
                    try {
                        Log.e("isIntheRoom", fromServerData.get("result").toString());
                        JSONObject ja = new JSONObject(fromServerData.toString());
                        Log.e("isIntheRoom", ja.get("result").toString());
                        String result = ja.get("result").toString();


                        if (result.equals("hasNoMembership"))
                        {
                            // 액티비티 입장 후 기존에 방이 있는지를 체크 & 없으면 입장요청
                            Log.i("ChatRoomActivity", "EnterCheck");
                            Message msg = new Message();
                            msg.setSystemMessage();
                            msg.systemMessage.setSystemType(Message.SystemMessage.ENTER);
                            msg.setRoomID(roomID);
                            msg.setRoomName(roomName);
                            msg.setSender_idx(userIdx);
                            msg.setSenderID(userID);
                            msg.setSender_nickname(nickname);


                            //클라이언트, 소켓연결, 메시지리스너 세팅
                            client = ChatClient.getInstance();
                            client.setInitial(getApplicationContext());
                            if(!client.isConnected()){
                                client.connect();
                            }
                            client.setOnRecieveMessageListener(ChatRoomActivity.this);
                            client.sendMsg(msg);
                        }
                        else if (result.equals("hasMembership"))
                        {
                            //클라이언트, 소켓연결, 메시지리스너 세팅
                            client = ChatClient.getInstance();
                            client.setInitial(getApplicationContext());
                            if(!client.isConnected()){
                                client.connect();
                            }
                            client.setOnRecieveMessageListener(ChatRoomActivity.this);
                        }
                        else
                        {
                            Log.i("ChatRoomActivity", "membershipCheck Error");
                        }

                        notifyReadMsg();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        };

        isIntheRoom.execute(requestCode);


    }



    @Override
    protected void onResume()
    {
        super.onResume();
        isRunning = true;

        notifyReadMsg();
//        모든 채팅 기록 불러오기
        getAllChatMsgFromDB();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        client.setOnRecieveMessageListener(this);

//        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case(R.id.activity_chat_room_sendMsg_TV):
                Log.i("ChatRoomActivity : Clicked - ", "activity_chat_room_sendMsg_TV");
                Message msg = new Message();

                msg.setTalkMessage();
                msg.talkMessage.setTalkType(Message.TalkMessage.TALK);
                msg.setSenderID(userID)
                    .setSender_idx(userIdx)
                    .setSender_nickname(nickname)
                    .setRoomID(roomID)
                    .setRoom_idx(room_idx)
                    .setRoomName(roomName);

                JSONObject talkJO = new JSONObject();
                try {
                    talkJO.put("talk", msgInputET.getText().toString());
                    talkJO.put("senderID", userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.setContent(talkJO.toString());
                //Log.i("ChatRoomActivity - OnClick(sendMsg) content ", msg.getContent());

                //SQLite에 메시지 저장
                ChatDBManager dbManager = new ChatDBManager(getApplicationContext());
                if (!dbManager.isExistedTable("Chatroom_" + roomID + "_Message"))   //해당 메시지 db테이블이 없을 경우 생성
                {
                    dbManager.createMsgTable(roomID);
                }

                dbManager.saveBelongedRoom(roomID);
                dbManager.saveSendMsg(msg);


//                String sql2 = "SELECT * FROM Chatroom_"+roomID+"_Message";
//                JSONArray allMessagesJA = dbManager.selectMsgTable(sql2);
//                Log.i("ChatRoomActivity onClick1 - 저장 후 송신 직전 전체 메시지 기록", allMessagesJA.toString());



                try {
                    JSONObject record = dbManager.selectMsgTable("SELECT * FROM Chatroom_"+roomID+"_Message ORDER BY client_msg_idx DESC LIMIT 1").getJSONObject(0);
                    //리사이클러뷰 아이템 추가
                    Chat chat = new Chat();
                    chat.setClient_msg_idx(record.optInt("client_msg_idx"));
                    chat.setContent(record.optString("content"));
                    //Log.i("ChatRoomActivity - record content ", record.optString("content"));

                    chat.setViewType(chat.ME);
                    chat.setTimestamp(Timestamp.valueOf(record.optString("client_sendtime")));

                    chatAdapter.addChat(chat);
                    chatAdapter.notifyDataSetChanged();
                    msgInputET.setText("");

                    String client_msg_idx = String.valueOf(record.optInt("client_msg_idx"));
                    msg.setClient_msg_idx(client_msg_idx)
                        .setClient_sendtime(record.optString("client_sendtime"));

                    boolean isConnected = client.isConnected();
                    if(isConnected)
                    {
//                        String sql11 = "SELECT * FROM Chatroom_"+roomID+"_Message";
//                        allMessagesJA = dbManager.selectMsgTable(sql11);
//                        Log.i("ChatRoomActivity onClick 1-1 - 송신 후 onReceive 이전 전체 메시지 기록", allMessagesJA.toString());

                        client.sendMsg(msg);

//                        String sql3 = "SELECT * FROM Chatroom_"+roomID+"_Message";
//                        allMessagesJA = dbManager.selectMsgTable(sql3);
//                        Log.i("ChatRoomActivity onClick2 - 송신 후 onReceive 이전 전체 메시지 기록", allMessagesJA.toString());
                    }

                    //스크롤 맨 밑으로 이동
                    dialogLayout.scrollToPositionWithOffset(chatAdapter.getItemCount()-1, 0);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            break;


        }

    }

    @Override
    public void onRecieve(Message message) {

        final Message finalMsg = message;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                switch (finalMsg.getMessageType())
                {

                    case TALK:

                        int talkType = finalMsg.getTalkType();

                        if(talkType == -1)
                            break;

                        if (talkType == Message.TalkMessage.TALK)
                        {
                            String thisRoomID = finalMsg.getRoomID();

                            if(roomID.equals(thisRoomID))
                            {
                                //내가 보낸 메세지
                                if(finalMsg.senderID.equals(userID))
                                {
                                    try
                                    {   // 서버에 받은 unread_users와 server_msg_idx를 chat에 반영
                                        // (저장은 ChatClient에서 방으로 넘겨주기 전에 이미 했음,
                                        // 그리고 unread_users도 서버에서 -1 해서 받은 것은 저장하기 때문에 보낸 내가 읽었다는 처리는 서버와 클라 양쪽 db에 저장됨)
                                        int client_msg_idx = Integer.parseInt(finalMsg.getClient_msg_idx());
                                        int unread_users = finalMsg.getUnread_users();
                                        int server_msg_idx = Integer.parseInt(finalMsg.getServer_msg_idx());

                                        Chat chat = chatAdapter.findChatByClientMsgIdx(client_msg_idx);

                                        if(chat != null)
                                        {
                                            chat.setServer_msg_idx(server_msg_idx);
                                            chat.setUnread_users(unread_users); // 서버에서 메시지 수신 후 맨 처음 룸에 속한 인원을 체크 후 -1 한 숫자
                                        }

//                                        ChatDBManager dbManager = new ChatDBManager(getApplicationContext());
//                                        String sql4 = "SELECT * FROM Chatroom_"+roomID+"_Message";
//                                        JSONArray allMessagesJA = dbManager.selectMsgTable(sql4);
//                                        Log.i("ChatRoomActivity onClick3 - onRecieve 이후", allMessagesJA.toString());

                                        chatAdapter.logChat();
                                        chatAdapter.notifyDataSetChanged();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                //남이 보낸 메세지
                                else
                                {
                                    //메세지를 읽으면 서버에 읽었다는 내용 전송
                                    if(isTopActivity())
                                    {
                                        notifyReadMsg();
                                    }

                                    String content = finalMsg.getContent();

                                    try
                                    {
                                        JSONObject talkJO = new JSONObject(content);
                                        String userName = finalMsg.getSender_nickname();
                                        String server_receivetime = finalMsg.getServer_recieve_time();
                                        int unread_users = finalMsg.getUnread_users();
                                        int server_msg_idx = Integer.parseInt(finalMsg.getServer_msg_idx());

                                        Chat chat = new Chat();

                                    if(isTopActivity())
                                    {
                                        chat.setUnread_users(unread_users-1);   // 이미 sqlite에는 위 notifyReadMsg()로 인하여 반영이 된 부분임
                                    }
                                    else
                                    {
                                        chat.setUnread_users(unread_users);
                                    }

                                        chat.setServer_msg_idx(server_msg_idx);
                                        chat.setContent(talkJO.toString());
                                        chat.setUserName(userName);
                                        chat.setViewType(chat.OTHERS);
                                        chat.setTimestamp(Timestamp.valueOf(server_receivetime));
                                        chat.setUserID(finalMsg.senderID);

                                        chatAdapter.addChat(chat);
                                        chatAdapter.notifyDataSetChanged();
                                    }

                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    //맨 밑을 보고있는 상태에서 메시지 수신시 스크롤 맨 밑으로 이동
                                    if(dialogLayout.findLastVisibleItemPosition() < chatAdapter.getItemCount()-2)
                                    {
                                        Toast.makeText(ChatRoomActivity.this, "새로운 메시지를 수신하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        dialogLayout.scrollToPositionWithOffset(chatAdapter.getItemCount()-1, 0);
                                    }

                                }
                            }
                        }
                        else if(talkType == Message.TalkMessage.READ)
                        {
                            //다른 사람이 메세지를 읽은 경우 서버로부터 unread_users 정보 최신화
                            ChatDBManager chatDBManager = new ChatDBManager(getApplicationContext());
                            chatDBManager.someoneReadMsg(finalMsg);

                            String content = finalMsg.getContent();

//                            Toast.makeText(ChatRoomActivity.this, "읽음 기록 수신." + finalMsg.getContent(), Toast.LENGTH_SHORT).show();
                            Log.i("ChatRoomActivity - 읽음기록 수신", finalMsg.getContent());

                            try
                            {
                                JSONArray recodes = new JSONArray(content); // 여러 메시지 내역을 JSONArray로 한번에 주고 받기에 메시지에 각각 get/set하기보다 JSON이용

                                for(int i=0; i<recodes.length(); ++i)
                                {
                                    int server_msg_idx = recodes.getJSONObject(i).getInt("server_msg_idx");

                                    //sqlite에 먼저 반영되어 변한 unread_users를 chat에 갱신
                                    String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE server_msg_idx = "+server_msg_idx+"";
                                    int unread_users = chatDBManager.selectMsgTable(sql).getJSONObject(0).optInt("unread_users");

                                    Chat chat = chatAdapter.findChatByServerMsgIdx(server_msg_idx);
                                    if(chat != null)
                                        chat.setUnread_users(unread_users);
                                }

                                //chatAdapter.logChat();
                                chatAdapter.notifyDataSetChanged();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case SYSTEM:

                        int systemType = finalMsg.getSystemType();

                        if(systemType == -1)
                            break;

                        if(systemType == Message.SystemMessage.ENTER)
                        {
                            String sender_id = finalMsg.getSenderID();
                            //다른 사람이 이 방에 들어온 경우에만 처리
//                            if(!sender_id.equals(userID))
//                            {
                            String sender_nickname = finalMsg.sender_nickname;
                            Chat chat = new Chat();
                            chat.setUserName(sender_nickname);
                            chat.setViewType(chat.ENTER_ALARM);
                            chatAdapter.addChat(chat);
                            chatAdapter.notifyDataSetChanged();
//                            }
                        }
                        else if(systemType == Message.SystemMessage.EXIT)
                        {
                            String senderID = finalMsg.getSenderID();

                            if(!senderID.equals(userID))
                            {
                                String sender_nickname = finalMsg.getSender_nickname();
                                Chat chat = new Chat();
                                chat.setUserName(sender_nickname);
                                chat.setViewType(chat.EXIT_ALARM);
                                chatAdapter.addChat(chat);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
                        else if(systemType == Message.SystemMessage.SYNC)
                        {
//                            String content = finalMsg.getContent();
//
//                            try
//                            {
//                                JSONArray messages = new JSONArray(content);
//                                int count = 0;
//                                int sender_idx = finalMsg.getSender_idx();
//                                String sender_idxStr = String.valueOf(sender_idx);
//
//                                if(sender_idx != userIdx)   // 현재 클라이언트가 a, 서버에 싱크 메시지를 보낸 클라이언트가 b일 경우
//                                {
//                                    for(int i=0; i<messages.length(); ++i)  //
//                                    {
//                                        if(sender_idxStr.equals(messages.getJSONObject(i).optString("sender_idx")))
//                                        {
//                                            count++;
//                                        }
//                                    }
//                                }
//                                else   // 현재 클라이언트가 a, 서버에 싱크 메시지를 보낸 클라이언트가 동일한 a일 경우
//                                {
//                                    for(int i=0; i<messages.length(); ++i)
//                                    {
//                                        if(!sender_idxStr.equals(messages.getJSONObject(i).optString("sender_idx")))
//                                        {
//                                            count++;
//                                        }
//                                    }
//                                }
//
//                                if(count > 0)
//                                {
//                                    ChatDBManager chatDBManager = new ChatDBManager(getApplicationContext());
//                                    // 현재 클라이언트가 아닌 메시지들을 count만큼 불러옴
//                                    String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE sender_idx <> "+userID+" ORDER BY client_msg_idx DESC LIMIT "+count+"";
//                                    messages = chatDBManager.selectMsgTable(sql);
//
//                                    for(int i=count-1; i>=0; --i)
//                                    {
//                                        String talk_content = messages.getJSONObject(i).optString("content");
//                                        String server_receivetime = messages.getJSONObject(i).optString("server_receivetime");
//                                        String sender_name = messages.getJSONObject(i).optString("sender_name");
//
//                                        Chat chat = new Chat();
//                                        chat.setContent(talk_content);
//                                        chat.setUserName(sender_name);
//                                        chat.setViewType(chat.OTHERS);
//                                        chat.setTimestamp(Timestamp.valueOf(server_receivetime));
//                                        chatAdapter.addChat(chat);
//                                    }
//
//                                    chatAdapter.notifyDataSetChanged();
//
//                                    //맨 밑을 보고있는 상태에서 메시지 수신시 스크롤 맨 밑으로 이동
//                                    if(dialogLayout.findLastVisibleItemPosition() < chatAdapter.getItemCount()-2)
//                                    {
//                                        Toast.makeText(ChatRoomActivity.this, "새로운 메시지를 수신하였습니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else
//                                    {
//                                        dialogLayout.scrollToPositionWithOffset(chatAdapter.getItemCount()-1, 0);
//                                    }
//
//                                }
//                            }
//                            catch (Exception e)
//                            {
//                                e.printStackTrace();
//                            }
                        }

                        break;

                    case FILE:

                        break;

                    default:

                        break;
                }
            }
        }, 0);

    }


    public void getAllChatMsgFromDB() { //내부 sqlite에서 전체 채팅 내역을 새로 불러와서 뿌려주는 메소드
        chatAdapter.clearChat();

        try {
            ChatDBManager dbManager = new ChatDBManager(getApplicationContext());
            String sql = "SELECT * FROM Chatroom_" + roomID + "_Message";
            JSONArray messages = dbManager.selectMsgTable(sql);

//            Toast.makeText(ChatRoomActivity.this, "getAllChatMsgFromDB : "  + messages.toString(), Toast.LENGTH_SHORT).show();
            Log.i("ChatRoomActivity - getAllChatMsgFromDB : ",  messages.toString());


            for (int i = 0; i < messages.length(); ++i) {
                Chat chat = new Chat();

                JSONObject message = messages.getJSONObject(i);

                String type = message.getString("type");
                String sender_idxStr = message.optString("sender_idx");
                int sender_idx = Integer.parseInt(sender_idxStr);
                String senderID = "";

                // 유저 프로필 썸네일을 서버에서 가져오려면 유저 아이디가 필요한데 서버 메시지 저장 내역에는 sender 아이디가 없다...
                // 다른 사람 메시지를 받으면 인덱스로 해당 아이디를 체크하여 저장하는 메소드를 추가하자
                // 메시지를 주고 받을 때마다 클라의 sqlite DB에 각 유저의 id와 idx를 저장하는 테이블을 만들어 가져오도록 하자

                if (sender_idx != userIdx)
                {
                    String sql4ID = "SELECT * FROM Chatroom_" + roomID + "_IdList WHERE user_idx = " + sender_idx;
                    senderID = dbManager.getUserIdFromTable(sql4ID);
                    chat.setUserID(senderID);
                    Log.e("ChatRoomActivity - getUserID", "MSG(" + i + ") : sql = " + sql4ID + " , senderID = " + senderID);
                }

                String sender_name = message.optString("sender_name");
                chat.setUserName(sender_name);

                switch (type) {
                    case "ENTER":

                        chat.setUserName(sender_name);
                        chat.setViewType(chat.ENTER_ALARM);

                        break;

                    case "EXIT":

                        chat.setUserName(sender_name);
                        chat.setViewType(chat.EXIT_ALARM);

                        break;

                    case "TALK":
                        //보낸 사람이 '나'인지 상대방인지 판단
                        String recordedUserIdx = message.optString("sender_idx");
//                        Log.i("ChatRoomActivity - getAllChatMsgFromDB : ", "recordedUserIdx : " + recordedUserIdx + ", userIdx : " + userIdx );
                        String userIdxStr = String.valueOf(userIdx);
                        if(recordedUserIdx.equals(userIdxStr))
                        {
                            chat.setViewType(chat.ME);
//                            Log.i("ChatRoomActivity - getAllChatMsgFromDB : ", "chat.ME selected, " + "recordedUserIdx : " + recordedUserIdx + ", userIdx : " + userIdx);
                        }
                        else
                        {
                            chat.setViewType(chat.OTHERS);
//                            Log.i("ChatRoomActivity - getAllChatMsgFromDB : ", "chat.OTHERS selected, " + "recordedUserIdx : " + recordedUserIdx + ", userIdx : " + userIdx);
                        }
                        break;

//                    case "PHOTO":
//                        break;
//                    case "VEDIO":
//                        break;
//                    case "FILE":
//                        break;

                    default:

                        break;
                }

                int server_msg_idx = message.optInt("server_msg_idx");
                chat.setServer_msg_idx(server_msg_idx);

                int client_msg_idx = message.optInt("client_msg_idx");
                chat.setClient_msg_idx(client_msg_idx);

                int unread_users = message.optInt("unread_users");
                chat.setUnread_users(unread_users);

                String talk = message.optString("content");
                chat.setContent(talk);

                if(!(message.getString("server_receivetime").equals("0") || message.getString("server_receivetime").equals("null")) )
                {
                    Timestamp server_receivetime = Timestamp.valueOf(message.getString("server_receivetime"));
                    chat.setTimestamp(server_receivetime);
                }
                else
                    {
                    chat.setTimestamp(new Timestamp(0L));
                }

                chatAdapter.addChat(chat);
                chatAdapter.notifyDataSetChanged();
            }

            //스크롤 맨 밑으로 이동
            dialogLayout.scrollToPositionWithOffset(chatAdapter.getItemCount() - 1, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //메세지를 읽으면 서버에 읽었다는 내용 전송
    public void notifyReadMsg()
    {
        //메시지들 읽음으로 수정
        ChatDBManager chatDBManager = new ChatDBManager(getApplicationContext());


        try
        {
            //읽지않은 메시지 모두 다 가져온 뒤, 서버에 전송. read는 현재 클라이언트 유저가 읽은 경우 1, 아닌 경우 0(테이블 생성시 디폴트 값)로 표시되고 따라서 다른 숫자가 올 수 없음
            //어느 메시지던 수신하는 talk메시지는 클라 - 서버 - 클라시점의 ChatClient의 리시브쓰레드에서 저장할 떄 read를 1로 하여 저장함
            String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE read = 0";
//            ChatDBManager.TYPE saveType = ChatDBManager.TYPE.TALK;
//            String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE read = 0 and type = 'TALK's";
            JSONArray messagesJA = chatDBManager.selectMsgTable(sql);

            Message msg = new Message();
            msg.setTalkMessage();
            msg.setTalkMessage().setTalkType(Message.TalkMessage.READ);
            msg.setRoom_idx(room_idx);
            msg.setRoomID(roomID);
            msg.setRoomName(roomName);
            msg.setSenderID(userID);
            msg.setSender_idx(userIdx);
            msg.setSender_nickname(nickname);

            JSONObject object = new JSONObject();
            object.put("messages", messagesJA.toString());
            msg.setContent(object.toString());

            client.sendMsg(msg);

            chatDBManager.IReadMsg(roomID); // read가 0인 메시지의 read 0 -> 1, unread_users -1 시키는 메소드


//            Toast.makeText(ChatRoomActivity.this, "읽음 기록 전송." + messagesJA.toString(), Toast.LENGTH_SHORT).show();
            Log.i("ChatRoomActivity - 읽음기록 전송", messagesJA.toString());

//            String sql2 = "SELECT * FROM Chatroom_"+roomID+"_Message";
//            JSONArray allMessagesJA = chatDBManager.selectMsgTable(sql2);
//            Log.i("ChatRoomActivity - 읽음기록 전송 + IReadMsg()실행 이후 전체 메시지 기록", allMessagesJA.toString());



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //현재 채팅 액티비티를 보고있는지 체크
    boolean isTopActivity()
    {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> Info = am.getRunningTasks(1);
        ComponentName topActivity = Info.get(0).topActivity;
        String packageName = topActivity.getPackageName();
        String activityName = topActivity.getClassName();
        Log.i("activityName", activityName);

        if(activityName.equals("com.teamnova.jaepark.travelmate.activities.Chat.ChatRoomActivity"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }



    private void saveTravelInfoWithRoomID(String roomID, String mTravelInfo) {
        //SQLite에 메시지 저장
        ChatDBManager dbManager = new ChatDBManager(getApplicationContext());
        if (!dbManager.isExistedTable("Chatroom_" + roomID + "_travelInfo"))   //해당 메시지 db테이블이 없을 경우 생성
        {
            dbManager.createTravelInfoTable(roomID);
        }

        dbManager.saveTravelInfo(roomID, mTravelInfo);
    }

//    @Override
//    public void onCompleteDownCload(File file) {
//
//    }
//
//    @Override
//    public void onCompleteUpload() {
//
//    }
//
//    @Override
//    public void onDownloading(int percent) {
//
//    }
//
//    @Override
//    public void onUploading(int percent) {
//
//    }


}
