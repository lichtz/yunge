package com.yunge.im

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.qmuiteam.qmui.layout.QMUIButton
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.yunge.im.interfaces.ILoginResult
import com.yunge.im.mode.UserBean
import com.yunge.im.util.AppCache
import com.yunge.im.util.SharePreferenceUtils
import okhttp3.*
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    var loginBtn: QMUIButton? = null;
    lateinit var account: EditText;
    private val mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val nightMode = PhoneUtils.isNightMode(this)
//        if (nightMode) {
//            QMUIStatusBarHelper.setStatusBarDarkMode(this)
//        }
//        else {
            QMUIStatusBarHelper.setStatusBarLightMode(this)
//        }

        try {
            val userStr = SharePreferenceUtils.get(getActivity(), "user", "") as String

            if (!TextUtils.isEmpty(userStr)){

                val userBean =
                    JSON.parseObject(userStr, UserBean::class.java)
                if (!TextUtils.isEmpty(userBean.loginAccount)){
                    login(userBean.loginAccount, userBean.loginPass, object : ILoginResult {
                        override fun result(userBean: UserBean?, errorTip: String?) {
                            if (userBean == null) {
                                runOnUiThread {
                                    showView();

                                }

                            } else {
                                startActivity(Intent(getActivity(), SettingActivity::class.java))
                                finish()
                            }
                        }
                    })
                    return

                }

            }
        }catch (e: Exception){

        }



        showView()
    }

    private fun showView() {
        setContentView(R.layout.activity_main)
        account = findViewById<EditText>(R.id.userAccount)
        val lastAc: String = SharePreferenceUtils.get(getActivity(), "loginAc", "") as String
        findViewById<TextView>(R.id.userAccount).text = lastAc;
        val pass = findViewById<EditText>(R.id.passInput)
        loginBtn = findViewById<QMUIButton>(R.id.login)
        loginBtn!!.setChangeAlphaWhenPress(true)
        loginBtn!!.setOnClickListener {
            var pList:ArrayList<String> = ArrayList();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
                if(i != PackageManager.PERMISSION_GRANTED ){
                    pList.add(Manifest.permission.CALL_PHONE)
                }
                val i2 =
                    getActivity().checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                if(i2 != PackageManager.PERMISSION_GRANTED ){
                    pList.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
                }
                val i3 =
                    getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                if(i3 != PackageManager.PERMISSION_GRANTED ){
                    pList.add(Manifest.permission.READ_PHONE_STATE)
                }

            }

            if (pList.size > 0) {
                val s = "【拨打电话权】限用于呼叫转移功能\n【通讯记录权限】限用于取消呼叫转移功能\n属于必要权限请授权"
                val pArr = pList.toTypedArray()
                MessageDialogBuilder(getActivity())
                    .setTitle("权限请求")
                    .setMessage(s)
                    .addAction(
                        "取消"
                    ) { dialog, index -> dialog.dismiss() }
                    .addAction(
                        0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE
                    ) { dialog, index ->
                        dialog.dismiss()

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            requestPermissions(pArr, 213)
                        };
                    }
                    .create(mCurrentDialogStyle).show()

                return@setOnClickListener
            }



            if (account.text != null && !account.text.toString()
                    .isEmpty() && pass.text != null && !pass.text.toString().isEmpty()
            ) {
    //                QMUILoadingView(this).start();
                tipDialog = QMUITipDialog.Builder(getActivity())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .create()
                tipDialog!!.show()
                login(account.text.toString(), pass.text.toString(), object : ILoginResult {
                    override fun result(userBean: UserBean?, errorTip: String?) {
                        if (userBean == null) {
                            tipDialog!!.dismiss()
                            showError(errorTip);
                        } else {
                            startActivity(Intent(getActivity(), SettingActivity::class.java))
                            finish()
                        }
                    }
                })
            }

        }
    }

    private var tipDialog: QMUITipDialog? = null;
    private fun login(name: String, pass: String, iLoginResult: ILoginResult) {

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
                    tipDialog!!.dismiss()

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
                        val userBean =
                            JSON.parseObject(string, UserBean::class.java)
                        userBean.loginAccount = name;
                        userBean.loginPass = pass;
                        AppCache.data["user"] = userBean
                        val toString = JSONObject.toJSONString(userBean)
                        SharePreferenceUtils.put(getActivity(), "user", toString)
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

    fun getActivity(): Activity {
        return this;
    }


    fun showError(tip: String?) {
        getActivity().runOnUiThread {
            tipDialog = QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(tip)
                .create()
            tipDialog!!.show()
            loginBtn?.postDelayed(Runnable { tipDialog!!.dismiss() }, 2500)
        }

    }


}