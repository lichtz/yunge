package com.yunge.im.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class PhoneUtils {
    companion object {
//        fun isMultiSim(context: Context): Boolean {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                val telephonyManager = context.getSystemService(AppCompatActivity.TELECOM_SERVICE) as TelecomManager
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.READ_PHONE_STATE
//                    ) == PackageManager.PERMISSION_GRANTED) {
//
//                }
//                val callCapablePhoneAccounts = telephonyManager.callCapablePhoneAccounts;
//                if (callCapablePhoneAccounts.size > 0) {
//                    return true;
//                }
//            }
//
//            return false;
//        }

        fun isNightMode(context: Context): Boolean {
            val currentNightMode = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }
    }




}