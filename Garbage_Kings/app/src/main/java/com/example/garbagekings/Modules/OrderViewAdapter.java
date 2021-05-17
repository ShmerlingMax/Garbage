package com.example.garbagekings.Modules;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import com.example.garbagekings.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderViewAdapter extends BaseAdapter {
    Activity activity;
    List<Order> orderList;
    LayoutInflater inflater;

    private enum statuses{
        WAITING,
        CANCELED
    }

    private final Map<Integer, String> statusesString = new HashMap<Integer, String>() {{
        put(statuses.WAITING.ordinal(), "Ожидание");
        put(statuses.CANCELED.ordinal(), "Отменен");
    }};

    private final Map<Integer, Integer> statusesColor = new HashMap<Integer, Integer>() {{
        put(statuses.WAITING.ordinal(), Color.GREEN);
        put(statuses.CANCELED.ordinal(), Color.RED);
    }};

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
        TextView txtStatus = itemView.findViewById(R.id.txt_status2);

        String address = "Адресс: " + orderList.get(i).getAddress();
        String garbageType = "Тип Мусора: " + orderList.get(i).getTypeOfGarbage();
        String comment = "Комментарий: " + orderList.get(i).getComment();
        String status = statusesString.get(orderList.get(i).getStatus());
        txtAddress.setText(address);
        txtGarbageType.setText(garbageType);
        txtComment.setText(comment);
        txtStatus.setText(status);
        txtStatus.setTextColor(statusesColor.get(orderList.get(i).getStatus()));

        Button button = (Button)itemView.findViewById(R.id.button_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String id = FirebaseAuth.getInstance().getUid();
                db.collection("users").document(id).collection("orders")
                        .whereEqualTo("address", orderList.get(i).getAddress())
                        .whereEqualTo("typeOfGarbage", orderList.get(i).getTypeOfGarbage())
                        .whereEqualTo("comment", orderList.get(i).getComment())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    for(QueryDocumentSnapshot d : document) {
                                        orderList.get(i).setStatus(statuses.CANCELED.ordinal());
                                        DocumentReference ref = db.collection("users").document(id).collection("orders").document(d.getId());
                                        ref.set(orderList.get(i));
                                        String newStatus = statusesString.get(orderList.get(i).getStatus());
                                        txtStatus.setText(newStatus);
                                        txtStatus.setTextColor(statusesColor.get(orderList.get(i).getStatus()));
                                    }
                                }
                            }
                        });
            }
        });

        return itemView;
    }
}
