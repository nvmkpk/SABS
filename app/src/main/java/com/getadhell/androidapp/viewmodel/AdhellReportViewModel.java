package com.getadhell.androidapp.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.ReportBlockedUrl;

import java.util.Date;
import java.util.List;

public class AdhellReportViewModel extends AndroidViewModel {
    private LiveData<List<ReportBlockedUrl>> reportBlockedUrls;
    private AppDatabase mDb;

    public AdhellReportViewModel(Application application) {
        super(application);
        mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
    }

    public LiveData<List<ReportBlockedUrl>> getReportBlockedUrls() {
        if (reportBlockedUrls == null) {
            reportBlockedUrls = new MutableLiveData<>();
            loadReportBlockedUrls();
        }
        return reportBlockedUrls;
    }

    private void loadReportBlockedUrls() {
        reportBlockedUrls =
                mDb.reportBlockedUrlDao().getReportBlockUrlBetween(
                        new Date(System.currentTimeMillis() - 24 * 3600 * 1000), new Date());
    }

}
