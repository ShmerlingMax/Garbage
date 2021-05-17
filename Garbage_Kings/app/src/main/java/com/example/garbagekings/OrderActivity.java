package com.example.garbagekings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.garbagekings.Modules.Order;
import com.example.garbagekings.Modules.OrderViewAdapter;

import java.util.LinkedList;
import java.util.List;

import com.example.garbagekings.Modules.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OrderActivity extends Fragment {

    private List<Order> orders = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_order, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String id = FirebaseAuth.getInstance().getUid();
        if(id != null) {
            db.collection("users").document(id).collection("orders")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if (documentSnapshots.isEmpty()) {
                                return;
                            } else {
                                List<Order> orders = documentSnapshots.toObjects(Order.class);

                                orders = new LinkedList<>(orders);
                                OrderViewAdapter orderAdapter = new OrderViewAdapter(getActivity(), orders);
                                ListView listView = (ListView) v.findViewById(R.id.listview_order);
                                listView.setAdapter(orderAdapter);
                            }
                        }
                    });
        }
        return v;
    }
}