package com.example.airconditionerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ServiceActivity extends AppCompatActivity {
    Button btnPesan;
    TextView txt_total;
    TextView detailUsername, detailEmail, detailPhone, detailAddress, btnAddress;
    CardView cardViewCuci1pk, cardViewCuci2pk, cardViewCuciInverter,  cardPasang1pk, cardPasang2pk, cardTambahFreon1pk, cardTambahFreon2pk;
    ListView listProses;
    CheckBox cbRutin, cbTidakDingin, cbBerisik, cbRusak;
    String cb;
    ArrayList<String> array_name, array_id;

    int hargaCuciAc1pk = 45000;
    int hargaCuciAc2pk = 75000;
    int hargaCuciInverter = 100000;
    int hargaPasang1pk = 125000;
    int hargaPasang2pk = 125000;
    int hargaFreon1pk = 150000;
    int hargaFreon2pk = 225000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        listProses = findViewById(R.id.LV);

        cbRutin = findViewById(R.id.service_rutin);
        cbTidakDingin = findViewById(R.id.ac_tidak_dingin);
        cbBerisik = findViewById(R.id.ac_berisik);
        cbRusak = findViewById(R.id.ac_rusak);

        cardViewCuci1pk = findViewById(R.id.cardCuci1pk);
        cardViewCuci2pk = findViewById(R.id.cardCuci2pk);
        cardViewCuciInverter = findViewById(R.id.cardCuciInverter2pk);
        cardPasang1pk = findViewById(R.id.cardPasang1pk);
        cardPasang2pk = findViewById(R.id.cardPasang2pk);
        cardTambahFreon1pk = findViewById(R.id.cardTambahFreon1pk);
        cardTambahFreon2pk = findViewById(R.id.cardTambahFreon2pk);

        txt_total = findViewById(R.id.total);

        cardViewCuci1pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titleCuci1pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaCuciAc1pk, totalHarga);
            }
        });
        cardViewCuci2pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titleCuci2pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaCuciAc2pk, totalHarga);
            }
        });
        cardViewCuciInverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titleCuciInverter1pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaCuciInverter, totalHarga);
            }
        });
        cardPasang1pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titlePasang1pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaPasang1pk, totalHarga);
            }
        });
        cardPasang2pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titlePasang2pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaPasang2pk, totalHarga);
            }
        });
        cardTambahFreon1pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titleTambahFreon1pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaFreon1pk, totalHarga);
            }
        });
        cardTambahFreon2pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView titleCuci = findViewById(R.id.titleTambahFreon2pk);
                int totalHarga = Integer.valueOf(txt_total.getText().toString());
                showDialogItemAdd(titleCuci, hargaFreon2pk, totalHarga);
            }
        });

        getDataCart();

        btnPesan = findViewById(R.id.btnPesan);
        btnAddress = findViewById(R.id.txtAturAlamat);

        detailUsername = findViewById(R.id.txtUsername);
        detailEmail = findViewById(R.id.txtEmail);
        detailPhone = findViewById(R.id.txtPhone);
        detailAddress = findViewById(R.id.txtAddress);

        Session session = new Session(ServiceActivity.this);
        detailUsername.setText(session.getUsername());
        detailEmail.setText(session.getEmail());
        detailPhone.setText(session.getPhone());

        String checkAddress = session.getAddress();
        String checkTotal = txt_total.getText().toString();
        btnPesan.setEnabled(false);

        if(checkAddress != "0" && !checkAddress.equals("0")){
            detailAddress.setText(checkAddress);
            btnPesan.setEnabled(true);
        }

        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddress();
            }
        });

        // btn checkout
        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cbRutin.isChecked() && !cbTidakDingin.isChecked() && !cbBerisik.isChecked() && !cbRusak.isChecked()){
                    cb = "null";
                } else {
                    cb = "";
                }

                if(cbRutin.isChecked()){
                    cb += "Service rutin,";
                    Log.d("cb", cb);
                }
                if(cbTidakDingin.isChecked()){
                    cb += "AC tidak dingin,";
                    Log.d("cb", cb);
                }
                if(cbBerisik.isChecked()){
                    cb += "AC berisik,";
                    Log.d("cb", cb);
                }
                if(cbRusak.isChecked()){
                    cb += "AC rusak,";
                    Log.d("cb", cb);
                }
                Session session = new Session(ServiceActivity.this);
                int id_user = session.getIdUser();

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                String url = "http://tkjbpnup.com/kelompok_5/acku/order/checkout";
                RequestParams requestParams = new RequestParams();
                requestParams.put("id_user", id_user);
                requestParams.put("keluhan", cb); // check box keluhan

                asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.getString("msg");
                            if(status){
                                Intent toSuccessActivity = new Intent(ServiceActivity.this, SuccessActivity.class);
                                startActivity(toSuccessActivity);
                            } else {
                                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        });
    }
    private void getDataCart(){
        initializeArray();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Session session = new Session(ServiceActivity.this);
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/getDataWhere/" + session.getIdUser();
        Log.d("data_cart", "called");
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");

                    if(status){
                        JSONObject js = jsonObject.getJSONObject("item");
                        String total_belanja = js.getString("total_belanja");
                        txt_total.setText(total_belanja);

                        btnPesan.setEnabled(true);

                        JSONArray jsonArray = jsonObject.getJSONArray("list_pesanan");
                        for(int i= 0; i < jsonArray.length(); i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String concatResult = jo.getString("nama_item") + " (x" + jo.getString("jumlah_pesanan") + ")";

                            array_name.add(concatResult);
                            array_id.add(String.valueOf(i));
                        }
                    } else {
                        btnPesan.setEnabled(false);
                    }

                    final CLV_DataCart adapter = new CLV_DataCart(ServiceActivity.this, array_name, array_id);
                    listProses.setAdapter(adapter);

                    Helper.getListViewSize(listProses);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void initializeArray() {
        array_name = new ArrayList<String>();
        array_id = new ArrayList<String>();
        array_name.clear();
        array_id.clear();
    }

    private void showDialogAddress()
    {
        final Dialog dialog = new Dialog(ServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_address);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText etAddress = dialog.findViewById(R.id.etAddress);
        final Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = etAddress.getText().toString().trim();
                if(address.isEmpty()){
                    etAddress.setError("address is required!");
                    etAddress.requestFocus();
                    return;
                }
                Session session = new Session(ServiceActivity.this);
                int id_user = session.getIdUser();

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                String url = "http://tkjbpnup.com/kelompok_5/acku/user/insertAddress";
                RequestParams requestParams = new RequestParams();
                requestParams.put("id_user", id_user);
                requestParams.put("address", address);

                asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        detailAddress.setText(address);
                        session.setAddress(address);
                        String result = new String(responseBody);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.getString("msg");
                            if(status){
                                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                btnPesan.setEnabled(true);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    private void showDialogItemAdd(TextView title, int harga, int total_harga)
    {
        final Dialog dialog = new Dialog(ServiceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_item_add);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView etItem = dialog.findViewById(R.id.etItem);
        final EditText etJumlahItem = dialog.findViewById(R.id.etJumlahItem);
        final Button btnSubmit = dialog.findViewById(R.id.btnAddToCart);
        etItem.setText(title.getText().toString());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jumlahPesanan = etJumlahItem.getText().toString().trim();
                if(jumlahPesanan.isEmpty()){
                    etJumlahItem.setError("jumlah item is required!");
                    etJumlahItem.requestFocus();
                    return;
                }

                int parsePesanan = Integer.valueOf(jumlahPesanan);
                int hargaTotal = parsePesanan * harga;
                int grandPrize = total_harga + hargaTotal;

                Session session = new Session(ServiceActivity.this);
                int id_user = session.getIdUser();

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                String url = "http://tkjbpnup.com/kelompok_5/acku/order/insertOrder";
                RequestParams requestParams = new RequestParams();
                requestParams.put("id_user", id_user);
                requestParams.put("list_pesanan", title.getText().toString());
                requestParams.put("jumlah_pesanan", jumlahPesanan);
                requestParams.put("total_harga", hargaTotal);

                asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        txt_total.setText(String.valueOf(grandPrize)); // set text total harga
                        String result = new String(responseBody);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.getString("msg");
                            if(status){
                                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                                getDataCart();
                            } else {
                                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });

                btnPesan.setEnabled(true);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

}