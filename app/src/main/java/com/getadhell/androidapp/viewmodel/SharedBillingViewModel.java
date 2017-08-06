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
    public BillingModel billingModel;
    private BillingClient mBillingClient;
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
                        subs.add("basic_premium_three_months");
                        mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.SUBS, subs, result -> {
                            if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                                String price1Month;
                                String price3Months;
                                if (result.getSkuDetailsList().get(0).getSku().equals("basic_pro_subs")) {
                                    price1Month = result.getSkuDetailsList().get(0).getPrice();
                                    price3Months = result.getSkuDetailsList().get(1).getPrice();
                                } else {
                                    price1Month = result.getSkuDetailsList().get(1).getPrice();
                                    price3Months = result.getSkuDetailsList().get(0).getPrice();
                                }
                                billingModel.priceLiveData.postValue(mContext.getString(R.string.subscribe).replace("{{price}}", price1Month));
                                billingModel.threeMonthPriceLiveData.postValue(mContext.getString(R.string.subscribe_three_month).replace("{{price}}", price3Months));
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

    public void startSubscriptionDialog(Activity activity, String subsId) {
        BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                .setSku(subsId).setType(BillingClient.SkuType.SUBS);
        int responseCode = mBillingClient.launchBillingFlow(activity, builder.build());
    }

}
