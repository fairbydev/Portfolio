package com.teamnova.jaepark.travelmate.activities.functionalClass;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.teamnova.jaepark.travelmate.R;
import com.teamnova.jaepark.travelmate.activities.Chat.Message;

public class NotificationAlarm {

    public NotificationAlarm() { }

    public void showAlarm(Context context, Message msg) {

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        String senderName = msg.getSender_nickname();


        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
            /**
             * 누가버전 이하 노티처리
             */
//            Toast.makeText(context.getApplicationContext(),"오레오이상",Toast.LENGTH_SHORT).show();
//            Log.e("notification", "Talkkkkkkkkkkkkkkkkkkkk");
//            BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher);
            BitmapDrawable bitmapDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.meeting);

            Bitmap bitmap = bitmapDrawable.getBitmap();


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.drawable.com_facebook_button_like_icon_selected)
                    .setWhen(System.currentTimeMillis()).
                            setShowWhen(true).
                            setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle("새로운 메이트 발견!")
                    .setContentText(senderName + "님이 여행톡에 참여하였습니다. 확인해보세요!")
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setFullScreenIntent(pendingIntent,true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());

        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                        Toast.makeText(getApplicationContext(),"오레오이상",Toast.LENGTH_SHORT).show();
            /**
             * 오레오 이상 노티처리
             */
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher);
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
            /**
             * 오레오 버전부터 노티를 처리하려면 채널이 존재해야합니다.
             */

            int importance = NotificationManager.IMPORTANCE_HIGH;
            String Noti_Channel_ID = "Noti";
            String Noti_Channel_Group_ID = "Noti_Group";

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(Noti_Channel_ID,Noti_Channel_Group_ID,importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{10000, 0, 10000, 0});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

//                    notificationManager.deleteNotificationChannel("testid"); 채널삭제

            /**
             * 채널이 있는지 체크해서 없을경우 만들고 있으면 채널을 재사용합니다.
             */
            if(notificationManager.getNotificationChannel(Noti_Channel_ID) != null){
//                    Toast.makeText(getApplicationContext(),"채널이 이미 존재합니다.",Toast.LENGTH_SHORT).show();
            }
            else{
//                    Toast.makeText(getApplicationContext(),"채널이 없어서 만듭니다.",Toast.LENGTH_SHORT).show();
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.createNotificationChannel(notificationChannel);
//                    Log.e("로그확인","===="+notificationManager.getNotificationChannel("testid1"));
//                    notificationManager.getNotificationChannel("testid");


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(),Noti_Channel_ID)
                    .setLargeIcon(null).setSmallIcon(R.drawable.meeting)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle("새로운 메이트 발견!")
                    .setContentText(senderName + "님이 여행톡에 참여하였습니다. 확인해보세요!")
                    .setContentIntent(pendingIntent);
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());

        }





    }

}
