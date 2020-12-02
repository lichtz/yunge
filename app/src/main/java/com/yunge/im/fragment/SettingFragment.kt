package com.yunge.im.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.google.android.material.switchmaterial.SwitchMaterial
import com.qmuiteam.qmui.layout.QMUIButton
import com.yunge.im.LoginActivity
import com.yunge.im.R
import com.yunge.im.SettingActivity
import com.yunge.im.UserLoginActivity
import com.yunge.im.activity.ChangeUserPass
import com.yunge.im.activity.WhSettingActivity
import com.yunge.im.mode.PhoneNumBean
import com.yunge.im.mode.UserBean
import com.yunge.im.util.AppCache
import com.yunge.im.util.PhoneUtil
import com.yunge.im.util.SharePreferenceUtils
import java.lang.Exception

class SettingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.findViewById<QMUIButton>(R.id.loginOut).setOnClickListener {

            AppCache.data["user"] = null
            SharePreferenceUtils.put(getActivity(), "user", "")
            startActivity(Intent(getActivity(), UserLoginActivity::class.java));
            activity!!.finish()
        }

        val setting = view.findViewById<View>(R.id.hjSetting)
        setting.setOnClickListener {
            val intent = Intent(activity, WhSettingActivity::class.java)
            startActivity(intent)
        }
        val name = view.findViewById<TextView>(R.id.name)
        val user:UserBean = AppCache.data["user"] as UserBean
        if (user != null) {
            name.text = user.username
        }
        view.findViewById<View>(R.id.changePass).setOnClickListener {
            val intent = Intent(activity, ChangeUserPass::class.java)
            startActivity(intent)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {}

}