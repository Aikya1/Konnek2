package com.aikya.konnek2.utils.listeners;


import com.aikya.konnek2.call.db.models.Attachment;

public interface OnMediaPickedListener {

    void onMediaPicked(int requestCode, Attachment.Type attachmentType, Object attachment);

    void onMediaPickError(int requestCode, Exception e);

    void onMediaPickClosed(int requestCode);
}