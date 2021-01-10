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

import com.yunge.im.R;
import com.yunge.im.interfaces.ICallEndLister;
import com.yunge.im.mode.PhoneNumBean;


public class PhoneReceiver extends BroadcastReceiver {

    String TAG = getClass().getName();
    String currentPhoneNumber = null;
    private ICallEndLister iCallEndLister;
    private boolean isV;
    private Activity activity;

    public PhoneReceiver() {

    }

    public PhoneReceiver(Activity activity, ICallEndLister iCallEndLister) {
        this.iCallEndLister = iCallEndLister;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Service.TELEPHONY_SERVICE);
        //设置监听
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //来电 去电判断
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            isV = true;
            currentPhoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "onReceive: currentPhoneNumber" + currentPhoneNumber);

        }
    }


    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "onCallStateChanged: s" + state + "  " + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:

                    if (TextUtils.isEmpty(incomingNumber) && TextUtils.isEmpty(currentPhoneNumber)) {

                        return;
                    }
                    PhoneNumBean phoneNumBean = AppCache.Companion.getPhoneNum(activity);
                    if (phoneNumBean == null || !phoneNumBean.isAuto() || TextUtils.isEmpty(incomingNumber)) {
                        iCallEndLister.showCallInfoAlert(false);
                    }else {
                        String phoneNum = null;
                            if (phoneNumBean.isMutableSim()) {

                                if (phoneNumBean.getCurrentSimIndex() == R.id.selectSimRbtn1) {
                                    phoneNum = phoneNumBean.getPhoneNum1();
                                } else if (phoneNumBean.getCurrentSimIndex() == R.id.selectSimRbtn2) {
                                    phoneNum = phoneNumBean.getPhoneNum2();
                                }
                            } else {
                                if (!TextUtils.isEmpty(phoneNumBean.getPhoneNum1())) {
                                    phoneNum = phoneNumBean.getPhoneNum1();
                                } else if (!TextUtils.isEmpty(phoneNumBean.getPhoneNum2())) {
                                    phoneNum = phoneNumBean.getPhoneNum2();
                                }
                            }
                        if (TextUtils.equals(phoneNum,incomingNumber)) {
                            iCallEndLister.end();
                            iCallEndLister.showCallInfoAlert(true);
                        }
                    }


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }
        }
    };
}