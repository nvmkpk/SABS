package com.getadhell.androidapp.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.TypeConverters;

import com.getadhell.androidapp.db.DateConverter;

@Dao
@TypeConverters(DateConverter.class)
public interface WhiteUrlDao {
}
