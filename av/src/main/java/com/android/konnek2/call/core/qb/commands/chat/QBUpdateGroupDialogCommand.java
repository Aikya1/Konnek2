package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.db.managers.DataManager;
import com.quickblox.chat.model.QBChatDialog;


import java.io.File;

public class QBUpdateGroupDialogCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBUpdateGroupDialogCommand(Context context, QBChatHelper chatHelper,
            String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, QBChatDialog dialog, File file) {
        Intent intent = new Intent(QBServiceConsts.UPDATE_GROUP_DIALOG_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, dialog);
        intent.putExtra(QBServiceConsts.EXTRA_FILE, file);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        QBChatDialog chatDialog = (QBChatDialog) extras.getSerializable(QBServiceConsts.EXTRA_DIALOG);
        File file = (File) extras.getSerializable(QBServiceConsts.EXTRA_FILE);

        if(file == null) {
            chatDialog = chatHelper.updateDialog(chatDialog);
        } else {
            chatDialog = chatHelper.updateDialog(chatDialog, file);
        }

        if (chatDialog != null) {
            DataManager.getInstance().getQBChatDialogDataManager().update(chatDialog);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(QBServiceConsts.EXTRA_DIALOG, chatDialog);

        return bundle;
    }
}