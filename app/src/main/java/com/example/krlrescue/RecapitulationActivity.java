package com.example.krlrescue;

import static com.example.krlrescue.RouteActivity.gerbong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecapitulationActivity extends AppCompatActivity {

    private ImageView btnInfo, btnHome, btnProfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulation);

        btnInfo = findViewById(R.id.btnInfo);
        btnHome = findViewById(R.id.btnHome);
        btnProfil = findViewById(R.id.btnProfile);
        Button btnDownload = findViewById(R.id.btn_Download);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

// Initialize your List of TableItems from Firebase data
        List<TableItem> tableItemList = new ArrayList<>();

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecapitulationActivity.this, InformationActivity.class);
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecapitulationActivity.this, MainActivity.class);
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecapitulationActivity.this, ProfileActivity.class);
                startActivity(i); overridePendingTransition(0, 0);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String csvData = convertTableItemListToCSV(tableItemList);
                boolean isWriteSuccessful = writeCSVToFile(getApplicationContext(), "history_data.csv", csvData);

                if (isWriteSuccessful) {
                    // Berhasil menulis data ke file di direktori penyimpanan internal aplikasi
                    File file = new File(getFilesDir(), "history_data.csv");
                    Uri csvUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.yourapp.fileprovider", file);

                    // Membagikan file CSV kepada pengguna menggunakan Intent
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_STREAM, csvUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Bagikan file CSV"));
                } else {
                    // Gagal menulis data ke file, tangani kesalahan jika diperlukan
                }

            }
        });


        // Mendapatkan data dari Firebase Realtime Database (Contoh)
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("mcu-device/history");
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int no = 1; // Nomor urut awal

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    TableItem tableItem = itemSnapshot.getValue(TableItem.class);
                    tableItem.setNo(no); // Set nomor urut untuk setiap item
                    tableItemList.add(tableItem);
                    no++; // Tingkatkan nomor urut untuk item berikutnya
                }

                // Sort data berdasarkan unixTime
                Collections.sort(tableItemList, new Comparator<TableItem>() {
                    @Override
                    public int compare(TableItem item1, TableItem item2) {
                        return Long.compare(item1.getUnixTime(), item2.getUnixTime());
                    }
                });

                // Setelah data diurutkan, inisialisasi RecyclerView dan adapter seperti sebelumnya.
                int numberOfColumns = 1;
                TableAdapter tableAdapter = new TableAdapter(tableItemList);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(tableAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }

    public String convertTableItemListToCSV(List<TableItem> tableItemList) {
        StringBuilder csvBuilder = new StringBuilder();

        // Header kolom
        csvBuilder.append("\"No \",\"Date \",\"Location \"\n");

        // Data dari List<TableItem>
        for (TableItem tableItem : tableItemList) {
            String dateTimeString = convertUnixTimeToDateTime(tableItem.getUnixTime());
            gerbong = "Gerbong " + tableItem.getLocationNumber();
            csvBuilder.append("\"").append(tableItem.getNo()).append("\",")
                    .append("\"").append(dateTimeString).append("\",")
                    .append("\"").append(gerbong).append("\"\n");
        }

        return csvBuilder.toString();
    }

    public boolean writeCSVToFile(Context context, String fileName, String csvData) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(csvData);
            bufferedWriter.close();
            return true; // Penulisan berhasil
        } catch (IOException e) {
            e.printStackTrace();
            // Tangani kesalahan jika terjadi saat menyimpan file
            return false; // Penulisan gagal
        }
    }

    public String convertUnixTimeToDateTime(long unixTime) {
        // Waktu dari UnixTime dalam bentuk milidetik (ms)
        Date date = new Date(unixTime * 1000);

        // Format yang ingin Anda gunakan untuk dateTime (misalnya "dd/MM/yyyy HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Mengonversi Date menjadi string dalam format yang diinginkan
        return sdf.format(date);
    }
}