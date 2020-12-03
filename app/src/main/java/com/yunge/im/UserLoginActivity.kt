package com.yunge.im

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.yunge.im.activity.PdfActivity
import com.yunge.im.interfaces.ILoginResult
import com.yunge.im.mode.UserBean
import com.yunge.im.util.SharePreferenceUtils
import com.yunge.im.util.UserUtil


class UserLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        QMUIStatusBarHelper.setStatusBarLightMode(this)
        try {
            val userStr = SharePreferenceUtils.get(this, "user", "") as String

            if (!TextUtils.isEmpty(userStr)){

                val userBean =
                    JSON.parseObject(userStr, UserBean::class.java)
                if (!TextUtils.isEmpty(userBean.loginAccount)){
                    UserUtil.login(
                        this,
                        userBean.loginAccount,
                        userBean.loginPass,
                        object : ILoginResult {
                            override fun result(userBean: UserBean?, errorTip: String?) {
                                if (userBean == null) {
                                    runOnUiThread {
                                        showView();

                                    }

                                } else {
                                    startActivity(
                                        Intent(
                                            this@UserLoginActivity,
                                            SettingActivity::class.java
                                        )
                                    )
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

    private var isAccecpt:Boolean = false;
    private fun showView() {
        setContentView(R.layout.activity_user_login)
        val account = findViewById<EditText>(R.id.userAccount)
        val pass = findViewById<EditText>(R.id.passInput)
        val loginBtn = findViewById<Button>(R.id.login)
        val xuke = findViewById<TextView>(R.id.listce)
        val content = "我已阅读并同意隐私政策和保密协议" //文本内容在上面已经有了

        val spannable = SpannableStringBuilder(content)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@UserLoginActivity, PdfActivity::class.java)
                intent.putExtra("acc","1");
                startActivity(intent);

            }

        }, 7, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@UserLoginActivity, PdfActivity::class.java)
                intent.putExtra("acc","2");
                startActivity(intent);
            }

        }, 12, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //这个一定要记得设置，不然点击不生效。
        //这个一定要记得设置，不然点击不生效。
        xuke.setMovementMethod(LinkMovementMethod.getInstance())
        xuke.setText(spannable)

        val lrd = findViewById<CheckBox>(R.id.lrd)
        lrd.setOnCheckedChangeListener { buttonView, isChecked ->
            isAccecpt = isChecked;
        }

        val lastAc: String = SharePreferenceUtils.get(this, "loginAc", "") as String
        (account as TextView).text = lastAc;
        loginBtn!!.setOnClickListener {
            if (!isAccecpt){
                Toast.makeText(this,"请勾选用户协议",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var pindex = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = checkSelfPermission("android.permission.CALL_PHONE")
                val i2 = checkSelfPermission("android.permission.PROCESS_OUTGOING_CALLS")
                if (i != PackageManager.PERMISSION_GRANTED && i2 != PackageManager.PERMISSION_GRANTED) {
                    pindex = 3;
                } else if (i2 != PackageManager.PERMISSION_GRANTED) {
                    pindex = 2;
                } else if (i != PackageManager.PERMISSION_GRANTED) {
                    pindex = 1;
                }
            }

            if (pindex != 0) {
                var s: String = "";
                var pArr = arrayOf("")
                if (pindex == 1) {
                    s = "【拨打电话权】用于呼叫转移功能，属于必要权限请授权";
                    pArr = arrayOf(
                        "android.permission.CALL_PHONE",
                    )
                } else if (pindex == 2) {
                    s = "【通讯记录权限】用于取消呼叫转移功能，属于必要权限请授权";
                    pArr = arrayOf(
                        "android.permission.PROCESS_OUTGOING_CALLS"
                    )
                } else if (pindex == 3) {
                    s = "【拨打电话权】限用于呼叫转移功能\n【通讯记录权限】限用于取消呼叫转移功能\n属于必要权限请授权"
                    pArr = arrayOf(
                        "android.permission.CALL_PHONE",
                        "android.permission.PROCESS_OUTGOING_CALLS"
                    )
                }

                QMUIDialog.MessageDialogBuilder(this)
                    .setTitle("权限请求")
                    .setMessage(s)
                    .addAction(
                        "取消"
                    ) { dialog, index -> dialog.dismiss() }
                    .addAction(
                        0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE
                    ) { dialog, index ->
                        dialog.dismiss()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(pArr, 213)
                        };
                    }
                    .create().show()

                return@setOnClickListener
            }

            if (account.text != null && !account.text.toString()
                    .isEmpty() && pass.text != null && !pass.text.toString().isEmpty()
            ) {
                var tipDialog = QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .create()
                tipDialog!!.show()
                UserUtil.login(
                    this,
                    account.text.toString(),
                    pass.text.toString(),
                    object : ILoginResult {
                        override fun result(userBean: UserBean?, errorTip: String?) {
                            tipDialog.dismiss()
                            if (userBean == null) {
                                showError(errorTip);
                            } else {
                                startActivity(
                                    Intent(
                                        this@UserLoginActivity,
                                        SettingActivity::class.java
                                    )
                                )
                                SharePreferenceUtils.put(
                                    this@UserLoginActivity,
                                    "loginAc",
                                    account.text.toString()
                                )
                                finish()
                            }
                        }
                    })
            }

        }
    }


    fun showError(tip: String?) {
        runOnUiThread {
            var tipDialog = QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(tip)
                .create()
            tipDialog!!.show()
            findViewById<Button>(R.id.login)?.postDelayed(Runnable { tipDialog.dismiss() }, 2500)
        }

    }


}