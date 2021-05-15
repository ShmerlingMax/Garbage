package com.example.garbagekings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateOrderActivity extends AppCompatActivity {

    Button btnConfirmOrder;
    FirebaseDatabase db;

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("orders/order");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
    }

    public void saveOrder(View view) {
        EditText addressText = findViewById(R.id.address);
        EditText garbageTypeText = findViewById(R.id.garbageType);
        EditText commentText = findViewById(R.id.comment);

        String address = addressText.getText().toString();
        String garbageType = garbageTypeText.getText().toString();
        String comment = commentText.getText().toString();

        Map<String, Object> dateToSave = new HashMap<>();
        dateToSave.put("address", address);
        dateToSave.put("garbage_type", garbageType);
        dateToSave.put("comment", comment);
        mDocRef.set(dateToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Order", "Order has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Order", "Order was not saved");
            }
        });
    }
}