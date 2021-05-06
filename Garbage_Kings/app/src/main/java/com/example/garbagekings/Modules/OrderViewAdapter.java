package com.example.garbagekings.Modules;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.garbagekings.R;

import java.util.List;

public class OrderViewAdapter extends BaseAdapter {
    Activity activity;
    List<Order> orderList;
    LayoutInflater inflater;

    public OrderViewAdapter(Activity activity, List<Order> orderList) {
        this.activity = activity;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) activity
                .getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.orderview_item, null);

        TextView txtAddress = itemView.findViewById(R.id.txt_address);
        TextView txtGarbageType = itemView.findViewById(R.id.txt_garbage_type);
        TextView txtComment = itemView.findViewById(R.id.txt_comment);

        txtAddress.setText(orderList.get(i).getAddress());
        txtGarbageType.setText(orderList.get(i).getTypeOfGarbage());
        txtComment.setText(orderList.get(i).getComment());

        return itemView;
    }
}
