package com.getadhell.androidapp.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.getadhell.androidapp.db.entity.BlockUrl;

import java.util.List;

@Dao
public interface BlockUrlDao {

    @Query("SELECT * FROM blockUrls")
    List<BlockUrl> getAll();

    @Query("DELETE FROM blockUrls WHERE urlProviderId = :urlProviderId")
    void deleteByUrlProviderId(int urlProviderId);

    @Query("DELETE FROM blockUrls")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(BlockUrl... blockUrls);


}
