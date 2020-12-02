package com.yunge.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunge.im.R;
import com.yunge.im.interfaces.IItemCliclListener;
import com.yunge.im.mode.CallLogBean;

import java.util.ArrayList;
import java.util.List;

public class CallInfoAdapter extends RecyclerView.Adapter<CallInfoAdapter.ContactViewHolder> {

    private List<CallLogBean> list = new ArrayList<>();
    private IItemCliclListener iItemCliclListener;

    public CallInfoAdapter(IItemCliclListener iItemCliclListener) {
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
                if (iItemCliclListener != null){
                    iItemCliclListener.itemClick(myContacts.number);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<CallLogBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView pnum;
        private View view;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            pnum = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.cname);
            this.view = itemView;
        }
    }


}
