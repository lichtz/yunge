package com.yunge.im.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;

import com.yunge.im.mode.CallLogBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class ContactUtils {
    public static ArrayList<CallLogBean> getAllContacts(Context context) {
        ArrayList<CallLogBean> contacts = new ArrayList<CallLogBean>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            CallLogBean temp = new CallLogBean();
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            temp.name = name;

            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                temp.number = phone;
            }

            //获取联系人备注信息
            Cursor noteCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data._ID, ContactsContract.CommonDataKinds.Nickname.NAME},
                    ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE + "'",
                    new String[]{contactId}, null);
            if (noteCursor.moveToFirst()) {
                do {
                    String note = noteCursor.getString(noteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    temp.note = note;
                    Log.i("note:", note);
                } while (noteCursor.moveToNext());
            }
            contacts.add(temp);
            //记得要把cursor给close掉
            phoneCursor.close();
            noteCursor.close();
        }
        cursor.close();
        return contacts;
    }

    //获取通话记录
    private static String[] columns = new String[]{
            CallLog.Calls.CACHED_NAME // 通话记录的联系人
            , CallLog.Calls.NUMBER // 通话记录的电话号码
            , CallLog.Calls.DATE // 通话记录的日期
            , CallLog.Calls.DURATION // 通话时长
            , CallLog.Calls.TYPE
    };

    public static List<CallLogBean> getContentCallLog(Context context) {
        List<CallLogBean> contacts = new ArrayList<CallLogBean>();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        while (cursor.moveToNext()) {
            try {
                CallLogBean callLogBean = new CallLogBean();
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));  //姓名
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
                long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
//                String time = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
//                String dayCurrent = new SimpleDateFormat("dd").format(new Date());
//                String dayRecord = new SimpleDateFormat("dd").format(new Date(dateLong));
                callLogBean.name = name;
                callLogBean.date = date;
                callLogBean.number = number;
                callLogBean.duration = formatDateTime(duration);
                callLogBean.type = type;
                callLogBean.beanType = 2;
                contacts.add(callLogBean);
            } catch (Exception e) {

            }


        }
        try {
            cursor.close();
        } catch (Exception e) {

        }
        return contacts;
    }
    public static String formatDateTime(long mss) {
        String DateTimes = null;
        long days = mss / ( 60 * 60 * 24);
        long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % ( 60 * 60)) /60;
        long seconds = mss % 60;
        if(days>0){
            DateTimes= days + " 天 " + hours + " 小时 " + minutes + " 分钟 "
                    + seconds + " 秒";
        }else if(hours>0){
            DateTimes=hours + " 小时 " + minutes + " 分钟 "
                    + seconds + " 秒";
        }else if(minutes>0){
            DateTimes=minutes + " 分钟 "
                    + seconds + " 秒";
        }else{
            DateTimes=seconds + " 秒";
        }

        return DateTimes;
    }



}
