package com.example.airconditionerproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView img = findViewById(R.id.idImgLogo);
        TextView text = findViewById(R.id.idTextLogo);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.down_from_top);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);

        img.startAnimation(animation1);
        text.startAnimation(animation2);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toLogin = new Intent(getApplicationContext(), OnBoardingActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toLogin);
            }
        }, 5000L);
    }
}