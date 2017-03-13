package com.getadhell.androidapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.getadhell.androidapp.model.BlockedDomain;
import com.sec.enterprise.firewall.DomainFilterReport;

import java.util.ArrayList;
import java.util.List;

public class AdhellDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = AdhellDatabaseHelper.class.getCanonicalName();
    private static AdhellDatabaseHelper sInstance;

    private static final String DATABASE_NAME = "adhellDatabase";
    private static final int DATABASE_VERSION = 1;

    // Blocked domains table
    private static final String TABLE_BLOCKED_DOMAIN = "BlockedDomain";

    // Blocked domains table's columns
    private static final String KEY_BLOCKED_DOMAIN_ID = "id";
    private static final String KEY_BLOCKED_DOMAIN_URL = "url";
    private static final String KEY_BLOCKED_DOMAIN_TIMESTAMP = "blockTimestamp";
    public static final String KEY_BLOCKED_PACKAGE_NAME = "packageName";


    public static synchronized AdhellDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdhellDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private AdhellDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BLOCKED_DOMAIN_TABLE = "CREATE TABLE " + TABLE_BLOCKED_DOMAIN +
                "(" +
                KEY_BLOCKED_DOMAIN_ID + " INTEGER PRIMARY KEY, " +
                KEY_BLOCKED_DOMAIN_URL + " TEXT," +
                KEY_BLOCKED_DOMAIN_TIMESTAMP + " INTEGER," +
                KEY_BLOCKED_PACKAGE_NAME + " TEXT" +
                ")";
        db.execSQL(CREATE_BLOCKED_DOMAIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_DOMAIN);
            onCreate(db);
        }
    }


    public void addBlockedDomain(BlockedDomain blockedDomain) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_BLOCKED_DOMAIN_URL, blockedDomain.url);
        values.put(KEY_BLOCKED_DOMAIN_TIMESTAMP, blockedDomain.blockTimestamp);
        values.put(KEY_BLOCKED_PACKAGE_NAME, blockedDomain.packageName);
        db.insert(TABLE_BLOCKED_DOMAIN, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<BlockedDomain> getBlockedDomainsBetween(long start, long end) {
        List<BlockedDomain> blockedDomains = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String GET_BLOCKED_DOMAINS_BETWEEN = String.format("SELECT * FROM %s WHERE %s BETWEEN %d AND %d", TABLE_BLOCKED_DOMAIN, KEY_BLOCKED_DOMAIN_TIMESTAMP, start, end);
        Cursor cursor = db.rawQuery(GET_BLOCKED_DOMAINS_BETWEEN, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    BlockedDomain blockedDomain = new BlockedDomain();
                    blockedDomain.id = cursor.getInt(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_ID));
                    blockedDomain.url = cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_URL));
                    blockedDomain.blockTimestamp = cursor.getLong(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_TIMESTAMP));
                    blockedDomain.packageName = cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_PACKAGE_NAME));
                    blockedDomains.add(blockedDomain);
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get posts from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return blockedDomains;
    }

    public BlockedDomain getLastBlockedDomain() {
        BlockedDomain blockedDomain = null;
        SQLiteDatabase db = getReadableDatabase();
        String GET_LAST_BLOCKED_DOMAIN = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_BLOCKED_DOMAIN, KEY_BLOCKED_DOMAIN_TIMESTAMP);
        Cursor cursor = db.rawQuery(GET_LAST_BLOCKED_DOMAIN, null);
        try {
            if (cursor.moveToFirst()) {
                blockedDomain = new BlockedDomain();
                blockedDomain.id = cursor.getInt(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_ID));
                blockedDomain.url = cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_URL));
                blockedDomain.blockTimestamp = cursor.getLong(cursor.getColumnIndex(KEY_BLOCKED_DOMAIN_TIMESTAMP));
                blockedDomain.packageName = cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_PACKAGE_NAME));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get last blocked domain", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return blockedDomain;
    }


    public void addBlockedDomains(List<DomainFilterReport> domainFilterReports) {
        BlockedDomain blockedDomain = getLastBlockedDomain();
        long lastBlockedTimestamp = 0;
        if (blockedDomain != null) {
            lastBlockedTimestamp = blockedDomain.blockTimestamp;
        }

        for (DomainFilterReport b : domainFilterReports) {
            if (b.getTimeStamp() > lastBlockedTimestamp) {
                BlockedDomain blockedDomain1 = new BlockedDomain();
                blockedDomain1.blockTimestamp = b.getTimeStamp();
                blockedDomain1.packageName = b.getPackageName();
                blockedDomain1.url = b.getPackageName();
                addBlockedDomain(blockedDomain1);
            }
        }
    }
}
