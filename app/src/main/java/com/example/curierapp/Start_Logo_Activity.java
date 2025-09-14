package com.example.curierapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Start_Logo_Activity extends AppCompatActivity {

    ImageView autoLogo;
    TextView appName;
    LinearLayout layoutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_logo);

        autoLogo = findViewById(R.id.image_auto_logo);
        appName = findViewById(R.id.text_app_name);
        layoutText = findViewById(R.id.layout_text);

        //запускает какой-то код через заданное время (в миллисекундах)
        new Handler().postDelayed(() -> {
            // анимация слева направо
    Animation translateLogo = AnimationUtils.loadAnimation(Start_Logo_Activity.this, R.anim.logo_move_translate);

    Animation translateLayout = AnimationUtils.loadAnimation(Start_Logo_Activity.this, R.anim.layout_move_translate);

//        appName.startAnimation(translateText);
    autoLogo.startAnimation(translateLogo);
    layoutText.startAnimation(translateLayout);

        }, 100);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Start_Logo_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        },3500);


    }
}

