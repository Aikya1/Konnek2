package com.aikya.konnek2.call.core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import com.aikya.konnek2.call.core.core.command.CompositeServiceCommand;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.network.NetworkGCMTaskService;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class QBLoginChatCompositeCommand extends CompositeServiceCommand {
    private static final String TAG = QBLoginChatCompositeCommand.class.getSimpleName();

    private static boolean isRunning;

    public QBLoginChatCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Log.i(TAG, "start");
        setIsRunning(true);
        Intent intent = new Intent(QBServiceConsts.LOGIN_CHAT_COMPOSITE_ACTION, null, context, QBService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

//        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        if (AppSession.ChatState.BACKGROUND == AppSession.getSession().getChatState()){
            scheduleLogin();
            return extras;
        }
        try {
            super.perform(extras);
        }
        catch (XMPPException | IOException | SmackException e){
            NetworkGCMTaskService.scheduleOneOff(context, "");
            throw e;
        }
        return extras;
    }

    private void scheduleLogin(){
        NetworkGCMTaskService.scheduleOneOff(context, "");
    }

    public static boolean isRunning(){
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        QBLoginChatCompositeCommand.isRunning = isRunning;
    }
}