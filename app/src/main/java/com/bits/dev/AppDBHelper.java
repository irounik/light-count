package com.bits.dev;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

public class AppDBHelper extends SQLiteOpenHelper {
    public static final int COL_INDEX = 0;
    public static final int COL_HOURS = 1;
    public static final int COL_MINUTES = 2;
    public static final int COL_SECONDS = 3;
    public static final int COL_LIGHT = 4;

    private static final String KEY_INDEX = "slno";
    private static final String KEY_HOURS = "hours";
    private static final String KEY_MINUTES = "minutes";
    private static final String KEY_SECONDS = "seconds";
    private static final String KEY_LIGHT = "light";
    private static final int DATABASE_VERSION = 1;
    private static final String DB_TABLE_NAME = "light";
    private static final String DB_NAME = "light.db";
    private String dbName;

    public AppDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        dbName = name;

    }

    @Override
    public void onCreate(SQLiteDatabase _db)
    {
        Calendar today = Calendar.getInstance();
        int date = today.get(Calendar.DATE);

        String dbTableName = DB_TABLE_NAME + "_" + date;

        String DB_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + dbTableName + " (" +
                        KEY_INDEX +  " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_HOURS + " INTEGER," +
                        KEY_MINUTES + " INTEGER," +
                        KEY_SECONDS + " INTEGER," +
                        KEY_LIGHT +  " REAL);";

        _db.execSQL(DB_TABLE_CREATE);

        Log.w("AppDBHelper", "Database table " + dbTableName + " was created.");

    }

    public void createTable(SQLiteDatabase _db, int date)
    {
        String dbTableName = DB_TABLE_NAME + "_" + date;

        String DB_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + dbTableName + " (" +
                        KEY_INDEX +  " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_HOURS + " INTEGER," +
                        KEY_MINUTES + " INTEGER," +
                        KEY_SECONDS + " INTEGER," +
                        KEY_LIGHT +  " REAL);";

        _db.execSQL(DB_TABLE_CREATE);

        Log.w("AppDBHelper", "Database table " + dbTableName + " was created.");

    }

    public void deleteTable(SQLiteDatabase _db, int date)
    {
        String dbTableName = DB_TABLE_NAME + "_" + date;

        String DB_TABLE_DELETE = "DROP TABLE IF EXISTS " + dbTableName;

        _db.execSQL(DB_TABLE_DELETE);

        Log.w("AppDBHelper", "Database table " + dbTableName + " was deleted.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {

        Calendar today = Calendar.getInstance();
        int date = today.get(Calendar.DATE);

        String dbTableName = DB_TABLE_NAME + "_" + date;

        Log.w("AppDBHelper", "Upgrading from version" +
                _oldVersion + "to" + _newVersion +
                ", which will destroy the old data");

        //Drop the old table
        _db.execSQL("DROP TABLE IF EXISTS " + dbTableName);
        //Create a new one
        onCreate(_db);
    }
}
