package com.programmeryuan.PKUCarrier.models;

import android.database.Cursor;

/**
 * Created by Michael on 2014/10/21.
 */
public abstract class DBEntry {
    public DBEntry() {

    }

    public DBEntry(Cursor c) {

    }

    public abstract String getDeletingSql();

    public abstract String getSavingSql();

    public abstract String getCreatingTableSql();
}
