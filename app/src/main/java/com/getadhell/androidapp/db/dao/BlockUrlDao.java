package com.getadhell.androidapp.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.getadhell.androidapp.db.entity.BlockUrl;

import java.util.List;

@Dao
public interface BlockUrlDao {

    @Query("SELECT * FROM BlockUrl WHERE url = :url")
    public LiveData<BlockUrl> getBlockUrlByUrl(String url);

    @Query("SELECT * FROM BlockUrl")
    List<BlockUrl> getAll();

    @Query("SELECT * FROM BlockUrl WHERE urlProviderId = :urlProviderId")
    List<BlockUrl> getUrlsByProviderId(long urlProviderId);

    @Query("DELETE FROM BlockUrl WHERE urlProviderId = :urlProviderId")
    void deleteByUrlProviderId(int urlProviderId);

    @Query("DELETE FROM BlockUrl")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockUrl> blockUrls);
}
