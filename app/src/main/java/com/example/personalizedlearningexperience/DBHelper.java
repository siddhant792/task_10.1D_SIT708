package com.example.personalizedlearningexperience;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY =
                "CREATE TABLE my_table (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT, " +
                        "email TEXT, " +
                        "password TEXT, " +
                        "interest TEXT, " +
                        "phone TEXT, " +
                        "image BLOB)";
        db.execSQL(CREATE_TABLE_QUERY);

        String CREATE_HISTORY_TABLE_QUERY =
                "CREATE TABLE history (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "question TEXT, " +
                        "answer1 TEXT, " +
                        "answer2 TEXT, " +
                        "answer3 TEXT, " +
                        "answer4 TEXT, " +
                        "correct TEXT, " +
                        "userAnswered TEXT)";
        db.execSQL(CREATE_HISTORY_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS my_table");
        db.execSQL("DROP TABLE IF EXISTS history");
        onCreate(db);
    }
}
