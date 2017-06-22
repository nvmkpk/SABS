package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "blockUrls",
        foreignKeys = @ForeignKey(
                entity = BlockUrlProvider.class,
                parentColumns = "_id",
                childColumns = "urlProviderId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"url", "urlProviderId"}, unique = true)}
)
public class BlockUrl {
    @PrimaryKey
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "urlProviderId")
    public int urlProviderId;
}
