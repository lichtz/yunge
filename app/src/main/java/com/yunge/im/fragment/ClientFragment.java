package com.yunge.im.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.recyclerView.QMUIRVItemSwipeAction;
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction;
import com.qmuiteam.qmui.recyclerView.QMUISwipeViewHolder;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.pullLayout.QMUIPullLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yunge.im.R;
import com.yunge.im.adapter.ClientAdapter;
import com.yunge.im.interfaces.IItemCliclListener;
import com.yunge.im.mode.ContactBean;
import com.yunge.im.mode.UserBean;
import com.yunge.im.util.AppCache;
import com.yunge.im.util.NoticeService;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ClientFragment extends Fragment {

    private RefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private ClientAdapter clientAdapter;
    private List<ContactBean> allContactBeans = new ArrayList<>();
    private QMUIProgressBar qmuiProgressBar2;
    private QMUIProgressBar qmuiProgressBar;
    private TextView yxNumTv;
    private TextView yxPNum;
    private TextView callPTv;
    private TextView customerNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        qmuiProgressBar2 = view.findViewById(R.id.qprogressBar2);
        qmuiProgressBar = view.findViewById(R.id.qprogressBar);
        yxNumTv = view.findViewById(R.id.yxNum);
        yxPNum = view.findViewById(R.id.yxPTv);
        callPTv = view.findViewById(R.id.callPTv);
        customerNum = view.findViewById(R.id.customerNum);
        UserBean user = (UserBean) AppCache.Companion.getData().get("user");
        if (user != null) {
            userId = user.id;
        }

        View tabAll = view.findViewById(R.id.tabAll);
        View tabUnCall = view.findViewById(R.id.tabUnCall);
        View tabCalled = view.findViewById(R.id.tabCalled);
        View tabCalling = view.findViewById(R.id.tabCalling);

        tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabUnCall.setEnabled(true);
                tabCalled.setEnabled(true);
                tabCalling.setEnabled(true);
                tabAll.setEnabled(false);
                fifter(-1);

            }
        });
        tabUnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabCalled.setEnabled(true);
                tabCalling.setEnabled(true);
                tabAll.setEnabled(true);
                tabUnCall.setEnabled(false);
                fifter(0);

            }
        });
        tabCalled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabCalling.setEnabled(true);
                tabAll.setEnabled(true);
                tabUnCall.setEnabled(true);
                tabCalled.setEnabled(false);
                fifter(1);
            }
        });
        tabCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAll.setEnabled(true);
                tabUnCall.setEnabled(true);
                tabCalled.setEnabled(true);
                tabCalling.setEnabled(false);
                fifter(2);

            }
        });
        initData();

    }


    private void initData() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onRefreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getLoadMoreData();

            }
        });


        QMUIRVItemSwipeAction swipeAction = new QMUIRVItemSwipeAction(true, new QMUIRVItemSwipeAction.Callback() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    clientAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                } catch (Exception e) {

                }
            }

            @Override
            public int getSwipeDirection(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return QMUIRVItemSwipeAction.SWIPE_LEFT;
            }

            @Override
            public void onClickAction(QMUIRVItemSwipeAction swipeAction, RecyclerView.ViewHolder selected, QMUISwipeAction action) {
                super.onClickAction(swipeAction, selected, action);
                try {
                    switch (action.getText()) {
                        case "已接通":
                            clientAdapter.getList().get(selected.getAdapterPosition()).type = 1;
                            clientAdapter.notifyItemChanged(selected.getAdapterPosition());
                            coumCall(clientAdapter.getList().get(selected.getAdapterPosition()));
                            break;
                        case "未接通":
                            clientAdapter.getList().get(selected.getAdapterPosition()).type = 2;
                            clientAdapter.notifyItemChanged(selected.getAdapterPosition());
                            coumCall(clientAdapter.getList().get(selected.getAdapterPosition()));
                            break;
                        case "拉黑":
                            clientAdapter.getList().get(selected.getAdapterPosition()).type = 3;
                            clientAdapter.notifyItemChanged(selected.getAdapterPosition());
                            break;
                        case "有意向":
                            clientAdapter.getList().get(selected.getAdapterPosition()).isCanSuccess = true;
                            coumCanSuccess(clientAdapter.getList().get(selected.getAdapterPosition()));

                            break;
                    }
                } catch (Exception e) {

                }
                swipeAction.clear();


            }
        });
        swipeAction.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clientAdapter = new ClientAdapter(getActivity(), new IItemCliclListener() {
            @Override
            public void itemClick(String s) {
                try {
                    NoticeService.iCallNumListenerWeakReference.get().call(s);
                    NoticeService.viewPagerWeakReference.get().setCurrentItem(2, true);
                } catch (Exception e) {

                }
            }
        });

        mRecyclerView.setAdapter(clientAdapter);
        getData();
    }

    private void coumCall(ContactBean rec) {
        if (allContactBeans != null) {
            int count = 0;

            for (ContactBean allContactBean : allContactBeans) {
                if (allContactBean.type == 1 || allContactBean.type == 2) {
                    count++;
                }else if (allContactBean.number.equals(rec.number)) {
                    allContactBean.type = rec.type;
                    count++;
                }

            }
            qmuiProgressBar.setMaxValue(allContactBeans.size());
            qmuiProgressBar.setProgress(count);
            float f = count / (allContactBeans.size() * 1.0f);
            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            percentInstance.setMaximumFractionDigits(2); // 保留小数两位
            String format = percentInstance.format(f); // 结果是81.25% ，最后一们四舍五入了
            callPTv.setText(format);
        }
    }

    private void coumCanSuccess(ContactBean rec) {
        if (allContactBeans != null) {
            int count = 0;
            for (ContactBean allContactBean : allContactBeans) {
                if (  allContactBean.isCanSuccess){
                    count++;
                }else if (allContactBean.number.equals(rec.number)) {
                    allContactBean.isCanSuccess = rec.isCanSuccess;
                    count++;
                }
            }
            qmuiProgressBar2.setMaxValue(allContactBeans.size());
            qmuiProgressBar2.setProgress(count);
            yxNumTv.setText(count+"");
            float f = count / (allContactBeans.size() * 1.0f);
            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            percentInstance.setMaximumFractionDigits(2); // 保留小数两位
            String format = percentInstance.format(f); // 结果是81.25% ，最后一们四舍五入了
            yxPNum.setText(format);
        }
    }

    private void getLoadMoreData() {
        page++;
        getData();

    }

    private void onRefreshData() {
        page = 0;
        if (clientAdapter != null) {
            clientAdapter.clear();
        }
        allContactBeans.clear();
        getData();

    }

    private int page = 0;
    private String userId = "1";

    private void getData() {
        String url = "http://8.135.13.129:9000//caller/api/accounts?userId=" + userId + "&page=" + page + "&perPage=30";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    List<ContactBean> contactBeans = JSON.parseArray(response.body().string(), ContactBean.class);
                    allContactBeans.addAll(contactBeans);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (contactBeans != null) {
                                if (contactBeans.size() > 0) {
                                    clientAdapter.setList(contactBeans);
                                }
                                if (contactBeans.size() < 30) {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadMore();
                                }
                            }
                            customerNum.setText(allContactBeans.size() + "");
                            qmuiProgressBar.setMaxValue(allContactBeans.size());
                        }
                    });


                } catch (Exception e) {

                }
            }
        });


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    private List<ContactBean> fifter = new ArrayList<>();

    private void fifter(int type) {
        if (clientAdapter == null) {
            return;
        }
        fifter.clear();
        if (type == -1) {
            clientAdapter.clear();
            clientAdapter.setList(allContactBeans);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (clientAdapter != null) {
                    if (allContactBeans != null && allContactBeans.size() > 0) {
                        for (ContactBean contactBean : allContactBeans) {
                            if (type == 0) {
                                if (0 == contactBean.type || 3 == contactBean.type) {
                                    fifter.add(contactBean);
                                }
                            } else {
                                if (type == contactBean.type) {
                                    fifter.add(contactBean);
                                }
                            }
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clientAdapter.clear();
                            clientAdapter.setList(fifter);
                        }
                    });
                }
            }
        }.start();


    }
}
