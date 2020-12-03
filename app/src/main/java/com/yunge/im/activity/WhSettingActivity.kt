package com.yunge.im.activity

import android.R.id.edit
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.qmuiteam.qmui.layout.QMUIButton
import com.yunge.im.R
import com.yunge.im.mode.PhoneNumBean
import com.yunge.im.util.AppCache
import com.yunge.im.util.PhoneUtil


class WhSettingActivity : AppCompatActivity() {
    var phoneNumBean: PhoneNumBean? = null
    val waitTime: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wh_setting)

        phoneNumBean = AppCache.getPhoneNum(this)
        val sim1EditText = findViewById<EditText>(R.id.sim1Num)
        if (!TextUtils.isEmpty(phoneNumBean!!.phoneNum1)) {
            sim1EditText.setText(phoneNumBean!!.phoneNum1)
        }
        val sim2EditText = findViewById<EditText>(R.id.sim2Num)

        if (!TextUtils.isEmpty(phoneNumBean!!.phoneNum2)) {
            sim2EditText.setText(phoneNumBean!!.phoneNum2)
        }
        val sim1EditBtn = findViewById<QMUIButton>(R.id.edit1)
        val sim2EditBtn = findViewById<QMUIButton>(R.id.edit2)
        sim1EditBtn.setChangeAlphaWhenPress(true)
        sim1EditBtn.tag = 1
        sim2EditBtn.setChangeAlphaWhenPress(true)
        sim2EditBtn.tag = 1
        sim1EditBtn.setOnClickListener {
            if (sim1EditBtn.tag == 1) {
                sim1EditText.isEnabled = true
                sim1EditText.requestFocus()
                (sim1EditText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
                sim1EditText.setSelection(sim1EditText.text.length)
                sim1EditBtn.tag = 0
                sim1EditBtn.text = "保存"

            } else {
                sim1EditText.isEnabled = false
                sim1EditBtn.tag = 1
                sim1EditBtn.text = "编辑"
                phoneNumBean!!.phoneNum1 = sim1EditText.text.toString()
                AppCache.setPhoneNum(this, phoneNumBean)
            }

        }
        sim2EditBtn.setOnClickListener {
            if (sim2EditBtn.tag == 1) {
                sim2EditText.isEnabled = true
                sim2EditText.requestFocus()
                (sim2EditText.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .toggleSoftInput(0, InputMethodManager.SHOW_FORCED)
                sim2EditText.setSelection(sim2EditText.text.length)
                sim2EditBtn.tag = 0
                sim2EditBtn.text = "保存"
            } else {
                sim2EditText.isEnabled = false
                sim2EditBtn.tag = 1
                sim2EditBtn.text = "编辑"
                phoneNumBean!!.phoneNum2 = sim2EditText.text.toString()
                AppCache.setPhoneNum(this, phoneNumBean)
            }

        }


        val selectSimRadioButton = findViewById<RadioGroup>(R.id.selectSim)
        if (phoneNumBean!!.currentSimIndex != null) {
            selectSimRadioButton.check(phoneNumBean!!.currentSimIndex!!.toInt())
        }
        selectSimRadioButton.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.selectSimRbtn1) {
                phoneNumBean!!.setSimIndex(0)
                phoneNumBean!!.currentSimIndex = R.id.selectSimRbtn1
                AppCache.setPhoneNum(this, phoneNumBean)
            } else {
                phoneNumBean!!.setSimIndex(1)
                phoneNumBean!!.currentSimIndex = R.id.selectSimRbtn2
                AppCache.setPhoneNum(this, phoneNumBean)
            }
        }



        val selectSimRadioButtonCallMode = findViewById<RadioGroup>(R.id.selectSimCallMode)
            if (phoneNumBean!!.callTwoSimMode  == "1" || phoneNumBean!!.callTwoSimMode == "") {
                selectSimRadioButtonCallMode.check(R.id.selectSimRbtnCall1)
            }else{
                selectSimRadioButtonCallMode.check(R.id.selectSimRbtnCall2)
            }
        selectSimRadioButtonCallMode.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.selectSimRbtnCall1) {
                phoneNumBean!!.callTwoSimMode ="1"
                AppCache.setPhoneNum(this, phoneNumBean)
            } else {
                phoneNumBean!!.callTwoSimMode ="2"
                AppCache.setPhoneNum(this, phoneNumBean)
            }
        }



        val selectYysRadioButton = this.findViewById<RadioGroup>(R.id.selectYys)
        if (phoneNumBean!!.yys != -1) {
            selectYysRadioButton.check(phoneNumBean!!.currentyysIndex!!.toInt())
        }
        selectYysRadioButton.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.selectYysLt) {
                phoneNumBean!!.yys = 2
                phoneNumBean!!.currentyysIndex = R.id.selectYysLt
                AppCache.setPhoneNum(this, phoneNumBean)
            } else if(checkedId == R.id.selectYysLdx){
                phoneNumBean!!.yys = 3
                phoneNumBean!!.currentyysIndex = R.id.selectYysLdx
                AppCache.setPhoneNum(this, phoneNumBean)
            }else{
                phoneNumBean!!.yys = 1
                phoneNumBean!!.currentyysIndex = R.id.selectYysYd
                AppCache.setPhoneNum(this, phoneNumBean)
            }
        }
        val switchMaterial = findViewById<SwitchMaterial>(R.id.switchBtn)
        if (phoneNumBean!!.isAutoCall) {
            switchMaterial.isChecked = true
        }
        switchMaterial.setOnCheckedChangeListener { _, isChecked ->

            var hasPer = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = checkSelfPermission("android.permission.CALL_PHONE")
                hasPer = i == PackageManager.PERMISSION_GRANTED
            }

            if (!hasPer) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf("android.permission.CALL_PHONE"), 213)
                }
                switchMaterial.isChecked = false

            } else {
                phoneNumBean!!.isAutoCall = isChecked
                AppCache.setPhoneNum(this, phoneNumBean)
            }


        }
        val clearBtn = findViewById<FrameLayout>(R.id.autoClearFm)
        clearBtn.setOnClickListener {
            val phoneNum = AppCache.getPhoneNum(this);
            if (phoneNum != null) {
                PhoneUtil.lauchCall(this, "%23%23002%23",phoneNum.getSimIndex())
            }
        }

        val hzs = findViewById<SwitchMaterial>(R.id.hzSwitchBtn)
        hzs.isChecked = phoneNumBean!!.isHz
        hzs.setOnCheckedChangeListener { _, isChecked ->

            var hasPer = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val i = checkSelfPermission("android.permission.CALL_PHONE")
                hasPer = i == PackageManager.PERMISSION_GRANTED
            }

            if (!hasPer) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf("android.permission.CALL_PHONE"), 213)
                }
                switchMaterial.isChecked = false

            } else {
                phoneNumBean!!.isHz = isChecked
                AppCache.setPhoneNum(this, phoneNumBean)
            }


        }
        val waitTime = findViewById<EditText>(R.id.waitTime)
        if (phoneNumBean!!.waitTime != -1) {
            val textView = waitTime as TextView
            textView.text = "${phoneNumBean!!.waitTime}"
        }
        waitTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    phoneNumBean?.waitTime = waitTime.text.toString().toInt()
                } catch (e: Exception) {

                }
            }
        })

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener { finish() }
    }

    override fun onPause() {
        try {
            if (waitTime?.text != null) {
                if (!waitTime.text.toString().equals(phoneNumBean?.waitTime)) {
                    phoneNumBean?.waitTime = waitTime.text.toString().toInt()
                    AppCache.setPhoneNum(this, phoneNumBean)
                }
            }
        } catch (e: Exception) {

        }
        super.onPause()
    }



}