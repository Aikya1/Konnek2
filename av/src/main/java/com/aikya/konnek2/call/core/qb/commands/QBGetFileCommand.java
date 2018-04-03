package com.aikya.konnek2.call.core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.aikya.konnek2.call.core.core.command.ServiceCommand;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;


public class QBGetFileCommand extends ServiceCommand {

    private static final String TAG = QBGetFileCommand.class.getSimpleName();

    public QBGetFileCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, int fileId) {
        Intent intent = new Intent(QBServiceConsts.GET_FILE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FILE_ID, fileId);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        Integer fileId = extras.getInt(QBServiceConsts.EXTRA_FILE_ID);

        QBFile qbFile = QBContent.getFile(fileId).perform();

        Bundle result = new Bundle();
        result.putSerializable(QBServiceConsts.EXTRA_FILE, qbFile);

        return result;
    }
}