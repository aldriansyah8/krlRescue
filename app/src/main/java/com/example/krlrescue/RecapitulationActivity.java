package com.example.krlrescue;

import static com.example.krlrescue.RouteActivity.gerbong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecapitulationActivity extends AppCompatActivity {

    private ImageView btnInfo, btnHome, btnProfil;
    private int mYear, mMonth, mDay;
    private List<TableItem> originalTableItemList; // Untuk menyimpan data asli
    private List<TableItem> filteredTableItemList; // Untuk menyimpan data yang telah difilter
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulation);

        btnInfo = findViewById(R.id.btnInfo);
        btnHome = findViewById(R.id.btnHome);
        btnProfil = findViewById(R.id.btnProfile);
        Button btnDownload = findViewById(R.id.btn_Download);
        // Inisialisasi originalTableItemList sebagai ArrayList kosong
        originalTableItemList = new ArrayList<>();
        filteredTableItemList = new ArrayList<>();

        // Inisialisasi RecyclerView dan adapter seperti sebelumnya
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tableAdapter = new TableAdapter(originalTableItemList);
        recyclerView.setAdapter(tableAdapter);

        // Panggil filterAndSortData dengan tanggal "0" untuk menampilkan semua data history secara default
        filterAndSortData(0, 0, 0);

        Button btnOpenDatePicker = findViewById(R.id.btnOpenDatePicker);
        Button btnReset = findViewById(R.id.btn_reset);

        btnOpenDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterAndSortData(0, 0, 0);
            }
        });

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
                String csvData = convertTableItemListToCSV(filteredTableItemList);
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

    }

    private void filterAndSortData(int year, int month, int day) {
        // Mendapatkan data dari Firebase Realtime Database (Contoh)
        // Initialize RecyclerView
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("mcu-device/history");
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                originalTableItemList.clear(); // Hapus data sebelumnya
                Integer no=1;
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    TableItem tableItem = itemSnapshot.getValue(TableItem.class);
                    tableItem.setNo(no);
                    originalTableItemList.add(tableItem);
                    no++;
                }

                // Filter data berdasarkan tanggal yang dipilih
                if (year == 0 && month == 0 && day == 0) {
                    // Tampilkan semua data history karena tanggal yang dipilih adalah tanggal "0-0-0"
                    filteredTableItemList.clear();
                    filteredTableItemList.addAll(originalTableItemList);
                } else {
                    filteredTableItemList = filterDataBySelectedDate(originalTableItemList, year, month, day);
                }

                Log.d("RecapitulationActivity", "Jumlah data [originalTableItemList]: " + originalTableItemList.size());
                Log.d("RecapitulationActivity", "Jumlah data [filteredTableItemList]: " + filteredTableItemList.size());

                if (filteredTableItemList.isEmpty()) {
                    Toast.makeText(RecapitulationActivity.this, "Data tidak ada untuk tanggal ini", Toast.LENGTH_SHORT).show();
                }
                // Sort data berdasarkan unixTime
                Collections.sort(filteredTableItemList, new Comparator<TableItem>() {
                    @Override
                    public int compare(TableItem item1, TableItem item2) {
                        return Long.compare(item1.getUnixTime(), item2.getUnixTime());
                    }
                });

                // Perbarui data di RecyclerView
                tableAdapter.updateData(filteredTableItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Error fetching data from Firebase", databaseError.toException());
            }
        });
    }
    private List<TableItem> filterDataBySelectedDate(List<TableItem> itemList, int year, int month, int day) {
        List<TableItem> filteredList = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, day);
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);
        long selectedUnixTime = selectedDate.getTimeInMillis() / 1000; // Konversi ke format unixTime
        long bod = getStartOfDayUnixTime(year, month, day);
        long eod = getEndOfDayUnixTime(year, month, day);
        Log.d("RecapitulationActivity", "Jumlah data [selectedUnixTime]: " + selectedUnixTime);
        for (TableItem item : itemList) {
            Log.d("RecapitulationActivity", "Jumlah data [item.getUnixTime()]: " + item.getUnixTime());
            // Jika unixTime pada item sama dengan unixTime yang dipilih, tambahkan ke daftar
            if (item.getUnixTime() >= bod && item.getUnixTime() <= eod) {
                filteredList.add(item);
            }
        }

        return filteredList; // Kembalikan hasil filtering
    }
    private void openDatePicker() {
        // Ambil tanggal saat ini sebagai default
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Tampilkan DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(RecapitulationActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;

                        // Periksa apakah tanggal yang dipilih adalah tanggal "0" (default) atau tanggal yang valid
                        if (mYear == 0 && mMonth == 0 && mDay == 0) {
                            // Tampilkan semua data history karena tanggal yang dipilih adalah tanggal "0"
                            filterAndSortData(0, 0, 0);
                        } else {
                            // Panggil metode untuk mengambil data berdasarkan tanggal yang dipilih
                            filterAndSortData(mYear, mMonth, mDay);
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    public String convertTableItemListToCSV(List<TableItem> tableItemList) {
        StringBuilder csvBuilder = new StringBuilder();

        // Header kolom
        csvBuilder.append("\"No \",\"Date \",\"Location \"\n");

        // Data dari List<TableItem>
        for (TableItem tableItem : tableItemList) {
            String dateTimeString = convertUnixTimeToDateTime(tableItem.getUnixTime());
            String loc = "";
            if (tableItem.getLocationNumber() == 1){
                loc = "A";
            } else if (tableItem.getLocationNumber() == 2){
                loc = "B";
            } else if (tableItem.getLocationNumber() == 3){
                loc = "C";
            } else if (tableItem.getLocationNumber() == 4){
                loc = "D";
            }
            gerbong = "Gerbong " + loc;
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

    private long getStartOfDayUnixTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    private long getEndOfDayUnixTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis() / 1000;
    }
}

