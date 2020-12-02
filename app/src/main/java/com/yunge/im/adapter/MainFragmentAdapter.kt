package com.yunge.im.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yunge.im.fragment.*

class MainFragmentAdapter: FragmentPagerAdapter {
    constructor(fm: FragmentManager) : super(fm) {}
    constructor(fm: FragmentManager, behavior: Int) : super(fm, behavior) {}

   companion object{
       var indexHomeFragment = IndexHomeFragment();
       var callPhoneFragment= CallPhoneFragment();

       var clientFragment= ClientFragment();
       var contactFragment= ContactFragment();
       var settingFragment= SettingFragment();
       var arr = arrayOf(indexHomeFragment, clientFragment, callPhoneFragment, settingFragment)
   }

    override fun getCount(): Int = arr.size;

    override fun getItem(position: Int): Fragment = arr[position];

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

}