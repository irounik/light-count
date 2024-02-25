package com.bits.dev;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public class AppDB {
    private SQLiteDatabase appDB;	//App Database
    private AppDBHelper appDBHelper;	//App Database Helper
    private final Context context;

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

    public AppDB(Context _context)
    {
        context = _context;

        // Instantiate the Database Helper
        appDBHelper = new AppDBHelper(context, DB_NAME, null, DATABASE_VERSION);

        Log.w("AppDB", "AppDBHelper was instantiated");
    }

    //Method for opening the database for writing
    public boolean open() throws SQLiteException
    {
        try {
            //Open the database for writing
            appDB = appDBHelper.getWritableDatabase();

            Log.w("AppDB", "AppDB:Database opened for writing");

            return true;
        } catch(SQLiteException ex)
        {
            //Open the database for reading, if exception occurs
            appDB = appDBHelper.getReadableDatabase();

            Log.w("AppDB", "AppDB:Database opened for reading only");

            return false;
        }
    }

    //Method for closing the database
    public void close()
    {
        appDB.close();

        Log.w("AppDB", "AppDB:Database closed");

    }

    //Method for inserting a row into the database
    public void insert(int date, int hour, int minute, int second, float light)
    {
        ContentValues newEntry = new ContentValues();

        String dbTableName = DB_TABLE_NAME + "_" + date;

        newEntry.put(KEY_HOURS, hour);
        newEntry.put(KEY_MINUTES, minute);
        newEntry.put(KEY_SECONDS, second);
        newEntry.put(KEY_LIGHT, light);

        if(appDB.insert(dbTableName, null, newEntry) == -1)
            Log.e("AppDB",
                    "Insert to database table " + dbTableName + ":" + hour + "." + minute + " failed.");
        else
            Log.w("AppDB",
                    "Insert to database table " + dbTableName + ":" + hour + "." + minute + " was successful.");
    }

	/* Query methods */

    /* Query all rows and all columns */
    public Cursor getAllRows(int date)
    {
        String dbTableName = DB_TABLE_NAME + "_" + date;

        return appDB.query(	dbTableName,
                new String[] {KEY_INDEX, KEY_HOURS, KEY_MINUTES, KEY_SECONDS, KEY_LIGHT},
                null, null, null, null, null);
    }

    public Cursor getLastRow(int date) {
        String dbTableName = DB_TABLE_NAME + "_" + date;

        return appDB.query(
                dbTableName,
                new String[] {KEY_LIGHT},
                null,
                null,
                null,
                null,
                KEY_INDEX + " DESC", // Replace PRIMARY_KEY_COLUMN_NAME with the name of your primary key column
                "1" // Limit the result to 1 row
        );
    }

    /* Query a specific row */
    public Cursor getHourSpecificRows(int date, int hours)
    {
        String dbTableName = DB_TABLE_NAME + "_" + date;

        return appDB.query(dbTableName,
                new String[] {KEY_INDEX, KEY_HOURS, KEY_MINUTES, KEY_SECONDS, KEY_LIGHT},
                KEY_HOURS + "=" + hours,
                null, null, null, null);
    }
}
