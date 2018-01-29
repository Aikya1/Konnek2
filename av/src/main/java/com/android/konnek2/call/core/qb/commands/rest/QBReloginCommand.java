

package com.android.konnek2.call.core.qb.commands.rest;

import android.content.Context;
import android.content.Intent;

import com.android.konnek2.call.core.core.command.CompositeServiceCommand;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;


public class QBReloginCommand extends CompositeServiceCommand {

    public QBReloginCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.RE_LOGIN_IN_CHAT_ACTION, null, context, QBService.class);
        context.startService(intent);
    }
}