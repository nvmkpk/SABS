package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "blockUrlProviders", indices = {@Index(value = {"url"}, unique = true)})
public class BlockUrlProvider {
    @PrimaryKey
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "count")
    public int count;

    @ColumnInfo(name = "lastUpdated")
    public String lastUpdated;

    @ColumnInfo(name = "deletable")
    public boolean deletable;

    @ColumnInfo(name = "selected")
    public boolean selected;
}
