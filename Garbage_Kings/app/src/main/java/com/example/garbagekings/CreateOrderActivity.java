package com.example.garbagekings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagekings.Modules.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateOrderActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView addressText;
    CheckBox paperBox;
    CheckBox plasticBox;
    CheckBox glassBox;
    EditText commentText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            String address = args.get("address").toString();
            addressText = findViewById(R.id.address);
            addressText.setText(address);
        }
    }

    public void saveOrder(View view) {
        addressText = findViewById(R.id.address);
        paperBox = findViewById(R.id.checkBoxPaper);
        plasticBox = findViewById(R.id.checkBoxPlastic);
        glassBox = findViewById(R.id.checkBoxGlass);
        commentText = findViewById(R.id.comment);

        String address = addressText.getText().toString();
        String garbageType = "";

        if (paperBox.isChecked()) {
            paperBox.setChecked(true);
            garbageType += paperBox.getText() + " ";
        }

        if (plasticBox.isChecked()) {
            plasticBox.setChecked(true);
            garbageType += plasticBox.getText() + " ";
        }

        if (glassBox.isChecked()) {
            glassBox.setChecked(true);
            garbageType += glassBox.getText() + " ";
        }

        String comment = commentText.getText().toString();

        Order order = new Order(address, garbageType, comment);
        String id = FirebaseAuth.getInstance().getUid();

        if (!paperBox.isChecked() && !glassBox.isChecked() && !plasticBox.isChecked()) {
            Snackbar mSnackbar = Snackbar.make(view, "Выберите тип мусора", Snackbar.LENGTH_SHORT);
            View mView = mSnackbar.getView();
            Snackbar.SnackbarLayout lp = (Snackbar.SnackbarLayout) mView;
            lp.setForegroundGravity(Gravity.CENTER);
            mView.setBackgroundColor(Color.WHITE);
            TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
            mTextView.setTextColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                mTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            mSnackbar.show();
            return;
        }

        if (id != null) {
            db.collection("users").document(id).collection("orders")
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
        onBackPressed();
    }

    public void cancel(View view) {
        onBackPressed();
    }
}
