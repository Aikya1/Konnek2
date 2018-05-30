package com.aikya.konnek2.base.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Lenovo on 26-10-2017.
 */

public class AppCallLogTable {


    private static final String TAG = AppCallLogTable.class.getSimpleName();
    private SQLiteDatabase db;
    private Context context;

    public AppCallLogTable(SQLiteDatabase mdb, Context context) {
        super();
        this.context = context;
        db = mdb;
    }

    public static final String DB_COLUMN_ID = "id";
    public static final String DB_COLUMN_USER = "call_user";
    public static final String DB_COLUMN_USER_ID = "call_user_id";
    public static final String DB_COLUMN_OPPONENT = "call_opponent";
    public static final String DB_COLUMN_OPPONENT_ID = "call_opponent_id";
    public static final String DB_COLUMN_TIME = "time";
    public static final String DB_COLUMN_DATE = "date";
    public static final String DB_COLUMN_CALL_STATUS = "call_status";
    public static final String DB_COLUMN_CALL_PRIORITY = "call_priority";
    public static final String DB_COLUMN_CALL_DURATION = "call_duration";
    public static final String DB_COLUMN_CALL_OPPONENT_STATUS = "call_opponet_status";
    public static final String DB_COLUMN_CALL_TYPE = "call_type";
    public static final String TABLE_CALL_LOG = "call_log";
    static String PRIMARYKEY_REG = DB_COLUMN_ID;

    public static final String TABLE_CALL_LOG_CREATE = " create table IF NOT EXISTS " +
            TABLE_CALL_LOG + " ("
            + DB_COLUMN_ID + " integer primary key autoincrement,"
            + DB_COLUMN_USER + " text,"
            + DB_COLUMN_USER_ID + " text,"
            + DB_COLUMN_OPPONENT + " text,"
            + DB_COLUMN_OPPONENT_ID + " text,"
            + DB_COLUMN_TIME + " text,"
            + DB_COLUMN_DATE + " text,"
            + DB_COLUMN_CALL_STATUS + " text,"
            + DB_COLUMN_CALL_PRIORITY + " text,"
            + DB_COLUMN_CALL_DURATION + " text,"
            + DB_COLUMN_CALL_OPPONENT_STATUS + " text,"
            + DB_COLUMN_CALL_TYPE + " text"
            + ")";


