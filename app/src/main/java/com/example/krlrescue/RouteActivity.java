package com.example.krlrescue;

import static com.example.krlrescue.SigninActivity.newUser;
import static com.example.krlrescue.SigninActivity.flagRoute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RouteActivity extends AppCompatActivity {

    private Spinner cmbRoute, cmbSerial;
    private Button btnSubmit;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    protected static String gerbong;
    String[] route = new String[] {"Jakarta Kota - Bogor", "Cikarang - Manggarai", "Bekasi - Jakarta Kota"};
    String[] serial = new String[] {"5738A", "5104A", "4060B"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mAuth = FirebaseAuth.getInstance();
        btnSubmit = findViewById(R.id.btnRoute);
        cmbRoute = (Spinner) findViewById(R.id.cmbRoute);

        ArrayAdapter<String> adapterRoute = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, route);
        adapterRoute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbRoute.setAdapter(adapterRoute);

        cmbSerial = (Spinner) findViewById(R.id.cmbSerialNumber);
        ArrayAdapter<String> adapterSerial = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serial);
        adapterSerial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbSerial.setAdapter(adapterSerial);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedRoute = cmbRoute.getSelectedItem().toString();
                String selectedSerial = cmbSerial.getSelectedItem().toString();
                database = FirebaseDatabase.getInstance();

                myRef = database.getReference("Users").child(newUser).child("route");
                myRef.setValue(selectedRoute);

                myRef = database.getReference("Users").child(newUser).child("serial-number");
                myRef.setValue(selectedSerial);
                gerbong = selectedSerial;

                if (flagRoute == true) {
                    Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (flagRoute == false) {
                    Intent intent = new Intent(RouteActivity.this, InformationActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}