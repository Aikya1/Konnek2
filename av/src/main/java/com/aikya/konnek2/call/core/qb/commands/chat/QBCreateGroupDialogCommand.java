package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.utils.UserFriendUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;


import java.util.ArrayList;

public class QBCreateGroupDialogCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBCreateGroupDialogCommand(Context context, QBChatHelper chatHelper,
                                      String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, String roomName, ArrayList<QMUser> friendList, String photoUrl) {
        Intent intent = new Intent(QBServiceConsts.CREATE_GROUP_CHAT_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_ROOM_NAME, roomName);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friendList);
        intent.putExtra(QBServiceConsts.EXTRA_ROOM_PHOTO_URL, photoUrl);
        context.startService(intent);
    }

    public static void start(Context context, String roomName, ArrayList<QMUser> friendList, String photoUrl, QBUser currentUser) {
        Intent intent = new Intent(QBServiceConsts.CREATE_GROUP_CHAT_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_ROOM_NAME, roomName);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friendList);
        intent.putExtra(QBServiceConsts.EXTRA_ROOM_PHOTO_URL, photoUrl);
        intent.putExtra(QBServiceConsts.EXTRA_USER, currentUser);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        ArrayList<QMUser> friendList = (ArrayList<QMUser>) extras.getSerializable(QBServiceConsts.EXTRA_FRIENDS);
        String roomName = (String) extras.getSerializable(QBServiceConsts.EXTRA_ROOM_NAME);
        String photoUrl = (String) extras.getSerializable(QBServiceConsts.EXTRA_ROOM_PHOTO_URL);
        QBUser currentUser = (QBUser) extras.get(QBServiceConsts.EXTRA_USER);
        QBChatDialog dialog = chatHelper.createGroupChat(roomName, UserFriendUtils.getFriendIdsFromUsersList(friendList),
                photoUrl, currentUser);

        extras.putSerializable(QBServiceConsts.EXTRA_DIALOG, dialog);
        return extras;
    }
}