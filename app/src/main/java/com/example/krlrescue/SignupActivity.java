package com.example.krlrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import static com.example.krlrescue.SigninActivity.newUser;

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

public class SignupActivity extends AppCompatActivity {

    protected static String email, password, username;
    protected static String phone;
    private EditText txtUsername, txtEmail, txtPhone;
    private Button btnSignup;
    private TextView toSignin;
    private TextInputEditText txtPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmailSignup);
        txtPhone = findViewById(R.id.txtPhoneSignup);
        txtPassword = findViewById(R.id.txtPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        toSignin = findViewById(R.id.toSignin);

        toSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //menjalankan function registrasi
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        username = txtUsername.getText().toString();
        email = txtEmail.getText().toString();
        phone = txtPhone.getText().toString();
        password = txtPassword.getText().toString();
        int mailIndex = email.indexOf("@");

        if (mailIndex != 1) {
            newUser = email.substring(0, mailIndex);
        }

        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("Users").child(newUser).child("username");
        myRef.setValue(username);

        myRef = database.getReference("Users").child(newUser).child("email");
        myRef.setValue(email);

        myRef = database.getReference("Users").child(newUser).child("phone");
        myRef.setValue(phone);

        myRef = database.getReference("Users").child(newUser).child("password");
        myRef.setValue(password);

        //verifikasi email dan password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter youremail!!",
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

        //autentikasi email dan password
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(SignupActivity.this,
                                    SigninActivity.class);
                            startActivity(intent);
                        } else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }
}