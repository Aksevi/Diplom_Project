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

import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import java.util.List;

public class StatsActivity extends AppCompatActivity {
//доступ к переменным
    Button buttonBack;
    TextView textTotal, textCompleted, textRemaining;
    ProgressBar progressBar;

    private RoomDB dataBase; // переменная базы данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        // доступ к разметке
        textTotal = findViewById(R.id.text_total);
        textCompleted = findViewById(R.id.text_completed);
        textRemaining = findViewById(R.id.text_remaining);
        buttonBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);

        dataBase = RoomDB.getInstance(this); // доступ к базе данных

        List<Address> allAddress = dataBase.mainDAO().getAll(); // получили списко адресов из базы

// жмем кнопку назад
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(StatsActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        int total = allAddress.size(); // переменная сколько всего заказов = размеру списка
        int completed = 0; // сколько выполнено. изначлаьно 0

        for (Address address : allAddress){ // идем по списк смотрим отмеченные
            if (address.isChecked()) {
                completed++; // если находим то учеличиваем выполнено на 1
            }
        }

        int remaining = total - completed; // переменная сколько осталось

        textTotal.setText("Total: " + total); // устанавливаем в поля данные
        textCompleted.setText("Complete: " + completed);
        textRemaining.setText("Remaining: " + remaining);

// заполняем прогрессбар
        progressBar.setMax(total); // макс знач - сколько заказов всего
        progressBar.setProgress(completed); // прогресс - меняеся от кол-ва выполненного

    }
}