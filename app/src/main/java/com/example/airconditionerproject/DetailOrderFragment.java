package com.example.airconditionerproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DetailOrderFragment extends Fragment {
    TextView tvTanggal,tvStatus, tvItem, tvHarga, tvKeluhan, tvUsername, tvEmail, tvPhone, tvAddress;
    ProgressBar progressBar;
    Button btnAmbilOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_order, container, false);
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
        progressBar = view.findViewById(R.id.progressBar2);
        btnAmbilOrder = view.findViewById(R.id.btnAmbilOrder);
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
                            String concatResult = jo.getString("nama_item") + " (x" + jo.getString("jumlah_pesanan") + ")\n";
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

        btnAmbilOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogMessage(id_order);
            }
        });
    }

    private void showDialogMessage(String idOrder){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_message);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etMessage = dialog.findViewById(R.id.etInputMessage);
        final Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString().trim();
                if(message.isEmpty()){
                    etMessage.setError("message is required!");
                    etMessage.requestFocus();
                    return;
                }
                Session session = new Session(getActivity());
                int id_user = session.getIdUser();

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                String url = "http://tkjbpnup.com/kelompok_5/acku/ambil_pesanan/insertPesanan";
                RequestParams requestParams = new RequestParams();
                requestParams.put("id_user", id_user);
                requestParams.put("id_order", idOrder);
                requestParams.put("pesan", message);

                asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.getString("msg");
                            if(status){
                                Intent toSuccessAmbilOrderan = new Intent(getActivity(), SuccessAmbilOrderanActivity.class);
                                startActivity(toSuccessAmbilOrderan);
                            } else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                btnSubmit.setEnabled(true);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }
}