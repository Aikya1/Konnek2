package com.aikya.konnek2.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.db.models.Attachment;
import com.quickblox.core.helper.MimeUtils;


import java.io.File;
import java.util.ArrayList;

public class StringUtils {

    public static String createHumanNameFromSystemPermission(String permission) {
        String permissionName = permission.replace("android.permission.", "");
        String[] words = permissionName.split("_", 0);
        String newPermissionName = "";
        for (String word : words) {
            newPermissionName += word.substring(0, 1) + word.substring(1).toLowerCase() + " ";
        }

        return newPermissionName;
    }

    public static String createCompositeString(ArrayList<String> permissions) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : permissions) {
            stringBuilder.append(createHumanNameFromSystemPermission(string));
            if (permissions.indexOf(string) == permissions.size() - 2) {
                stringBuilder.append(" and ");
            } else if (permissions.indexOf(string) == permissions.size() - 1) {
                stringBuilder.append("");
            } else {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public static String getAttachmentNameByType(Context context, Attachment.Type type) {
        String attachmentName = "";

        switch (type) {
            case IMAGE:
                attachmentName = context.getString(com.aikya.konnek2.R.string.dialog_attach_image);
                break;
            case AUDIO:
                attachmentName = context.getString(com.aikya.konnek2.R.string.dialog_attach_audio);
                break;
            case VIDEO:
                attachmentName = context.getString(com.aikya.konnek2.R.string.dialog_attach_video);
                break;
            case LOCATION:
                attachmentName = context.getString(com.aikya.konnek2.R.string.dialog_location);
                break;

            case CONTACT:
                attachmentName = context.getString(R.string.dialog_attach_contact);
                break;

            case DOC:
                attachmentName = context.getString(com.aikya.konnek2.R.string.dialog_attach_document);
                break;
            //will be extend for new attachment types
        }

        return attachmentName;
    }

    public static Attachment.Type getAttachmentTypeByFile(File file) {
        return getAttachmentTypeByFileName(file.getName());
    }

    public static Attachment.Type getAttachmentTypeByFileName(String fileName) {
        Attachment.Type attachmentType;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);
        if (mimeType == null) {
            attachmentType = Attachment.Type.OTHER;
        } else if (mimeType.startsWith("image")) {
            attachmentType = Attachment.Type.IMAGE;
        } else if (mimeType.startsWith("audio")) {
            attachmentType = Attachment.Type.AUDIO;
        } else if (mimeType.startsWith("video")) {
            attachmentType = Attachment.Type.VIDEO;
        } else if (mimeType.startsWith("text")) {
            attachmentType = Attachment.Type.DOC;
        } else if (mimeType.startsWith("application")) {
            attachmentType = Attachment.Type.DOC;
        } else {
            attachmentType = Attachment.Type.OTHER;
        }

        return attachmentType;
    }

    public static boolean isImageFile(File file) {
        return Attachment.Type.IMAGE.equals(StringUtils.getAttachmentTypeByFile(file));
    }

    public static String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = App.getInstance().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}