    public long saveCallLog(ArrayList<AppCallLogModel> appcallLogModel) {

        long flag = 0;
        try {

            ContentValues initialValues = gerContentValues(appcallLogModel);
            Cursor mCursor = db.query(TABLE_CALL_LOG, new String[]
                            {DB_COLUMN_USER,
                                    DB_COLUMN_USER_ID,
                                    DB_COLUMN_OPPONENT,
                                    DB_COLUMN_OPPONENT_ID,
                                    DB_COLUMN_TIME,
                                    DB_COLUMN_DATE,
                                    DB_COLUMN_CALL_STATUS,
                                    DB_COLUMN_CALL_PRIORITY,
                                    DB_COLUMN_CALL_DURATION,
                                    DB_COLUMN_CALL_TYPE},
                    DB_COLUMN_USER + "=?" + " AND " +
                            DB_COLUMN_USER_ID + "=?" + " AND " +
                            DB_COLUMN_OPPONENT + "=?" + " AND " +
                            DB_COLUMN_OPPONENT_ID + "=?" + " AND " +
                            DB_COLUMN_TIME + "=?" + " AND " +
                            DB_COLUMN_DATE + "=?" + " AND " +
                            DB_COLUMN_CALL_STATUS + "=?" + " AND " +
                            DB_COLUMN_CALL_PRIORITY + "=?" + " AND " +
                            DB_COLUMN_CALL_DURATION + "=?" + " AND " +
                            DB_COLUMN_CALL_TYPE + "=?",
                    new String[]{
                            appcallLogModel.get(0).getCallUserName(),
                            appcallLogModel.get(0).getUserId(),
                            appcallLogModel.get(0).getCallOpponentName(),
                            appcallLogModel.get(0).getCallOpponentId(),
                            appcallLogModel.get(0).getCallTime(),
                            appcallLogModel.get(0).getCallDate(),
                            appcallLogModel.get(0).getCallStatus(),
                            appcallLogModel.get(0).getCallPriority(),
                            appcallLogModel.get(0).getCallDuration(),
                            appcallLogModel.get(0).getCallType()
                    },
                    null,
                    null,
                    null);

            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null) {
                    mCursor.close();
                }
                flag = db.insert(TABLE_CALL_LOG, null, initialValues);

            } else {

                flag = db.update(TABLE_CALL_LOG, initialValues, DB_COLUMN_USER + "=?" + " AND " +
                        DB_COLUMN_USER_ID + "=?" + " AND " +
                        DB_COLUMN_OPPONENT + "=?" + " AND " +
                        DB_COLUMN_OPPONENT_ID + "=?" + " AND " +
                        DB_COLUMN_TIME + "=?" + " AND " +
                        DB_COLUMN_DATE + "=?" + " AND " +
                        DB_COLUMN_CALL_STATUS + "=?" + " AND " +
                        DB_COLUMN_CALL_PRIORITY + "=?" + " AND " +
                        DB_COLUMN_CALL_DURATION + "=?" + " AND " +
                        DB_COLUMN_CALL_TYPE + "=?", new String[]{appcallLogModel.get(0).getCallUserName(),
                        appcallLogModel.get(0).getUserId(),
                        appcallLogModel.get(0).getCallOpponentName(),
                        appcallLogModel.get(0).getCallOpponentId(),
                        appcallLogModel.get(0).getCallTime(),
                        appcallLogModel.get(0).getCallDate(),
                        appcallLogModel.get(0).getCallStatus(),
                        appcallLogModel.get(0).getCallPriority(),
                        appcallLogModel.get(0).getCallDuration(),
                        appcallLogModel.get(0).getCallType()
                });
            }

        } catch (Exception e) {
            e.getMessage();

        }
        return flag;
    }

    public ContentValues gerContentValues(ArrayList<AppCallLogModel> appcallLogModel) {
        ContentValues initialValues = new ContentValues();
        try {

            initialValues.put(DB_COLUMN_USER, appcallLogModel.get(0).getCallUserName());
            initialValues.put(DB_COLUMN_USER_ID, appcallLogModel.get(0).getUserId());
            initialValues.put(DB_COLUMN_OPPONENT, appcallLogModel.get(0).getCallOpponentName());
            initialValues.put(DB_COLUMN_OPPONENT_ID, appcallLogModel.get(0).getCallOpponentId());
            initialValues.put(DB_COLUMN_TIME, appcallLogModel.get(0).getCallTime());
            initialValues.put(DB_COLUMN_DATE, appcallLogModel.get(0).getCallDate());
            initialValues.put(DB_COLUMN_CALL_STATUS, appcallLogModel.get(0).getCallStatus());
            initialValues.put(DB_COLUMN_CALL_PRIORITY, appcallLogModel.get(0).getCallPriority());
            initialValues.put(DB_COLUMN_CALL_DURATION, appcallLogModel.get(0).getCallDuration());
            initialValues.put(DB_COLUMN_CALL_TYPE, appcallLogModel.get(0).getCallType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }


    public ArrayList<AppCallLogModel> getCallHistory(String UserName) {


        ArrayList<AppCallLogModel> callLogModelArrayList = null;
        try {
            callLogModelArrayList = new ArrayList<AppCallLogModel>();
            Cursor mCursor = db.query(TABLE_CALL_LOG, new String[]
                            {
                                    DB_COLUMN_USER,
                                    DB_COLUMN_USER_ID,
                                    DB_COLUMN_OPPONENT,
                                    DB_COLUMN_OPPONENT_ID,
                                    DB_COLUMN_TIME,
                                    DB_COLUMN_DATE,
                                    DB_COLUMN_CALL_STATUS,
                                    DB_COLUMN_CALL_PRIORITY,
                                    DB_COLUMN_CALL_DURATION,
                                    DB_COLUMN_CALL_TYPE,
                            },
                    DB_COLUMN_USER + "=?", new String[]{UserName},
                    null,
                    null,
                    null,
                    null);
            if (mCursor == null || mCursor.getCount() <= 0) {
                if (mCursor != null)
                    mCursor.close();
                return callLogModelArrayList;
            } else {
                mCursor.moveToFirst();
                for (int counter = 0; counter < mCursor.getCount(); counter++) {
                    AppCallLogModel appcallLogModel = new AppCallLogModel();

                    appcallLogModel.setCallUserName(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_USER)));
                    appcallLogModel.setUserId(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_USER_ID)));
                    appcallLogModel.setCallOpponentName(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_OPPONENT)));
                    appcallLogModel.setCallOpponentId(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_OPPONENT_ID)));
                    appcallLogModel.setCallDate(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_DATE)));
                    appcallLogModel.setCallTime(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_TIME)));
                    appcallLogModel.setCallStatus(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_STATUS)));
                    appcallLogModel.setCallPriority(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_PRIORITY)));
                    appcallLogModel.setCallType(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_TYPE)));
                    appcallLogModel.setCallDuration(mCursor.getString(mCursor.getColumnIndex(DB_COLUMN_CALL_DURATION)));

                    callLogModelArrayList.add(appcallLogModel);

                    mCursor.moveToNext();

                }
            }
            mCursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return callLogModelArrayList;
    }

    public long UpdateCallLog(ArrayList<AppCallLogModel> appcallLogModel) {
        long flag = 0;
        try {
            ContentValues initialValues = updateContentValues(appcallLogModel);
            flag = db.update(TABLE_CALL_LOG, initialValues, DB_COLUMN_USER + "=?" + " AND " +
                            DB_COLUMN_DATE + "=?" + " AND " +
                            DB_COLUMN_TIME + "=?",
                    new String[]{appcallLogModel.get(0).getCallUserName(),
                            appcallLogModel.get(0).getCallDate(),
                            appcallLogModel.get(0).getCallTime()
                    });


        } catch (Exception e) {
            e.getMessage();

        }
        return flag;
    }

    public ContentValues updateContentValues(ArrayList<AppCallLogModel> appcallLogModel) {

        ContentValues initialValues = new ContentValues();
        try {
            initialValues.put(DB_COLUMN_CALL_DURATION, appcallLogModel.get(0).getCallDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialValues;
    }
}
