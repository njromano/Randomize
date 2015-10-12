package com.njromano.choosr;

import android.provider.BaseColumns;

/**
 * Created by Nick on 10/6/15.
 *
 * Contract for SQLite implementation
 */
public class ListContract {
    // empty constructor for safety
    public ListContract() {}

    // implementation of BaseColumns so it will play nice with Android framework
    public static abstract class UserList implements BaseColumns
    {
        public static final String TABLE_NAME = "UserList";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static abstract class UserListItem implements BaseColumns
    {
        public static final String TABLE_NAME = "UserListItem";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "long";
        public static final String COLUMN_NAME_USERLIST_ID = "UserList_ID";
    }
}
