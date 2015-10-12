package com.njromano.choosr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.njromano.choosr.ListContract.UserList;
import com.njromano.choosr.ListContract.UserListItem;

/**
 * Created by Nick on 10/6/15.
 *
 * Implementation of SQLite helper to store lists on device storage
 */
public class ListDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1; // update this when changing schema
    public static final String DATABASE_NAME = "Lists.db";

    // creation and deletion SQL statements, generalized for easy changes
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String REAL_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String NOT_NULL = " NOT NULL";

    private static final String SQL_CREATE_USERLIST =
            "CREATE TABLE " + UserList.TABLE_NAME + " (" +
                    UserList._ID + " INTEGER PRIMARY KEY," +
                    UserList.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    UserList.COLUMN_NAME_DATE + DATE_TYPE + NOT_NULL + " )";
    private static final String SQL_CREATE_USERLISTITEM =
            "CREATE TABLE " + UserListItem.TABLE_NAME + " (" +
                    UserListItem._ID + " INTEGER PRIMARY KEY," +
                    UserListItem.COLUMN_NAME_IMAGE + BLOB_TYPE + COMMA_SEP +
                    UserListItem.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    UserListItem.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    UserListItem.COLUMN_NAME_LONG + REAL_TYPE + COMMA_SEP +
                    UserListItem.COLUMN_NAME_USERLIST_ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    "FOREIGN KEY(" + UserListItem.COLUMN_NAME_USERLIST_ID + ") REFERENCES " +
                    UserList.TABLE_NAME + "(" + UserList._ID + "))";
    private static final String SQL_DELETE_USERLIST =
            "DROP TABLE IF EXISTS " + UserList.TABLE_NAME;
    private static final String SQL_DELETE_USERLISTITEM =
            "DROP TABLE IF EXISTS " + UserListItem.TABLE_NAME;

    // test population
    private static final String SQL_POPULATE =
            "INSERT INTO " + UserList.TABLE_NAME + "(" + UserList.COLUMN_NAME_TITLE +
                    COMMA_SEP + UserList.COLUMN_NAME_DATE + ") VALUES ('test title', datetime('now')";

    public ListDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_USERLIST);
        db.execSQL(SQL_CREATE_USERLISTITEM);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db,oldVersion,newVersion);
    }

}
