package com.example.garbagekings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.garbagekings.Modules.User;
import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends Fragment {

    FirebaseFirestore db_fs = FirebaseFirestore.getInstance();
    TextView mUserName;
    TextView mEmail;
    TextView mPhoneNumber;
    Button btnSettings;
    Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);

        btnSettings = (Button) v.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        logoutButton = (Button) v.findViewById((R.id.logoutButton));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mUserName = v.findViewById(R.id.tvUserName);
        mEmail = v.findViewById(R.id.tvEmail);
        mPhoneNumber = v.findViewById(R.id.tvPhoneNumber);

        String id = FirebaseAuth.getInstance().getUid();
        if (id == null) {
            mUserName.setText("Неавторизованный пользователь");
            mEmail.setText("Неавторизованный пользователь");
            mPhoneNumber.setText("Неавторизованный пользователь");
        }
        else {
            db_fs.collection("users").document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                User currentUser = document.toObject(User.class);

                                mUserName.setText(currentUser.getName());
                                mEmail.setText(currentUser.getEmail());
                                mPhoneNumber.setText(currentUser.getPhone());
                            }
                        }
                    });
        }

        return v;
    }

}