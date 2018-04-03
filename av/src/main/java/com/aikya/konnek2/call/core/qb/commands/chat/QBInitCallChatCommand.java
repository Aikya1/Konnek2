package com.aikya.konnek2.call.core.qb.commands.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.qb.helpers.QBCallChatHelper;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.chat.QBChatService;


public class QBInitCallChatCommand extends ServiceCommand {

    private QBCallChatHelper qbCallChatHelper;

    public QBInitCallChatCommand(Context context, QBCallChatHelper qbCallChatHelper, String successAction,
            String failAction) {
        super(context, successAction, failAction);
        this.qbCallChatHelper = qbCallChatHelper;
    }

    public static void start(Context context, Class<? extends Activity> callClass) {
        Intent intent = new Intent(QBServiceConsts.INIT_CALL_CHAT_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_CALL_ACTIVITY, callClass);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        if (extras == null || extras.getSerializable(QBServiceConsts.EXTRA_CALL_ACTIVITY) == null) { // global init
            qbCallChatHelper.init(QBChatService.getInstance());
            Log.d("test_crash_1", "+++ perform 1 +++");
        } else {
            // init call activity
            Log.d("test_crash_1", "+++ perform 2 +++");
            qbCallChatHelper.initActivityClass((Class<? extends Activity>) extras.getSerializable(
                    QBServiceConsts.EXTRA_CALL_ACTIVITY));
        }
        return extras;
    }
}