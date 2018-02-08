package com.android.konnek2.utils.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.call.CallActivity;
import com.android.konnek2.utils.SystemUtils;
import com.android.konnek2.utils.helpers.notification.ChatNotificationHelper;


public class ChatMessageReceiver extends BroadcastReceiver {

    private static final String TAG = ChatMessageReceiver.class.getSimpleName();
    private static final String callActivityName = CallActivity.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "--- onReceive() ---");

        String activityOnTop = SystemUtils.getNameActivityOnTopStack();

        if (!SystemUtils.isAppRunningNow() && !callActivityName.equals(activityOnTop)) {
            ChatNotificationHelper chatNotificationHelper = new ChatNotificationHelper(context);

            String message = intent.getStringExtra(QBServiceConsts.EXTRA_CHAT_MESSAGE);
            QMUser user = (QMUser) intent.getSerializableExtra(QBServiceConsts.EXTRA_USER);
            String dialogId = intent.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID);

            chatNotificationHelper.saveOpeningDialogData(user.getId(), dialogId);
            chatNotificationHelper.sendChatNotification(message, user.getId(), dialogId);
        }
    }

}