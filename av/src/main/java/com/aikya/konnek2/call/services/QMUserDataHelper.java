package com.aikya.konnek2.call.services;

import android.content.Context;

import com.aikya.konnek2.call.services.model.QMUser;
import com.android.konnek2.call.R;

/**
 * Created by Lenovo on 08-11-2017.
 */

public class QMUserDataHelper extends  QMBaseDataHelper {


    private static final Class<?>[] TABLES = {
            QMUser.class
    };

    public QMUserDataHelper(Context context) {
        super(context, context.getString(R.string.db_name), context.getResources().getInteger(R.integer.db_version), R.raw.orm);
    }

    @Override
    protected Class<?>[] getTables() {
        return TABLES;
    }
}
