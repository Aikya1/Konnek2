package com.android.konnek2.call.core.models;

import android.content.Context;

import com.android.konnek2.call.R;
import com.android.konnek2.call.core.utils.ChatUtils;
import com.android.konnek2.call.core.utils.UserFriendUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.db.models.DialogNotification;
import com.android.konnek2.call.db.models.DialogOccupant;
import com.android.konnek2.call.db.models.Message;
import com.android.konnek2.call.services.model.QMUser;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;




import java.io.Serializable;
import java.util.List;

/**
 * Created by pelipets on 3/13/17.
 */

public class DialogSearchWrapper implements Serializable {

    private QBChatDialog chatDialog;

    private QMUser opponentUser;

    private String label;

    public DialogSearchWrapper (Context context, DataManager dataManager, QBChatDialog chatDialog) {
        this.chatDialog = chatDialog;
        transform(context, dataManager);
    }

    private void transform(Context context, DataManager dataManager){
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager().getDialogOccupantsListByDialogId(chatDialog.getDialogId());

        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            QMUser currentUser = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
            opponentUser = ChatUtils.getOpponentFromPrivateDialog(currentUser, dialogOccupantsList);
        } else {
            List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);
            Message message = dataManager.getMessageDataManager().getLastMessageWithTempByDialogId(dialogOccupantsIdsList);
            DialogNotification dialogNotification = dataManager.getDialogNotificationDataManager().getLastDialogNotificationByDialogId(dialogOccupantsIdsList);
            label = ChatUtils.getDialogLastMessage(
                    context.getResources().getString(R.string.cht_notification_message),
                    message,
                    dialogNotification);
        }
    }

    public QBChatDialog getChatDialog() {
        return chatDialog;
    }

    public QMUser getOpponentUser() {
        return opponentUser;
    }

    public String getLabel() {
        return label;
    }
}
