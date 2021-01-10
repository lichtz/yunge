package com.yunge.im

import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSON
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.yunge.im.adapter.MainFragmentAdapter
import com.yunge.im.content.Config
import com.yunge.im.dialog.CallCancelDialog
import com.yunge.im.interfaces.ICallEndLister
import com.yunge.im.mode.PhoneNumBean
import com.yunge.im.util.*
import java.lang.ref.WeakReference
import java.util.*


class SettingActivity : AppCompatActivity(), BottomNavigationBar.OnTabSelectedListener,
    ICallEndLister {
    private val TAG = "SettingActivityXX"
    private var qmuiViewPager: ViewPager? = null;
    private var phoneReceiver: PhoneReceiver? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val nightMode = PhoneUtils.isNightMode(this)
//        if (nightMode) {
//            QMUIStatusBarHelper.setStatusBarDarkMode(this)
//        }
//        else {
//        QMUIStatusBarHelper.setStatusBarLightMode(this)

//        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        AppCache.getPhoneNum(this);
        setContentView(R.layout.activity_setting)
        initBottomNav();
        qmuiViewPager = findViewById<ViewPager>(R.id.viewpager)
        qmuiViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (bottomNavigationBar != null) {
                    bottomNavigationBar.selectTab(position, false)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        NoticeService.viewPagerWeakReference = WeakReference(qmuiViewPager);
        qmuiViewPager!!.adapter = MainFragmentAdapter(supportFragmentManager);
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
        phoneReceiver = PhoneReceiver(this, this);
        registerReceiver(phoneReceiver, intentFilter)
        qmuiViewPager!!.offscreenPageLimit = 4;

    }

    fun getActivity(): SettingActivity {
        return this;
    }

    lateinit var bottomNavigationBar: BottomNavigationBar;
    private fun initBottomNav() {
        bottomNavigationBar = findViewById<BottomNavigationBar>(R.id.bottom_navigation_bar)
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor(R.color.white);


        bottomNavigationBar
            .addItem(
                BottomNavigationItem(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_home_wifi_fill),
                    "首页"
                ).setActiveColorResource(R.color.tab)
                    .setInactiveIconResource(R.drawable.ic_home_wifi_line)
                    .setInActiveColorResource(R.color.tab)
            ).addItem(
                BottomNavigationItem(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_global_fill),
                    "CRM"
                ).setActiveColorResource(R.color.tab1)
                    .setInactiveIconResource(R.drawable.ic_global_line)
                    .setInActiveColorResource(R.color.tab)
            ).addItem(
                BottomNavigationItem(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_phone_fill),
                    "快拨"
                ).setActiveColorResource(R.color.tab2)
                    .setInactiveIconResource(R.drawable.ic_phone_line)
                    .setInActiveColorResource(R.color.tab)
            ).addItem(
                BottomNavigationItem(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_user_settings_fill),
                    "个人中心"
                ).setActiveColorResource(R.color.tab3)
                    .setInactiveIconResource(R.drawable.ic_user_settings_line)
                    .setInActiveColorResource(R.color.tab)
            )

            .setFirstSelectedPosition(0)
            .initialise()
        bottomNavigationBar.setTabSelectedListener(this)
    }


    override fun onTabSelected(position: Int) {
        qmuiViewPager?.setCurrentItem(position, true)
    }

    override fun onTabUnselected(position: Int) {
    }

    override fun onTabReselected(position: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (phoneReceiver != null) {
            unregisterReceiver(phoneReceiver)
        }
        val phoneNum = AppCache.getPhoneNum(this)
        var simIndex = 0;
        if (phoneNum != null) {
            simIndex = phoneNum.getSimIndex()

        }
        if (isExit) {
            PhoneUtil.lauchCall(this, "%23%23002%23", simIndex)
        }
    }

    override fun end() {
        Log.d(TAG, "end: ")
        Config.isClearing = true;
        val phoneNumBean = AppCache.getPhoneNum(this)
        qmuiViewPager?.postDelayed(object : Runnable {
            override fun run() {
                Config.isCalling = false;
                Config.isClearing = false;
            }

        }, phoneNumBean!!.waitTime * 2000L)

        val phoneNum = AppCache.getPhoneNum(this)
        val hz = phoneNum!!.isHz
        if (hz) {
            qmuiViewPager?.postDelayed(object : Runnable {
                override fun run() {
                    PhoneUtil.lauchCall(getActivity(), "%23%23002%23", phoneNum.getSimIndex())
                }
            }, 2000)
        }
    }

    override fun showCallInfoAlert(isAuto: Boolean) {
        if (!isAuto) {
            val callCancelDialog = CallCancelDialog(this)
            callCancelDialog.setClickListener(cancelDialogBtn);
            callCancelDialog.show();
        }
    }

    val cancelDialogBtn: View.OnClickListener = View.OnClickListener {
        end()
    }


    override fun onBackPressed() {
        exitBy2Click()
    }

    var isExit: Boolean = false;
    private fun exitBy2Click() {

        var tExit: Timer? = null
        if (!isExit) {

            isExit = true // 准备退出
            Toast.makeText(this, "请再按一次退出程序", Toast.LENGTH_LONG).show();
            tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false // 取消退出
                }
            }, 2000) // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            val callCancelDialog = CallCancelDialog(this)
            callCancelDialog.setCancelable(false)
            callCancelDialog.show("正在清除呼叫转移，3s后退出请稍等。")
            val phoneNum = AppCache.getPhoneNum(this)
            val hz = phoneNum!!.isHz
            if (hz) {
                qmuiViewPager?.postDelayed(object : Runnable {
                    override fun run() {
                        PhoneUtil.lauchCall(getActivity(), "%23%23002%23", phoneNum.getSimIndex())
                    }
                }, 0)

            }

            qmuiViewPager?.postDelayed(object :Runnable{
                override fun run() {
                    callCancelDialog.dismiss()
                    finish()
                }
            },4000)



//            System.exit(0);
        }
    }



}