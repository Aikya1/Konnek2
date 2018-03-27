package com.android.konnek2.utils.helpers.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.android.konnek2.R;
import com.android.konnek2.call.core.models.NotificationEvent;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.QMUserService;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.base.activity.AppSplashActivity;
import com.android.konnek2.ui.activities.main.MainActivity;
import com.android.konnek2.utils.broadcasts.ChatMessageReceiver;
import com.quickblox.chat.model.QBChatDialog;


public class NotificationManagerHelper {

    public final static int NOTIFICATION_ID = NotificationManagerHelper.class.hashCode();
    private static final String TAG = NotificationManagerHelper.class.getSimpleName();

    static String channelId = "com.android.konnek2.ANDROID";
    static CharSequence channelName = "konnek2.ANDROID";

    public static void sendChatNotificationEvent(Context context, int userId, String dialogId,
                                                 NotificationEvent notificationEvent) {

        QBChatDialog chatDialog = DataManager.getInstance().getQBChatDialogDataManager()
                .getByDialogId(dialogId);
        QMUser user = QMUserService.getInstance().getUserCache().get((long) userId);
//        Intent intent = new Intent(context, SplashActivity.class);

//        Intent intent = new Intent(context, AppSplashActivity.class);
        Intent intent = new Intent(context, MainActivity.class);

        intent.putExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, true);
        sendChatNotificationEvent(context, intent, notificationEvent);
        sendNotifyIncomingMessage(context, dialogId);
    }

    private static void sendNotifyIncomingMessage(Context context, String dialogId) {
        Intent intent = new Intent(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendCommonNotificationEvent(Context context,
                                                   NotificationEvent notificationEvent) {

//        Intent intent = new Intent(context, SplashActivity.class);
        Intent intent = new Intent(context, AppSplashActivity.class);
        sendChatNotificationEvent(context, intent, notificationEvent);

    }

    private static void sendChatNotificationEvent(Context context, Intent intent, NotificationEvent notificationEvent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(notificationEvent.getTitle())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationEvent.getSubject()))
                .setContentText(notificationEvent.getBody())
                .setAutoCancel(true)
                .setContentIntent(contentIntent);


        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(getNotificationChannel());
//        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static NotificationChannel getNotificationChannel() {

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);

        notificationChannel.enableLights(true);

        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        return notificationChannel;

    }


    public static void clearNotificationEvent(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}