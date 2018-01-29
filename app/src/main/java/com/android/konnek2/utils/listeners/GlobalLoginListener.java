package com.android.konnek2.utils.listeners;

public interface GlobalLoginListener {

    void onCompleteQbLogin();

    void onCompleteQbChatLogin();

    void onCompleteWithError(String error);
}