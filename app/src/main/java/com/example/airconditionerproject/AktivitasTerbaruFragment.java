package com.example.airconditionerproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AktivitasTerbaruFragment extends Fragment {
    TextView tvTanggal,tvStatus, tvItem, tvHarga, tvKeluhan, tvUsername, tvEmail, tvPhone, tvAddress, tvUserService, tvPhoneService, tvPesan;
    ProgressBar progressBar;
    CardView cardOnProcess;
    Button btnCall, btnServiceSelesai;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aktivitas_terbaru, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTanggal = view.findViewById(R.id.tvDetailTglPembelian);
        tvStatus = view.findViewById(R.id.tvDetailStatus);
        tvItem = view.findViewById(R.id.tvDetailItem);
        tvKeluhan = view.findViewById(R.id.tvDetailKeluhan);
        tvUsername = view.findViewById(R.id.txtUsername);
        tvEmail = view.findViewById(R.id.txtEmail);
        tvPhone = view.findViewById(R.id.txtPhone);
        tvAddress = view.findViewById(R.id.txtAddress);
        tvHarga = view.findViewById(R.id.txtTotalHarga);
        tvUserService = view.findViewById(R.id.txtUsernameServis);
        tvPhoneService = view.findViewById(R.id.txtPhoneServis);
        tvPesan = view.findViewById(R.id.txtPesan);
        cardOnProcess = view.findViewById(R.id.cardOnProcess);
        btnCall = view.findViewById(R.id.btnCall);
        btnServiceSelesai = view.findViewById(R.id.btnServiceSelesai);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);




        String id_order = getArguments().getString(HomeUserFragment.EXTRA_ID_ORDER);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/getDetailOrder/" + Integer.valueOf(id_order);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    if(status){
                        JSONObject js = jsonObject.getJSONObject("item");
                        String total_belanja = js.getString("total_belanja");
                        String id_order = js.getString("id_order");

                        btnServiceSelesai.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Info")
                                        .setMessage("Apakah Anda ingin menyelasaikan service?")
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                updateStatusService(id_order);
                                            }
                                        })
                                        .setNegativeButton("Tidak", null)
                                        .show();
                            }
                        });

                        tvTanggal.setText(js.getString("date_created"));
                        tvStatus.setText(js.getString("status"));
                        tvHarga.setText(String.format("Rp. %d", Integer.valueOf(total_belanja)));
                        tvUsername.setText(js.getString("username"));
                        tvEmail.setText(js.getString("email"));
                        tvPhone.setText(js.getString("phone"));
                        tvAddress.setText(js.getString("address"));

                        String keluhan = js.getString("keluhan");
                        if(keluhan.equals("") || keluhan.equals("null")){
                            tvKeluhan.setText("Tidak ada keluhan");
                        } else{
                            tvKeluhan.setText(keluhan);
                        }

                        String txtPenampung = "";
                        JSONArray jsonArray = jsonObject.getJSONArray("list_pesanan");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String concatResult;
                            if(jsonArray.length() == 1){
                                concatResult = jo.getString("nama_item") + " (x" + jo.getString("jumlah_pesanan") + ")";
                            } else {
                                concatResult = jo.getString("nama_item") + " (x" + jo.getString("jumlah_pesanan") + ")\n";
                            }
                            txtPenampung += concatResult;
                        }
                        tvItem.setText(txtPenampung);
                    } else {

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        AsyncHttpClient httpOnProcess = new AsyncHttpClient();
        String url_on_process = "http://tkjbpnup.com/kelompok_5/acku/order/orderOnProcess/" +  Integer.valueOf(id_order);
        httpOnProcess.get(url_on_process, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    if(status){
                        JSONObject js = jsonObject.getJSONObject("data");
                        cardOnProcess.setVisibility(View.VISIBLE);
                        btnServiceSelesai.setVisibility(View.VISIBLE);

                        tvUserService.setText(js.getString("username"));
                        tvPhoneService.setText(js.getString("phone"));
                        tvPesan.setText(js.getString("pesan"));

                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhoneService.getText().toString()));
                                startActivity(intent);
                            }
                        });
                    } else {
                        cardOnProcess.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }
    private void updateStatusService(String id_order)
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/statusServiceSucess/" + Integer.valueOf(id_order);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    if(status){
                        Intent i = new Intent(getActivity(), SuccessServiceActivity.class);
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}