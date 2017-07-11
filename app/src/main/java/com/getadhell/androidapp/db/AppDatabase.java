package com.getadhell.androidapp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.getadhell.androidapp.db.dao.AppInfoDao;
import com.getadhell.androidapp.db.dao.BlockUrlDao;
import com.getadhell.androidapp.db.dao.BlockUrlProviderDao;
import com.getadhell.androidapp.db.dao.ReportBlockedUrlDao;
import com.getadhell.androidapp.db.dao.UserBlockUrlDao;
import com.getadhell.androidapp.db.dao.WhiteUrlDao;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.db.entity.ReportBlockedUrl;
import com.getadhell.androidapp.db.entity.UserBlockUrl;
import com.getadhell.androidapp.db.entity.WhiteUrl;

@Database(entities = {
        BlockUrlProvider.class,
        BlockUrl.class,
        AppInfo.class,
        ReportBlockedUrl.class,
        WhiteUrl.class,
        UserBlockUrl.class
}, version = 14)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "adhell-database")
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract BlockUrlDao blockUrlDao();

    public abstract BlockUrlProviderDao blockUrlProviderDao();

    public abstract ReportBlockedUrlDao reportBlockedUrlDao();

    public abstract AppInfoDao applicationInfoDao();

    public abstract WhiteUrlDao whiteUrlDao();

    public abstract UserBlockUrlDao userBlockUrlDao();

}