package com.example.krlrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {

    private EditText txtEmail;
    private TextInputEditText txtPassword;
    private Button btnSignin;
    private TextView toSignup;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    protected static String newUser;
    protected static Boolean flagRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //untuk keluar ke home android
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmailSignin);
        txtPassword = findViewById(R.id.txtPasswordSignin);
        btnSignin = findViewById(R.id.btnSignsin);
        toSignup = findViewById(R.id.createAccount);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //menjalan fungsi login
                loginUserAccount();
            }
        });

        //tombol daftar untuk berpindah ke halaman daftar
        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUserAccount() {
        String email, password;
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        int mailIndex = email.indexOf("@");

        if (mailIndex != 1) {
            newUser = email.substring(0, mailIndex);
        }

        //verifikasi email dan password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter your name!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // autentikasi login dengan email dan passwords
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Login successful!!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar

                            // if sign-in is successful
                            // intent to home activity

                            flagRoute = true;
                            Intent intent
                                    = new Intent(SigninActivity.this,
                                    RouteActivity.class);
                            startActivity(intent);

                        } else {

                            // sign-in failed
                            Toast.makeText(getApplicationContext(),
                                            "Login failed!!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                        }
                    }
                });
    }

    //keluar dari aplikasi saat menekan tombol back pada android
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda ingin keluar dari aplikasi?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}