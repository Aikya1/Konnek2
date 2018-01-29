package com.android.konnek2.call.core.qb.commands.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.konnek2.call.core.core.command.ServiceCommand;
import com.android.konnek2.call.core.qb.helpers.QBFriendListHelper;
import com.android.konnek2.call.core.service.QBService;
import com.android.konnek2.call.core.service.QBServiceConsts;


public class QBAddFriendCommand extends ServiceCommand {

    private QBFriendListHelper friendListHelper;

    public QBAddFriendCommand(Context context, QBFriendListHelper friendListHelper, String successAction,
            String failAction) {
        super(context, successAction, failAction);
        this.friendListHelper = friendListHelper;
    }

    public static void start(Context context, int userId) {

        Log.d("FIRENDREQUEST","QBAddFriendCommand start "+userId);
        Intent intent = new Intent(QBServiceConsts.ADD_FRIEND_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        int userId = extras.getInt(QBServiceConsts.EXTRA_USER_ID);
        Log.d("FIRENDREQUEST","QBAddFriendCommand  Bundle  perform "+userId);
        friendListHelper.addFriend(userId);

        Bundle result = new Bundle();
        result.putSerializable(QBServiceConsts.EXTRA_FRIEND_ID, userId);

        return result;
    }
}