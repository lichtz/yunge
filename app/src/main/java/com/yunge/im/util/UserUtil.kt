package com.yunge.im.util

import android.app.Activity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.yunge.im.interfaces.ILoginResult
import com.yunge.im.mode.UserBean
import okhttp3.*
import java.io.IOException

class UserUtil {

    companion object {
        public fun login(ac:Activity,name: String, pass: String, iLoginResult: ILoginResult) {

            var url = "http://8.135.13.129:9000/caller/api/login" //必须以反斜杠结尾
            val JSONM = MediaType.parse("application/json; charset=utf-8");
            val json = com.alibaba.fastjson.JSONObject();
            json["username"] = name;
            json["password"] = pass
            val toJSONString = json.toJSONString()
            val request: Request = Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONM, toJSONString))
                .build()
            val okHttpClient = OkHttpClient()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (iLoginResult != null) {
                        iLoginResult.result(null, "请求错误，请联系供应商")
                    }


                }

                override fun onResponse(call: Call, response: Response) {
                    try {

                        val code = response.code()
                        if (code == 400) {
                            if (iLoginResult != null) {
                                iLoginResult.result(null, "用户不存在")
                            }

                        } else if (code == 403) {
                            if (iLoginResult != null) {
                                iLoginResult.result(null, "密码错误")
                            }
                        } else if (code == 200) {
                            val string = response.body()!!.string()
                            val userBean = JSON.parseObject(string, UserBean::class.java)
                            userBean.loginAccount = name;
                            userBean.loginPass = pass;
                            AppCache.data["user"] = userBean
                            val toString = JSONObject.toJSONString(userBean)
                            SharePreferenceUtils.put(ac, "user", toString)
                            if (iLoginResult != null) {
                                iLoginResult.result(userBean, "")
                            }

                        } else {
                            if (iLoginResult != null) {
                                iLoginResult.result(null, "请求错误，请联系供应商")
                            }
                        }

                    } catch (e: Exception) {
                        if (iLoginResult != null) {
                            iLoginResult.result(null, "请求错误，请联系供应商")
                        }
                    }
                }
            });


        }
    }
}