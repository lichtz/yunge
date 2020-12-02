package com.yunge.im.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.yunge.im.R
import com.yunge.im.activity.WhSettingActivity
import com.yunge.im.adapter.ContactAdapter
import com.yunge.im.interfaces.IBlackResult
import com.yunge.im.interfaces.ICallNumListener
import com.yunge.im.mode.CallLogBean
import com.yunge.im.mode.UserBean
import com.yunge.im.util.AppCache
import com.yunge.im.util.ContactUtils
import com.yunge.im.util.NoticeService
import com.yunge.im.util.PhoneUtil
import java.lang.ref.WeakReference


class CallPhoneFragment : Fragment(), View.OnClickListener {
    private val TAG = "CallPhoneFragment"
    private var permissionList: Array<String>? = null;
    var clientAdapter: ContactAdapter? = null;
    var allContacts: List<CallLogBean>? = null;
    var contentCallLog: List<CallLogBean>? = null;
    var panterl: LinearLayout? = null;
    var stringBuilder: StringBuilder = StringBuilder();
    var showKeyboardView: View? = null;
    var mRecyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.call_keyboard_layout, null)
    }

    lateinit var numTv: TextView;
    var clickTag:String ? = null;
    val iCallNumListener: ICallNumListener = ICallNumListener {
        clickTag = it;
        numTv.text = it;
        showKeyboardView?.visibility = View.GONE;
        panterl?.visibility = View.VISIBLE;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NoticeService.iCallNumListenerWeakReference = WeakReference(iCallNumListener)
        val f0 = view.findViewById<FrameLayout>(R.id.f0);
        f0.setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f1).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f2).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f3).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f4).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f5).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f6).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f7).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f8).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.f9).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.fa).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.fb).setOnClickListener(this);
        view.findViewById<FrameLayout>(R.id.hide).setOnClickListener(this);
        panterl = view.findViewById<LinearLayout>(R.id.callPanlter)
        showKeyboardView = view.findViewById(R.id.showKeyboard);
        showKeyboardView!!.setOnClickListener {
            showKeyboardView?.visibility = View.GONE
            panterl?.visibility = View.VISIBLE

        }
        numTv = view.findViewById<EditText>(R.id.num)
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(numTv.windowToken, 0)
        numTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    if (!s.toString().equals(clickTag)) {
                        clickTag = null;
                        checkNum(s);
                    }
                } catch (e: java.lang.Exception) {

                }
            }


        });
        view.findViewById<FrameLayout>(R.id.call).setOnClickListener(this)
        val deleteBtn = view.findViewById<FrameLayout>(R.id.delete)
        deleteBtn.setOnClickListener(this)
        f0.setOnLongClickListener {
            setNumText("+")
            true
        }
        deleteBtn.setOnLongClickListener {

            numTv.text = null;
            stringBuilder.clear();

            true
        }

        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(context))
        clientAdapter = ContactAdapter { s ->
            try {
                clickTag = s;
                numTv.text = s;
                showKeyboardView?.visibility = View.GONE;
                panterl?.visibility = View.VISIBLE;
                stringBuilder.clear();
            } catch (e: Exception) {
            }
        }
        mRecyclerView!!.adapter = clientAdapter
        mRecyclerView!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                panterl?.visibility = View.GONE
                showKeyboardView?.visibility = View.VISIBLE
                return false
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasCall =
                activity!!.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
            val hasContact =
                activity!!.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

            if (hasCall && hasContact) {
            } else if (hasCall) {
                permissionList = arrayOf(Manifest.permission.READ_CONTACTS)
            } else if (hasContact) {
                permissionList = arrayOf(Manifest.permission.READ_CALL_LOG)
            } else {
                permissionList =
                    arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS)
            }
        }

        if (permissionList == null) {
            initData()
        }

    }

    private fun initData() {
        Thread {
            try {
                contentCallLog = ContactUtils.getContentCallLog(activity)
                activity?.runOnUiThread {
                    clientAdapter?.setList(contentCallLog);

                }

            } catch (e: java.lang.Exception) {

            }


        }.start()
    }

    private fun getContact() {
        Thread {
            allContacts = ContactUtils.getAllContacts(activity)
        }.start()
    }

    fun setNumText(str: String) {
        stringBuilder.append(str);
        numTv.text = stringBuilder;
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.f0 -> {
                setNumText("0");
            }
            R.id.f1 -> {
                setNumText("1");
            }
            R.id.f2 -> {
                setNumText("2");
            }
            R.id.f3 -> {
                setNumText("3");
            }
            R.id.f4 -> {
                setNumText("4");
            }
            R.id.f5 -> {
                setNumText("5");
            }
            R.id.f6 -> {
                setNumText("6");
            }
            R.id.f7 -> {
                setNumText("7");
            }
            R.id.f8 -> {
                setNumText("8");
            }
            R.id.f9 -> {
                setNumText("9");
            }
            R.id.fa -> {
                setNumText("*");
            }
            R.id.fb -> {
                setNumText("#");
            }
            R.id.call -> {
                if (!TextUtils.isEmpty(numTv.text)) {
                    var phoneNum: String? = null;
                    val phoneNumBean = AppCache.getPhoneNum(activity)
                    if (phoneNumBean != null) {
                        if (!TextUtils.isEmpty(phoneNumBean.currentNum)) {
                            phoneNum = phoneNumBean.currentNum;
                        } else if (!TextUtils.isEmpty(phoneNumBean.phoneNum1)) {
                            phoneNum = phoneNumBean.phoneNum1;
                        } else {
                            phoneNum = phoneNumBean.phoneNum2;
                        }
                    }


                    if (TextUtils.isEmpty(phoneNum)) {
                        val tipDialog = QMUITipDialog.Builder(context)
                            .setTipWord("请在外呼设置页正确填写信息")
                            .create()
                        tipDialog.show()
                        numTv.postDelayed({
                            tipDialog.dismiss()
                            var intent: Intent = Intent(activity, WhSettingActivity::class.java)
                            startActivity(intent);
                        }, 2000);
                        return
                    }
                    PhoneUtil.checkUserInfo(activity, numTv.text.toString(), object : IBlackResult {
                        override fun canCall(can: Boolean) {
                            if (can) {

                                val userBean: UserBean = AppCache.data["user"] as UserBean
                                if (userBean.transferAllowed) {
                                    if (phoneNumBean!!.isHz) {
                                        val yys = phoneNumBean.yys
                                        var yssNum: String = "";
                                        if (yys == 2) {
                                            yssNum = "**21*${numTv.text.toString()}*11%23";
                                        } else if (yys == 3) {
                                            yssNum = "*72${numTv.text.toString()}";
                                        } else {
                                            yssNum = "**21*${numTv.text.toString()}%23";
                                        }
                                        PhoneUtil.lauchCall(
                                            activity,
                                            yssNum
                                        )
                                        numTv.postDelayed(Runnable {
                                            PhoneUtil.lauchCall(activity, phoneNum)
                                        }, phoneNumBean.waitTime * 1000L)
                                    } else {
                                        PhoneUtil.lauchCall(activity, numTv.text.toString())
                                    }

                                } else {
                                    PhoneUtil.lauchCall(activity, numTv.text.toString())
                                }

                            } else {
                                showError("此号码存在高风险，不允许呼叫")
                            }
                        }
                    })

                }
            }
            R.id.delete -> {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length - 1);
                }
                numTv.text = stringBuilder;

            }
            R.id.hide -> {
                panterl?.visibility = View.GONE;
                showKeyboardView?.visibility = View.VISIBLE

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {}

    fun showError(tip: String) {
        getActivity()!!.runOnUiThread {
            var tipDialog = QMUITipDialog.Builder(getActivity())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(tip)
                .create()
            tipDialog!!.show()
            numTv?.postDelayed(Runnable { tipDialog!!.dismiss() }, 2500)
        }

    }


    private var hasShow = false

    override fun onResume() {
        super.onResume()
        if (permissionList != null && !hasShow) {
            hasShow = true
            MessageDialogBuilder(activity)
                .setTitle("权限请求")
                .setMessage("【通讯录权限】用于本地展示，请授权。")
                .setSkinManager(QMUISkinManager.defaultInstance(activity))
                .addAction(
                    "取消"
                ) { dialog, index -> dialog.dismiss() }
                .addAction(
                    0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE
                ) { dialog, index ->
                    dialog.dismiss()
                    requestPermissions(permissionList!!, 233)
                }
                .create().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions == null || permissions.size == 0) {
            return
        }
        for (i in permissions.indices) {
            if (Manifest.permission.READ_CALL_LOG == permissions[i]) {
                if (i >= grantResults.size) {
                    return
                }
                var hasAllGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED
                if (hasAllGranted) {
                    initData()
                }
            } else if (Manifest.permission.READ_CONTACTS == permissions[i]) {
                var hasAllGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED
                if (hasAllGranted) {
                    getContact()
                }
            }
        }

    }

    val fifterCallLog: ArrayList<CallLogBean> = ArrayList<CallLogBean>();
    private fun checkNum(s: Editable?) {
        if (contentCallLog != null && contentCallLog!!.size > 0) {
            (mRecyclerView?.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset(
                0,
                0
            )
        }
        if (s == null || TextUtils.isEmpty(s.toString())) {
            clientAdapter?.setList(contentCallLog)
        } else {
            fifterCallLog.clear();
            Thread {
                try {
                    val num: String = s.toString()
                    contentCallLog?.forEach {
                        if (it.number.contains(num)) {
                            fifterCallLog.add(it)
                        }
                    }
                    allContacts?.forEach {
                        if (it.number.contains(num)) {
                            fifterCallLog.add(it)
                        }
                    }

                    activity?.runOnUiThread {
                        clientAdapter?.setList(fifterCallLog)
                    }

                } catch (e: java.lang.Exception) {

                }

            }.start()
        }
    }
}