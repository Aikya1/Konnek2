package com.aikya.konnek2.call.core.qb.commands.rest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.core.command.CompositeServiceCommand;
import com.aikya.konnek2.call.core.utils.helpers.CoreSharedHelper;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.db.managers.DataManager;
import com.quickblox.messages.services.SubscribeService;


public class QBLogoutCompositeCommand extends CompositeServiceCommand {

    private static final String TAG = QBLogoutCompositeCommand.class.getSimpleName();

    public QBLogoutCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOGOUT_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        try {
            super.perform(extras);
            unSubscribeFromPushes();
            resetCacheData();
            resetSharedPreferences();
        } catch (Exception e) {
            ErrorUtils.logError(TAG, e);
        }
        return extras;
    }

    private void unSubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(context);
    }

    private void resetCacheData() {
        DataManager.getInstance().clearAllTables();
    }

    private void resetSharedPreferences() {
        CoreSharedHelper.getInstance().clearAll();
    }
}