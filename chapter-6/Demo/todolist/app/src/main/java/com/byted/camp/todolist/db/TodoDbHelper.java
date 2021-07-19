package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 2;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE NOTE (id INTEGER PRIMARY KEY,date TEXT,state INTEGER,content TEXT,priority INTEGER)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS NOTE";

    public TodoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: 2021/7/19 3. 这里执行创建数据库操作
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 2021/7/19 4. 这里执行升级数据库操作
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
