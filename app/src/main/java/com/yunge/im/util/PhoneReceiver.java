package com.yunge.im.util;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.yunge.im.interfaces.ICallEndLister;


public class PhoneReceiver extends BroadcastReceiver {

    String TAG = getClass().getName();
    String currentPhoneNumber = null;
    private ICallEndLister iCallEndLister;
    private boolean isV;

    public PhoneReceiver() {

    }

    public PhoneReceiver(Activity activity,ICallEndLister iCallEndLister) {
        this.iCallEndLister = iCallEndLister;
                    TelephonyManager tm = (TelephonyManager) activity.getSystemService(Service.TELEPHONY_SERVICE);
            //设置监听
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //来电 去电判断
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            isV = true;
            currentPhoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "onReceive: currentPhoneNumber"+currentPhoneNumber);

        }
    }

    private boolean hasHook = false;

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "onCallStateChanged: s"+state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
//                    if (isV && iCallEndLister != null && hasHook) {
//                        isV = false;
//                        hasHook = false;
//                    }
                    if (hasHook){
                        iCallEndLister.end();
                        hasHook = false;
                    }


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    hasHook = true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }
        }
    };
}