package com.getadhell.androidapp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
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
import com.getadhell.androidapp.db.migration.Migration_14_15;
import com.getadhell.androidapp.db.migration.Migration_15_16;
import com.getadhell.androidapp.db.migration.Migration_16_17;
import com.getadhell.androidapp.db.migration.Migration_17_18;
import com.getadhell.androidapp.db.migration.Migration_18_19;

@Database(entities = {
        BlockUrlProvider.class,
        BlockUrl.class,
        AppInfo.class,
        ReportBlockedUrl.class,
        WhiteUrl.class,
        UserBlockUrl.class
}, version = 19)
public abstract class AppDatabase extends RoomDatabase {
    private static final Migration MIGRATION_14_15 = new Migration_14_15(14, 15);
    private static final Migration MIGRATION_15_16 = new Migration_15_16(15, 16);
    private static final Migration MIGRATION_16_17 = new Migration_16_17(16, 17);
    private static final Migration MIGRATION_17_18 = new Migration_17_18(17, 18);
    private static final Migration MIGRATION_18_19 = new Migration_18_19(18, 19);
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "adhell-database")
                            .addMigrations(MIGRATION_14_15)
                            .addMigrations(MIGRATION_15_16)
                            .addMigrations(MIGRATION_16_17)
                            .addMigrations(MIGRATION_17_18)
                            .addMigrations(MIGRATION_18_19)
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