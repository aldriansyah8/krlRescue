package com.example.krlrescue;

import static com.example.krlrescue.SigninActivity.newUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    private ImageView btnInfo, btnHome, btnProfil;
    private ImageView alarm;
    private TextView txtRoute, txtSerial, txtLocation;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    private String alarmPosition;
    private Integer location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mAuth = FirebaseAuth.getInstance();

        btnInfo = findViewById(R.id.btnInfo);
        btnHome = findViewById(R.id.btnHome);
        btnProfil = findViewById(R.id.btnProfile);
        alarm = findViewById(R.id.alarm);
        txtRoute = findViewById(R.id.txtKrlRoute);
        txtSerial = findViewById(R.id.txtKrlSerial);
        txtLocation = findViewById(R.id.txtLocation);

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
        readData();
    }

    private void warningSystem(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("mcu-device").child("data");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location = snapshot.child("locationNumber").getValue(Integer.class);

                if (location == 1){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_a));
                    alarmPosition = "pintu A.";
                } else if (location == 2){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_b));
                    alarmPosition = "pintu B";
                } else if (location == 3){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_c));
                    alarmPosition = "pintu C";
                } else if (location == 4){
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_d));
                    alarmPosition = "pintu D";
                } else {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_default));
                    alarmPosition = "tidak ada alarm";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

                txtLocation.setText("Gerbong serial number " + serial + ", " + alarmPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}