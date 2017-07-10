package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.getadhell.androidapp.db.DateConverter;


@Entity(indices = {@Index("appName"), @Index("installTime")})
@TypeConverters(DateConverter.class)
public class AppInfo {
    @PrimaryKey
    public int id;
    public String packageName;
    public String appName;
    public long installTime;
    public boolean system;
}
