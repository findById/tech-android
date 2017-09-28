package org.cn.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database = null;

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.d("SH", "upgrade version " + oldVersion + " to " + newVersion);
        }
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    public SQLiteDatabase getDatabase(boolean readonly) {
        if (database == null) {
            database = readonly ? getReadableDatabase() : getWritableDatabase();
        } else if (database.isReadOnly() && !readonly) {
            database.close();
            database = getWritableDatabase();
        }
        return database;
    }

    public Cursor executeQuery(String sql, String... args) {
        getDatabase(true);
        // Log.d("ORM", sql);
        return database.rawQuery(sql, args);
    }

    public int executeUpdate(String sql, Object... args) {
        try {
            getDatabase(false);
            database.execSQL(sql, args);
            return 0;
        } catch (Throwable e) {
            return -1;
        }
    }

    public int executeUpdate(String sql) {
        getDatabase(false);
        SQLiteStatement statement = null;
        try {
            statement = database.compileStatement(sql);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            Log.e("SH", "update " + e.getMessage(), e);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return -1;
    }

    public void beginTransaction() {
        Log.d("SH", "begin " + database.inTransaction());
        if (!database.inTransaction()) {
            database.beginTransaction();
            Log.d("SH", "beginTransaction");
        }
    }

    public void commit() {
        try {
            Log.d("SH", "commit " + database.inTransaction());
            if (database.inTransaction()) {
                database.setTransactionSuccessful();
                Log.d("SH", "commit setTransactionSuccessful");
            }
        } catch (Throwable e) {
            Log.e("SH", "commit " + e.getMessage(), e);
        } finally {
            database.endTransaction();
            Log.d("SH", "commit endTransaction");
        }
    }

    public void rollback() {
        database.endTransaction();
        Log.d("SH", "endTransaction");
    }
}
