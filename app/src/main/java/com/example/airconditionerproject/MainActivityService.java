package com.example.airconditionerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.airconditionerproject.databinding.ActivityMainBinding;
import com.example.airconditionerproject.databinding.ActivityMainServiceBinding;
import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityService extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainServiceBinding binding;

    CircleImageView profileCircleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appbar.toolbar);

        DrawerLayout drawerLayout = binding.drawerLayoutService;
        NavigationView navigationView = binding.navView;
        Menu menuNav = navigationView.getMenu();

        profileCircleImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.email);
        MenuItem logout = menuNav.findItem(R.id.nav_logout);


        Session session = new Session(MainActivityService.this);
        String picture = session.getPicture();
        if(picture.equals("0")){
            profileCircleImageView.setImageResource(R.drawable.default_photo_profile);
        } else {
            String imageUrl = "http://tkjbpnup.com/kelompok_5/acku/upload/" + picture;
            Glide.with(MainActivityService.this)
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

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivityService.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent toLogin = new Intent(MainActivityService.this, LoginActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toLogin);
                return true;
            }
        });


        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_about, R.id.nav_profile)
                .setOpenableLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_service);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_service);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}