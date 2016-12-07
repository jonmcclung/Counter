package com.lerenard.counter3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.lerenard.counter3.Count;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc on 06-Dec-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final int
            DATABASE_VERSION = 1;

    private static final String
            DATABASE_NAME = "counter3.db",
            _ID = BaseColumns._ID,
            TABLE_COUNTS = "COUNTS",
            COUNTS_NAME = "name",
            COUNTS_COUNT = "count",

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
        // TODO
    }

    public void addCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();
        count.setId(db.insert(TABLE_COUNTS, null, getValues(count)));
        db.close();

    }

    public static Count getCountFromCursor(Cursor cursor) {
        return new Count(
                cursor.getInt(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(COUNTS_NAME)),
                cursor.getInt(cursor.getColumnIndex(COUNTS_COUNT)));
    }

    public Count getCount(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTS,
                new String[] {_ID, COUNTS_NAME, COUNTS_COUNT},
                _ID + " = ?",
                new String[] {Integer.toString(id)},
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

        Cursor cursor = db.rawQuery("SELECT "
                + _ID + ", "
                + COUNTS_NAME + ", "
                + COUNTS_COUNT + " FROM "
                + TABLE_COUNTS, null);

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
        return values;
    }

    public void updateCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(
                TABLE_COUNTS,
                getValues(count),
                _ID + " = ?",
                new String[] {Long.toString(count.getId())});

        db.close();
    }

    public void deleteCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                TABLE_COUNTS,
                _ID + " = ?",
                new String[] {Long.toString(count.getId())});

        db.close();
    }

    public Cursor getCursor() {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT "
                                    + _ID + ", "
                                    + COUNTS_NAME + ", "
                                    + COUNTS_COUNT + " FROM "
                                    + TABLE_COUNTS, null);
    }
}
