package com.lerenard.counter3.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.lerenard.counter3.Count;
import com.lerenard.counter3.CountRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by mc on 06-Dec-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final int
            DATABASE_VERSION = 4;

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
            db.execSQL("ALTER TABLE " + TABLE_COUNTS + " ADD COLUMN " + COUNTS_POSITION +
                       " INTEGER DEFAULT -1");
        }
        if (oldVersion < 4) {
            // I accidentally incremented the database version without actually changing anything...
        }
    }

    public void moveCount(long fromId, int fromPosition, int toPosition) {
        if (Math.abs(fromPosition - toPosition) != 1) {
            Log.e(TAG, "fromPosition: " + fromPosition + ", toPosition: " + toPosition +
                       ". But they should differ by exactly one");
        }

        Log.d(TAG, toString());

        ContentValues newValuesForTo = new ContentValues();
        newValuesForTo.put(COUNTS_POSITION, fromPosition);

        ContentValues newValuesForFrom = new ContentValues();
        newValuesForFrom.put(COUNTS_POSITION, toPosition);

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            db.update(
                    TABLE_COUNTS,
                    newValuesForTo,
                    COUNTS_POSITION + " = ?",
                    new String[]{String.valueOf(toPosition)});

            db.update(
                    TABLE_COUNTS,
                    newValuesForFrom,
                    _ID + " = ?",
                    new String[]{String.valueOf(fromId)});

            db.setTransactionSuccessful();
            Log.d(TAG, "successfully moved " + fromPosition + " to " + toPosition);
        } finally {
            db.endTransaction();
        }
        db.close();
        Log.d(TAG, toString());
    }

    public void addCount(Count count, int position) {
        SQLiteDatabase db = getWritableDatabase();

        if (position != itemCount++) {
            db.execSQL(
                    "UPDATE " + TABLE_COUNTS + " SET " + COUNTS_POSITION + " = " + COUNTS_POSITION +
                    " + 1 WHERE " + COUNTS_POSITION + " >= " + position);
        }

        ContentValues values = getValues(count);
        values.put(COUNTS_POSITION, position);
        count.setId(db.insert(TABLE_COUNTS, null, values));
        db.close();
    }

    public void addCount(Count count) {
        addCount(count, itemCount);
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
     * Retrieve the values to be inserted for the given count.
     * Doesn't include the count id as it's assumed that you will
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
                new String[]{Long.toString(count.getId())});

        db.close();
    }

    public void batchDeleteCount(Collection<Count> counts) {

    }

    public String toString() {
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder stringBuilder = new StringBuilder(), line = new StringBuilder();
        int width = 15;
        String formatString = "%1$-" + width + "s";
        String[] star = {_ID, COUNTS_NAME, COUNTS_COUNT, COUNTS_POSITION};
        for (String column : star) {
            stringBuilder.append(String.format(Locale.US, formatString, column))
                         .append('|');
            line.append(new String(new char[width]).replace('\0', '-'))
                .append('+');
        }
        stringBuilder.append('\n')
                     .append(line.toString())
                     .append('\n');
        Cursor cursor = db.query(
                TABLE_COUNTS,
                star,
                null, null, null, null,
                COUNTS_POSITION + " DESC");
        if (cursor.moveToFirst()) {
            do {
                for (String column : star) {
                    stringBuilder.append(String.format(Locale.US, formatString,
                                                       Trimmer.trim(cursor.getString(
                                                               cursor.getColumnIndexOrThrow(
                                                                       column)), width)))
                                 .append('|');
                }
                stringBuilder.append('\n');
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stringBuilder.toString();
    }

    public void deleteCount(Count count) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(
                    TABLE_COUNTS,
                    new String[]{COUNTS_POSITION},
                    _ID + " = ?",
                    new String[]{String.valueOf(count.getId())},
                    null, null, null);
            int positionColumn = cursor.getColumnIndexOrThrow(COUNTS_POSITION);
            if (cursor.moveToFirst()) {
                int oldPosition = cursor.getInt(positionColumn);
                cursor.close();

                db.delete(
                        TABLE_COUNTS,
                        _ID + " = ?",
                        new String[]{Long.toString(count.getId())});
                --itemCount;

                db.execSQL("UPDATE " + TABLE_COUNTS +
                           " SET " + COUNTS_POSITION + " = " + COUNTS_POSITION + " - 1" +
                           " WHERE " + COUNTS_POSITION + " > " + Integer.toString(oldPosition));
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
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
                + " ORDER BY " + COUNTS_POSITION, null);
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
