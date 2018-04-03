package com.aikya.konnek2.utils.bridges;

public interface ConnectionBridge {

    boolean checkNetworkAvailableWithError();

    boolean isNetworkAvailable();
}