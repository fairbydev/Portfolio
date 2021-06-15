package com.teamnova.jaepark.travelmate.activities.MenuFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.ChatDBManager;
import com.teamnova.jaepark.travelmate.activities.ChatList.ChatList;
import com.teamnova.jaepark.travelmate.activities.ChatList.ChatListAdapter;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;


public class MenuFragment_01 extends Fragment {

    private CallSharedPreference callSprf;

    //채팅 RecyclerView
    RecyclerView chatRV;
    ChatListAdapter chatlistAdapter;
    LinearLayoutManager chatlistLayout;


    // 속해있는 채팅방 ID ArrayList
    ArrayList <String> chatRoomIdAL = new ArrayList<>();
    ArrayList <String> travelInfoAL = new ArrayList<>();
    ArrayList <String> msgInfoAL = new ArrayList<>();
    ArrayList<ChatList> clistAL;


    public MenuFragment_01(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment가 화면에 보여질 때 호출됨

        View view = inflater.inflate(R.layout.layout_frag_1, container, false);

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = getContext();

        ChatDBManager dbManager = new ChatDBManager(getContext());


        // Chatroom_List에 저장된 방 불러오기
        String sql = "SELECT * FROM Chatroom_List";
        JSONArray allBelongedRoomIDJA = dbManager.selectBelongedRoomTable(sql);

        if (allBelongedRoomIDJA.length() > 0 )  // 소속된 채팅방이 있을 경우
        {

            //리사이클러뷰 세팅
            chatRV = view.findViewById(R.id.fragment_menu_chat_RecyclerView);
            chatlistAdapter = new ChatListAdapter(getContext());
            chatRV.setAdapter(chatlistAdapter);
            chatlistLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            chatRV.setLayoutManager(chatlistLayout);
            Log.e("MenuFragment01 : allBelongedRoomIDJA 길이", String.valueOf(allBelongedRoomIDJA.length()));

            for(int i=0; i < allBelongedRoomIDJA.length(); i++)
            {
                try
                {
                    int roomIdx = allBelongedRoomIDJA.getJSONObject(i).getInt("roomIdx");
                    Log.e("MenuFragment01 : allBelongedRoomIDJA - roomIdx", String.valueOf(roomIdx));
                    String roomID = String.valueOf(roomIdx);
                    chatRoomIdAL.add(roomID);
                    Log.e("MenuFragment01 : OnCreate ", "chatRoomIdAL(" + i +  ") = " + chatRoomIdAL.get(i));

                    // Chatroom_roomID_travelInfo 에 저장된 정보 불러오기
                    if (dbManager.isExistedTable("Chatroom_" + roomID + "_travelInfo")){
                        String sql2 = "SELECT * FROM Chatroom_" + roomID + "_travelInfo";
                        JSONArray travelInfoJA = dbManager.selectTravelinfoTable(sql2);
                        Log.e("MenuFragment01 : travelInfoJA.toString", travelInfoJA.toString());
                        Log.e("MenuFragment01 : travelInfoJA 길이", String.valueOf(travelInfoJA.length()));


                        // 해당 채팅룸에 맞는 여행정보가 있을 경우 메시지 테이블 정보 가져오기
                        if ((travelInfoJA.getJSONObject(0).getString("travelInfo") != null)
                                || !travelInfoJA.getJSONObject(0).getString("travelInfo").equals(""))
                        {
                            String travelInfoStr = travelInfoJA.getJSONObject(0).getString("travelInfo");
                            travelInfoAL.add(travelInfoStr);
                            Log.e("MenuFragment01 : OnCreate ", "chatRoomIdAL(" + i +  ") = " + chatRoomIdAL.get(i) + ", travelInfo = " + travelInfoStr);

                            // ChatRoom 메시지 테이블 중 type이 TALK인 메시지 정보 가져오기
                            String lastMsgInfo = getLastMsgFromDB(roomID);
                            JSONObject MsgJO = new JSONObject(lastMsgInfo);
                            String msgStatus = MsgJO.getString("result");

                            if (msgStatus.equals("isData"))
                            {
                                msgInfoAL.add(lastMsgInfo);
                            }
                            Log.e("MenuFragment01 : OnCreate ", "chatRoomIdAL(" + i +  ") = " + chatRoomIdAL.get(i) + ", lastMsgInfo = " + lastMsgInfo);

                        }
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            ArrayList<Timestamp> timeAL = new ArrayList<>();

            for (int k = 0; k < msgInfoAL.size(); k++)
            {
                try
                {
                    JSONObject lastMsgJO = new JSONObject(msgInfoAL.get(k));
                    Timestamp serverReceiveTime = Timestamp.valueOf(lastMsgJO.getString("server_receivetime"));
                    timeAL.add(serverReceiveTime);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            Collections.sort(timeAL);
            Collections.reverse(timeAL);

//            for (int l = 0; l < timeAL.size(); l++)
//            {
//                Log.e("MenuFragment01 : timeAL ", "timeAL(" + l +  ") = " + timeAL.get(l).toString());
//            }

            clistAL = new ArrayList<>();

            for (int m = 0; m < timeAL.size(); m++)
            {
                JSONObject lastMsgJO = null;
                for (int n = 0; n < msgInfoAL.size(); n++)
                {
                    try
                    {
                        lastMsgJO = new JSONObject(msgInfoAL.get(n));
                        Timestamp serverReceiveTime = Timestamp.valueOf(lastMsgJO.getString("server_receivetime"));
                        if (timeAL.get(m).toString().equals(serverReceiveTime.toString()))
                        {
                            JSONObject travelInfoJO = new JSONObject(travelInfoAL.get(n));
                            JSONObject contentJO = new JSONObject(lastMsgJO.getString("content"));  //{\"talk\":\"frfr\",\"senderID\":\"111@gmail.com\"} 형태

                            String place = travelInfoJO.getString("place");
                            String hostNickname = travelInfoJO.getString("hostNickname");
                            String senderID = contentJO.getString("senderID");
                            String senderName = lastMsgJO.getString("sender_name");
                            String talkContent = contentJO.getString("talk");
                            serverReceiveTime = Timestamp.valueOf(lastMsgJO.getString("server_receivetime"));
                            Log.d("MenuFragment01 : cList(" + m + ")", "place = " + place + ", hostNickname = " + hostNickname + ", senderName = " + senderName + ", talkContent = " + talkContent + ", serverReceiveTime = " + serverReceiveTime);

                            ChatList cList = new ChatList();
                            cList.setPlace(place);
                            cList.setHostNickname(hostNickname);
                            cList.setSenderID(senderID);
                            cList.setSenderName(senderName);
                            cList.setTalkContent(talkContent);
                            cList.setServerReceiveTime(serverReceiveTime);
                            cList.setTravelInfo(travelInfoAL.get(n));

                            clistAL.add(cList);

                            chatlistAdapter.addChatlist(cList);
                            chatlistAdapter.notifyDataSetChanged();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

            }








        }


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        travelInfoAL.clear();
        msgInfoAL.clear();
        chatRoomIdAL.clear();
        if(chatlistAdapter != null)
        {
            chatlistAdapter.clearList();
        }
        super.onDestroyView();
    }


    private String getLastMsgFromDB(String roomID) throws JSONException {
        ChatDBManager dbManager = new ChatDBManager(getContext());
        String sql = "SELECT * FROM Chatroom_" + roomID + "_Message where type = 'TALK'";
        JSONArray messages = dbManager.selectLastMsgTable(sql);
        String lastMsgInfoJOStr = messages.getJSONObject(0).toString();

        return lastMsgInfoJOStr;

    }


}
