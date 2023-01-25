package com.example.airconditionerproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.airconditionerproject.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    CircleImageView profileCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appbar.toolbar);

        DrawerLayout drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        Menu menuNav = navigationView.getMenu();

        profileCircleImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.email);
        MenuItem logout = menuNav.findItem(R.id.nav_logout);

        Session session = new Session(MainActivity.this);
        String picture = session.getPicture();
        if(picture.equals("0")){
            profileCircleImageView.setImageResource(R.drawable.default_photo_profile);
        } else {
            String imageUrl = "http://tkjbpnup.com/kelompok_5/acku/upload/" + session.getPicture();
            Glide.with(MainActivity.this)
                    .load(imageUrl)
                    .into(profileCircleImageView);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/user/getDataWhere/" + session.getIdUser();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    username.setText(jsonArray.getJSONObject(0).getString("username"));
                    email.setText(jsonArray.getJSONObject(0).getString("email"));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Session session = new Session(MainActivity.this);
                session.setIdUser(0);
                session.setUsername("");
                session.setPicture("");
                session.setRole("");
                session.setEmail("");
                session.setAddress("");
                session.setPhone("");

//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.clear();
//                editor.commit();

                Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toLogin);
                return true;
            }
        });

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_activity, R.id.nav_about, R.id.nav_profile, R.id.action_nav_home_to_aktivitasTerbaruFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}