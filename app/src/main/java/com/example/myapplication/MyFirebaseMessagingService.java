package com.example.myapplication;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;

/**
 * Created by 이진재 on 2018-05-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {  // google notification API를 사용하기 위해 만들어진 서비스
                                                                            // 전체적인 notification을 관리한다
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  // data의 title과 text를 읽어와 안드로이드에 notification을 내보낸다

        if (remoteMessage.getData().size() > 0) {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
            PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "TAG" );
            wakeLock.acquire(3000);

            String title = remoteMessage.getData().get("title");
            String text = remoteMessage.getData().get("text");
            sendNotification(title, text);

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String text) { // notification을 보낼 때 사용하는 함수
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }
        builder = builder
                .setSmallIcon(R.mipmap.ic_splash)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{300, 500, 300, 500})
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0 /* ID of notification */, builder.build());
    }
}
