package com.teamnova.jaepark.travelmate.activities.Chat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;
import com.teamnova.jaepark.travelmate.activities.functionalClass.NotificationAlarm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class ChatClient {

    private static ChatClient client;
    public CallSharedPreference callSprf;

    //서버 정보
    public final String DOMAIN = "poyapo123.cafe24.com";
    public final static int PORT = 8831;
    public final static int DOWNLOAD_FILE_PORT = 10000;
    public final static int UPLOAD_FILE_PORT = 10001;

    //유저 정보
    String userID;
    String userNickname;
    int userIdx;

    //Notification
    Context context;

    //Chat DB
    ChatDBManager dbManager;

    //메시지 수신 리스너
    ArrayList<OnRecieveMessageListener> listeners = new ArrayList<OnRecieveMessageListener>();

    //Socket
    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;
    ReceiveThread receive;
    AliveThread alive;
    SyncThread syncThread;
    ReconnectThread reconnectThread = new ReconnectThread();


    boolean isShutdown = false;


    private ChatClient() {
    }

    public static ChatClient getInstance() {
        if (client == null) {
            Log.e("ChatClient", "new Instance 생성");
            client = new ChatClient();
        }

        return client;
    }

    public void setInitial(Context context) {
        this.context = context;

        //sharedPreFerence 선언
        callSprf = new CallSharedPreference();
        callSprf.mContext = context;

        this.userID = callSprf.getPreferences("memberInfo", "id");
        this.userNickname = callSprf.getPreferences("memberInfo", "nickname");
        this.userIdx = Integer.parseInt(callSprf.getPreferences("memberInfo", "idx"));
        this.dbManager = new ChatDBManager(context);
    }

    public void setOnRecieveMessageListener(OnRecieveMessageListener listener) {
        if (listeners == null)
            this.listeners = new ArrayList<OnRecieveMessageListener>();

        if (listeners.indexOf(listener) == -1)
            this.listeners.add(listener);
    }

    class ReceiveThread extends Thread {

        public ReceiveThread() {

        }

        // 메세지 수신후 DB저장 및 Listener로 전달
        public void run() {
            try {
                while (true) {

                    synchronized (input) {
                        final Message msg = (Message) input.readObject();

                        if (msg != null) {

                            System.out.println("---------- Recieve Message ----------");
                            Message.MessageType type = msg.getMessageType();
                            String senderID = msg.getSenderID();
                            String senderNickname = msg.getSender_nickname();
                            String content = msg.getContent();
                            System.out.println("Message Type : " + type + ", SenderID : " + senderID + ", SenderNickname : " + senderNickname);
                            System.out.println("Content : " + content);


                            switch (type) {

                                case SYSTEM:

                                    int systemType = msg.getSystemType();

                                    switch (systemType) {
                                        case Message.SystemMessage.ALIVE:
                                            System.out.println("System Type : ALIVE");
                                            break;

                                        case Message.SystemMessage.ENTER:
                                            System.out.println("System Type : " + msg.systemMessage.systemType);

                                            String roomID = String.valueOf(msg.getRoom_idx());
                                            String roomName = msg. getRoomName();
                                            System.out.println("RoomIdx : " + roomID + ", RoomName : (" + roomName + ")");


                                            // 다른사람의 talk-talk메시지를 수신하면 해당 id와 idx를 저장함
                                            if (!dbManager.isExistedTable("Chatroom_" + roomID + "_IdList"))
                                            {
                                                //테이블이 없으면 새로 생성 밑 저장
                                                dbManager.createUserIdTable(msg.getRoomID());
                                                dbManager.saveUserId(msg);
                                            }
                                            else
                                            {  //테이블이 있는 경우

                                                // 기존에 저장 내역이 있는지 체크
                                                String sql4ID = "SELECT * FROM Chatroom_" + msg.getRoomID() + "_IdList WHERE user_idx = " + msg.getSender_idx();
                                                String senderIDinClient = dbManager.getUserIdFromTable(sql4ID);
                                                // 기존 내역이 없으면 저장
                                                if (senderIDinClient == null || senderIDinClient.equals(""))
                                                {
                                                    dbManager.saveUserId(msg);
                                                    Log.d("ReceiveThrad", "ID&IDX Save - Room" + msg.getRoomID() + ", ID = " + msg.getSenderID() + ", IDX = " + msg.getSender_idx());
                                                }
                                            }


                                            //채팅방 관련 db 생성/ 메시지 저장
                                            if (msg.getSenderID().equals(userID))
                                            {
                                                if (!dbManager.isExistedTable("Chatroom_" + roomID + "_Message"))
                                                {
                                                    dbManager.createMsgTable(roomID);
                                                    dbManager.saveBelongedRoom(roomID);
                                                    dbManager.saveReceiveMsg(msg);

                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            if (listeners != null && !listeners.isEmpty()) {
                                                                for (OnRecieveMessageListener listener : listeners) {
                                                                    listener.onRecieve(msg);
                                                                }
                                                            } else
                                                                Log.e("ChatClient", "Listener is null OR empty");

                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                            else    // 다른 유저 입장할 경우
                                            {
                                                //메시지 정보 저장
                                                dbManager.saveReceiveMsg(msg);

                                                //노티피케이션 알립
                                                NotificationAlarm noti = new NotificationAlarm();
                                                noti.showAlarm(context, msg);

                                                // 이하 작업은 핸들러를 통해 onReceive로 넘겨 처리함
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (listeners != null && !listeners.isEmpty()) {
                                                            for (OnRecieveMessageListener listener : listeners) {
                                                                listener.onRecieve(msg);
                                                            }
                                                        } else
                                                            Log.e("ChatClient", "Listener is null OR empty");

                                                    }
                                                });
                                                break;
                                            }



                                            //다른 사람이 본인이 만든 방으로 입장 -> 본인이 생성/입장 후 해당 방의 메시지 테이블이 생성되어 있으므로 다른 사람의 입장 메시지는 db테이블 생성없이 바로 저장만 함.
                                            //입장횟수
                                            int enterRecodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + msg.getRoom_idx() + "_Message WHERE type = 'ENTER' AND sender_idx = " + msg.getSender_idx());
                                            //퇴장횟수
                                            int exitRecodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + msg.getRoom_idx() + "_Message WHERE type = 'EXIT' AND sender_idx = " + msg.getSender_idx());

                                            if (enterRecodeCount - exitRecodeCount < 1) {
                                                dbManager.saveReceiveMsg(msg);

                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (listeners != null && !listeners.isEmpty()) {
                                                            for (OnRecieveMessageListener listener : listeners) {
                                                                listener.onRecieve(msg);
                                                            }
                                                        } else
                                                            Log.e("ChatClient", "Listener is null OR empty");

                                                    }
                                                });
                                            }


                                            break;

                                        case Message.SystemMessage.EXIT:

                                            System.out.println("System Type : EXIT");
                                            roomID = String.valueOf(msg.getRoom_idx());
                                            roomName = msg.getRoomName();
                                            System.out.println("RoomID : " + roomID + ", RoomName : " + roomName);

                                            //본인이 나간 경우
                                            if (msg.getSender_idx() == userIdx) {
                                                if(dbManager.isExistedTable("Chatroom_" + roomID + "_Message"))
                                                {
                                                    dbManager.deleteMsgTable(roomID);
                                                    //내가 속한 모임 테이블에 방금 나간 모임 데이터 삭제
                                                    dbManager.deleteBelongedRoom(roomID);
                                                }

                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (listeners != null && !listeners.isEmpty()) {
                                                            for (OnRecieveMessageListener listener : listeners) {
                                                                listener.onRecieve(msg);
                                                            }
                                                        } else
                                                            Log.e("ChatClient", "Listener is null OR empty");

                                                    }
                                                });
                                                break;
                                            }

                                            //다른 누군가가 나간경우
                                            enterRecodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + msg.getRoom_idx() + "_Message WHERE type = 'ENTER' AND sender_idx = " + msg.getSender_idx());
                                            exitRecodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + msg.getRoom_idx() + "_Message WHERE type = 'EXIT' AND sender_idx = " + msg.getSender_idx());

                                            if (enterRecodeCount - exitRecodeCount >= 1) {
                                                dbManager.saveReceiveMsg(msg);

                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (listeners != null && !listeners.isEmpty()) {
                                                            for (OnRecieveMessageListener listener : listeners) {
                                                                listener.onRecieve(msg);
                                                            }
                                                        } else
                                                            Log.e("ChatClient", "Listener is null OR empty");

                                                    }
                                                });
                                            }

                                            break;

                                        case Message.SystemMessage.SYNC:

                                            break;
                                    }
                                    break;

                                case TALK:
                                    int talkType = msg.getTalkType();
                                    if (talkType == Message.TalkMessage.TALK)
                                    {


                                        System.out.println("System Type : RECEIVE : TALK");
                                        String roomID = msg.getRoomID();
                                        System.out.println("RoomID : " + roomID);
                                        content = msg.getContent();
                                        System.out.println("Content : " + content);
                                        String client_msg_idx = msg.getClient_msg_idx();
                                        System.out.println("Client_msg_idx : " + client_msg_idx);
                                        String client_sendtime = msg.getClient_sendtime();
                                        System.out.println("client_sendtime : " + client_sendtime);
                                        String server_msg_idx = msg.getServer_msg_idx();
                                        System.out.println("Server_msg_idx : " + server_msg_idx);
                                        String server_recieve_time = msg.getServer_recieve_time();
                                        System.out.println("Server_msg_idx : " + server_recieve_time);


                                        if (msg.getSenderID().equals(userID))
                                        {
//                                            ChatDBManager chatDBManager = new ChatDBManager(context);
//                                            String sql1 = "SELECT * FROM Chatroom_"+msg.getRoomID()+"_Message";
//                                            JSONArray messagesJA1 = chatDBManager.selectMsgTable(sql1);
//                                            Log.e("ChatClient - TALK:TALK1 ", messagesJA1.toString());

                                            dbManager.updateReceiveMsg(msg);
//                                            System.out.println("System Type : RECEIVE : TALK - updateReceiveMsg 실행");


//                                            String sql2 = "SELECT * FROM Chatroom_"+msg.getRoomID()+"_Message";
//                                            JSONArray messagesJA2 = chatDBManager.selectMsgTable(sql2);
//                                            Log.e("ChatClient - TALK:TALK2 ", messagesJA2.toString());
                                        }
                                        else
                                        {
                                            dbManager.saveReceiveMsg(msg);
//                                            System.out.println("System Type : RECEIVE : TALK - saveReceiveMsg 실행");


                                            // 다른사람의 talk-talk메시지를 수신하면 해당 id와 idx를 저장함
                                            if (!dbManager.isExistedTable("Chatroom_" + roomID + "_IdList"))
                                            {
                                                //테이블이 없으면 새로 생성 밑 저장
                                                dbManager.createUserIdTable(msg.getRoomID());
                                                dbManager.saveUserId(msg);
                                            }
                                            else
                                            {  //테이블이 있는 경우

                                                // 기존에 저장 내역이 있는지 체크
                                                String sql4ID = "SELECT * FROM Chatroom_" + msg.getRoomID() + "_IdList WHERE user_idx = " + msg.getSender_idx();
                                                String senderIDinClient = dbManager.getUserIdFromTable(sql4ID);
                                                // 기존 내역이 없으면 저장
                                                if (senderIDinClient == null || senderIDinClient.equals(""))
                                                {
                                                    dbManager.saveUserId(msg);
                                                    Log.d("ReceiveThrad", "ID&IDX Save - Room" + msg.getRoomID() + ", ID = " + msg.getSenderID() + ", IDX = " + msg.getSender_idx());
                                                }
                                            }
                                        }
                                        Log.d("ReceiveThrad", "Talk Msg Come");


                                    }
                                    else if (msg.talkMessage.talkType == Message.TalkMessage.READ)
                                    {
                                        Log.d("ReceiveThrad", "READ Msg Come");
                                    }

                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {


//                                            NotificationAlarm noti = new NotificationAlarm();
//                                            noti.showAlarm(context, msg);

                                            if (listeners != null && !listeners.isEmpty()) {
                                                for (OnRecieveMessageListener listener : listeners) {
                                                    listener.onRecieve(msg);
                                                }
                                            } else
                                                Log.e("ChatClient", "Listener is null OR empty");
                                        }
                                    });

                                    break;

                                case FILE:

                                    break;

                                default:

                                    break;
                            }

                            System.out.println("-------------------------------------");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                reConnect();
            }
        }
    }

    class SendThread extends Thread {
        Message sendMsg;

        public SendThread(Message msg) {
            this.sendMsg = msg;
        }

        public void run() {
            try {
                while (true) {
                    if (socket.isConnected()) {
                        if (sendMsg != null) {

                            synchronized (output) {

                                System.out.println("-----send msg-----");
                                Message.MessageType messageType = sendMsg.getMessageType();
                                System.out.println("Type : " + messageType);
                                String senderID = sendMsg.getSenderID();
                                System.out.println("SenderID : " + senderID);
                                String senderName = sendMsg.getSender_nickname();
                                System.out.println("SenderName : " + senderName);

                                if (messageType == Message.MessageType.SYSTEM) {
                                    int systemType = sendMsg.getSystemType();
                                    switch (systemType) {
                                        case Message.SystemMessage.ALIVE:
                                            System.out.println("System Type : ALIVE");
                                            sendMsg.setRoomID("ALIVE RoomID");
                                            sendMsg.setRoomName("ALIVE RoomName");
                                            System.out.println("ALIVE CONTENT : " + sendMsg.getContent());
                                            break;

                                        case Message.SystemMessage.ENTER:
                                            System.out.println("Enter RoomID : " + sendMsg.getRoomID());
                                            System.out.println("Enter Room_idx : " + sendMsg.getRoomID());
                                            System.out.println("System Type : ENTER");
                                            break;

                                        case Message.SystemMessage.EXIT:

                                            System.out.println("System Type : EXIT");
                                            break;

                                        case Message.SystemMessage.SYNC:
                                            System.out.println("System Type : SYNC");
                                            break;
                                    }
                                } else if (messageType == Message.MessageType.TALK) {
                                    int talkType = sendMsg.getTalkType();

                                    switch (talkType) {

                                        case Message.TalkMessage.TALK:
                                            System.out.println("System Type : TALK");
                                            String roomID = sendMsg.getRoomID();
                                            System.out.println("RoomID : " + roomID);
                                            String content = sendMsg.getContent();
                                            System.out.println("Content : " + content);
                                            String client_msg_idx = sendMsg.getClient_msg_idx();
                                            System.out.println("Client_msg_idx : " + client_msg_idx);
                                            String client_sendtime = sendMsg.getClient_sendtime();
                                            System.out.println("client_sendtime : " + client_sendtime);
                                            String server_msg_idx = sendMsg.getServer_msg_idx();
                                            System.out.println("Server_msg_idx : " + server_msg_idx);
                                            String server_recieve_time = sendMsg.getServer_recieve_time();
                                            System.out.println("Server_msg_idx : " + server_recieve_time);
                                            break;

                                        case Message.TalkMessage.READ:
                                            System.out.println("System Type : READ");
                                            int room_idx = sendMsg.getRoom_idx();
                                            System.out.println("Read : room_idx : " + room_idx);
                                            break;

                                        default:
                                            System.out.println("String Type is Null");
                                            break;
                                    }
                                }
                                else if (messageType == Message.MessageType.FILE) { }
                                else { }

                                System.out.println("------------------");

                                //Message 전송
                                output.writeObject(sendMsg);
                                output.flush();

                                break;
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                reConnect();
            }
        }
    }

    class AliveThread extends Thread {
        Message aliveMsg;

        public AliveThread() {
            aliveMsg = new Message().setSenderID(userID)
                                    .setSender_idx(userIdx)
                                    .setContent("aliveMsg from client" + userID)
                                    .setSender_nickname(userNickname)
                                    .setSystemMessage()
                                    .setSystemType(Message.SystemMessage.ALIVE)
                                    .complete();
        }

        //사용자 정보 서버에 전달
        public void run() {
            try {
                while (true) {
                    sendMsg(aliveMsg);
                    sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                reConnect();
            }

        }
    }

    class SyncThread extends Thread {
        Message syncMsg;

        public SyncThread() {
            syncMsg = new Message().setSenderID(userID)
                    .setSender_idx(userIdx)
                    .setContent("aliveMsg from client" + userID)
                    .setSender_nickname(userNickname)
                    .setSystemMessage()
                    .setSystemType(Message.SystemMessage.SYNC)
                    .complete();
        }

        public void run() {
            sendMsg(syncMsg);
        }
    }

    class ReconnectThread extends Thread {

        public void run() {

            if (receive != null) {
                if (receive.isAlive())
                    receive.interrupt();
            }

            if (alive != null) {
                if (alive.isAlive())
                    alive.interrupt();
            }

            if (syncThread != null) {
                if (syncThread.isAlive())
                    syncThread.interrupt();
            }

            try {
                if (input != null)
                    input.close();

                if (output != null)
                    output.close();

                if (socket != null) {
                    if (socket.isConnected()) {
                        socket.close();
                    }

                }

                try {
                    Thread.sleep(2000);
                    connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void connect() {
        if (this.context != null) {
            //연결후 바로 ReceiveThread 시작
            Thread connect = new Thread(new Runnable() {
                @Override
                public void run() {

                    Handler handler = new Handler(Looper.getMainLooper());

                    try {
                        socket = new Socket(DOMAIN, PORT);
                        socket.setSoTimeout(15000);
                        socket.setKeepAlive(true);
                        output = new ObjectOutputStream(socket.getOutputStream());
                        input = new ObjectInputStream(socket.getInputStream());

                        //KeepAlive
                        alive = new AliveThread();
                        alive.start();

                        //메세지 수신
                        receive = new ReceiveThread();
                        receive.start();

                        //메세지 동기화
                        syncThread = new SyncThread();
                        syncThread.start();

                        Log.i("Chat Client", "is Connected");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(context, "채팅 서버와 연결되었습니다.", Toast.LENGTH_SHORT).show();

                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Chat Client", "Attempting to connect...");
                        reConnect();

                    }

                }
            });
            connect.start();
        } else {
            Log.d("Chat Client", "setInital() method must be preceeded.");
        }
    }

    public void reConnect() {
        if (!reconnectThread.isAlive()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "서버와의 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "재연결 중입니다.", Toast.LENGTH_SHORT).show();
                }
            });

            reconnectThread = new ReconnectThread();
            reconnectThread.start();
        }
    }

    public boolean isConnected() {
        if (socket != null) {
            if (socket.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public void sendMsg(Message message) {
        //메시지 sqlite에 저장 후 SendThread를 통해 메시지 전송

        if (!isConnected()) {
            Toast.makeText(context, "서버와 연결이 끊어진 상태입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message != null) {
            if (dbManager.isExistedTable("Chatroom_" + message.getRoom_idx() + "_Message")) {
                if (message.getMessageType() == Message.MessageType.FILE) {
                    dbManager.saveSendMsg(message);
                } else if (message.getTalkType() == Message.TalkMessage.TALK) {
//                    dbManager.saveSendMsg(message);   // 중복저장으로 인해 주석 처리하여 막아둠
                } else if (message.getSystemType() == Message.SystemMessage.ENTER) {
                    // 해당 채팅방 메시지 db테이블이 존재할 경우
                    if (dbManager.isExistedTable("Chatroom_" + message.getRoom_idx() + "_Message")) {
                        int recodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + message.getRoom_idx() + "_Message WHERE type = 'ENTER' AND sender_idx = " + message.getSender_idx());
                        if (recodeCount < 1)
                            dbManager.saveSendMsg(message);
                    }
                } else if (message.getSystemType() == Message.SystemMessage.EXIT) {
                    if (dbManager.isExistedTable("Chatroom_" + message.getRoom_idx() + "_Message")) {
                        int recodeCount = dbManager.getRecodeCount("SELECT * FROM Chatroom_" + message.getRoom_idx() + "_Message WHERE type = 'EXIT' AND sender_idx = " + message.getSender_idx());
                        if (recodeCount < 1)
                            dbManager.saveSendMsg(message);
                    }
                } else {

                }
            }else{//해당 방의 메시지 테이블이 존재하지 않는 경우
                if (message.systemMessage.systemType == Message.SystemMessage.ALIVE) { }
                else
                    {
                        Log.e("sendMsg()", "Chatroom_" + message.getRoom_idx() + "_Message Doesn't exist");
                    }
            }

            //서버에 메시지 전송
            SendThread send = new SendThread(message);
            send.start();

        } else {
            Log.e("sendMsg()", "message is null");
        }


    }

}
