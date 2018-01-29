package com.android.konnek2.utils;

/**
 * Created by Lenovo on 23-10-2017.
 */

public interface AppConversionCallaback {


    public void onConversionSuccess(String result);

    public void onConversionCompletion();

    public void onConversionErrorOccured(String errorMessage);
}
