package com.example.hyeoukloginchat.cloudmessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.hyeoukloginchat.MainActivity;
import com.example.hyeoukloginchat.R;
import com.google.firebase.messaging.RemoteMessage;

//클라우드 메세지 알림
public class firebasecloudmessagingservice extends  com.google.firebase.messaging.FirebaseMessagingService {

    private  static final  String tag ="FirebaseMsgService";
    private  String msg,title;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(tag,"onMessageReceived");
        title =remoteMessage.getNotification().getTitle();
        msg =remoteMessage.getNotification().getBody();


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);

        NotificationCompat.Builder mbuilder  = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.eulji)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1,1000});

        NotificationManager notificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,mbuilder.build());
       mbuilder.setContentIntent(contentIntent);

    }
}
