package com.teamnova.jaepark.travelmate.activities.Chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;



//
//public class ChatDBManager extends SQLiteOpenHelper {
////
////    SQLiteDatabase db;
////
////    public ChatDBManager(Context context)
////    {
////        super(context, "Chat.db", null, 1);
////        db = getWritableDatabase();
////    }
////
////    @Override
////    public void onCreate(SQLiteDatabase sqLiteDatabase)
////    {
////        //채팅방 목록 테이블 생성
////        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS Chatroom_List (idx INTEGER PRIMARY KEY AUTOINCREMENT,room_idx INTEGER UNIQUE, entered_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";
////        sqLiteDatabase.execSQL(sqlCreateTbl);
////    }
////
////    @Override
////    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
////    {
////
////    }
////
////    @Override
////    public void onOpen(SQLiteDatabase db)   // 데이터베이스 오픈
////    {
////        super.onOpen(db);
////        this.db = db;
////    }
////
////    public void createMsgTable(String roomID)   //채팅방 생성
////    {
////        String sql = "CREATE TABLE IF NOT EXISTS Chatroom_" + roomID + "_Message (type TEXT , client_msg_idx INTEGER PRIMARY KEY AUTOINCREMENT, server_msg_idx INTEGER UNIQUE, sender_idx INTEGER, sender_name TEXT, room_idx INTEGER, content TEXT, client_sendtime TIMESTAMP NOT NULL DEFAULT 0, server_receivetime TIMESTAMP NOT NULL DEFAULT 0, readtime TIMESTAMP NOT NULL DEFAULT 0, unread_users INTEGER, read INTEGER DEFAULT 0)";
////        db.execSQL(sql);
////    }
////
////    public void deleteMsgTable(String roomID)   //채팅방 삭제
////    {
////        String sql = "DROP TABLE IF EXISTS Chatroom_"+roomID+"_Message";
////        db.execSQL(sql);
////    }
////
////    public void saveBelongedRoom(String roomID)   //채팅방 목록에 신규추가
////    {
////        String sql = "INSERT OR REPLACE INTO Chatroom_List (room_idx) VALUES ("+roomID+")";
////        db.execSQL(sql);
////    }
////
////    public void deleteBelongedRoom(String roomID)   //속해있는 채팅방 목록에서 삭제
////    {
////        String sql = "DELETE FROM Chatroom_List WHERE room_idx = "+roomID+"";
////        db.execSQL(sql);
////    }
////
////    public void saveSendMsg(Message msg)   //서버로 전송하기 전에 (토크)메시지 저장 또는 수정
////    {
//////        try
//////        {
//////            if(msg.getAction().equals("talk"))
//////            {
//////                String roomID = msg.getToRoomID();
//////                String senderID = msg.getSenderID();
//////                String content = msg.getContent();
//////                JSONObject object = new JSONObject(content);
//////                String senderName = object.getString("username");
//////                String talkContent = object.getString("talk");
//////
//////                String sql = "INSERT OR REPLACE INTO Chatroom_"+roomID+"_Message (sender_idx, sender_name, room_idx, content, client_sendtime, is_read) VALUES ("+senderID+", '"+senderName+"', "+roomID+", '"+talkContent+"', DATETIME('NOW', 'LOCALTIME'), 1)";
//////                db.execSQL(sql);
//////            }
//////
//////            else
//////            {
//////                Log.e("ChatDBManager", "this message is not save because msg type is not 'talk'");
//////            }
//////
//////
//////        }
//////        catch (JSONException e)
//////        {
//////            e.printStackTrace();
//////        }
////
////
////    }
////
////    public void saveReceiveMsg(Message msg) //chat 서버로 부터 받은 메세지 db에 저장
////    {
//////        try
//////        {
//////            String senderID = msg.getSenderID();
//////            String roomID = msg.getToRoomID();
//////            String content = msg.getContent();
//////
//////            JSONObject object = new JSONObject(content);
//////            String server_msg_idx = object.optString("server_msg_idx");
//////            String sender_name = object.optString("username");
//////            String talkContent = object.optString("talk");
//////            String server_receivetime = object.optString("server_receivetime");
//////            int un_read_count = object.optInt("un_read_count");
//////
//////            String sql = "INSERT OR REPLACE INTO Chatroom_"+roomID+"_Message (server_msg_idx, sender_idx, sender_name, room_idx, content, server_receivetime, un_read_count) VALUES ("+server_msg_idx+", "+senderID+", '"+sender_name+"', "+roomID+", '"+talkContent+"', '"+server_receivetime+"', "+un_read_count+")";
//////            db.execSQL(sql);
//////        }
//////
//////        catch (Exception e)
//////        {
//////            e.printStackTrace();
//////        }
////
////    }
////
////    public void saveReceiveMsg(JSONObject content)
////    {
//////        try
//////        {
//////            String server_msg_idx = content.optString("server_msg_idx");
//////            String sender_idx = content.optString("sender_idx");
//////            String sender_name = content.optString("sender_name");
//////            String talkContent = content.optString("content");
//////            String room_idx = content.optString("room_idx");
//////            String server_receivetime = content.optString("server_receivetime");
//////            int un_read_count = content.optInt("un_read_count");
//////
//////            String sql = "INSERT OR REPLACE INTO Chatroom_"+room_idx+"_Message (server_msg_idx, sender_idx, sender_name, room_idx, content, server_receivetime, un_read_count) VALUES ("+server_msg_idx+", "+sender_idx+", '"+sender_name+"', "+room_idx+", '"+talkContent+"', '"+server_receivetime+"', "+un_read_count+")";
//////            db.execSQL(sql);
//////        }
//////
//////        catch (Exception e)
//////        {
//////            e.printStackTrace();
//////        }
////
////    }
////
////    public void updateReceiveMsg(Message msg)
////    {
//////        String roomID = msg.getToRoomID();
//////        String content = msg.getContent();
//////
//////        try
//////        {
//////            JSONObject object = new JSONObject(content);
//////            int client_msg_idx = object.optInt("client_msg_idx");
//////            int server_msg_idx = object.optInt("server_msg_idx");
//////            String server_receivetime = object.optString("server_receivetime");
//////            int un_read_count = object.optInt("un_read_count");
//////
//////            if(client_msg_idx != 0 && server_msg_idx != 0 && !server_receivetime.equals(""))
//////            {
//////                String sql = "UPDATE Chatroom_"+roomID+"_Message SET server_receivetime = '"+server_receivetime+"', server_msg_idx = "+server_msg_idx+", un_read_count = "+un_read_count+" WHERE client_msg_idx = "+client_msg_idx+"";
//////                db.execSQL(sql);
//////            }
//////        }
//////
//////        catch (Exception e)
//////        {
//////            e.printStackTrace();
//////        }
////    }
////
////    public void updateReceiveMsg(JSONObject content)
////    {
//////        try
//////        {
//////            String room_idx = content.optString("room_idx");
//////            String client_msg_idx = content.optString("client_msg_idx");
//////            String server_msg_idx = content.optString("server_msg_idx");
//////            String server_receivetime = content.optString("server_receivetime");
//////            int un_read_count = content.optInt("un_read_count");
//////
//////            if(!client_msg_idx.equals("") && !server_msg_idx.equals("") && !server_receivetime.equals("") && !room_idx.equals(""))
//////            {
//////                String sql = "UPDATE Chatroom_"+room_idx+"_Message SET server_receivetime = '"+server_receivetime+"', server_msg_idx = "+server_msg_idx+", un_read_count = "+un_read_count+" WHERE client_msg_idx = "+client_msg_idx+"";
//////                db.execSQL(sql);
//////            }
//////        }
//////
//////        catch (Exception e)
//////        {
//////            e.printStackTrace();
//////        }
////
////    }
////
////    //다른 사람이 메세지를 읽으면 un_read_count-1 로 업데이트
////    public void someoneReadMsg(Message msg)
////    {
//////        try
//////        {
//////            String roomID = msg.getToRoomID();
//////            String content = msg.getContent();
//////
//////            JSONArray recodes = new JSONArray(content);
//////            for(int i=0; i<recodes.length(); ++i)
//////            {
//////                int un_read_count = recodes.getJSONObject(i).optInt("un_read_count");
//////                int server_msg_idx = recodes.getJSONObject(i).optInt("server_msg_idx");
//////
//////                String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE server_msg_idx = "+server_msg_idx+"";
//////                int db_un_read_count = selectMsgTable(sql).getJSONObject(0).optInt("un_read_count");
//////
//////                if(db_un_read_count >= un_read_count)
//////                {
//////                    sql = "UPDATE Chatroom_"+roomID+"_Message SET un_read_count = "+un_read_count+" WHERE server_msg_idx = "+server_msg_idx+"";
//////                    db.execSQL(sql);
//////                }
//////
//////            }
//////        }
//////
//////        catch (Exception e)
//////        {
//////            e.printStackTrace();
//////        }
////    }
////
////    //내가 메시지를 읽으면 un_read_count-1, is_read 1로 업데이트
////    public void IReadMsg(String roomID)
////    {
//////        String sql = "UPDATE Chatroom_"+roomID+"_Message SET un_read_count = un_read_count-1, is_read = 1 WHERE is_read = 0";
//////        db.execSQL(sql);
////    }
////
////    public void someoneReadMsg(String roomID, String myID)
////    {
//////        String sql = "UPDATE Chatroom_"+roomID+"_Message SET un_read_count = un_read_count-1 WHERE sender_idx <> "+myID+"";
//////        db.execSQL(sql);
////    }
////
////
////    public JSONArray selectRoomTable(String sql)
////    {
////        JSONArray recodes = new JSONArray();
////        JSONObject recode = null;
////        int count = 0;
////        try
////        {
////            Cursor cursor = null ;
////
////            cursor = db.rawQuery(sql, null) ;
////            while (cursor.moveToNext())
////            {
////                int idx = cursor.getInt(0);
////                int room_idx = cursor.getInt(1);
////                String entered_time = cursor.getString(2);
////
////                recode = new JSONObject();
////                recode.put("idx", idx);
////                recode.put("room_idx", room_idx);
////                recode.put("entered_time", entered_time);
////
////                recodes.put(count, recode);
////                count++;
////            }
////
////            return recodes;
////        }
////
////        catch (Exception e)
////        {
////            e.printStackTrace();
////            return null;
////        }
////    }
////
////
////    public JSONArray selectMsgTable(String sql)
////    {
////        JSONArray recodes = new JSONArray();
////        JSONObject recode = null;
////        int count = 0;
////        try
////        {
////            Cursor cursor = null ;
////
////            cursor = db.rawQuery(sql, null) ;
////            while (cursor.moveToNext())
////            {
////                int client_msg_idx = cursor.getInt(0);
////                int server_msg_idx = cursor.getInt(1);
////                int sender_idx = cursor.getInt(2);
////                String sender_name = cursor.getString(3);
////                int room_idx = cursor.getInt(4);
////                String content = cursor.getString(5);
////                String client_sendtime = cursor.getString(6);
////                String server_receivetime = cursor.getString(7);
////                String readtime = cursor.getString(8);
////                int un_read_count = cursor.getInt(9);
////                int is_read = cursor.getInt(10);
////
////                recode = new JSONObject();
////                recode.put("client_msg_idx", client_msg_idx);
////                recode.put("server_msg_idx", server_msg_idx);
////                recode.put("sender_idx", sender_idx);
////                recode.put("sender_name", sender_name);
////                recode.put("room_idx", room_idx);
////                recode.put("content", content);
////                recode.put("client_sendtime", client_sendtime);
////                recode.put("server_receivetime", server_receivetime);
////                recode.put("readtime", readtime);
////                recode.put("un_read_count", un_read_count);
////                recode.put("is_read", is_read);
////
////                recodes.put(count, recode);
////                count++;
////            }
////
////            return recodes;
////        }
////
////        catch (Exception e)
////        {
////            e.printStackTrace();
////            return null;
////        }
////    }
////
////    public int getMaxValues(String tableName, String columsName)
////    {
////        String sql = "SELECT MAX("+columsName+") FROM "+tableName+"";
////
////        Cursor cursor = null ;
////        cursor = db.rawQuery(sql, null) ;
////        int values = 0;
////        while (cursor.moveToNext())
////        {
////            values = cursor.getInt(0);
////        }
////
////        return values;
////    }
////
////    public int getCount(String sql)
////    {
////
////        int count = 0;
////
////        try
////        {
////            Cursor cursor = null ;
////
////            cursor = db.rawQuery(sql, null) ;
////            while (cursor.moveToNext())
////            {
////                count++;
////            }
////        }
////
////        catch (Exception e)
////        {
////            e.printStackTrace();
////
////        }
////
////        finally
////        {
////            return count;
////        }
////
////    }
//
//}
