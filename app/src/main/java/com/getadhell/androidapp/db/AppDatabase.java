package com.getadhell.androidapp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;

@Database(entities = {BlockUrlProvider.class, BlockUrl.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
}
