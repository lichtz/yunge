package com.yunge.im.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunge.im.R;
import com.yunge.im.interfaces.IItemCliclListener;
import com.yunge.im.mode.CallLogBean;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<CallLogBean> list = new ArrayList<>();
    private IItemCliclListener iItemCliclListener;

    public ContactAdapter(IItemCliclListener iItemCliclListener) {
        this.iItemCliclListener = iItemCliclListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.include_local_contact_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (position >= list.size()) {
            return;
        }
        CallLogBean myContacts = list.get(position);
        if (myContacts == null) {
            return;
        }
        holder.name.setText(myContacts.name);
        holder.pnum.setText(myContacts.number);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iItemCliclListener != null) {
                    iItemCliclListener.itemClick(myContacts.number);
                }
            }
        });
        if (myContacts.beanType == 2) {
            holder.type.setVisibility(View.VISIBLE);
            if (myContacts.type == 1) {
                holder.type.setImageResource(R.drawable.ic_login_box_line);
            } else if (myContacts.type == 2) {
                holder.type.setImageResource(R.drawable.ic_logout_box_line);
            } else {
                holder.type.setImageResource(R.drawable.ic_loader_3_line);
            }
        } else {
            holder.type.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(myContacts.date)) {
            holder.time.setVisibility(View.GONE);
        } else {
            holder.time.setText(myContacts.date + "");
            holder.time.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(myContacts.duration)) {
            holder.duration.setVisibility(View.GONE);
        } else {
            holder.duration.setText(myContacts.duration + "");
            holder.duration.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<CallLogBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView pnum;
        private View view;
        private final ImageView type;
        private final TextView time;
        private final TextView duration;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            pnum = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.cname);
            type = itemView.findViewById(R.id.type);
            time = itemView.findViewById(R.id.time);
            duration = itemView.findViewById(R.id.duration);
            this.view = itemView;
        }
    }
}
