package com.teamnova.jaepark.travelmate.activities.Chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;




public class ChatDBManager extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public enum TYPE {
        EXIT, ENTER, TALK, PHOTO, FILE, VIDEO
    }

    public ChatDBManager(Context context)
    {
        super(context, "TM_Chat.db", null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) // (없을 경우) 채팅방 목록 테이블 생성
    {
        //채팅방 목록 테이블 생성
        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS Chatroom_List";
        sqlCreateTbl += " (idx INTEGER PRIMARY KEY AUTOINCREMENT,room_idx INTEGER UNIQUE, entered_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        sqLiteDatabase.execSQL(sqlCreateTbl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    @Override
    public void onOpen(SQLiteDatabase db)   // db open
    {
        super.onOpen(db);
        this.db = db;
    }

    public void createMsgTable(String roomID)   // 채팅방 생성, read 컬럼은 서버에는 없음
    {
        String sql = "CREATE TABLE IF NOT EXISTS Chatroom_"+roomID+"_Message ";
        sql += "(type TEXT, client_msg_idx INTEGER PRIMARY KEY AUTOINCREMENT, server_msg_idx INTEGER UNIQUE,";
        sql += " sender_idx INTEGER, sender_name TEXT, room_idx INTEGER, content TEXT, client_sendtime TIMESTAMP NOT NULL DEFAULT 0,";
        sql += " server_receivetime TIMESTAMP NOT NULL DEFAULT 0, readtime TIMESTAMP NOT NULL DEFAULT 0, unread_users INTEGER, ";
        sql += "read INTEGER DEFAULT 0, file_path TEXT)";
        db.execSQL(sql);
    }

    public void createUserIdTable(String roomID)   // 채팅방 생성, read 컬럼은 서버에는 없음
    {
        String sql = "CREATE TABLE IF NOT EXISTS Chatroom_"+roomID+"_IdList";
        sql += "(idx INTEGER PRIMARY KEY AUTOINCREMENT, room_idx INTEGER, user_idx INTEGER, userID TEXT)";
        db.execSQL(sql);
    }

    public void saveUserId(Message receivedMsg)   // 채팅방 생성, read 컬럼은 서버에는 없음
    {
        String roomIDstr = receivedMsg.getRoomID();

        int roomIdx = Integer.parseInt(roomIDstr);
        String userID = receivedMsg.getSenderID();
        int userIdx = receivedMsg.getSender_idx();

//        String sql = "INSERT OR REPLACE INTO Chatroom_"+roomIDstr+"_IdList ( room_idx, user_idx, userID ) ";
        String sql = "INSERT INTO Chatroom_"+roomIDstr+"_IdList ( room_idx, user_idx, userID ) ";

        sql += "VALUES ("+roomIdx+", "+userIdx+", '"+userID+"')";
        db.execSQL(sql);
    }


    public void deleteMsgTable(String roomID)   // 채팅방 삭제
    {
        String sql = "DROP TABLE IF EXISTS Chatroom_"+roomID+"_Message";
        db.execSQL(sql);
    }

    public void saveBelongedRoom(String roomID)   //채팅방 목록에 신규추가
    {
        String sql = "INSERT OR REPLACE INTO Chatroom_List (room_idx) VALUES ("+roomID+")";
        db.execSQL(sql);
    }

    public void deleteBelongedRoom(String roomID)   //속해있는 채팅방 목록에서 삭제
    {
        String sql = "DELETE FROM Chatroom_List WHERE room_idx = "+roomID+"";
        db.execSQL(sql);
    }

    public JSONArray selectBelongedRoomTable(String sql)
    {
        JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;
            while (cursor.moveToNext())
            {
                int index = cursor.getInt(0);
                int roomIdx = cursor.getInt(1);

                recode = new JSONObject();
                recode.put("index", index);
                recode.put("roomIdx", roomIdx);

                recodes.put(count, recode);
                count++;
            }

            return recodes;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void saveSendMsg(Message msg)    //클라이언트에서 서버로 보내는 메시지 저장
    {

        TYPE dbType = null;
        switch (msg.getMessageType())
        {

            case SYSTEM:

                switch (msg.getSystemType())
                {
                    case Message.SystemMessage.ENTER:
                        dbType = TYPE.ENTER;

                        break;

                    case Message.SystemMessage.EXIT:
                        dbType = TYPE.EXIT;
                        break;
                }
                break;

            case TALK:

                switch (msg.getTalkType())
                {
                    case Message.TalkMessage.TALK:
                        dbType = TYPE.TALK;
                        break;

                    case Message.TalkMessage.READ:
                        dbType = TYPE.TALK;
                        break;
                }
                break;

            case FILE:

                switch (msg.getFileType())
                {
                    case Message.FileMessage.PHOTO:

                        dbType = TYPE.PHOTO;
                        break;

                    case Message.FileMessage.VIDEO:

                        dbType = TYPE.VIDEO;
                        break;

                    case Message.FileMessage.FILE:

                        dbType = TYPE.FILE;
                        break;
                }
                break;

            default:
                Log.e("ChatDBManager", "dbType is Null");
                return;
        }

        String roomID = msg.roomID;
        int room_idx = msg.room_idx;
        int sender_idx = msg.getSender_idx();
        String senderName = msg.getSender_nickname();
        String talkContent = msg.getContent();


        String sql = "INSERT OR REPLACE INTO Chatroom_"+roomID+"_Message (type, sender_idx, sender_name, room_idx, content, client_sendtime, read) ";
        sql += "VALUES ('"+dbType.toString()+"', "+sender_idx+", '"+senderName+"', "+room_idx+", '"+talkContent+"', DATETIME('NOW', 'LOCALTIME'), 1)";
        db.execSQL(sql);
    }

    public void saveReceiveMsg(Message msg) //서버로 부터 받은 메세지 db에 저장
    {
        TYPE dbType = null;
        switch (msg.getMessageType())
        {
            case FILE:

                switch (msg.getFileType())
                {
                    case Message.FileMessage.PHOTO:

                        dbType = TYPE.PHOTO;
                        break;

                    case Message.FileMessage.VIDEO:

                        dbType = TYPE.VIDEO;
                        break;

                    case Message.FileMessage.FILE:

                        dbType = TYPE.FILE;
                        break;

                    default:
                        return;
                }
                break;

            case SYSTEM:

                switch (msg.getSystemType())
                {
                    case Message.SystemMessage.ENTER:
                        dbType = TYPE.ENTER;
                        break;

                    case Message.SystemMessage.EXIT:
                        dbType = TYPE.EXIT;
                        break;

                    default:
                        return;
                }
                break;

            case TALK:

                switch (msg.getTalkType())
                {
                    case Message.TalkMessage.TALK:
                        dbType = TYPE.TALK;
//                        try
//                        {
//                            String roomID = msg.getRoomID();
//                            int room_idx = msg.getRoom_idx();
//                            String content = msg.getContent();
//
//                            JSONObject object = new JSONObject(content);
//                            String talkContent = object.optString("talk");
//                            String server_receivetime = msg.getServer_recieve_time();
//                            String server_msg_idx = msg.getServer_msg_idx();
//                            String sender_name = msg.getSender_nickname();
//                            int sender_idx = msg.getSender_idx();
//                            int unread_users = msg.getUnread_users();
//
//                            String sql = "INSERT OR REPLACE INTO Chatroom_"+roomID+"_Message";
//                            sql += "(server_msg_idx, sender_idx, sender_name, room_idx, content, server_receivetime, unread_users) VALUES";
//                            sql += "("+server_msg_idx+", "+sender_idx+", '"+sender_name+"', "+room_idx+", '"+content+"', '"+server_receivetime+"', "+unread_users+")";
//
//                            db.execSQL(sql);
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
                        break;

                    default:
                        return;
                }
                break;

            default:
                Log.e("ChatDBManager - saveReceiveMsg : ", "dbType is Null");
                return;
        }

        int sender_idx = msg.getSender_idx();
        String roomID = msg.getRoomID();
        int room_idx = msg.getRoom_idx();
        String content = msg.getContent();
        String server_msg_idx = msg.getServer_msg_idx();
        String sender_name = msg.getSender_nickname();
        String server_receivetime = msg.getServer_recieve_time();
        int unread_users = msg.getUnread_users();

        String sql = "INSERT OR REPLACE INTO Chatroom_"+roomID+"_Message ";
        sql += "(type, server_msg_idx, sender_idx, sender_name, room_idx, content, server_receivetime, unread_users)";
        sql += " VALUES ('";
        sql += dbType.toString()+"' ,"+server_msg_idx+", "+sender_idx+", '"+sender_name+"', "+room_idx+", '"+content+"', '"+server_receivetime+"', "+unread_users+")";
        db.execSQL(sql);
    }

    public void updateReceiveMsg(Message msg)
    {
        String roomID = msg.getRoomID();
        String client_msg_idx = msg.getClient_msg_idx();
        String server_msg_idx = msg.getServer_msg_idx();
        String server_receivetime = msg.getServer_recieve_time();
        int unread_users = msg.getUnread_users();

        String sql = "UPDATE Chatroom_"+roomID+"_Message SET server_receivetime = '"+server_receivetime+"', server_msg_idx = "+server_msg_idx+", unread_users = "+unread_users+" WHERE client_msg_idx = "+client_msg_idx+"";
        db.execSQL(sql);
    }

    public JSONArray selectMsgTable(String sql)
    {
        JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;
            while (cursor.moveToNext())
            {
                String type = cursor.getString(0);
                int client_msg_idx = cursor.getInt(1);
                int server_msg_idx = cursor.getInt(2);
                int sender_idx = cursor.getInt(3);
                String sender_name = cursor.getString(4);
                int room_idx = cursor.getInt(5);
                String content = cursor.getString(6);
                String client_sendtime = cursor.getString(7);
                String server_receivetime = cursor.getString(8);
                String readtime = cursor.getString(9);
                int unread_users = cursor.getInt(10);
                int read = cursor.getInt(11);

                recode = new JSONObject();
                recode.put("type", type);
                recode.put("client_msg_idx", client_msg_idx);
                recode.put("server_msg_idx", server_msg_idx);
                recode.put("sender_idx", sender_idx);
                recode.put("sender_name", sender_name);
                recode.put("room_idx", room_idx);
                recode.put("content", content);
                recode.put("client_sendtime", client_sendtime);
                recode.put("server_receivetime", server_receivetime);
                recode.put("readtime", readtime);
                recode.put("unread_users", unread_users);
                recode.put("read", read);

                recodes.put(count, recode);
                count++;
            }

            return recodes;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public String getUserIdFromTable(String sql)
    {
        String result = "";

        Cursor cursor = null ;

        cursor = db.rawQuery(sql, null) ;
        String userID = "";
        while (cursor.moveToNext()){
            int idx = cursor.getInt(0);
            String roomID = cursor.getString(1);
            int userIdx = cursor.getInt(2);
            userID = cursor.getString(3);
        }

        result = userID;

        return result;
    }

    public int getRecodeCount(String sql)
    {
        int count = 0;

        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;
            while (cursor.moveToNext())
            {
                count++;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            return count;
        }

    }


    public boolean isExistedTable(String table_name)
    {
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name ='"+table_name+"'";

        int count = 0;

        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;
            while (cursor.moveToNext())
            {
                count++;
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();

        }

        finally
        {
            if(count < 1)
                return false;
            else
                return true;
        }

    }

    // 서버에서 보내준 unread_users 내용을 클라이언트 데이터베이승에 적용함
    public void someoneReadMsg(Message msg)
    {
        try
        {
            String roomID = msg.getRoomID();
            String content = msg.getContent();

            JSONArray recodes = new JSONArray(content);
            for(int i=0; i<recodes.length(); ++i)
            {
                int unread_users = recodes.getJSONObject(i).optInt("unread_users");
                int server_msg_idx = recodes.getJSONObject(i).optInt("server_msg_idx");

                String sql = "SELECT * FROM Chatroom_"+roomID+"_Message WHERE server_msg_idx = "+server_msg_idx+"";
                int db_unread_users = selectMsgTable(sql).getJSONObject(0).optInt("unread_users");

                if(db_unread_users >= unread_users)
                {
                    sql = "UPDATE Chatroom_"+roomID+"_Message SET unread_users = "+unread_users+" WHERE server_msg_idx = "+server_msg_idx+"";
                    db.execSQL(sql);
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //내가 메시지를 읽으면 unread_users-1, read 1로 업데이트
    public void IReadMsg(String roomID)
    {
        String sql = "UPDATE Chatroom_"+roomID+"_Message SET unread_users = unread_users-1, read = 1 WHERE read = 0";
        db.execSQL(sql);
    }

    public void someoneReadMsg(String roomID, int myID)
    {
        String sql = "UPDATE Chatroom_"+roomID+"_Message SET unread_users = unread_users-1 WHERE sender_idx <> "+myID+"";
        db.execSQL(sql);
    }

    // 이하 채팅리스트 리사이클러뷰에 필요한 정보와 관련된 메소드
    public void createTravelInfoTable(String roomID)
    {
        //채팅방 정보 테이블 생성
        String sql = "CREATE TABLE IF NOT EXISTS Chatroom_"+roomID+"_travelInfo";
        sql += "(idx INTEGER PRIMARY KEY AUTOINCREMENT, roomID TEXT, travelInfo TEXT)";;
        db.execSQL(sql);
    }

    public void saveTravelInfo(String roomID, String mTravelInfo)
    {
        String sql = "INSERT INTO Chatroom_"+roomID+"_travelInfo (roomID, travelInfo ) ";
        sql += "VALUES ('"+ roomID+"', '"+mTravelInfo+"')";
        db.execSQL(sql);
    }

    public JSONArray selectTravelinfoTable(String sql)
    {
        JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;
            if (cursor != null && cursor.getCount() != 0)
            {
                cursor.moveToLast();
                int index = cursor.getInt(0);
                String roomID = cursor.getString(1);
                String travelInfo = cursor.getString(2);

                recode = new JSONObject();
                recode.put("idx", index);
                recode.put("roomIdx", roomID);
                recode.put("travelInfo", travelInfo);


                recodes.put(count, recode);
            }

            return recodes;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray selectLastMsgTable(String sql)
    {
        JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
            Cursor cursor = null ;

            cursor = db.rawQuery(sql, null) ;

            if (cursor != null && cursor.getCount() != 0)
            {
                cursor.moveToLast();

                String type = cursor.getString(0);
                int client_msg_idx = cursor.getInt(1);
                int server_msg_idx = cursor.getInt(2);
                int sender_idx = cursor.getInt(3);
                String sender_name = cursor.getString(4);
                int room_idx = cursor.getInt(5);
                String content = cursor.getString(6);
                String client_sendtime = cursor.getString(7);
                String server_receivetime = cursor.getString(8);
                String readtime = cursor.getString(9);
                int unread_users = cursor.getInt(10);
                int read = cursor.getInt(11);

                recode = new JSONObject();
                recode.put("result", "isData");
                recode.put("type", type);
                recode.put("client_msg_idx", client_msg_idx);
                recode.put("server_msg_idx", server_msg_idx);
                recode.put("sender_idx", sender_idx);
                recode.put("sender_name", sender_name);
                recode.put("room_idx", room_idx);
                recode.put("content", content);
                recode.put("client_sendtime", client_sendtime);
                recode.put("server_receivetime", server_receivetime);
                recode.put("readtime", readtime);
                recode.put("unread_users", unread_users);
                recode.put("read", read);

                recodes.put(count, recode);
            }
            else if(cursor == null)
            {
                recode = new JSONObject();
                recode.put("result", "noData");
                recodes.put(count, recode);
            }

            return recodes;

        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray selectEnterMsgTable(String sql)
    {
        JSONArray recodes = new JSONArray();
        JSONObject recode = null;
        int count = 0;
        try
        {
            Cursor cursor = null ;
            cursor = db.rawQuery(sql, null) ;

            while (cursor.moveToNext())
            {
                String type = cursor.getString(0);
                int client_msg_idx = cursor.getInt(1);
                int server_msg_idx = cursor.getInt(2);
                int sender_idx = cursor.getInt(3);
                String sender_name = cursor.getString(4);
                int room_idx = cursor.getInt(5);
                String content = cursor.getString(6);
                String client_sendtime = cursor.getString(7);
                String server_receivetime = cursor.getString(8);
                String readtime = cursor.getString(9);
                int unread_users = cursor.getInt(10);
                int read = cursor.getInt(11);

                recode = new JSONObject();
                recode.put("result", "isData");
                recode.put("type", type);
                recode.put("client_msg_idx", client_msg_idx);
                recode.put("server_msg_idx", server_msg_idx);
                recode.put("sender_idx", sender_idx);
                recode.put("sender_name", sender_name);
                recode.put("room_idx", room_idx);
                recode.put("content", content);
                recode.put("client_sendtime", client_sendtime);
                recode.put("server_receivetime", server_receivetime);
                recode.put("readtime", readtime);
                recode.put("unread_users", unread_users);
                recode.put("read", read);

                recodes.put(count, recode);
                count++;
            }

            return recodes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
