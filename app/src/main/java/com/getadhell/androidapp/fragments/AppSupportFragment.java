package com.getadhell.androidapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.getadhell.androidapp.R;

import java.util.ArrayList;
import java.util.List;

public class AppSupportFragment extends Fragment implements PurchasesUpdatedListener {
    private static final String TAG = AppSupportFragment.class.getCanonicalName();
    private BillingClient mBillingClient;
    private TextView supportDevelopmentTextView;
    private Button subscriptionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: check if in-app billing is available
        mBillingClient = new BillingClient.Builder(getActivity()).setListener(this).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_support_fragment_title));
        View view = inflater.inflate(R.layout.fragment_app_support, container, false);

        supportDevelopmentTextView = (TextView) view.findViewById(R.id.supportDevelopmentTextView);
        subscriptionButton = (Button) view.findViewById(R.id.subscriptionButton);
        subscriptionButton.setEnabled(false);

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);

                    // Check for purchased items
                    Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    if (purchasesResult.getPurchasesList().size() > 0) {
                        supportDevelopmentTextView.setText(R.string.premium_subscriber_message);
                        subscriptionButton.setText(R.string.already_premium);
                        subscriptionButton.setEnabled(false);
                    } else {
                        // Get currency and price
                        subscriptionButton.setEnabled(true);
                        List<String> subs = new ArrayList<>();
                        subs.add("basic_pro_subs");
                        mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.SUBS, subs, result -> {
                            if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                                subscriptionButton.setText(getString(R.string.subscribe).replace("{{price}}", result.getSkuDetailsList().get(0).getPrice()));
                            }
                        });
                        subscriptionButton.setOnClickListener(v -> {
                            BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                                    .setSku("basic_pro_subs").setType(BillingClient.SkuType.SUBS);
                            int responseCode = mBillingClient.launchBillingFlow(getActivity(), builder.build());
                        });
                    }

                } else {
                    Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });

        return view;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if (purchases != null && purchases.size() > 0) {
//            purchases.get(0).
            supportDevelopmentTextView.setText("Thank you for being premium subscriber");
            subscriptionButton.setText("Your subscription is valid");
            subscriptionButton.setEnabled(false);
        }

    }
}
