package com.example.shubchintak;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();
//        Toast.makeText(this, messageTitle, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, messageBody, Toast.LENGTH_SHORT).show();

        Log.d("notti",messageTitle);
        Log.d("notti",messageBody);


//        String click_action = remoteMessage.getNotification().getClickAction();

//        String dataMessage = remoteMessage.getData().get("from_user");
//        String dataFrom = remoteMessage.getData().get("from_user_id");
//        Toast.makeText(this, dataMessage, Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.logoa)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody);

//        Intent resultIntent = new Intent(click_action);
//        resultIntent.putExtra("from_user", dataMessage);
////        resultIntent.putExtra("from_user_id", dataFrom);
//
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        mBuilder.setContentIntent(resultPendingIntent);



        int mNotificationId = (int) System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}
