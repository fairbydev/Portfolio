package com.teamnova.jaepark.travelmate.activities.Chat;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.teamnova.jaepark.travelmate.activities.functionalClass.CallSharedPreference;

import java.io.File;


public class ChatService extends Service implements OnFileListener {

    Context context;
    private CallSharedPreference callSprf;

    public ChatService()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("ChatService", "onCreate");
        context = getApplicationContext();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("ChatService", "onDestory");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //포어 그라운드
        startForeground(1,new Notification());

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run()
            {

                ChatClient client = ChatClient.getInstance();
                //sharedPreFerence 선언
                callSprf = new CallSharedPreference();
                callSprf.mContext = getApplicationContext();

                while (true)
                {


                    //채팅 소켓 연결
                    String userID = callSprf.getPreferences("memberInfo", "id");
                    String user_nickname = callSprf.getPreferences("memberInfo", "nickname");

                    if(!client.isConnected() && !userID.equals("") && !user_nickname.equals(""))
                    {
                        client.setInitial(getApplicationContext());
                        client.connect();
                    }
                }
            }
        });
        connect.start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCompleteDownload(File file) {

    }

    @Override
    public void onCompleteUpload() {

    }

    @Override
    public void onDownloading(int percent) {

    }

    @Override
    public void onUploading(int percent) {

    }


}
