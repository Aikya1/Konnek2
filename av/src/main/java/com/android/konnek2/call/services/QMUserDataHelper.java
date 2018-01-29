package com.android.konnek2.call.services;

import android.content.Context;

import com.android.konnek2.call.R;
import com.android.konnek2.call.services.model.QMUser;

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
