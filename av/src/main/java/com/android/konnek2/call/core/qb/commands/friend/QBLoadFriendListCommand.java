package com.android.konnek2.call.core.qb.commands.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;

import java.util.Collection;

public class QBLoadFriendListCommand extends ServiceCommand {

    private QBFriendListHelper friendListHelper;

    public QBLoadFriendListCommand(Context context, QBFriendListHelper friendListHelper, String successAction,
            String failAction) {
        super(context, successAction, failAction);
        this.friendListHelper = friendListHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOAD_FRIENDS_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        Collection<Integer> userIdsList = friendListHelper.updateFriendList();

        if (extras == null) {
            extras = new Bundle();
        }

        extras.putSerializable(QBServiceConsts.EXTRA_FRIENDS, (java.io.Serializable) userIdsList);

        return extras;
    }
}