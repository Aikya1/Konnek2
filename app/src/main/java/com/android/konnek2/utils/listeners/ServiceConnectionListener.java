package com.android.konnek2.utils.listeners;


import com.android.konnek2.call.core.service.QBService;

public interface ServiceConnectionListener {

    void onConnectedToService(QBService service);
}