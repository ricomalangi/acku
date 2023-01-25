package com.example.airconditionerproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Activities implements Parcelable {
    private String title;
    private String status;
    private String[] pembelian;
    private Integer totalHarga;

    public Activities(String title, String status, Integer totalHarga) {
        this.title = title;
        this.status = status;
        this.totalHarga = totalHarga;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getPembelian() {
        return pembelian;
    }

    public void setPembelian(String[] pembelian) {
        this.pembelian = pembelian;
    }

    public Integer getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(Integer totalHarga) {
        this.totalHarga = totalHarga;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.status);
//        dest.writeStringArray(this.pembelian);
        dest.writeValue(this.totalHarga);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.status = source.readString();
//        this.pembelian = source.createStringArray();
        this.totalHarga = (Integer) source.readValue(Integer.class.getClassLoader());
    }

    protected Activities(Parcel in) {
        this.title = in.readString();
        this.status = in.readString();
//        this.pembelian = in.createStringArray();
        this.totalHarga = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Activities> CREATOR = new Creator<Activities>() {
        @Override
        public Activities createFromParcel(Parcel source) {
            return new Activities(source);
        }

        @Override
        public Activities[] newArray(int size) {
            return new Activities[size];
        }
    };
}
