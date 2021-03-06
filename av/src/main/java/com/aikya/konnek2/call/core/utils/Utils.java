package com.aikya.konnek2.call.core.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;

import com.aikya.konnek2.call.core.models.UserCustomData;
import com.aikya.konnek2.call.db.utils.ErrorUtils;
import com.aikya.konnek2.call.services.model.QMUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.quickblox.core.exception.QBResponseException;

import com.quickblox.users.model.QBUser;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Utils {

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            ErrorUtils.logError(e);
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            ErrorUtils.logError(e);
        }
        return null;
    }

    public static boolean isTokenDestroyedError(QBResponseException e) {
        List<String> errors = e.getErrors();
        for (String error : errors) {
            if (ConstsCore.TOKEN_REQUIRED_ERROR.equals(error)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExactError(QBResponseException e, String msgError) {
        Log.d(Utils.class.getSimpleName(), "");
        List<String> errors = e.getErrors();
        for (String error : errors) {
            Log.d(Utils.class.getSimpleName(), "error =" + error);
            if (error.contains(msgError)) {
                Log.d(Utils.class.getSimpleName(), error + " contains " + msgError);
                return true;
            }
        }
        return false;
    }


    public static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                ErrorUtils.logError(e);
            }
        }
    }

    public static QBUser friendToUser(QMUser friend) {
        if (friend == null) {
            return null;
        }
        QBUser user = new QBUser();
        user.setId(friend.getId());
        user.setFullName(friend.getFullName());
        return user;
    }


    public static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        int i = 0;
        for (Integer e : integerList) {
            intArray[i++] = e.intValue();
        }
        return intArray;
    }

    public static ArrayList<Integer> toArrayList(int[] itemArray) {
        ArrayList<Integer> integerList = new ArrayList<Integer>(itemArray.length);
        for (int item : itemArray) {
            integerList.add(item);
        }
        return integerList;
    }

    public static boolean validateNotNull(Object object) {
        return object != null;
    }

    public static String customDataToString(UserCustomData userCustomData) {

        /*  userCustomData.setFirstName(firstName);
                        userCustomData.setLastName(lastName);
                        userCustomData.setAvatarUrl(s1);
                        userCustomData.setAge(22);
                        userCustomData.setContactno(phNo);
                        userCustomData.setPrefEmail(userEmail);
                        userCustomData.setStatus(status.getText().toString());
                        userCustomData.setSignUpType(signUpType);
                        userCustomData.setDeviceUDid("asdasdasd");*/
        JSONObject jsonObject = new JSONObject();
        setJsonValue(jsonObject, UserCustomData.TAG_AVATAR_URL, userCustomData.getAvatarUrl());
        setJsonValue(jsonObject, UserCustomData.TAG_STATUS, userCustomData.getStatus());
        setJsonValue(jsonObject, UserCustomData.TAG_firstName, userCustomData.getFirstName());
        setJsonValue(jsonObject, UserCustomData.TAG_lastName, userCustomData.getLastName());
        setJsonValue(jsonObject, UserCustomData.TAG_age, userCustomData.getAge());
        setJsonValue(jsonObject, UserCustomData.TAG_STATUS, userCustomData.getStatus());
        setJsonValue(jsonObject, UserCustomData.TAG_signUpType, userCustomData.getSignUpType());
        setJsonValue(jsonObject, UserCustomData.TAG_prefEmail, userCustomData.getPrefEmail());
        setJsonValue(jsonObject, UserCustomData.TAG_contactNo, userCustomData.getContactno());
        setJsonValue(jsonObject, UserCustomData.FACEBOOK_ID, userCustomData.getFacebookId());
        setJsonValue(jsonObject, UserCustomData.TAG_isEuropean, userCustomData.getIsEuropean());

        setJsonValue(jsonObject, UserCustomData.TAG_deviceUdid, userCustomData.getDeviceUDid());
        setJsonValue(jsonObject, UserCustomData.TAG_AVATAR_URL, userCustomData.getAvatarUrl());
        setJsonValue(jsonObject, UserCustomData.TAG_COUNTRY, userCustomData.getCountry());
        setJsonValue(jsonObject, UserCustomData.TAG_countryCode, userCustomData.getCountryCode());
        setJsonValue(jsonObject, UserCustomData.IS_LOCATION_TO_SHARE, String.valueOf(userCustomData.getIsLocationToShare()));
        setJsonValue(jsonObject, UserCustomData.TAG_LAST_SEEN, userCustomData.getLastSeen());
        setJsonValue(jsonObject, UserCustomData.TAG_prefEmail, userCustomData.getPrefEmail());
        setJsonValue(jsonObject, UserCustomData.TAG_prefInApp, userCustomData.getPrefInApp());
        setJsonValue(jsonObject, UserCustomData.TAG_prefsms, userCustomData.getPrefSms());
        setJsonValue(jsonObject, UserCustomData.TAG_prefLanguage, userCustomData.getPrefLanguage());
        setJsonValue(jsonObject, UserCustomData.TAG_prefLanguage1, userCustomData.getPrefLanguage1());

        setJsonValue(jsonObject, UserCustomData.TAG_gender, userCustomData.getGender());
        setJsonValue(jsonObject, UserCustomData.TAG_dob, userCustomData.getDob());
        setJsonValue(jsonObject, UserCustomData.TAG_addressLine1, userCustomData.getAddressLine1());
        setJsonValue(jsonObject, UserCustomData.TAG_CITY, userCustomData.getCity());
        setJsonValue(jsonObject, UserCustomData.TAG_COUNTRY, userCustomData.getCountry());
        setJsonValue(jsonObject, UserCustomData.TAG_postalcode, userCustomData.getPostalcode());
        setJsonValue(jsonObject, UserCustomData.TAG_prefInApp, userCustomData.getPrefInApp());

        String sms = userCustomData.getPrefSms();
        setJsonValue(jsonObject, UserCustomData.TAG_prefsms, userCustomData.getPrefSms());
        setJsonValue(jsonObject, UserCustomData.TAG_USER_LATITUDE, String.valueOf(userCustomData.getLatitude()));
        setJsonValue(jsonObject, UserCustomData.TAG_USER_LONGITUDE, String.valueOf(userCustomData.getLongitude()));


        return jsonObject.toString();
    }

    private static void setJsonValue(JSONObject jsonObject, String key, String value) {
//        if (!TextUtils.isEmpty(value)) {
        if (value == null) {
            value = "";
        }
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            ErrorUtils.logError(e);
        }
//        }
    }

    public static UserCustomData customDataToObject(String userCustomDataString) {
        if (TextUtils.isEmpty(userCustomDataString)) {
            return new UserCustomData();
        }

        UserCustomData userCustomData = null;
        GsonBuilder gsonBuilder = new GsonBuilder();

        if (userCustomDataString.contains("[") || userCustomDataString.contains("]")) {
            userCustomDataString = userCustomDataString.replace("[", "{");
            userCustomDataString = userCustomDataString.replace("]", "}");
        }

        Gson gson = gsonBuilder.create();

        try {
            userCustomData = gson.fromJson(userCustomDataString, UserCustomData.class);
        } catch (JsonSyntaxException e) {
            ErrorUtils.logError(e);
        }

        return userCustomData;
    }

}