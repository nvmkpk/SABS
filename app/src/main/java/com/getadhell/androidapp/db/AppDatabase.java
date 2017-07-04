package com.getadhell.androidapp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.getadhell.androidapp.db.dao.BlockUrlDao;
import com.getadhell.androidapp.db.dao.BlockUrlProviderDao;
import com.getadhell.androidapp.db.dao.ReportBlockedUrlDao;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.db.entity.ReportBlockedUrl;

@Database(entities = {BlockUrlProvider.class, BlockUrl.class, ReportBlockedUrl.class}, version = 7)
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
}
