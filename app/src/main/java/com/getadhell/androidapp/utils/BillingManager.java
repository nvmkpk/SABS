package com.getadhell.androidapp.utils;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {
    private final static String TAG = BillingManager.class.getCanonicalName();
    private static final HashMap<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.SUBS, Collections.singletonList("basic_pro_subs"));
    }

    public final BillingClient mBillingClient;
    private final Activity mActivity;

    public BillingManager(Activity mActivity) {
        this.mActivity = mActivity;
        mBillingClient = new BillingClient.Builder(mActivity).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                } else {
                    Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {

    }

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String skuType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        mBillingClient.querySkuDetailsAsync(skuType, skuList, listener);
    }

}
