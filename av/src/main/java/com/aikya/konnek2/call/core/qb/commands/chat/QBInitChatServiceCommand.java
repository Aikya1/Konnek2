package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.qb.helpers.QBChatRestHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;


public class QBInitChatServiceCommand extends ServiceCommand {

    private QBChatHelper chatHelper;
    private QBChatRestHelper chatRestHelper;

    public QBInitChatServiceCommand(Context context, QBChatHelper chatHelper,
                                    QBChatRestHelper chatRestHelper, String successAction,
                                    String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
        this.chatRestHelper = chatRestHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.INIT_CHAT_SERVICE_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {

        chatRestHelper.initChatService();

        chatHelper.initChatService();
        return extras;
    }
}