package com.android.konnek2.base.activity;

import com.quickblox.chat.model.QBChatDialog;

import java.util.List;

/**
 * Created by Lenovo on 13-01-2018.
 */


 // EvenBus Class  by suresh
public class MessageEvent {

    public List<QBChatDialog> qbChatDialogList;

    public MessageEvent(List<QBChatDialog> qbChatDialogList) {
        this.qbChatDialogList = qbChatDialogList;
    }

    public List<QBChatDialog> getQbChatDialogList() {
        return qbChatDialogList;
    }
}
