package com.aikya.konnek2.call.db.models;

import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.call.services.model.QMUserColumns;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import static com.aikya.konnek2.call.db.models.UserRequest.Column.ID;
import static com.aikya.konnek2.call.db.models.UserRequest.Column.REQUEST_STATUS;
import static com.aikya.konnek2.call.db.models.UserRequest.Column.TABLE_NAME;
import static com.aikya.konnek2.call.db.models.UserRequest.Column.TEXT_STATUS;
import static com.aikya.konnek2.call.db.models.UserRequest.Column.UPDATED_DATE;


@DatabaseTable(tableName = TABLE_NAME)
public class
UserRequest implements Serializable {

    @DatabaseField(
            generatedId = true,
            unique = true,
            columnName = ID)
    private int requestId;

    @DatabaseField(
            foreign = true,
            foreignAutoRefresh = true,
            unique = true,
            canBeNull = false,
            columnName = QMUserColumns.ID)
    private QMUser user;

    @DatabaseField(
            columnName = REQUEST_STATUS)
    private RequestStatus requestStatus;

    @DatabaseField(
            columnName = TEXT_STATUS)
    private String textStatus;

    @DatabaseField(
            columnName = UPDATED_DATE)
    private long updatedDate;

    public UserRequest() {
    }

    public UserRequest(long updatedDate, String textStatus, RequestStatus requestStatus, QMUser user) {
        this.updatedDate = updatedDate;
        this.textStatus = textStatus;
        this.requestStatus = requestStatus;
        this.user = user;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public QMUser getUser() {
        return user;
    }

    public void setUser(QMUser user) {
        this.user = user;
    }

    public String getTextStatus() {
        return textStatus;
    }

    public void setTextStatus(String textStatus) {
        this.textStatus = textStatus;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public boolean isIncoming(String ownerLogin) {
        return !user.getLogin().equals(ownerLogin);
    }

    @Override
    public String toString() {
        return "UserRequest [requestId='" + requestId + "', user='" + user + "']";
    }

    public enum RequestStatus {

        INCOMING(0),
        OUTGOING(1);

        private int code;

        RequestStatus(int code) {
            this.code = code;
        }

        public static RequestStatus parseByCode(int code) {
            RequestStatus[] valuesArray = RequestStatus.values();
            RequestStatus result = null;
            for (RequestStatus value : valuesArray) {
                if (value.getCode() == code) {
                    result = value;
                    break;
                }
            }
            return result;
        }

        public int getCode() {
            return code;
        }
    }

    public interface Column {

        String TABLE_NAME = "user_request";
        String ID = "user_request_id";
        String TEXT_STATUS = "text_status";
        String UPDATED_DATE = "updated_date";
        String REQUEST_STATUS = "request_status";
    }
}