package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.chat.model.QBDialogType;


public class QBDeleteChatCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBDeleteChatCommand(Context context, QBChatHelper chatHelper, String successAction,
                               String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, String dialogId, int dialogType) {
        Intent intent = new Intent(QBServiceConsts.DELETE_DIALOG_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_TYPE, dialogType);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        String dialogId = extras.getString(QBServiceConsts.EXTRA_DIALOG_ID);
        QBDialogType dialogType = QBDialogType.parseByCode(extras.getInt(QBServiceConsts.EXTRA_DIALOG_TYPE));
        chatHelper.deleteDialog(dialogId, dialogType);
        return extras;
    }
}