package com.example.garbagekings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateOrderActivity extends AppCompatActivity {

    Button btnConfirmOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText addressText = findViewById(R.id.address);
                EditText garbageTypeText = findViewById(R.id.garbageType);
                EditText commentText = findViewById(R.id.comment);

                String address = addressText.getText().toString();
                String garbageType = garbageTypeText.getText().toString();
                String comment = commentText.getText().toString();
            }
        });
    }
}