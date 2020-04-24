package com.example.downloadfiles.multithreaddownload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @author 徐国林
 * @data 2020/4/22
 * @decription
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String CREATE_TASK = "create table point ("
            + "_id integer primary key autoincrement,"
            + "task text,"
            + "thread integer,"
            + "position integer);";

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
