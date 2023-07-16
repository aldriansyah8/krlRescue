package com.example.krlrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnInfo, btnHome, btnProfil;
    private ImageView alarm;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Integer location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        btnInfo = findViewById(R.id.btnInfo);
        btnHome = findViewById(R.id.btnHome);
        btnProfil = findViewById(R.id.btnProfile);
        alarm = findViewById(R.id.alarm);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, InformationActivity.class);
                startActivity(i);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        warningSystem();
    }

    private void warningSystem(){
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location = snapshot.child("mcu-device").child("data").child("locationNumber").getValue(Integer.class);

                if (location == 1){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_a));
                } else if (location == 2){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_b));
                } else if (location == 3){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_c));
                } else if (location == 4){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_d));
                } else {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_default));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}