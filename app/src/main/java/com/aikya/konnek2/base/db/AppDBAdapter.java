package com.aikya.konnek2.base.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aikya.konnek2.utils.AppConstant;


/**
 * Created by Lenovo on 26-10-2017.
 */

public class AppDBAdapter {


    private static final String TAG = AppDBAdapter.class.getSimpleName();

    private static final String DATABASE_NAME = AppConstant.DATABASE_CALL_NAME;
    @SuppressLint("SdCardPath")
    public static final String DB_FULL_PATH = AppConstant.DB_FULL_PATH + DATABASE_NAME;
    private static final int DATABASE_CURRENT_VERSION = 1;
    private static final int DATABASE_PREVIOUS_VERSION = 0;

    private static Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private static AppDBAdapter mDBAdapter = null;


    public AppDBAdapter(Context context) {

        AppDBAdapter.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public static AppDBAdapter getInstance(Context context) {


        if (mDBAdapter == null) {
            mDBAdapter = new AppDBAdapter(context);
        }
        return mDBAdapter;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_CURRENT_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(AppCallLogTable.TABLE_CALL_LOG_CREATE);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public SQLiteDatabase getDataBase() throws SQLException {

        return db;
    }


    public AppDBAdapter open() throws SQLException {

        try {
            db = DBHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {

        try {
            if (DBHelper != null) {
                DBHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
