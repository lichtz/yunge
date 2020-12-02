package com.yunge.im.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yunge.im.R;
import com.yunge.im.adapter.ContactAdapter;
import com.yunge.im.interfaces.IItemCliclListener;
import com.yunge.im.mode.CallLogBean;
import com.yunge.im.util.ContactUtils;
import com.yunge.im.util.NoticeService;

import java.util.ArrayList;


public class ContactFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ContactAdapter clientAdapter;
    private boolean hasP = true;
    private boolean hasShow = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_contact_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clientAdapter = new ContactAdapter(new IItemCliclListener() {
            @Override
            public void itemClick(String s) {
                try {
                    NoticeService.iCallNumListenerWeakReference.get().call(s);
                } catch (Exception e) {

                }
            }
        });
        mRecyclerView.setAdapter(clientAdapter);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasP = getActivity().checkSelfPermission("android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED;
        }

        if (hasP) {
            initData();
        }

    }

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


    @Override
    public void onResume() {
        super.onResume();
        if (!hasP && !hasShow) {
            hasShow = true;
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("权限请求")
                    .setMessage("【通讯录权限】用于本地展示，请授权。")
                    .setSkinManager(QMUISkinManager.defaultInstance(getActivity()))
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 233);
                        }
                    })
                    .create(mCurrentDialogStyle).show();

        }
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    ArrayList<CallLogBean> allContacts = ContactUtils.getAllContacts(getContext());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clientAdapter.setList(allContacts);
                        }
                    });
                } catch (Exception e) {

                }


            }
        }.start();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        //判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        if (permissions == null || permissions.length == 0) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            if ("android.permission.READ_CONTACTS".equals(permissions[i])){
                    if (i>= grantResults.length){
                        return;
                    }
                    hasAllGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (hasAllGranted) {
            initData();
        } else {
        }
    }
}
