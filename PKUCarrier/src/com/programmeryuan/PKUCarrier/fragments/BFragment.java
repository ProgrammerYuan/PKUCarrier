package com.programmeryuan.PKUCarrier.fragments;

import android.os.Bundle;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.utils.LoadingDialog;

public class BFragment extends AngelFragment {
    LoadingDialog dialog;
    private String realname = null;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.forceDismiss();
        }
        super.onDestroyView();
    }

    public String getRealName() {
        return realname;
    }

    public void setRealName(String name) {
        realname = name;
    }

    public boolean allowBackPress() {
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
