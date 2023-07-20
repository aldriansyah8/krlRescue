package com.example.krlrescue;

import static com.example.krlrescue.SigninActivity.newUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
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
    private Integer n = 1;
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
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, ProfileActivity.class);
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        warningSystem();
        readData();
    }

    private void warningSystem() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("mcu-device").child("data");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location = snapshot.child("locationNumber").getValue(Integer.class);

                if (location == 1) {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_a));
                    alarmPosition = "pintu A.";
                    pushNotification(n, "WARNING!, Petugas harap segera menuju lokasi", "Emergency button pintu A ditekan");
                } else if (location == 2) {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_b));
                    alarmPosition = "pintu B";
                    pushNotification(n, "WARNING!, Petugas harap segera menuju lokasi", "Emergency button pintu B ditekan");
                } else if (location == 3) {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_c));
                    alarmPosition = "pintu C";
                    pushNotification(n, "WARNING!, Petugas harap segera menuju lokasi", "Emergency button pintu C ditekan");
                } else if (location == 4) {
                    alarm.setBackground(ContextCompat.getDrawable(NotificationActivity.this, R.drawable.img_d));
                    alarmPosition = "pintu D";
                    pushNotification(n, "WARNING!, Petugas harap segera menuju lokasi", "Emergency button pintu D ditekan");
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

    private void readData() {
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

                txtLocation.setText("Serial number " + serial + ", " + alarmPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //fungsi untuk menampilkan notifikasi
    private void pushNotification(Integer n, String ContentTitle, String ContentText) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(n.toString(), "Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final String CHANNEL_ID = n.toString();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_logo)
                .setContentTitle(ContentTitle)
                .setContentText(ContentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(n, builder.build());

        this.n++;
    }

}