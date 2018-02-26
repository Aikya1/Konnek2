package com.android.konnek2.call.services.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.android.konnek2.call.services.model.QMUserCustomData;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Lenovo on 08-11-2017.
 */

public class Utils {


    public static QMUserCustomData customDataToObject(String userCustomDataString) {
        if (TextUtils.isEmpty(userCustomDataString)) {
            return new QMUserCustomData();
        }

        QMUserCustomData userCustomData = null;
        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = gsonBuilder.create();

        if (userCustomDataString.contains("[") || userCustomDataString.contains("]")) {
            userCustomDataString = userCustomDataString.replace("[", "{");
            userCustomDataString = userCustomDataString.replace("]", "}");
        }

        try {

           /* Type collectionType = new TypeToken<List<QMUserCustomData>>() {
            }.getType();
            List<QMUserCustomData> lcs = gson.fromJson(userCustomDataString, collectionType);
            userCustomData = lcs.get(0);*/

            userCustomData = gson.fromJson(userCustomDataString, QMUserCustomData.class);
        } catch (JsonSyntaxException e) {
            ErrorUtils.logError(e);
        }

        return userCustomData;
    }
}
