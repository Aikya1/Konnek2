package com.aikya.konnek2.call.core.utils.helpers;

import android.text.TextUtils;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_IOS_VOIP;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_TYPE;
import static com.aikya.konnek2.call.core.utils.ConstsCore.MESSAGE_VOIP_TYPE;
import static com.aikya.konnek2.call.core.utils.ConstsCore.PUSH_MESSAGE_TYPE_CALL;
import static com.aikya.konnek2.call.core.utils.ConstsCore.PUSH_MESSAGE_TYPE_VOIP;

public class CoreNotificationHelper {

   /* public static QBEvent createPushEvent(List<Integer> userIdsList, String message, String messageType) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
//        event.setMessage(message);
        setMessage(event, message, messageType);
        return event;
    }

    private static void setMessage(QBEvent event, String message, String messageType) {
        if (!setMessageWithTypeIfNeed(event, message, messageType)) {
            event.setMessage(message);
        }
    }

    private static boolean setMessageWithTypeIfNeed(QBEvent event, String message, String messageType) {
        if (!TextUtils.isEmpty(messageType)) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("data.message", message);
            data.put("data.type", messageType);
            event.setMessage(data);
            return true;
        }
        return false;
    }*/

    public static QBEvent createPushEvent(List<Integer> userIdsList, String message, String messageType) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.addAll(userIdsList);
        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        setMessage(event, message, messageType);
        return event;
    }

    private static void setMessage(QBEvent event, String message, String messageType) {
        String eventMessage = message;
        if (isMessageWithParam(messageType)) {
            eventMessage = messageWithParams(message, messageType);
        }
        event.setMessage(eventMessage);
    }

    private static boolean isMessageWithParam(String messageType) {
        return !TextUtils.isEmpty(messageType);
    }

    private static String messageWithParams(String message, String messageType) {
        JSONObject json = new JSONObject();
        try {
            json.put(MESSAGE, message);
            // custom parameters
            json.put(MESSAGE_TYPE, messageType);
            if (isCallType(messageType)) {
                json.put(MESSAGE_IOS_VOIP, PUSH_MESSAGE_TYPE_VOIP);
                json.put(MESSAGE_VOIP_TYPE, PUSH_MESSAGE_TYPE_VOIP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private static boolean isCallType(String messageType) {
        return TextUtils.equals(messageType, PUSH_MESSAGE_TYPE_CALL);
    }

}