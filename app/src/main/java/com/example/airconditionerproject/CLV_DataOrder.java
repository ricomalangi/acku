package com.example.airconditionerproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.ArrayList;

public class CLV_DataOrder extends ArrayAdapter<String> {
    public static final String EXTRA_ID_ORDER = "extra_id_order";

    private final Activity context;
    private ArrayList<String> listPembeli;
    private ArrayList<String> listAlamat;
    private ArrayList<String> listIdOrder;

    public CLV_DataOrder(Activity context, ArrayList<String> pembeli, ArrayList<String> alamat, ArrayList<String> idOrder){
        super(context, R.layout.list_order, pembeli);
        this.context = context;
        this.listPembeli = pembeli;
        this.listAlamat = alamat;
        this.listIdOrder = idOrder;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_order, null, true);

        TextView namaPembeli = rowView.findViewById(R.id.tvNamaPembeli);
        TextView address = rowView.findViewById(R.id.tvAddress);
        Button btnDetail = rowView.findViewById(R.id.btnDetail);

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mBundle = new Bundle();
                mBundle.putString(EXTRA_ID_ORDER, listIdOrder.get(position));
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_detailOrderFragment, mBundle);
            }
        });
        namaPembeli.setText(listPembeli.get(position));
        address.setText(listAlamat.get(position));
        return rowView;
    }
}
