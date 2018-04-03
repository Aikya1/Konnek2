package com.aikya.konnek2.call.core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.aikya.konnek2.call.core.qb.helpers.QBChatHelper;
import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.content.model.QBFile;

import java.io.File;

public class QBLoadAttachFileCommand extends ServiceCommand {

    private static final String TAG = QBLoadAttachFileCommand.class.getSimpleName();

    private final QBChatHelper chatHelper;

    public QBLoadAttachFileCommand(Context context, QBChatHelper chatHelper,
            String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, File file, String dialogId) {
        Intent intent = new Intent(QBServiceConsts.LOAD_ATTACH_FILE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FILE, file);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        File file = (File) extras.getSerializable(QBServiceConsts.EXTRA_FILE);
        String dialogId = (String) extras.getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);

        QBFile qbFile = chatHelper.loadAttachFile(file);

        Bundle result = new Bundle();
        result.putSerializable(QBServiceConsts.EXTRA_ATTACH_FILE, qbFile);
        result.putString(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        result.putString(QBServiceConsts.EXTRA_FILE_PATH, file.getAbsolutePath());

        return result;
    }
}