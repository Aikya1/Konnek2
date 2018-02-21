package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.Command;
import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.db.managers.DataManager;
import com.android.konnek2.call.services.model.QMUser;
import com.quickblox.chat.model.QBChatDialog;

import java.io.File;

/**
 * Created by rajeev on 16/2/18.
 */

public class QBRemoveUserFromGroupCommand extends ServiceCommand {


    private QBChatHelper chatHelper;

    public QBRemoveUserFromGroupCommand(Context context, QBChatHelper chatHelper,
                                        String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }


    public static void start(Context context, QBChatDialog dialog, QMUser selectedUser) {
        Intent intent = new Intent(QBServiceConsts.REMOVE_USER_FROM_GROUP_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, dialog);
        intent.putExtra(QBServiceConsts.REMOVE_SELECTED_USER_FROM_GROUP, selectedUser);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        QBChatDialog chatDialog = (QBChatDialog) extras.getSerializable(QBServiceConsts.EXTRA_DIALOG);
        QMUser selectedUser = (QMUser) extras.getSerializable(QBServiceConsts.REMOVE_SELECTED_USER_FROM_GROUP);
        chatHelper.removeUserFromGroup(chatDialog, selectedUser);
        if (chatDialog != null) {
            DataManager.getInstance().getQBChatDialogDataManager().update(chatDialog);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        return bundle;
    }
}
