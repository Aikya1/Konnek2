package com.aikya.konnek2.utils.helpers.notification;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.service.CallService;
import com.aikya.konnek2.utils.helpers.SharedHelper;
import com.aikya.konnek2.call.core.models.NotificationEvent;
import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.utils.SystemUtils;

import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_DIALOG_ID;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_USER_ID;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_VOIP_TYPE;


public class ChatNotificationHelper {

    public static final String MESSAGE = "message";
    public static final String DIALOG_ID = "dialog_id";
    public static final String USER_ID = "user_id";
    public static final String MESSAGE_TYPE = "type";


    private Context context;
    private SharedHelper appSharedHelper;
    private String dialogId;
    private int userId;

    private static String message;
    private static boolean isLoginNow;
    private static String messageType;
    private static String messageTypeVOIP;


    public ChatNotificationHelper(Context context) {
        this.context = context;
        appSharedHelper = App.getInstance().getAppSharedHelper();
    }

    public void parseChatMessage(Bundle extras) {

      /*  if (extras.getString(ChatNotificationHelper.MESSAGE) != null) {
            message = extras.getString(ChatNotificationHelper.MESSAGE);
        }

        if (extras.getString(ChatNotificationHelper.USER_ID) != null) {
            userId = Integer.parseInt(extras.getString(ChatNotificationHelper.USER_ID));
        }

        if (extras.getString(ChatNotificationHelper.DIALOG_ID) != null) {
            dialogId = extras.getString(ChatNotificationHelper.DIALOG_ID);
        }

        if (extras.getString(ChatNotificationHelper.MESSAGE_TYPE) != null) {
            messageType = extras.getString(ChatNotificationHelper.MESSAGE_TYPE);
        }

        */

        message = extras.getString(MESSAGE);
        userId = extras.getString(MESSAGE_USER_ID) == null ? 0 : Integer.parseInt(extras.getString(MESSAGE_USER_ID));
        dialogId = extras.getString(MESSAGE_DIALOG_ID);
        messageType = extras.getString(MESSAGE_TYPE);
        messageTypeVOIP = extras.getString(MESSAGE_VOIP_TYPE);


        boolean callPush = TextUtils.equals(messageType, ConstsCore.PUSH_MESSAGE_TYPE_CALL);

        if (callPush && shouldProceedCall()) {
            CallService.start(context);
            return;
        }

        if (SystemUtils.isAppRunningNow()) {
            return;
        }


        if (isOwnMessage(userId)) {

            return;
        }

        boolean chatPush = userId != 0 && !TextUtils.isEmpty(dialogId);

        if (chatPush) {
            saveOpeningDialogData(userId, dialogId);
            saveOpeningDialog(true);
            sendChatNotification(message, userId, dialogId);
        } else {
            sendCommonNotification(message);
        }

    }


    public void sendChatNotification(String message, int userId, String dialogId) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setTitle(context.getString(R.string.app_name));
        notificationEvent.setSubject(message);
        notificationEvent.setBody(message);
        NotificationManagerHelper.sendChatNotificationEvent(context, userId, dialogId, notificationEvent);
    }

    private void sendCommonNotification(String message) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setTitle(context.getString(R.string.app_name));
        notificationEvent.setSubject(message);
        notificationEvent.setBody(message);
        NotificationManagerHelper.sendCommonNotificationEvent(context, notificationEvent);
    }

    public void saveOpeningDialogData(int userId, String dialogId) {
        appSharedHelper.savePushUserId(userId);
        appSharedHelper.savePushDialogId(dialogId);
    }

    public void saveOpeningDialog(boolean open) {
        appSharedHelper.saveNeedToOpenDialog(open);
    }

    private boolean isOwnMessage(int senderUserId) {
        return appSharedHelper.getUserId() == senderUserId;
    }

    private boolean shouldProceedCall() {

        AppSession.ChatState state = AppSession.getSession().getChatState();
        boolean isAppRun = SystemUtils.isAppRunningNow();

        return !SystemUtils.isAppRunningNow() || AppSession.ChatState.BACKGROUND == AppSession.getSession().getChatState();
    }
}