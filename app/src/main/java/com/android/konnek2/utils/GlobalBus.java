package com.android.konnek2.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Lenovo on 13-01-2018.
 */

public class GlobalBus {

    private static EventBus sBus;
    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }
}
