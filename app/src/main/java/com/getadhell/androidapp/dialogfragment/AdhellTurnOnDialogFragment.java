package com.getadhell.androidapp.dialogfragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;

public class AdhellTurnOnDialogFragment extends DialogFragment {

    public AdhellTurnOnDialogFragment() {
    }

    public static AdhellTurnOnDialogFragment newInstance(String title) {
        AdhellTurnOnDialogFragment adhellTurnOnDialogFragment = new AdhellTurnOnDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        adhellTurnOnDialogFragment.setArguments(args);
        return adhellTurnOnDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_turn_on_adhell, container);
    }
}
