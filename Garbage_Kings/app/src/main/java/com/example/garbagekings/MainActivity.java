package com.example.garbagekings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbagekings.Modules.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegistration;
    FirebaseAuth auth;
    FirebaseDatabase db;
    FirebaseFirestore db_fs = FirebaseFirestore.getInstance();
    DatabaseReference users;

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //НАДО УДАЛИТЬ ПРИ ПРОДАКШЕНЕ!!!
        //startActivity(new Intent(com.example.garbagekings.MainActivity.this, com.example.garbagekings.MenuActivity.class));

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegistration = findViewById(R.id.btnRegistration);

        root = findViewById(R.id.rootElement);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationWindow();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInWindow();
            }
        });
    }

    private void showSignInWindow()
    {
        /*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Вход");
        dialog.setMessage("Введите данные для входа");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_window, null);
        dialog.setView(signInWindow);*/

        final MaterialEditText email = findViewById(R.id.emailField);
        final MaterialEditText password = findViewById(R.id.passwordField);

        /*dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });*/

        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(root, "Введите адрес электронной почты", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (password.getText().toString().length() < 8) {
            Snackbar.make(root, "Введите пароль, который больше 8 символов", Snackbar.LENGTH_SHORT).show();
            return;
        }
        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(com.example.garbagekings.MainActivity.this, com.example.garbagekings.MenuActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Ошибка входа", Snackbar.LENGTH_SHORT).show();
                    }
                });
        /*dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Введите адрес электронной почты", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 8) {
                    Snackbar.make(root, "Введите пароль, который больше 8 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(com.example.garbagekings.MainActivity.this, com.example.garbagekings.MenuActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root, "Ошибка входа", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        dialog.show();*/

    }

    private void showRegistrationWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Регистрация");
        dialog.setMessage("Введите данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registrationWindow = inflater.inflate(R.layout.registration_window, null);
        dialog.setView(registrationWindow);

        final MaterialEditText email = registrationWindow.findViewById(R.id.emailField);
        final MaterialEditText password = registrationWindow.findViewById(R.id.passwordField);
        final MaterialEditText name = registrationWindow.findViewById(R.id.nameField);
        final MaterialEditText phone = registrationWindow.findViewById(R.id.phoneField);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Готово", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(email.getText().toString()))
                {
                    Snackbar.make(root, "Введите адрес электронной почты", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    Snackbar.make(root, "Введите имя", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString()))
                {
                    Snackbar.make(root, "Введите номер телефона", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 8)
                {
                    Snackbar.make(root, "Введите пароль, который больше 8 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setName(name.getText().toString());
                                user.setEmail(email.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setPhone(phone.getText().toString());
                                user.setBonus(100);

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar mSnackbar = Snackbar.make(root, "Вы успешно зарегистрировались", Snackbar.LENGTH_SHORT);
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
                                            }
                                        });
                                
                                String id = auth.getUid();
                                db_fs.collection("users").document(id)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("User", "User has been saved");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("User", "User was not saved");
                                            }
                                        });
                            }
                        });
            }
        });

        dialog.show();
    }
}