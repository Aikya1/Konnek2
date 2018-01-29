package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.qb.helpers.QBChatRestHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;


public class QBLogoutAndDestroyChatCommand extends ServiceCommand {

    private QBChatRestHelper chatRestHelper;
    private QBChatHelper chatHelper;

    public QBLogoutAndDestroyChatCommand(Context context, QBChatRestHelper chatRestHelper, QBChatHelper chatHelper, String successAction,
                                         String failAction) {
        super(context, successAction, failAction);
        this.chatRestHelper = chatRestHelper;
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, boolean destroyChat) {
        Intent intent = new Intent(QBServiceConsts.LOGOUT_AND_DESTROY_CHAT_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.DESTROY_CHAT, destroyChat);
        context.startService(intent);
    }

    public static void start(Context context) {
       start(context, false);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        boolean destroy = true;

        if (extras != null) {
            destroy = extras.getBoolean(QBServiceConsts.DESTROY_CHAT, true);
        }

        if (chatRestHelper != null && chatRestHelper.isLoggedIn()) {
            chatHelper.leaveDialogs();
            chatRestHelper.logout();
            if (destroy) {
                chatRestHelper.destroy();
            }
        }

        return extras;
    }
}