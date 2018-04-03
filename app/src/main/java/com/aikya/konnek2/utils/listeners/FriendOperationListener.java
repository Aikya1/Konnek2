package com.aikya.konnek2.utils.listeners;

public interface FriendOperationListener {

    void onAcceptUserClicked(int position, int userId);

    void onRejectUserClicked(int position, int userId);
}