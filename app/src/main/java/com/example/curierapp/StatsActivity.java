package com.example.curierapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatsActivity extends AppCompatActivity {

    Button buttonBack;
    TextView textTotal, textCompleted, textRemaining;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        textTotal = findViewById(R.id.text_Total);
        textCompleted = findViewById(R.id.text_Completed);
        textRemaining = findViewById(R.id.text_Remaining);
        buttonBack = findViewById(R.id.btn_Back);


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}