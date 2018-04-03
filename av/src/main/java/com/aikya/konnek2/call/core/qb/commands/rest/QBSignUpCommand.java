package com.aikya.konnek2.call.core.qb.commands.rest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.aikya.konnek2.call.core.core.command.CompositeServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.services.QMAuthService;
import com.quickblox.users.model.QBUser;

import java.io.File;

public class QBSignUpCommand extends CompositeServiceCommand {

    public QBSignUpCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, QBUser user, File image) {
        Intent intent = new Intent(QBServiceConsts.SIGNUP_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        intent.putExtra(QBServiceConsts.EXTRA_FILE, image);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {

        File file = (File) extras.getSerializable(QBServiceConsts.EXTRA_FILE);
        QBUser currentUser = (QBUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);

        Bundle params = new Bundle();
        params.putSerializable(QBServiceConsts.EXTRA_USER, currentUser);

        QMAuthService.getInstance().signup(currentUser);

        return params;
    }

}