package com.yunge.im.service;

import android.annotation.SuppressLint;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class PhoneCallService extends InCallService {
    private static final String TAG = "PhoneCallService";

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            switch (state) {
                case Call.STATE_ACTIVE: {
                    Log.d(TAG, "onStateChanged: ");
                    break; // 通话中
                }
                case Call.STATE_DISCONNECTED: {
                    Log.d(TAG, "STATE_DISCONNECTED: ");
                    break; // 通话结束
                }
            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.d(TAG, "onCallAdded: ");
        call.registerCallback(callback);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.d(TAG, "onCallRemoved: ");
        call.unregisterCallback(callback);
    }
}