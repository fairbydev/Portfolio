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
import com.teamnova.jaepark.travelmate.activities.News.NewsList;
import com.teamnova.jaepark.travelmate.activities.News.NewsListAdapter;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class MenuFragment_02 extends Fragment {

    private CallSharedPreference callSprf;

    //채팅 RecyclerView
    RecyclerView newsRV;
    NewsListAdapter newsListAdapter;
    LinearLayoutManager newsListLayout;


    // 속해있는 채팅방 ID ArrayList
    ArrayList <String> chatRoomIdAL = new ArrayList<>();
    ArrayList <String> travelInfoAL = new ArrayList<>();
    ArrayList <String> msgInfoAL = new ArrayList<>();
    ArrayList<NewsList> nlistAL;

    public MenuFragment_02(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment가 화면에 보여질 때 호출됨

        View view = inflater.inflate(R.layout.layout_frag_2, container, false);

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
            newsRV = view.findViewById(R.id.fragment_menu_new_RecyclerView);
            newsListAdapter = new NewsListAdapter(getContext());
            newsRV.setAdapter(newsListAdapter);
            newsListLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            newsRV.setLayoutManager(newsListLayout);
            Log.e("MenuFragment02 : allBelongedRoomIDJA 길이", String.valueOf(allBelongedRoomIDJA.length()));


            for(int i=0; i < allBelongedRoomIDJA.length(); i++)
            {
                try
                {
                    int roomIdx = allBelongedRoomIDJA.getJSONObject(i).getInt("roomIdx");
                    Log.e("MenuFragment02 : allBelongedRoomIDJA - roomIdx", String.valueOf(roomIdx));
                    String roomID = String.valueOf(roomIdx);
                    chatRoomIdAL.add(roomID);
//                    Log.e("MenuFragment02 : OnCreate ", "chatRoomIdAL(" + i +  ") = " + chatRoomIdAL.get(i));

                    // Chatroom_roomID_travelInfo 에 저장된 정보 불러오기
                    if (dbManager.isExistedTable("Chatroom_" + roomID + "_travelInfo")){
                        String sql2 = "SELECT * FROM Chatroom_" + roomID + "_travelInfo";
                        JSONArray travelInfoJA = dbManager.selectTravelinfoTable(sql2);
//                        Log.e("MenuFragment02 : travelInfoJA.toString", travelInfoJA.toString());
//                        Log.e("MenuFragment02 : travelInfoJA 길이", String.valueOf(travelInfoJA.length()));


                        // 해당 채팅룸에 맞는 여행정보가 있을 경우 메시지 테이블 정보 가져오기
                        if ((travelInfoJA.getJSONObject(0).getString("travelInfo") != null)
                                || !travelInfoJA.getJSONObject(0).getString("travelInfo").equals(""))
                        {
                            String travelInfoStr = travelInfoJA.getJSONObject(0).getString("travelInfo");
                            travelInfoAL.add(travelInfoStr);
//                            Log.e("MenuFragment02 : OnCreate ", "chatRoomIdAL(" + i +  ") = " + chatRoomIdAL.get(i) + ", travelInfo = " + travelInfoStr);

                            // ChatRoom 메시지 테이블 중 type이 ENTER인 메시지 정보 가져오기
                            String userIdx = callSprf.getPreferences("memberInfo", "idx");
                            String enterMsgInfoJA = getEnterMsgFromDB(roomID, userIdx);
                            JSONArray enterMsgJA = new JSONArray(enterMsgInfoJA);

//                            Log.e("MenuFragment02 : OnCreate ", "enterMsgJA = " + enterMsgJA.toString());


                            for (int g = 0; g < enterMsgJA.length(); g++)
                            {
                                JSONObject MsgJO = enterMsgJA.getJSONObject(g);
                                String msgStatus = MsgJO.getString("result");
                                if (msgStatus.equals("isData"))
                                {
                                    msgInfoAL.add(MsgJO.toString());
//                                    Log.e("MenuFragment02 : OnCreate ", "enterMsgJA.getJSONObject(" + g +  ") = " + MsgJO.toString());
                                }
                            }
                        }
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < msgInfoAL.size(); i++)
            {
                Log.e("MenuFragment02 : OnCreate ", "msgInfoAL.getJSONObject(" + i +  ") = " + msgInfoAL.get(i) + "\n");
            }

            ArrayList<String> clientIdxAl = new ArrayList<>();

            for (int k = 0; k < msgInfoAL.size(); k++)
            {
                try
                {
                    JSONObject enterMsgJO = new JSONObject(msgInfoAL.get(k));
                    String clientMsgIdx = enterMsgJO.getString("client_msg_idx");
                    clientIdxAl.add(clientMsgIdx);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            Collections.sort(clientIdxAl);
            Collections.reverse(clientIdxAl);

//            for (int i = 0; i < msgInfoAL.size(); i++)
//            {
//                Log.e("MenuFragment02 : OnCreate ", "clientIdxAl.getJSONObject(" + i +  ") = " + clientIdxAl.get(i) + "\n");
//            }

//            for (int l = 0; l < clientIdxAl.size(); l++)
//            {
//                Log.e("MenuFragment02 : clientIdxAl ", "clientIdxAl(" + l +  ") = " + clientIdxAl.get(l).toString());
//            }

            nlistAL = new ArrayList<>();

            Log.e("MenuFragment02 : msgInfoAL ", "msgInfoAL.size() = " + msgInfoAL.size()); // 자기자신 입장내역도 포함되었음

//            for (int m = 0; m < clientIdxAl.size(); m++)
//            {
                JSONObject enterMsgJO = null;
                for (int n = 0; n < msgInfoAL.size(); n++)
                {
                    try
                    {
                        enterMsgJO = new JSONObject(msgInfoAL.get(n));
                        String clientMsgIdx = enterMsgJO.getString("client_msg_idx");
//                        if (clientIdxAl.get(m).equals(clientMsgIdx))
//                        {
                            //룸아이디를 조회해서 해당 메시지에 맞는 방정보 찾기
                            JSONObject travelInfoJO;

                            String roomID = enterMsgJO.get("room_idx").toString();
                            for (int p = 0; p < travelInfoAL.size(); p++)
                            {
                                JSONObject travelJO = new JSONObject(travelInfoAL.get(p));
                                if (roomID.equals(travelJO.getString("seq")))
                                {
                                    travelInfoJO = new JSONObject(travelInfoAL.get(p));

                                    String place = travelInfoJO.getString("place");
                                    String hostNickname = travelInfoJO.getString("hostNickname");

                                    String nickname = callSprf.getPreferences("memberInfo", "nickname");
                                    String userID = callSprf.getPreferences("memberInfo", "id");
                                    String userIdx = callSprf.getPreferences("memberInfo", "idx");

                                    String senderName;
                                    String senderID;

                                    if (enterMsgJO.getString("sender_idx").equals(userIdx)) //본인 입장 메시지일 경우
                                    {

                                    }
                                    else
                                    {
                                        senderName = enterMsgJO.getString("sender_name");
                                        String sql4ID = "SELECT * FROM Chatroom_" + roomID + "_IdList WHERE user_idx = " + enterMsgJO.getString("sender_idx");
                                        senderID = dbManager.getUserIdFromTable(sql4ID);

                                        NewsList nList = new NewsList();
                                        nList.setPlace(place);
                                        nList.setHostNickname(hostNickname);
                                        nList.setSenderID(senderID);
                                        nList.setSenderName(senderName);
                                        nList.setTravelInfo(travelInfoAL.get(p));

                                        nlistAL.add(nList);

                                        newsListAdapter.addNewsList(nList);
                                        newsListAdapter.notifyDataSetChanged();
                                    }


                                }
                            }
//                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

//            }

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
        if (newsListAdapter != null)
        {
            newsListAdapter.clearList();
        }
        super.onDestroyView();
    }


    private String getEnterMsgFromDB(String roomID, String uerIdx) throws JSONException {
        ChatDBManager dbManager = new ChatDBManager(getContext());
        String sql = "SELECT * FROM Chatroom_" + roomID + "_Message where type = 'ENTER' and sender_idx <> '" + uerIdx + "'";
        JSONArray enterMessages = dbManager.selectEnterMsgTable(sql);
        String enterMsgInfoJAStr = enterMessages.toString();

        return enterMsgInfoJAStr;
    }


}
