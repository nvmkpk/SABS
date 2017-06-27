package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.getadhell.androidapp.db.DateConverter;

import java.util.Date;


@Entity(
        foreignKeys = @ForeignKey(
                entity = BlockUrlProvider.class,
                parentColumns = "_id",
                childColumns = "urlProviderId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"url", "urlProviderId"}, unique = true)}
)
@TypeConverters(DateConverter.class)
public class BlockUrl {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "urlProviderId")
    public int urlProviderId;

    public Date updatedAt;

}
