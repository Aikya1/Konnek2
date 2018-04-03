package com.aikya.konnek2.utils.listeners;


import com.aikya.konnek2.call.core.service.QBService;

public interface ServiceConnectionListener {

    void onConnectedToService(QBService service);
}