package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.users.model.QBUser;

public class QBInitChatsCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBInitChatsCommand(Context context, QBChatHelper chatHelper, String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.INIT_CHATS_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        QBUser user;

        if (extras == null) {
            user = AppSession.getSession().getUser();
        } else {
            user = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        }

        chatHelper.init(user);

        return extras;
    }
}