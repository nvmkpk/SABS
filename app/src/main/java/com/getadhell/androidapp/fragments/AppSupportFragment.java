package com.getadhell.androidapp.fragments;


import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.viewmodel.SharedBillingViewModel;

public class AppSupportFragment extends LifecycleFragment {
    private static final String TAG = AppSupportFragment.class.getCanonicalName();
    private TextView supportDevelopmentTextView;
    private Button subscriptionButton;
    private Button goThreeMonthPremium;
    private SharedBillingViewModel sharedBillingViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedBillingViewModel = ViewModelProviders.of(this).get(SharedBillingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_support_fragment_title));
        View view = inflater.inflate(R.layout.fragment_app_support, container, false);

        supportDevelopmentTextView = view.findViewById(R.id.supportDevelopmentTextView);
        subscriptionButton = view.findViewById(R.id.subscriptionButton);
        goThreeMonthPremium = view.findViewById(R.id.goThreeMonthPremium);
        subscriptionButton.setEnabled(false);
        goThreeMonthPremium.setEnabled(false);

        sharedBillingViewModel.billingModel.isSupportedLiveData.observe(this, (isSupported) -> {
            if (isSupported != null && isSupported) {
                sharedBillingViewModel.billingModel.isPremiumLiveData.observe(this, (isPremium) -> {
                    if (isPremium != null && isPremium) {
                        supportDevelopmentTextView.setText(R.string.premium_subscriber_message);
                        subscriptionButton.setText(R.string.already_premium);
                        subscriptionButton.setEnabled(false);
                        goThreeMonthPremium.setText(R.string.already_premium);
                        goThreeMonthPremium.setEnabled(false);
                    } else {
                        supportDevelopmentTextView.setText(R.string.help_developers_to_keep_up_development);
                        sharedBillingViewModel.billingModel.priceLiveData.observe(this, (text) -> {
                            subscriptionButton.setText(text);
                        });
                        subscriptionButton.setEnabled(true);
                        subscriptionButton.setOnClickListener(v -> {
                            sharedBillingViewModel.startSubscriptionDialog(this.getActivity(), "basic_pro_subs");
                        });

                        sharedBillingViewModel.billingModel.threeMonthPriceLiveData.observe(this, (text) -> {
                            goThreeMonthPremium.setText(text);
                        });
                        goThreeMonthPremium.setEnabled(true);
                        goThreeMonthPremium.setOnClickListener(v -> {
                            sharedBillingViewModel.startSubscriptionDialog(this.getActivity(), "basic_premium_three_months");
                        });
                    }
                });
            } else {
                supportDevelopmentTextView.setText(R.string.subs_not_supported_text_view);
                subscriptionButton.setText(R.string.billing_not_supported);
                subscriptionButton.setEnabled(false);
                goThreeMonthPremium.setText(R.string.billing_not_supported);
                goThreeMonthPremium.setEnabled(false);
                Log.w(TAG, "Billing not supported");
            }
        });
        return view;
    }
}
