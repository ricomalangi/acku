package com.example.airconditionerproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ActivityUserFragment extends Fragment {
    ArrayList<String> array_tanggal, array_status, array_item, array_keluhan, array_user, array_phone, array_address_pesanan, array_harga;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewActivity);
        getData();
    }

    private void initializeArray()
    {
        array_tanggal = new ArrayList<String>();
        array_status = new ArrayList<String>();
        array_item = new ArrayList<String>();
        array_keluhan = new ArrayList<String>();
        array_user = new ArrayList<String>();
        array_phone = new ArrayList<String>();
        array_address_pesanan = new ArrayList<String>();
        array_harga = new ArrayList<String>();
        array_tanggal.clear();
        array_status.clear();
        array_item.clear();
        array_keluhan.clear();
        array_user.clear();
        array_phone.clear();
        array_address_pesanan.clear();
        array_harga.clear();

    }
    private void getData()
    {
        initializeArray();
        Session session = new Session(getActivity());
        int id_user = session.getIdUser();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/getUserOrder/" + id_user;;
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    Log.d("res_status", status.toString());
                    if(status){
                        JSONArray ja = jsonObject.getJSONArray("data");
                        Log.d("data_result", ja.toString());
                        for(int i = 0; i < ja.length(); i++){
                            JSONObject joResult = ja.getJSONObject(i);
                            Log.d("username", joResult.getString("username"));
                            array_tanggal.add(joResult.getString("date_created"));
                            array_status.add(joResult.getString("status"));
                            array_keluhan.add(joResult.getString("keluhan"));
                            array_user.add(joResult.getString("username"));
                            array_phone.add(joResult.getString("phone"));
                            array_address_pesanan.add(joResult.getString("address"));
                            array_harga.add(joResult.getString("total_belanja"));

                            JSONArray jaListPesanan = joResult.getJSONArray("list_pesanan_decode");
                            JSONArray jaParse = jaListPesanan.getJSONArray(0);
                            String txtPenampung = "";
                            for(int j = 0; j < jaParse.length(); j++){
                                JSONObject joListPesanan = jaParse.getJSONObject(j);
                                String concatResult;
                                if(jaParse.length() == 1){
                                    concatResult = joListPesanan.getString("nama_item") + " (x" + joListPesanan.getString("jumlah_pesanan") + ")";
                                } else {
                                    concatResult = joListPesanan.getString("nama_item") + " (x" + joListPesanan.getString("jumlah_pesanan") + ")\n";
                                }
                                txtPenampung += concatResult;

                            }
                            array_item.add(txtPenampung);

                        }

                        final CLV_ListOrderUser adapter = new CLV_ListOrderUser(getActivity(), array_tanggal, array_status, array_item, array_keluhan, array_user, array_phone, array_address_pesanan, array_harga);
                        listView.setAdapter(adapter);
                        listView.setDivider(null);
                    } else {

                    }
                } catch (Exception e){

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}