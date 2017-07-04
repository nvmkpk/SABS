package com.getadhell.androidapp.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "ReportBlockedUrl")
public class ReportBlockedUrl {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;
    public String url;
    public String packageName;
    public Date blockDate;

    public ReportBlockedUrl() {
    }

    public ReportBlockedUrl(String url, String packageName, Date blockDate) {
        this.url = url;
        this.packageName = packageName;
        this.blockDate = blockDate;
    }

    @Override
    public String toString() {
        return "ReportBlockedUrl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", packageName='" + packageName + '\'' +
                ", blockDate=" + blockDate +
                '}';
    }
}
