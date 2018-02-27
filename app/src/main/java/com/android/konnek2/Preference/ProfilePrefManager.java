package com.android.konnek2.Preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by usr3 on 16/2/18.
 */

public class ProfilePrefManager
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PROFILE_PREF_NAME = "Konnek2_profile_slider";

    private static final String IS_FIRSTS_TIME_LAUNCH = "IsFirstsTimeLaunch";

    public ProfilePrefManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PROFILE_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRSTS_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRSTS_TIME_LAUNCH, true);
    }


}
