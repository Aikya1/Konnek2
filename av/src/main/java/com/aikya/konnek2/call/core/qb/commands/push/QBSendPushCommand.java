package com.aikya.konnek2.call.core.qb.commands.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.utils.ConstsCore;
import com.aikya.konnek2.call.core.utils.helpers.CoreNotificationHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEvent;


import java.util.ArrayList;

public class QBSendPushCommand extends ServiceCommand {

    public QBSendPushCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, String message, ArrayList<Integer> friendIdsList, String messageType) {
        Intent intent = new Intent(QBServiceConsts.SEND_PUSH_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friendIdsList);
        intent.putExtra(ConstsCore.PUSH_MESSAGE, message);
        intent.putExtra(ConstsCore.PUSH_MESSAGE_TYPE, messageType);
        context.startService(intent);
    }

    public static void start(Context context, String message, Integer friendId, String messageType) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        friendIdsList.add(friendId);
        start(context, message, friendIdsList, messageType);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        ArrayList<Integer> usersIdsList = (ArrayList<Integer>) extras.getSerializable(
                QBServiceConsts.EXTRA_FRIENDS);
        String message = extras.getString(ConstsCore.PUSH_MESSAGE);
        String messageType = extras.getString(ConstsCore.PUSH_MESSAGE_TYPE);
        QBEvent pushEvent = CoreNotificationHelper.createPushEvent(usersIdsList, message, messageType);
        try {
            QBPushNotifications.createEvent(pushEvent).perform();


        } catch (QBResponseException e) {
            /* ignore message = "No one can receive the message" */
        }

        return null;
    }
}