package com.example.airconditionerproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx){
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUsername(String username){
        prefs.edit().putString("username", username).commit();
    }

    public String getUsername(){
        String username = prefs.getString("username", "");
        return username;
    }

    public void setEmail(String email){
        prefs.edit().putString("email", email).commit();
    }

    public String getEmail(){
        String email = prefs.getString("email", "");
        return email;
    }

    public void setPhone(String phone){
        prefs.edit().putString("phone", phone).commit();
    }

    public String getPhone(){
        String phone = prefs.getString("phone", "");
        return phone;
    }

    public void setIdUser(int idUser){
        prefs.edit().putInt("id_user", idUser).commit();
    }

    public int getIdUser(){
        int id_user = prefs.getInt("id_user", 0);
        return id_user;
    }

    public void setAddress(String address){
        prefs.edit().putString("address", address).commit();
    }

    public String getAddress(){
        String address = prefs.getString("address", "");
        return address;
    }

    public void setPicture(String picture){
        prefs.edit().putString("picture", picture).commit();
    }

    public String getPicture(){
        String picture = prefs.getString("picture", "");
        return picture;
    }

    public void setRole(String role){
        prefs.edit().putString("role", role).commit();
    }

    public String getRole(){
        String role = prefs.getString("role", "");
        return role;
    }

    public void setOnBoarding(Boolean status){
        prefs.edit().putBoolean("onboarding", status).commit();
    }

    public Boolean getOnBoarding(){
        Boolean status = prefs.getBoolean("onboarding", false);
        return status;
    }
}
