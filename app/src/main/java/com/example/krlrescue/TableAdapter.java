package com.example.krlrescue;

import static com.example.krlrescue.RouteActivity.gerbong;
import static com.example.krlrescue.SigninActivity.newUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
        private List<TableItem> tableItemList;

        public TableAdapter(List<TableItem> tableItemList) {
            this.tableItemList = tableItemList;
        }

        @NonNull
        @Override
        public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_layout, parent, false);
            return new TableViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
            TableItem tableItem = tableItemList.get(position);
            String dateTimeString = convertUnixTimeToDateTime(tableItem.getUnixTime());
            String loc = convertNumberToExcelColumn(tableItem.getLocationNumber());
            gerbong = "Pintu " + loc;
            holder.noTextView.setText(String.valueOf(tableItem.getNo()));
            holder.unixTimeTextView.setText(dateTimeString);
            holder.locationNumberTextView.setText(gerbong);

        }

        @Override
        public int getItemCount() {
            return tableItemList.size();
        }

        // ...
        public void updateData(List<TableItem> newData) {
            this.tableItemList = newData;
            notifyDataSetChanged();
        }
    public String convertUnixTimeToDateTime(long unixTime) {
        // Waktu dari UnixTime dalam bentuk milidetik (ms)
        Date date = new Date(unixTime * 1000);

        // Format yang ingin Anda gunakan untuk dateTime (misalnya "dd/MM/yyyy HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Mengonversi Date menjadi string dalam format yang diinginkan
        return sdf.format(date);
    }
        public static class TableViewHolder extends RecyclerView.ViewHolder {
            public TextView noTextView;
            public TextView unixTimeTextView;
            public TextView locationNumberTextView;

            public TableViewHolder(@NonNull View itemView) {
                super(itemView);
                noTextView = itemView.findViewById(R.id.noTextView);
                unixTimeTextView = itemView.findViewById(R.id.unixTimeTextView);
                locationNumberTextView = itemView.findViewById(R.id.locationNumberTextView);
            }
        }

    private String convertNumberToExcelColumn(int number) {
        StringBuilder column = new StringBuilder();

        while (number > 0) {
            int remainder = (number - 1) % 26; // Dapatkan sisa pembagian dari 0 hingga 25
            char ch = (char) (remainder + 'A'); // Konversi ke huruf berdasarkan ASCII
            column.insert(0, ch);
            number = (number - 1) / 26; // Hitung nilai selanjutnya untuk iterasi berikutnya
        }

        return column.toString();
    }
    }
