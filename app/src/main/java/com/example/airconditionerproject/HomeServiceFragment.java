package com.example.airconditionerproject;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeServiceFragment extends Fragment {
    ListView listOrderan;
    TextView tvUsername;
    SwipeRefreshLayout swipeRefreshMain;
    ArrayList<String> array_pembeli, array_address, array_id_order;
    Button btnPesananYangDiambil;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshMain = view.findViewById(R.id.swipeContainer);
        listOrderan = view.findViewById(R.id.listOrderan);
        tvUsername = view.findViewById(R.id.sayHello);
        btnPesananYangDiambil = view.findViewById(R.id.btnLihatPesanan);

        Session session = new Session(getActivity());

        tvUsername.setText("Hello, " + session.getUsername());

        btnPesananYangDiambil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_pesananYangDiambilFragment);
            }
        });

        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                swipeRefreshMain.setRefreshing(false);
            }
        });

        swipeRefreshMain.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        scrollRefresh();
    }

    private void scrollRefresh()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mengambil data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 2000);
    }

    private void initializeArray()
    {
        array_pembeli = new ArrayList<String>();
        array_address = new ArrayList<String>();
        array_id_order = new ArrayList<String>();
        array_pembeli.clear();
        array_address.clear();
        array_id_order.clear();
    }

    private void getData()
    {
        initializeArray();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/getOrder";
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");

                    if(status){
                        progressDialog.dismiss();
                        JSONArray ja = jsonObject.getJSONArray("item");
                        for(int i = 0; i < ja.length(); i++){
                            JSONObject joResult = ja.getJSONObject(i);

                            array_pembeli.add(joResult.getString("username"));
                            array_address.add(joResult.getString("address"));
                            array_id_order.add(joResult.getString("id_order"));

                        }

                        final CLV_DataOrder adapter = new CLV_DataOrder(getActivity(), array_pembeli, array_address, array_id_order);
                        listOrderan.setAdapter(adapter);
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