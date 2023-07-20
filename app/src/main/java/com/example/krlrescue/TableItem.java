package com.example.krlrescue;

public class TableItem {
    private int No;
    private int locationNumber;
    private long unixTime;

    // Buat constructor, getter, dan setter sesuai kebutuhan.

    public TableItem() {
    }

    public int getNo() {
        return No;
    }

    public void setNo(int No) {
        this.No = No;
    }

    public int getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }
}

