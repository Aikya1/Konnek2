package com.android.konnek2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lenovo on 15-10-2017.
 */

public class AppPreference {


    private Context context;
    public static SharedPreferences sigInPreference;
    public static SharedPreferences quickBloxPreference;
    public static SharedPreferences.Editor signInEditor;
    public static SharedPreferences.Editor quickBloxEditor;

    public AppPreference(Context context) {
        this.context = context;

        sigInPreference = context.getSharedPreferences(AppConstant.SIGN_PREFERENCE, 0);
        quickBloxPreference = context.getSharedPreferences(AppConstant.QUICKBLOX_PREFERENCE, 0);
        signInEditor = sigInPreference.edit();
        quickBloxEditor = quickBloxPreference.edit();

    }

    public static void putQBUserId(String qbUserId) {
        signInEditor.putString(AppConstant.QB_USER_ID, qbUserId);
        signInEditor.apply();


    }

    public static void putQbUser(String qbUsers) {
        signInEditor.putString(AppConstant.QB_USER, qbUsers);
        signInEditor.apply();

    }

    public static void putUserName(String admin) {

        signInEditor.putString(AppConstant.QB_USER_NAME, admin);
        signInEditor.apply();

    }



    public static void putDialogId(String dialogId) {

        quickBloxEditor.putString(AppConstant.PREFERENCE_DIALOG_ID, dialogId);
        quickBloxEditor.apply();

    }


    public static String getQBUserId() {
        return sigInPreference.getString(AppConstant.QB_USER_ID, null);
    }

    public static String getQBUser() {
        return sigInPreference.getString(AppConstant.QB_USER, null);
    }

    public static String getUserName() {
        return sigInPreference.getString(AppConstant.QB_USER_NAME, null);
    }



    public static String getDialogId() {
        return quickBloxPreference.getString(AppConstant.PREFERENCE_DIALOG_ID, null);
    }


}
