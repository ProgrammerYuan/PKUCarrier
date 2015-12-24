package com.programmeryuan.PKUCarrier.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.models.DBEntry;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;

import java.util.ArrayList;

/**
 * Created by Michael on 2014/10/21.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static String database_name = "BangYoung";
    // Database creation sql statement
    public static ArrayList<String> database_create = new ArrayList<String>();//"create table MyEmployees( _id integer primary key,name text not null);";
    public static ArrayList<Class<? extends DBEntry>> entry_tables;
    public static int database_version = 8;

    public DBHelper(Context context) {
        super(context, database_name + "_" + PKUCarrierApplication.getUserId(), new SQLiteCursorFactory(true), database_version);
        init();
    }

    public static void init() {
        entry_tables = new ArrayList<Class<? extends DBEntry>>();
        entry_tables.add(User.class);
        entry_tables.add(PrivateMessage.class);
        for (Class<? extends DBEntry> cl : entry_tables) {
            try {
                database_create.add(cl.newInstance().getCreatingTableSql());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
//        db.execSQL(database_create);
        Logger.out("onOpen");
        Logger.out("db version:" + db.getVersion());

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.out("onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.out("onUpgrade oldVersion:" + oldVersion + ",newVersion:" + newVersion);
        upgradeDB(db, oldVersion + 1);
    }

    void upgradeDB(SQLiteDatabase db, int db_version) {
        Logger.out("upgradeDB db_version:" + db_version);
    }
}

class SQLiteCursorFactory implements SQLiteDatabase.CursorFactory {

    private boolean debugQueries = true;

    public SQLiteCursorFactory() {
        this.debugQueries = false;
    }

    public SQLiteCursorFactory(boolean debugQueries) {
        this.debugQueries = debugQueries;
    }

    @Override
    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
                            String editTable, SQLiteQuery query) {
        if (debugQueries) {
//            Log.d("SQL", query.toString());
            Logger.out(query.toString());
        }
        return new SQLiteCursor(db, masterQuery, editTable, query);
    }

}