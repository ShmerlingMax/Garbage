package com.example.garbagekings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagekings.Modules.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateOrderActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        Order order = new Order(address, garbageType, comment);
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Order", "Order has been saved");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Order", "Order was not saved");
                    }
                });
    }

    public void cancel(View view) {
        //startActivity(new Intent(com.example.garbagekings.CreateOrderActivity.this, com.example.garbagekings.SettingsActivity.class));
        //onBackPressed();
    }
}
