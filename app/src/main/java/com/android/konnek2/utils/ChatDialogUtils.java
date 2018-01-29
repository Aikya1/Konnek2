package com.android.konnek2.utils;

import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.utils.ChatUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
;

public class ChatDialogUtils {

    public static String getTitleForChatDialog(QBChatDialog chatDialog, DataManager dataManager) {
        if (QBDialogType.GROUP.equals(chatDialog.getType())) {
            return chatDialog.getName();
        } else {
            Integer currentUserId = AppSession.getSession().getUser().getId();
            return ChatUtils.getFullNameById(dataManager, getPrivateChatOpponentId(chatDialog, currentUserId));
        }
    }

    public static Integer getPrivateChatOpponentId(QBChatDialog chatDialog, Integer currentUserId){
        for (Integer opponentID : chatDialog.getOccupants()){
            if (!opponentID.equals(currentUserId)){
                return opponentID;
            }
        }

        return 0;
    }
}
