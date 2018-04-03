package com.aikya.konnek2.utils.helpers;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.aikya.konnek2.call.db.models.DialogOccupant;
import com.aikya.konnek2.call.services.QMUserService;
import com.aikya.konnek2.ui.activities.chats.BaseDialogActivity;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;


public class ActivityUIHelper {

    private BaseActivity baseActivity;

    public ActivityUIHelper(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void showChatMessageNotification(Bundle extras) {
        QMUser senderUser = (QMUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        String message = extras.getString(QBServiceConsts.EXTRA_CHAT_MESSAGE);
        String dialogId = extras.getString(QBServiceConsts.EXTRA_DIALOG_ID);
        if (isChatDialogExist(dialogId) && senderUser != null) {
            message = baseActivity.getString(R.string.snackbar_new_message_title, senderUser.getFullName(), message);
            if (!TextUtils.isEmpty(message)) {
                showNewNotification(getChatDialogForNotification(dialogId), senderUser, message);
            }
        }
    }

    private boolean isChatDialogExist(String dialogId) {
        return DataManager.getInstance().getQBChatDialogDataManager().exists(dialogId);
    }

    private QBChatDialog getChatDialogForNotification(String dialogId) {
        return DataManager.getInstance().getQBChatDialogDataManager().getByDialogId(dialogId);
    }

    public void showContactRequestNotification(Bundle extras) {
        int senderUserId = extras.getInt(QBServiceConsts.EXTRA_USER_ID);
        QMUser senderUser = QMUserService.getInstance().getUserCache().get((long) senderUserId);
        DialogOccupant dialogOccupant = DataManager.getInstance().getDialogOccupantDataManager().getDialogOccupantForPrivateChat(senderUserId);

        if (dialogOccupant != null && senderUser != null) {
            String dialogId = dialogOccupant.getDialog().getDialogId();
            if (isChatDialogExist(dialogId)) {
                String message = baseActivity.getString(R.string.snackbar_new_contact_request_title, senderUser.getFullName());
                if (!TextUtils.isEmpty(message)) {
                    showNewNotification(getChatDialogForNotification(dialogId), senderUser, message);
                }
            }
        }
    }

    private void showNewNotification(final QBChatDialog chatDialog, final QMUser senderUser, String message) {
        baseActivity.hideSnackBar();
        baseActivity.showSnackbar(message, Snackbar.LENGTH_LONG, R.string.dialog_reply,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDialog(chatDialog, senderUser);
                    }
                });

        /*NotificationManager notificationManager = (NotificationManager)
                baseActivity.getSystemService(NOTIFICATION_SERVICE);

        Notification noti = new Notification.Builder(baseActivity.getApplicationContext())
                .setContentTitle("Message" + senderUser.getFullName())
                .setContentText(message)
                .setSmallIcon(R.drawable.app_logo)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .build();

        notificationManager.notify(001, noti);*/


    }

    private void showDialog(QBChatDialog chatDialog, QMUser senderUser) {
        if (baseActivity instanceof BaseDialogActivity) {
            baseActivity.finish();
        }

        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            baseActivity.startPrivateChatActivity(senderUser, chatDialog);
        } else {
            baseActivity.startGroupChatActivity(chatDialog);
        }
    }
}