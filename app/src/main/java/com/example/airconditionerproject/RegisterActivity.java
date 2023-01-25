package com.example.airconditionerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class RegisterActivity extends AppCompatActivity {
    TextView tvLogin;
    Button btnRegister;
    EditText etEmail, etPassword, etUsername, etPhone;
    ProgressBar progressBar;
    Spinner etRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etInputUsername);
        etEmail = findViewById(R.id.etInputEmail);
        etPassword = findViewById(R.id.etInputPassword);
        etPhone = findViewById(R.id.etInputPhone);
        etRole = findViewById(R.id.spinnerRole);
        progressBar = findViewById(R.id.progressBar);

        tvLogin = findViewById(R.id.textLogin);

        btnRegister = findViewById(R.id.buttonRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });
    }

    private void registerUser()
    {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String role = etRole.getSelectedItem().toString();
        if(username.isEmpty()){
            etUsername.setError("username is required!");
            etUsername.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etEmail.setError("email is required!");
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
        if(phone.isEmpty()){
            etPhone.setError("phone number is required");
            etPhone.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/register";

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("email", email);
        params.put("phone", phone);
        params.put("password", password);
        params.put("role", role);

        client.post(url, params,  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                String result = new String(responseBody);

                try {
                    JSONObject responseObject = new JSONObject(result);

                    Boolean status = responseObject.getBoolean("status");
                    String msg = responseObject.getString("msg");

                    if(status){
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setMessage(msg)
                                .setCancelable(false)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(toLogin);
                                    }
                                }).show();

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
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
}