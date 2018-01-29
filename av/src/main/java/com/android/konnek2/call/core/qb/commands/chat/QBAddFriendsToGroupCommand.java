package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.core.utils.DbUtils;
import com.android.konnek2.call.db.managers.DataManager;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

public class QBAddFriendsToGroupCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBAddFriendsToGroupCommand(Context context, QBChatHelper chatHelper, String successAction,
                                      String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, String dialogId, ArrayList<Integer> friendIdsList) {
        Intent intent = new Intent(QBServiceConsts.ADD_FRIENDS_TO_GROUP_ACTION, null, context,
                QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friendIdsList);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        String dialogId = extras.getString(QBServiceConsts.EXTRA_DIALOG_ID);
        ArrayList<Integer> friendIdsList = (ArrayList<Integer>) extras.getSerializable(QBServiceConsts.EXTRA_FRIENDS);

        QBChatDialog qbDialog = chatHelper.addUsersToDialog(dialogId, friendIdsList);

        if (qbDialog != null) {
            DataManager.getInstance().getQBChatDialogDataManager().update(qbDialog);
            DbUtils.saveDialogsOccupants(DataManager.getInstance(), qbDialog, true);
        }

        Bundle returnedBundle = new Bundle();
        returnedBundle.putSerializable(QBServiceConsts.EXTRA_DIALOG, qbDialog);

        return returnedBundle;
    }
}