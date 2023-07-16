package com.example.krlrescue;

import static com.example.krlrescue.SigninActivity.newUser;
import static com.example.krlrescue.SigninActivity.flagRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InformationActivity extends AppCompatActivity {

    private ImageView btnHome, btnProfil;
    private TextView txtRoute, txtSerial;
    private Button btnEdit;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mAuth = FirebaseAuth.getInstance();

        btnHome = findViewById(R.id.btnHome);
        btnProfil = findViewById(R.id.btnProfile);
        btnEdit = findViewById(R.id.btnEdit);
        txtRoute = findViewById(R.id.txtRoute);
        txtSerial = findViewById(R.id.txtSerialNumber);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InformationActivity.this, RouteActivity.class);
                startActivity(i);

                flagRoute = false;
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InformationActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InformationActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        readData();
    }

    private void readData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(newUser);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String route, serial;
                route = snapshot.child("route").getValue(String.class);
                txtRoute.setText(route);
                serial = snapshot.child("serial-number").getValue(String.class);
                txtSerial.setText(serial);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}