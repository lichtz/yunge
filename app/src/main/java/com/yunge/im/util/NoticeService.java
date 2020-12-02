package com.yunge.im.util;

import androidx.viewpager.widget.ViewPager;

import com.yunge.im.interfaces.ICallNumListener;

import java.lang.ref.WeakReference;

public class NoticeService {
    public static WeakReference<ViewPager> viewPagerWeakReference ;
    public static WeakReference<ICallNumListener> iCallNumListenerWeakReference ;
}
