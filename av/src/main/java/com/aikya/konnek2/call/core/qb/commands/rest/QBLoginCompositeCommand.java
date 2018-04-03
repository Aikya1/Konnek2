package com.aikya.konnek2.call.core.qb.commands.rest;

import android.content.Context;
import android.content.Intent;


import com.aikya.konnek2.call.core.core.command.CompositeServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.users.model.QBUser;

public class QBLoginCompositeCommand extends CompositeServiceCommand {

    public QBLoginCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser user) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
}