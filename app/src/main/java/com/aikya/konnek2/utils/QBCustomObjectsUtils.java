package com.aikya.konnek2.utils;

import com.aikya.konnek2.base.Model_base.Profile;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.HashMap;

/**
 * Created by usr3 on 20/2/18.
 */

public class QBCustomObjectsUtils {

    public static String parseField(String field, QBCustomObject customObject) {
        Object object = customObject.getFields().get(field);
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public static QBCustomObject createCustomObject(String phoneno) {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put(Profile.Contract.PHONENO, phoneno);


        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setClassName(AppConstant.PROFILE_CLASS_NAME);
        qbCustomObject.setFields(fields);

        return qbCustomObject;
    }


}
