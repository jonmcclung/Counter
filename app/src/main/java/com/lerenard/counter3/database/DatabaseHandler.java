package com.lerenard.counter3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.lerenard.counter3.Count;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc on 06-Dec-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final int
            DATABASE_VERSION = 3;

    private int itemCount = -1;

    private static final String
            DATABASE_NAME = "counter3.db",
            _ID = BaseColumns._ID,
            TABLE_COUNTS = "COUNTS",
            COUNTS_NAME = "name",
            COUNTS_COUNT = "count",
            COUNTS_POSITION = "position",

    CREATE_TABLE_COUNTS =
            "CREATE TABLE " + TABLE_COUNTS + "("
            + _ID + " INTEGER PRIMARY KEY,"
            + COUNTS_NAME + " TEXT,"
            + COUNTS_COUNT + " INTEGER)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COUNTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading from " + oldVersion + " to " + newVersion);
        // this code never should be executed, but it has been executed on my device
        /*if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_COUNTS +
                       " ADD COLUMN " + COUNTS_POSITION + "INTEGER DEFAULT -1");
                       // forgot to add a space before integer which caused it to be named wrong.
        }*/
        if (oldVersion == 2) {
            String TABLE_COUNTS_BACKUP = TABLE_COUNTS + "_BACKUP";
            db.execSQL("BEGIN TRANSACTION");
            db.execSQL("CREATE TEMPORARY TABLE " + TABLE_COUNTS_BACKUP + "("
                       + _ID + " INTEGER PRIMARY KEY,"
                       + COUNTS_NAME + " TEXT,"
                       + COUNTS_COUNT + " INTEGER)");
            db.execSQL("INSERT INTO " + TABLE_COUNTS_BACKUP +
                       " SELECT " + _ID + ", " + COUNTS_NAME + ", " + COUNTS_COUNT +
                       " FROM " + TABLE_COUNTS);
            db.execSQL("DROP TABLE " + TABLE_COUNTS);
            db.execSQL(CREATE_TABLE_COUNTS);
            db.execSQL("INSERT INTO " + TABLE_COUNTS +
                       " SELECT " + _ID + ", " + COUNTS_NAME + ", " + COUNTS_COUNT +
                       " FROM " + TABLE_COUNTS_BACKUP);
            db.execSQL("DROP TABLE " + TABLE_COUNTS_BACKUP);
            db.execSQL("COMMIT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_COUNTS + " ADD COLUMN " + COUNTS_POSITION + " INTEGER DEFAULT -1");
        }
    }

    public void addCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();
        if (count.getPosition() == -1) {
            count.setPosition(itemCount++);
        }
        count.setId(db.insert(TABLE_COUNTS, null, getValues(count)));
        db.close();

    }

    public static Count getCountFromCursor(Cursor cursor) {
        return new Count(
                cursor.getInt(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(COUNTS_NAME)),
                cursor.getInt(cursor.getColumnIndex(COUNTS_COUNT)),
                cursor.getInt(cursor.getColumnIndex(COUNTS_POSITION)));
    }

    public Count getCount(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTS,
                new String[]{_ID, COUNTS_NAME, COUNTS_COUNT, COUNTS_POSITION},
                _ID + " = ?",
                new String[]{Integer.toString(id)},
                null, null, null);
        cursor.moveToFirst();

        Count count = getCountFromCursor(cursor);

        cursor.close();
        db.close();
        return count;
    }

    public ArrayList<Count> getAllCounts() {
        ArrayList<Count> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = getCursor(db);

        if (cursor.moveToFirst()) {
            do {
                res.add(getCountFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return res;
    }

    /**
     * doesn't include the count id as it's assumed that you will
     * use a where clause with that or that it's otherwise
     * not useful.
     */
    private ContentValues getValues(Count count) {
        ContentValues values = new ContentValues();
        values.put(COUNTS_NAME, count.getName());
        values.put(COUNTS_COUNT, count.getCount());
        values.put(COUNTS_POSITION, count.getPosition());
        return values;
    }

    public void updateCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(
                TABLE_COUNTS,
                getValues(count),
                _ID + " = ?",
                new String[]{Long.toString(count.getId())});

        db.close();
    }

    public void deleteCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                TABLE_COUNTS,
                _ID + " = ?",
                new String[]{Long.toString(count.getId())});
        --itemCount;
        db.close();
    }

    private Cursor getCursor(SQLiteDatabase db) {
        Cursor res = db.rawQuery(
                "SELECT "
                + _ID + ", "
                + COUNTS_NAME + ", "
                + COUNTS_COUNT + ", "
                + COUNTS_POSITION + " FROM "
                + TABLE_COUNTS
                + " ORDER BY " + COUNTS_POSITION + " DESC", null);
        itemCount = res.getCount();
        return res;
    }

    public int getCount() {
        return itemCount;
    }

    public Cursor getCursor() {
        return getCursor(getWritableDatabase());
    }
}
