package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.chat.model.QBChatDialog;


public class QBLeaveGroupDialogCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBLeaveGroupDialogCommand(Context context, QBChatHelper chatHelper, String successAction,
                                     String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, QBChatDialog chatDialog) {
        Intent intent = new Intent(QBServiceConsts.LEAVE_GROUP_DIALOG_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        QBChatDialog chatDialog = (QBChatDialog) extras.getSerializable(QBServiceConsts.EXTRA_DIALOG);
        chatHelper.leaveRoomChat(chatDialog);

        return extras;
    }
}