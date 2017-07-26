package com.getadhell.androidapp.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.model.BillingModel;

import java.util.ArrayList;
import java.util.List;

public class SharedBillingViewModel extends AndroidViewModel {
    private static final String TAG = SharedAppPermissionViewModel.class.getCanonicalName();
    public BillingClient mBillingClient;
    public BillingModel billingModel;
    private Context mContext;


    public SharedBillingViewModel(Application application) {
        super(application);
        mContext = application.getApplicationContext();
        billingModel = new BillingModel();
        mBillingClient = new BillingClient.Builder(mContext)
                .setListener((responseCode, purchases) -> {
                    if (purchases != null && purchases.size() > 0) {
                        billingModel.isPremiumLiveData.postValue(true);
                    }
                })
                .build();
        startBillingConnection();
    }

    public void startBillingConnection() {
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                    billingModel.isSupportedLiveData.postValue(true);

                    // Check for purchased items
                    Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    if (purchasesResult.getPurchasesList().size() > 0) {
                        billingModel.isPremiumLiveData.postValue(true);
                    } else {
                        billingModel.isPremiumLiveData.postValue(false);
                        List<String> subs = new ArrayList<>();
                        subs.add("basic_pro_subs");
                        mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.SUBS, subs, result -> {
                            if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                                billingModel.priceLiveData.postValue(mContext.getString(R.string.subscribe).replace("{{price}}", result.getSkuDetailsList().get(0).getPrice()));
                            }
                        });
                    }

                } else {
                    billingModel.isSupportedLiveData.postValue(false);
                    Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    public void startSubscriptionDialog(Activity activity) {
        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku("basic_pro_subs").setType(BillingClient.SkuType.SUBS);
        int responseCode = mBillingClient.launchBillingFlow(activity, builder.build());
    }

}
