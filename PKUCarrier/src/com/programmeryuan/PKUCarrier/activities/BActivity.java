package com.programmeryuan.PKUCarrier.activities;

import android.os.Bundle;
public class BActivity extends AngelActivity {
//    public LoadingDialog dialog;
//    public boolean destroyed  = false;
    protected boolean threadRunning = false;

    protected void onCreate(Bundle savedInstanceState, int[] feature_id) {
        super.onCreate(savedInstanceState, feature_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

//    @Override
//    protected void onDestroy() {
//        destroyed = true;
//        if(dialog!=null){
//            dialog.forceDismiss();
//        }
//
//        super.onDestroy();
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
