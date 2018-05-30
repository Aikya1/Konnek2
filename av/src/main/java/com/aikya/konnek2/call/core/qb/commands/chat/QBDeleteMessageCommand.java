package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.chat.model.QBDialogType;

public class QBDeleteMessageCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBDeleteMessageCommand(Context context, QBChatHelper chatHelper, String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, String messageId) {
        Intent intent = new Intent(QBServiceConsts.DELETE_MESSAGE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_MESSAGE_ID, messageId);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {

        String messageId = extras.getString(QBServiceConsts.EXTRA_MESSAGE_ID);
        chatHelper.deleteMessageById(messageId);
        return extras;
    }
}
