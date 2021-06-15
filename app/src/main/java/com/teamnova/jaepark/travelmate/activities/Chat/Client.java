package com.teamnova.jaepark.travelmate.activities.Chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;





public class Client {
//
//    private static Client client;
//
//    private CallSharedPreference callSprf;
//
//    //서버 정보
//    public final String DOMAIN = "poyapo123.cafe24.com";
//    public final static int PORT = 8831;
//    public final static int DOWNLOAD_FILE_PORT = 10000;
//    public final static int UPLOAD_FILE_PORT = 10001;
//
//    //유저 정보
//    String userID;
//    String userName;
//
//    //메시지 수신 리스너
//    ArrayList<OnRecieveMessageListener> listeners = new ArrayList<OnRecieveMessageListener>();
//
//    Socket socket;
//    ObjectOutputStream output;
//    ObjectInputStream input;
//    ReceiveThread receive;
//    AliveThread alive;
//    SyncThread syncThread;
//
//    //ChatDB
//    ChatDBManager dbManager;
//
//    //노피티케이션
//    Context context;
//
//    boolean isShutdown = false;
//
//
//
//
//
//    private Client() {
//
//    }
//
//    public static Client getInstance() {
//        if (client == null) {
//            Log.e("Client", "Client 생성");
//            client = new Client();
//        }
//
//        return client;
//    }
//
//    public void initialSetting(Context context)
//    {
//        this.context = context;
//
//        //sharedPreFerence 선언
//        callSprf = new CallSharedPreference();
//        callSprf.mContext = context;
//        callSprf.getPreferences("memberInfo", "id");
//
//        this.userID = callSprf.getPreferences("memberInfo", "id");
//
//        this.userName = callSprf.getPreferences("memberInfo", "nickname");
//
////        this.dbManager = new ChatDBManager(context);
//
//    }
//
//    public void setOnRecieveMessageListener(OnRecieveMessageListener listener) {
//        this.listeners.add(listener);
//    }
//
//    public void unSetOnRecieveMessageListener(OnRecieveMessageListener listener) {
//        this.listeners.remove(listener);
//    }
//
//    //Download File
////    public void downloadFile(String fileName, OnFileListener fileListener) {
////        DownloadThread thread = new DownloadThread(fileName, fileListener);
////        thread.start();
////    }
//
//    //Upload File
////    public void uploadFile(String fileName, OnFileListener fileListener) {
////        UploadThread thread = new UploadThread(fileName, fileListener);
////        thread.start();
////    }
//
//    //Download File Thread
////    public class DownloadThread extends Thread {
////
////        private String fileName;
////        private OnFileListener fileListener;
////
////        Socket downloadFileSocket;
////        OutputStream download_out;
////        InputStream download_in;
////        DataOutputStream download_dout;
////        DataInputStream download_din;
////
////        public DownloadThread(String fileName, OnFileListener fileListener) {
////            this.fileName = fileName;
////            this.fileListener = fileListener;
////        }
////
////        @Override
////        public void run() {
////            super.run();
////            String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/file/";
////
////            File dir = new File(dirName);
////            if (!dir.exists())
////                dir.mkdir();
////
////            String filePath = dirName + fileName;
////            File file = new File(filePath);
////
////            if (!file.exists())
////                try {
////                    file.createNewFile();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////            try {
////
////                downloadFileSocket = new Socket(DOMAIN, DOWNLOAD_FILE_PORT);
////                downloadFileSocket.setSoTimeout(15000);
////
////                // 데이터를 통신을 위해서 소켓의 스트림 얻기.
////                download_in = downloadFileSocket.getInputStream();
////                download_out = downloadFileSocket.getOutputStream();
////                download_din = new DataInputStream(download_in);
////                download_dout = new DataOutputStream(download_out);
////                System.out.println("서버접속 완료");
////
////                download_dout.writeUTF(fileName);
////
////                System.out.println("서버에 파일 요청을 했습니다.");
////                System.out.println("서버에서 파일 데이터를 받아옵니다.");
////                FileOutputStream fos = new FileOutputStream(filePath);
////                // BufferedOutputStream bos = new BufferedOutputStream(fos);
////                // byte[] buffer = new byte[1024];
////
////                while (true) {
////                    int data = download_din.read(/* buffer */);
////                    if (data == -1)
////                        break;
////                    fos.write(data);
////                    // bos.write(buffer,0,data);
////                    fileListener.onDownloading(0);
////                }
////
////                System.out.println("전송 작업이 완료되었습니다.");
////
////                // 스트림 , 소켓 닫기
////                fos.close();
////                download_dout.close();
////                download_din.close();
////                download_in.close();
////                download_out.close();
////                downloadFileSocket.close();
////
////                fileListener.onCompleteDownload(new File(filePath));
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////
////        }
////    }
//
//    //Upload File Thread
////    public class UploadThread extends Thread {
////
////        String fileName;
////        OnFileListener fileListener;
////
////        public UploadThread(String fileName, OnFileListener fileListener) {
////            this.fileName = fileName;
////            this.fileListener = fileListener;
////        }
////
////        @Override
////        public void run() {
////            super.run();
////
////            try {
////                Socket socket = new Socket(DOMAIN, UPLOAD_FILE_PORT);
////
////                InputStream in = socket.getInputStream();
////                DataInputStream dis = new DataInputStream(in);
////
////                OutputStream out = socket.getOutputStream();
////                DataOutputStream dos = new DataOutputStream(out);
////
////                dos.writeUTF(fileName);
////
////                String dirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/file/";
////                //파일 읽은 후 서버에 전송
////                String filePath = dirName + fileName;
////                FileInputStream fin = new FileInputStream(filePath);
////                System.out.println("서버에 파일을 업로드 중입니다.");
////
////                while (true) { // FileInputStream을 통해 파일을 읽어들여서 소켓의 출력스트림을 통해 출력.
////                    int data = fin.read();
////                    if (data == -1)
////                        break;
////                    dos.write(data);
////                    fileListener.onUploading(0);
////                }
////
////                System.out.println("업로드가 완료되었습니다.");
////
////                // 스트림 , 소켓 닫기
////                fin.close();
////                dos.close();
////                dis.close();
////                out.close();
////                socket.close();
////
////                fileListener.onCompleteUpload();
////
////            } catch (Exception e) {
////                // TODO: handle exception
////                e.printStackTrace();
////            }
////
////        }
////    }
//
//    //Chat
//    public void connect() {
//
//        //연결후 바로 ReceiveThread 시작
//        Thread connect = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    socket = new Socket(DOMAIN, PORT);
//                    socket.setSoTimeout(30000);
//                    output = new ObjectOutputStream(socket.getOutputStream());
//                    input = new ObjectInputStream(socket.getInputStream());
//
//                    //KeepAlive
//                    alive = new AliveThread();
//                    alive.start();
//
//                    //메세지 수신
//                    receive = new ReceiveThread();
//                    receive.start();
//
//                    //메세지 동기화
////                    syncThread = new SyncThread();
////                    syncThread.start();
//
//                    Log.e("Client", "Connect");
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    reConnect();
//                }
//
//            }
//        });
//        connect.start();
//
//    }
//
//    public void disConnect() {
//        isShutdown = true;
//
//        //통신 관련 쓰레드 종료
//        if (receive != null)
//            receive.interrupt();
//        if (alive != null)
//            alive.interrupt();
//        if (syncThread != null)
//            syncThread.interrupt();
//
//        //소켓 종료
//        Thread disconnect = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (socket != null)
//                        socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        disconnect.start();
//    }
//
//    public void reConnect() {
//
//        //통신 관련 쓰레드 종료
//        if (receive != null) {
//            if (receive.isAlive())
//                receive.interrupt();
//        }
//
//        if (alive != null) {
//            if (alive.isAlive())
//                alive.interrupt();
//        }
//
//        if (syncThread != null) {
//            if (syncThread.isAlive())
//                syncThread.interrupt();
//        }
//
//        Thread reConnect = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //기존 소켓 종료
//                    if (socket != null)
//                        socket.close();
//
//                    //재연결
//                    socket = new Socket(DOMAIN, PORT);
//                    socket.setSoTimeout(30000);
//                    output = new ObjectOutputStream(socket.getOutputStream());
//                    input = new ObjectInputStream(socket.getInputStream());
//
//                    //KeepAlive
//                    alive = new AliveThread();
//                    alive.start();
//
//                    //메세지 수신
//                    receive = new ReceiveThread();
//                    receive.start();
//
//                    //메세지 동기화
//                    syncThread = new SyncThread();
//                    syncThread.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    reConnect();
//                }
//
//            }
//        });
//        reConnect.start();
//    }
//
//    public void sendMsg(Message message) {
//        //메세지 타입이 'talk'일 경우 sqlite에 저장(메시지 테이블)
//        Message sendingMsg = message;
//        Message.MessageType mType = message.getMessageType();
//        if (sendingMsg.getMessageType() != null) {
//            if (mType.equals(Message.MessageType.TALK)) {
//                //client_sendtime, client_idx 알아오기
//                String roomID = "room" + message.getRoom_idx();
//                String room_idx = message.getRoom_idx();
////                String tableName = "Chatroom_" + roomID + "_Message";
////                String sql = "SELECT * FROM " + tableName + " ORDER BY client_sendtime DESC LIMIT 1";
////                try {
//                    //메시지에 client_sendtime, client_idx 값 추가
////                    JSONArray recodes = dbManager.selectMsgTable(sql);
////                    JSONObject recode = recodes.getJSONObject(0);
////                    String client_msg_idx = recode.getString("client_msg_idx");
////                    String client_sendtime = recode.getString("client_sendtime");
////                    String content = message.getContent();
////                    JSONObject object = new JSONObject(content);
////                    message.setContent(object.toString());
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//
//                //메시지에 client_sendtime, client_idx 값 추가
//                message.setClient_sendtime("");
//                message.setClient_sendtime("121212");
//
//
//                //서버에 데이터 전송
//                SendThread send = new SendThread(message);
//                send.start();
//            } else {
//                SendThread send = new SendThread(message);
//                send.start();
//            }
//        } else {
//            Log.e("sendMsg", "message is null");
//        }
//    }
//
//    public boolean isConnected() {
//        if (socket != null) {
//            if (socket.isConnected()) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//    }
//
//    class SendThread extends Thread {
//        Message sendMsg;
//
//        public SendThread(Message msg) {
//            this.sendMsg = msg;
//        }
//
//        public void run() {
//            try {
//                while (true) {
//                    if (socket.isConnected()) {
//                        if (sendMsg != null) {
//                            synchronized (output) {
//                                Log.e("SendThread Msg : ", sendMsg.getMessageType() + " / " + sendMsg.getSender_idx() + " : " + sendMsg.getContent());
//                                output.writeObject(sendMsg);
//                                output.flush();
//                                break;
//                            }
//                        }
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    class ReceiveThread extends Thread {
//
//        public ReceiveThread() {
//
//        }
//
//        // 메세지 수신후 Listener로 전달
//        public void run() {
//            try {
//                while (true) {
//                    Message msg = (Message) input.readObject();
//
//                    if (msg != null) {
//
//                        switch (msg.getMessageType()) {
//
//                            case  TALK:
//                                Log.e("msg", msg.getAction());
//                                Log.e("msg", msg.getSenderID() + " : " + msg.getContent());
//
//                                //내가 보낸 메세지 서버로 부터 다시 받을때(ECHO) DB Update
//                                if (msg.getSenderID().equals(userID)) {
////                                    dbManager.updateReceiveMsg(msg);
//
//                                    //방에 전달
//                                    for (int i = 0; i < listeners.size(); ++i) {
//                                        listeners.get(i).onRecieve(msg);
//                                    }
//
//                                } else {
//                                    //chat 서버로 부터 받은 메세지 db에 저장
////                                    dbManager.saveReceiveMsg(msg);
//
//                                    //notification
//                                    if (!ChatRoomActivity.isRunning) {
////                                        sendNotification();
//                                    } else {
//                                        if (!ChatRoomActivity.roomID.equals(msg.getToRoomID())) {
////                                            sendNotification();
//                                        } else {
//                                            //방에 전달
//                                            for (int i = 0; i < listeners.size(); ++i) {
//                                                listeners.get(i).onRecieve(msg);
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//
//                            case  SYSTEM:
//                                switch (msg.systemMessage.systemType){
//                                    case Message.SystemMessage.ENTER:
//                                        //다른 사람이 들어왔다는 메세지 저장
//
//                                        //방에 전달
//                                        for (int i = 0; i < listeners.size(); ++i) {
//                                            listeners.get(i).onRecieve(msg);
//                                        }
//                                        break;
//                                    case Message.SystemMessage.EXIT:
//                                        //다른 사람이 나갔다는 메세지 저장
//
//                                        //방에 전달
//                                        for (int i = 0; i < listeners.size(); ++i) {
//                                            listeners.get(i).onRecieve(msg);
//                                        }
//                                        break;
//                                    case Message.SystemMessage.ALIVE:
//                                        Log.e("alive_msg", msg.getAction());
//                                        break;
//                                    case Message.SystemMessage.SYNC:
//
//                                        Log.e("sync_msg", msg.getContent());
//                                        //동기화 데이터 DB에 저장
//
////                                boolean alreadySendNoti = false;
////                                JSONArray messages = new JSONArray(msg.getContent());
////
////                                for (int i = 0; i < messages.length(); ++i) {
////                                    int sender_idx = messages.getJSONObject(i).getInt("sender_idx");
////
////                                    //내가 보낸 메세지 서버측에서 받았으므로 receive_time 업데이트
////                                    if (userID.equals(Integer.toString(sender_idx))) {
////                                        dbManager.updateReceiveMsg(messages.getJSONObject(i));
////                                    }
////
////                                    //다른 사람 메세지는 내 테이블에 메세지 삽입
////                                    else {
////                                        //메세지 저장
////                                        dbManager.saveReceiveMsg(messages.getJSONObject(i));
////                                        //notification
////                                        if (ChatRoomActivity.isRunning && alreadySendNoti == false) {
//////                                            sendNotification();
//////                                            alreadySendNoti = true;
////                                        } else {
////                                            if (!ChatRoomActivity.roomID.equals(msg.getToRoomID()) && alreadySendNoti == false) {
//////                                                sendNotification();
//////                                                alreadySendNoti = true;
////                                            }
////                                        }
////                                    }
////                                }
////
////                                //방에 전달
////                                for (int j = 0; j < listeners.size(); ++j) {
////                                    listeners.get(j).onRecieve(msg);
////                                }
//
//                                        break;
//                                    default:
//                                        break;
//                                }
//
//                                break;
//
//                            case  FILE:
//
//                                break;
//
//                            default:
//                                Log.e("Client ReceiveThread", "default + sender : " + msg.getSender_idx());
//                                break;
//                        }
//                    }
//
//
//                    if (msg != null) {
//                        switch (msg.getMessageType()) {
//                            case "sync":
//
//
//                                break;
//
//                            //다른 사람이 메세지를 읽으면 서버로부터 받은 un_read_count 최신화
//                            case "read_msg":
//
////                                Log.e("read_msg", "reader_idx : " + msg.getSenderID() + " content :" + msg.getContent());
//
////                                dbManager.someoneReadMsg(msg);
////
////                                //방에 전달
////                                for (int i = 0; i < listeners.size(); ++i) {
////                                    listeners.get(i).onRecieve(msg);
////                                }
//
//                                /*if(msg.getSenderID().equals(userID))
//                                {
//
//                                }
//
//                                else
//                                {
//                                    dbManager.someoneReadMsg(msg);
//
//                                    //방에 전달
//                                    for(int i=0; i<listeners.size(); ++i)
//                                    {
//                                        listeners.get(i).onRecieve(msg);
//                                    }
//
//                                }*/
//
//                                break;
//
//                            case "alive":
//
//                                break;
//
//                            case "talk":
//
//
//                                break;
//
//                            case "entered_room":
//
//
//                                break;
//
//                            case "exited_room":
//
//
//
//                            default:
//
//
//                                break;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (!isShutdown)
//                    reConnect();
//            }
//        }
//    }
//
//
//
//
//    class SyncThread extends Thread {
//        Message syncMsg;
//
//        public SyncThread() {
//
//        }
//
//        public void run() {
////
////            try {
////                //참여중인 모든 채팅방 메시지 동기화
////
////                //참여중인 모든 채팅방 ID 알아오기
////                String sql = "SELECT * FROM Chatroom_List";
////                JSONArray roomRecodes = dbManager.selectRoomTable(sql);
////                for (int i = 0; i < roomRecodes.length(); ++i) {
////                    JSONObject roomRecode = roomRecodes.getJSONObject(i);
////
////                    if (roomRecode.has("room_idx")) {
////                        int roomID = roomRecode.optInt("room_idx");
////
////                        //마지막으로 받은 메세지의 시간을 서버에 전송
////                        sql = "SELECT * FROM Chatroom_" + roomID + "_Message ORDER BY server_receivetime DESC LIMIT 1";
////                        JSONArray msgRecodes = dbManager.selectMsgTable(sql);
////
////                        String lastReceiveTime = msgRecodes.getJSONObject(0).getString("server_receivetime");
////                        JSONObject object = new JSONObject();
////                        object.put("last_receive_time", lastReceiveTime);
////
////                        //서버가 받지 못한 메시지 모두 전송
////                        sql = "SELECT * FROM Chatroom_" + roomID + "_Message WHERE server_receivetime = 0";
////                        msgRecodes = dbManager.selectMsgTable(sql);
////
////                        syncMsg = new Message();
////                        syncMsg.setAction("sync");
////                        syncMsg.setSenderID(userID);
////                        syncMsg.setToRoomID(Integer.toString(roomID));
////
////                        object.put("messages", msgRecodes.toString());
////                        syncMsg.setContent(object.toString());
////
////                        synchronized (output) {
////                            Log.d("syncThread", "room_idx :" + roomID + " data : " + syncMsg.getContent());
////                            output.writeObject(syncMsg);
////                            output.flush();
////                        }
////
////                    }
////
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//        }
//
//    }
//
//    class AliveThread extends Thread {
//        Message aliveMsg;
//
//        public AliveThread() {
//            //메시지 셋팅
//            aliveMsg = new Message();
//            if (aliveMsg.getMessageType().equals(Message.SystemMessage.ALIVE)){
//            // aliveMsg.setSender_idx(sender_idx);
//            Log.d("AliveThread Msg : ", "check alive1");
//            }
//        }
//
//        //사용자 정보 서버에 전달
//        public void run() {
//            try {
//                while (true) {
//                    synchronized (output) {
//                        Log.d("AliveThread Msg : ", "check alive2");
//                        output.writeObject(aliveMsg);
//                        output.flush();
//                    }
//
//                    sleep(15000);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    /*public boolean isConnectedNetwork()
//    {
//        ConnectivityManager manager = (ConnectivityManager)context.getSystemService (Context.CONNECTIVITY_SERVICE);
//        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
//        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
//        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
//        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
//
//        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
//            return true;
//        }else{
//            return false;
//        }
//    }*/
//
////    public void sendNotification() {
////        Intent intent = new Intent(context, ChatActivity.class);
////        //노피티케이션
////        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ChatActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
////        Notification notification = new NotificationCompat.Builder(context.getApplicationContext())
////                .setContentTitle("Woori")
////                .setContentText("새로운 메시지가 도착하였습니다.")
////                .setSmallIcon(R.drawable.fork)
////                .setTicker("새로운 메시지가 도착하였습니다.")
////                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
////                //.setContentIntent(pendingIntent)
////                .setAutoCancel(true)
////                .build();
////
////        notificationManager.notify(7777, notification);
////    }
//
//    @Override
//    public void finalize() {
//        Log.d("Client", "서버와의 소켓 연결 해제됨.");
//    }


}
