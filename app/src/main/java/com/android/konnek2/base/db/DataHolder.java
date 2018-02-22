package com.android.konnek2.base.db;

import com.android.konnek2.base.Model_base.Profile;

import java.util.Map;

/**
 * Created by usr3 on 20/2/18.
 */

public class DataHolder {
    private static DataHolder instance;
    private Map<String, Profile> profileMap;

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public void addProfileToMap(Profile profile) {
        profileMap.put(profile.getPhoneno(), profile);
    }
}
