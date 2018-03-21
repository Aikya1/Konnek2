package com.android.konnek2.utils;

/**
 * Created by Lenovo on 23-10-2017.
 */

public interface AppConversionCallaback {


    void onConversionSuccess(String result);

    void onConversionCompletion();

    void onConversionErrorOccured(String errorMessage);
}
