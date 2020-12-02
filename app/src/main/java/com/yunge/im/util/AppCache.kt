package com.yunge.im.util

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.yunge.im.mode.PhoneNumBean
import java.util.*

class AppCache {
    companion object {
        var data: MutableMap<String, Any?> = HashMap<String, Any?>()
        private var phoneNumBean: PhoneNumBean? = null;
        fun setPhoneNum(ac: Activity?, phoneBean: PhoneNumBean?) {
            phoneNumBean = phoneBean;
            if (phoneBean != null) {
                val toJSONString = JSON.toJSONString(phoneNumBean)
                SharePreferenceUtils.put(ac, "phoneBean", toJSONString);
            }
        }

        fun setPhoneNum(ac: Activity?, phoneBean: PhoneNumBean?, isSave: Boolean) {
            phoneNumBean = phoneBean;
            if (isSave && phoneBean != null) {
                val toJSONString = JSON.toJSONString(phoneNumBean)
                SharePreferenceUtils.put(ac, "phoneBean", toJSONString);
            }
        }

        fun getPhoneNum(ac: Activity?): PhoneNumBean? {
            if (phoneNumBean != null) {
                return phoneNumBean;
            }

            val phoneBeanStr = SharePreferenceUtils.get(ac, "phoneBean", "")
            if (phoneBeanStr != null && !TextUtils.isEmpty(phoneBeanStr.toString())) {
                phoneNumBean = JSON.parseObject(phoneBeanStr.toString(), PhoneNumBean::class.java)
            }
            if (phoneNumBean == null) {
                phoneNumBean = PhoneNumBean();
            }
            return phoneNumBean;
        }
    }

}