package com.example.airconditionerproject;

import android.app.Activity;
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

import java.util.ArrayList;

public class CLV_DataCart extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> listItem;
    private ArrayList<String> listIdItem;

    public CLV_DataCart(Activity context, ArrayList<String> item, ArrayList<String> idItem){
        super(context, R.layout.list_cart, item);
        this.context = context;
        this.listItem = item;
        this.listIdItem = idItem;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cart, null, true);

        TextView item = rowView.findViewById(R.id.txtItem);
        TextView idItem = rowView.findViewById(R.id.txtIndexItem);
        Button btnEdit = rowView.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("edit", listIdItem.get(position));
                Toast.makeText(view.getContext(), listIdItem.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        item.setText(listItem.get(position));
        idItem.setText(listIdItem.get(position));
        return rowView;
    }
}
