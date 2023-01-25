package com.example.airconditionerproject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CLV_ListOrderUser extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> listTanggal, listStatus, listItemDibeli, listKeluhan, listUser, listPhone, listAddress, listHarga;
    TextView tvTanggal,tvStatus, tvItem, tvHarga, tvKeluhan, tvUsername, tvPhone, tvAddress;

    public CLV_ListOrderUser(Activity context, ArrayList<String> tanggal,ArrayList<String> status, ArrayList<String> item, ArrayList<String> keluhan, ArrayList<String> user,ArrayList<String> phone, ArrayList<String> address,ArrayList<String> harga){
        super(context, R.layout.list_order, tanggal);
        this.context = context;
        this.listTanggal = tanggal;
        this.listStatus = status;
        this.listItemDibeli = item;
        this.listKeluhan = keluhan;
        this.listUser = user;
        this.listPhone = phone;
        this.listAddress = address;
        this.listHarga = harga;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_order_user, null, true);

        tvTanggal = rowView.findViewById(R.id.tvDetailTglPembelian);
        tvStatus = rowView.findViewById(R.id.tvDetailStatus);
        tvItem = rowView.findViewById(R.id.tvDetailItem);
        tvKeluhan = rowView.findViewById(R.id.tvDetailKeluhan);
        tvUsername = rowView.findViewById(R.id.txtUsername);
        tvPhone = rowView.findViewById(R.id.txtPhone);
        tvAddress = rowView.findViewById(R.id.txtAddress);
        tvHarga = rowView.findViewById(R.id.txtTotalHarga);
        tvTanggal.setText(listTanggal.get(position));
        tvStatus.setText(listStatus.get(position));
        tvItem.setText(listItemDibeli.get(position));
        String keluhan = listKeluhan.get(position);
        if(keluhan.equals("null")){
            tvKeluhan.setText("Tidak ada keluhan");
        } else {
            tvKeluhan.setText(listKeluhan.get(position));
        }

        tvUsername.setText(listUser.get(position));
        tvPhone.setText(listPhone.get(position));
        tvAddress.setText(listAddress.get(position));
        tvHarga.setText(listHarga.get(position));

        return rowView;
    }
}
