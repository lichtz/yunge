package com.yunge.im.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qmuiteam.qmui.recyclerView.QMUISwipeAction;
import com.qmuiteam.qmui.recyclerView.QMUISwipeViewHolder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yunge.im.R;
import com.yunge.im.interfaces.IItemCliclListener;
import com.yunge.im.mode.ContactBean;

import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<QMUISwipeViewHolder> {

    private List<ContactBean> list = new ArrayList<>();
    private IItemCliclListener iItemCliclListener;

    final QMUISwipeAction mAction1;
    final QMUISwipeAction mAction2;
    final QMUISwipeAction mAction3;
    final QMUISwipeAction mAction4;
    public ClientAdapter(Context context,IItemCliclListener iItemCliclListener) {
        this.iItemCliclListener = iItemCliclListener;
        QMUISwipeAction.ActionBuilder builder = new QMUISwipeAction.ActionBuilder()
                .textSize(QMUIDisplayHelper.sp2px(context, 14))
                .textColor(Color.WHITE)
                .paddingStartEnd(QMUIDisplayHelper.dp2px(context, 8));

        mAction1 = builder
                .text("已接通")
                .backgroundColor(Color.parseColor("#db7c64"))
                .build();
        mAction2 = builder
                .text("未接通")
                .backgroundColor(Color.parseColor("#db7c64"))
                .build();
        mAction3 = builder
                .text("有意向")
                .backgroundColor(Color.parseColor("#db7c64"))
                .build();

        mAction4 = builder
                .text("拉黑")
                .backgroundColor(Color.BLACK)
                .build();


    }

    @NonNull
    @Override
    public QMUISwipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_contact_item, parent, false);
        final QMUISwipeViewHolder vh = new QMUISwipeViewHolder(view);
        vh.addSwipeAction(mAction1);
        vh.addSwipeAction(mAction2);
        vh.addSwipeAction(mAction3);
        vh.addSwipeAction(mAction4);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int adapterPosition = vh.getAdapterPosition();
                    if (iItemCliclListener != null) {
                        ContactBean contactBean = list.get(adapterPosition);
                        iItemCliclListener.itemClick(contactBean.number);
                    }
                }catch (Exception e){

                }

            }
        });
        return    vh;
    }


    @Override
    public void onBindViewHolder(@NonNull QMUISwipeViewHolder holder, int position) {
        if (position >= list.size()) {
            return;
        }
        ContactBean contactBean = list.get(position);
        if (contactBean == null) {
            return;
        }
        if (contactBean.type ==3){
            holder.itemView.setBackgroundColor(Color.parseColor("#80000000"));
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        TextView typeTv = holder.itemView.findViewById(R.id.typeTv);
        switch (contactBean.type){
            case 0:
                typeTv.setText("未拨打");
                typeTv.setVisibility(View.VISIBLE);
                break;
            case 1:
                typeTv.setText("已接通");
                typeTv.setVisibility(View.VISIBLE);
                break;
            case 2:
                typeTv.setText("未接通");
                typeTv.setVisibility(View.VISIBLE);
                break;
            default:
                typeTv.setVisibility(View.GONE);
                break;

        }


        TextView namTv = holder.itemView.findViewById(R.id.num);
        namTv .setText(contactBean.number);
        TextView cnameTv = holder.itemView.findViewById(R.id.cname);
        cnameTv .setText(contactBean.name);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<ContactBean> list) {
        this.list .addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        notifyDataSetChanged();
    }



    public List<ContactBean> getList() {
        return list;
    }
}
