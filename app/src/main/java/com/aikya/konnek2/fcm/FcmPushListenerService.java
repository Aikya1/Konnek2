package com.aikya.konnek2.fcm;

import android.os.Bundle;
import android.util.Log;

import com.aikya.konnek2.utils.helpers.notification.ChatNotificationHelper;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_DIALOG_ID;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_TYPE;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_USER_ID;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_VOIP_TYPE;


public class FcmPushListenerService extends QBFcmPushListenerService {
    private String TAG = FcmPushListenerService.class.getSimpleName();

    /*private ChatNotificationHelper chatNotificationHelper;

    public FcmPushListenerService() {
        this.chatNotificationHelper = new ChatNotificationHelper(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);

        //Here the parameter data (map) does not display the Dialog_ID and User_ID in case of 1 - 1 AUDIO CALLS
        //But it works fine when a text message is sent to the user.
        //How do I customize the notification message? how can I customize t

        String userId = (String) data.get(ChatNotificationHelper.USER_ID);
        String pushMessage = (String) data.get(ChatNotificationHelper.MESSAGE);
        String dialogId = (String) data.get(ChatNotificationHelper.DIALOG_ID);
        String pushMessageType = (String) data.get(ChatNotificationHelper.MESSAGE_TYPE);

        Log.v(TAG, "sendPushMessage\n" + "Message: " + pushMessage + "\nUser ID: " + userId + "\nDialog ID: " + dialogId);

        Bundle extras = new Bundle();
        extras.putString(ChatNotificationHelper.USER_ID, userId);
        extras.putString(ChatNotificationHelper.MESSAGE, pushMessage);
        extras.putString(ChatNotificationHelper.DIALOG_ID, dialogId);
        extras.putString(ChatNotificationHelper.MESSAGE_TYPE, pushMessageType);


        chatNotificationHelper.parseChatMessage(extras);
    }*/


    private ChatNotificationHelper chatNotificationHelper;

    public FcmPushListenerService() {
        this.chatNotificationHelper = new ChatNotificationHelper(this);
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);

        String userId = (String) data.get(MESSAGE_USER_ID);
        String pushMessage = (String) data.get(MESSAGE);
        String dialogId = (String) data.get(MESSAGE_DIALOG_ID);
        String pushMessageType = (String) data.get(MESSAGE_TYPE);
        String pushVOIPType = (String) data.get(MESSAGE_VOIP_TYPE);

        Log.v(TAG, "sendPushMessage\n" + "Message: " + pushMessage + "\nUser ID: " + userId + "\nDialog ID: " + dialogId);

        Bundle extras = new Bundle();
        extras.putString(MESSAGE_USER_ID, userId);
        extras.putString(MESSAGE, pushMessage);
        extras.putString(MESSAGE_DIALOG_ID, dialogId);
        extras.putString(MESSAGE_TYPE, pushMessageType);
        extras.putString(MESSAGE_VOIP_TYPE, pushVOIPType);

        chatNotificationHelper.parseChatMessage(extras);
    }


}
