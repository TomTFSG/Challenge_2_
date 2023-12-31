package com.example.challenge_2_;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_NOTE = "note";
    SQLiteDatabase sql;
    public static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_NOTE + " TEXT)";;
    public static final String SQL_DELETE_ENTRIES="DROP TABLE IF EXISTS " + TABLE_NAME;;
    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sql, int oldVersion, int newVersion){
    }
    public  void resetDatabase(){
        sql.execSQL(SQL_DELETE_ENTRIES);
        sql.execSQL(SQL_CREATE_ENTRIES);
    }
}
