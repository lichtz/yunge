package com.yunge.im.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.yunge.im.interfaces.IBlackResult
import com.yunge.im.mode.UserBean
import okhttp3.*
import java.io.IOException

object PhoneUtil {
    private const val TAG = "PhoneUtil"

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */


    val DUAL_SIM_TYPES: Array<String> = arrayOf<String>(
        "subscription",
        "Subscription",
        "com.android.phone.extra.slot",
        "phone",
        "com.android.phone.DialingMode",
        "simId",
        "simnum",
        "phone_type",
        "simSlot",
        "extra_asus_dial_use_dualsim",
        "slot",
        "sim_slot",
        "slot_id",
        "slotId",
        "slotIdx"
    )


    fun callPhone1(activity: Activity, phoneNum: String, intCallSim: Int) {
        val intent = Intent(Intent.ACTION_CALL)
        val data = Uri.parse("tel:$phoneNum")
        val p = AppCache.getPhoneNum(activity)
        if (p!!.isMutableSim) {
            for (dualSimType in DUAL_SIM_TYPES) {
                intent.putExtra(dualSimType, intCallSim);
            }
            intent.putExtra("com.android.phone.force.slot", true);
            intent.putExtra("Cdma_Supp", true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(
                    "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                    " here You have to get phone account handle list by using telecom manger for both sims:- using this method getCallCapablePhoneAccounts()"
                );
            }
            Log.d(TAG, "callPhone1: mmmtable")
        }
        intent.data = data
        activity.startActivity(intent)
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone2(activity: Activity, phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        activity.startActivity(intent)
    }


    fun lauchCall(activity: Activity?, phoneNum: String?, intCallSim: Int) {
        if (activity == null || TextUtils.isEmpty(phoneNum)) {
            return
        }
        val p = AppCache.getPhoneNum(activity)
        if (p!!.isMutableSim && intCallSim == -1) {
            return
        }
        call(activity, phoneNum, intCallSim)
    }

    private fun call(activity: Activity, phoneNum: String?, intCallSim: Int) {
        var needAutoCall = false;
        if (AppCache.getPhoneNum(activity) != null && AppCache.getPhoneNum(activity)!!.isAutoCall) {
            needAutoCall = true;
        }
        if (!needAutoCall) {
            callPhone2(activity, phoneNum!!)
        } else {
            var hasPer = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                hasPer = i == PackageManager.PERMISSION_GRANTED
            }
            if (hasPer) {
                callPhone1(activity, phoneNum!!, intCallSim)
            } else {
                callPhone2(activity, phoneNum!!)
            }
        }
    }

    public fun checkUserInfo(activity: Activity?, num: String, iBlackResult: IBlackResult) {
        val userBean: UserBean = AppCache.data["user"] as UserBean
        if (userBean.bwsAllowd) {
            getBlackData(userBean.bwsToken, num, iBlackResult);
        }
    }

    private fun getBlackData(bwstoken: String, num: String, iBlackResult: IBlackResult) {
        val url = "http://112.51.246.6:9003/bws/caller/$bwstoken/$num"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get() //默认就是GET请求，可以不写
            .build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                iBlackResult?.canCall(false)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body()?.string()
                    val arseObject = JSON.parseObject(body)
                    val any = arseObject["code"]
                    iBlackResult?.canCall("200" == any)
                } catch (e: Exception) {
                    iBlackResult?.canCall(false)
                }


            }
        })
    }


}