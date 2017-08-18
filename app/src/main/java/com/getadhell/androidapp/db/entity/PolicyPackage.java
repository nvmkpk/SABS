package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.getadhell.androidapp.db.DateConverter;

import java.util.Date;
import java.util.List;

@Entity(
        tableName = "PolicyPackage"
)
@TypeConverters(DateConverter.class)
public class PolicyPackage {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "active")
    public boolean active;

    @ColumnInfo(name = "createdAt")
    public Date createdAt;

    @ColumnInfo(name = "updatedAt")
    public Date updatedAt;

    @Ignore
    public List<DisabledPackage> disabledPackages;

    @Ignore
    public List<BlockUrlProvider> blockUrlProviders;

    @Ignore
    public List<UserBlockUrl> userBlockUrls;

    @Ignore
    public List<WhiteUrl> whiteUrls;

    @Ignore
    public List<AppPermission> appPermissions;

}
