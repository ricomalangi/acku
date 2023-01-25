package com.example.airconditionerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister;
    EditText etEmail, etPassword;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkLogin();

        etEmail = findViewById(R.id.etInputEmail);
        etPassword = findViewById(R.id.etInputPassword);
        tvRegister = findViewById(R.id.textRegister);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar3);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegister);
            }
        });

    }

    private void login()
    {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setError("username is required!");
            etEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("please provide valid email!");
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etPassword.setError("password is required");
            etPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/login";
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", email);
        requestParams.put("password", password);

        asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                String result = new String(responseBody);

                try {
                    JSONObject responseObject = new JSONObject(result);

                    Boolean status = responseObject.getBoolean("status");
                    String msg = responseObject.getString("msg");

                    if(status){
                        JSONObject dataObj = responseObject.getJSONObject("data");

                        int id_user = dataObj.getInt("id_user");
                        String username = dataObj.getString("username");
                        String email = dataObj.getString("email");
                        String phone = dataObj.getString("phone");
                        String address = dataObj.getString("address");
                        String role = dataObj.getString("role");
                        String picture = dataObj.getString("picture");

                        Session session = new Session(LoginActivity.this);
                        session.setIdUser(id_user);
                        session.setUsername(username);
                        session.setEmail(email);
                        session.setPhone(phone);
                        session.setAddress(address);
                        session.setPicture(picture);
                        session.setRole(role);
                        if(role.equals("pengguna")){
                            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            toMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(toMainActivity);
                        } else if(role.equals("tukang servis")){
                            Intent toMainActivityService = new Intent(LoginActivity.this, MainActivityService.class);
                            toMainActivityService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(toMainActivityService);
                        }

                    } else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage(msg)
                                .setCancelable(false)
                                .setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
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

    private void checkLogin()
    {
        Session session = new Session(LoginActivity.this);
        String username = session.getUsername();
        String role = session.getRole();
        if(username != null && !username.equals("") && role.equals("pengguna")){
            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivity);
        } else if(username != null && !username.equals("") && role.equals("tukang servis")) {
            Intent toMainActivityService = new Intent(LoginActivity.this, MainActivityService.class);
            startActivity(toMainActivityService);
        }
    }
}