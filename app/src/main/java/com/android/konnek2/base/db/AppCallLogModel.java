package com.android.konnek2.base.db;

/**
 * Created by Lenovo on 26-10-2017.
 */

public class AppCallLogModel {


    private String callOpponentName;
    private String callOpponentId;
    private String callUserName;
    private String callTime;
    private String callStatus;
    private String callDate;
    private String callPriority;
    private String callType;
    private String userId;
    private String callDuration;
    private String contactName;
    private String contactNumber;

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallOpponentId() {
        return callOpponentId;
    }

    public void setCallOpponentId(String callOpponentId) {
        this.callOpponentId = callOpponentId;
    }

    public String getCallOpponentName() {
        return callOpponentName;
    }

    public void setCallOpponentName(String callOpponentName) {
        this.callOpponentName = callOpponentName;
    }

    public String getCallUserName() {
        return callUserName;
    }

    public void setCallUserName(String callUserName) {
        this.callUserName = callUserName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallPriority() {
        return callPriority;
    }

    public void setCallPriority(String callPriority) {
        this.callPriority = callPriority;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
