package com.example.airconditionerproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeUserFragment extends Fragment {
    TextView tvTanggal, tvStatus, tvHarga, tvItem;
    Button btnDetailAktivitas;
    String id_order;
    SwipeRefreshLayout swipeRefreshMain;
    public static final String EXTRA_ID_ORDER = "extra_id_order";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnService = view.findViewById(R.id.btnService);
        btnDetailAktivitas = view.findViewById(R.id.btnDetailAktivitasTerbaru);
        tvTanggal = view.findViewById(R.id.txtTanggal);
        tvStatus = view.findViewById(R.id.txtStatus);
        tvHarga = view.findViewById(R.id.txtTotalHarga);
        tvItem = view.findViewById(R.id.txtItem);
        swipeRefreshMain = view.findViewById(R.id.swipeContainer);
        TextView sayHello = view.findViewById(R.id.sayHello);
        CardView cardEmpty = view.findViewById(R.id.cardViewEmpty);
        CardView cardFilled = view.findViewById(R.id.cardView);
        swipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDataLastOrder(sayHello, cardEmpty, cardFilled);
                    }
                }, 2000);

                swipeRefreshMain.setRefreshing(false);
            }
        });

        swipeRefreshMain.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        getDataLastOrder(sayHello, cardEmpty, cardFilled);

        btnService.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_service)
        );

        btnDetailAktivitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mBundle = new Bundle();
                mBundle.putString(EXTRA_ID_ORDER, id_order);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_aktivitasTerbaruFragment, mBundle);
            }
        });
    }

    private void getDataLastOrder(TextView sayHello, CardView cardEmpty, CardView cardFilled)
    {

        Session session = new Session(getActivity());

        sayHello.setText("Hello, " + session.getUsername());

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/order/getLastOrder/" + session.getIdUser();
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");

                    if(status){
                        JSONObject js = jsonObject.getJSONObject("item");
                        String total_belanja = js.getString("total_belanja");
                        cardEmpty.setVisibility(View.INVISIBLE);
                        cardFilled.setVisibility(View.VISIBLE);

                        id_order = js.getString("id_order");
                        tvTanggal.setText(js.getString("date_created"));
                        tvHarga.setText(String.format("Rp. %d", Integer.valueOf(total_belanja)));
                        tvStatus.setText(js.getString("status"));
                        String txtPenampung = "";
                        JSONArray jsonArray = jsonObject.getJSONArray("list_pesanan");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String concatResult = jo.getString("nama_item") + " (x" + jo.getString("jumlah_pesanan") + ")" + "\n";

                            txtPenampung += concatResult;
                        }
                        tvItem.setText(txtPenampung);

                    } else {
                        cardEmpty.setVisibility(View.VISIBLE);
                        cardFilled.setVisibility(View.INVISIBLE);
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