package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;


public class QBJoinGroupChatsCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBJoinGroupChatsCommand(Context context, QBChatHelper chatHelper, String successAction,
                                   String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.JOIN_GROUP_CHAT_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        chatHelper.tryJoinRoomChats();
        return null;
    }
}