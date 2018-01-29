package com.android.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBChatHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;
import com.android.konnek2.call.services.model.QMUser;
import com.quickblox.chat.model.QBChatDialog;


public class QBCreatePrivateChatCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBCreatePrivateChatCommand(Context context, QBChatHelper chatHelper, String successAction,
                                      String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, QMUser friend) {
        Intent intent = new Intent(QBServiceConsts.CREATE_PRIVATE_CHAT_ACTION, null, context,
                QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIEND, friend.getId());
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        Integer friendId = (Integer) extras.getSerializable(QBServiceConsts.EXTRA_FRIEND);

        QBChatDialog privateDialog = chatHelper.createPrivateDialogIfNotExist(friendId);
        extras.putSerializable(QBServiceConsts.EXTRA_DIALOG, privateDialog);
        return extras;
    }
}