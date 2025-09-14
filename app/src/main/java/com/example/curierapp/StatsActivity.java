package com.example.curierapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.curierapp.Adapter.AddressListAdapter;
import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.DataBase.StatisticsDAO;
import com.example.curierapp.Models.Address;
import com.example.curierapp.Models.Statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {
    //доступ к переменным
    Button buttonBack, buttonCloseItem, buttonStatisticsClear;
    TextView textTotal, textCompleted, textRemaining, textStatistics, textPerDay;
    ProgressBar progressBar;

    AddressListAdapter addressListAdapter;

    private RoomDB dataBase; // переменная базы данных

    StatisticsDAO statisticsDAO;
    int totalTaskAllTime;
    int avgPerDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        // доступ к разметке
        textTotal = findViewById(R.id.text_total);
        textCompleted = findViewById(R.id.text_completed);
        textRemaining = findViewById(R.id.text_remaining);
        textStatistics = findViewById(R.id.text_statistics);
        textPerDay = findViewById(R.id.text_per_day);

        buttonBack = findViewById(R.id.btn_back);
        buttonCloseItem = findViewById(R.id.btn_close_item);
        buttonStatisticsClear = findViewById(R.id.btn_statistics_clear);

        progressBar = findViewById(R.id.progress_bar);

        dataBase = RoomDB.getInstance(this); // доступ к базе данных

        List<Address> allAddress = dataBase.mainDAO().getAll(); // получили список адресов из базы

        statisticsDAO = dataBase.statisticsDao();
        totalTaskAllTime = statisticsDAO.getTotalTasks();
        List<Statistics> allStats = statisticsDAO.getAll();
        avgPerDay = 0;
        if (!allStats.isEmpty()) {
            avgPerDay = totalTaskAllTime / allStats.size();
        }

        textStatistics.setText("За месяц: " + totalTaskAllTime);
        textPerDay.setText("Среднее в день: " + avgPerDay);


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
        int completed = 0; // сколько выполнено. изначально 0

        for (Address address : allAddress) { // идем по списк смотрим отмеченные
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

        if (completed == total && total > 0 && completed > 0) {
            buttonCloseItem.setEnabled(true);
        } else buttonCloseItem.setEnabled(false);

// кнопка закрытия смены
        buttonCloseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
                builder.setTitle("Закрыть смену?").setMessage("Вы уверены? Все задания будут очищены!").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                completedShift(); // метод закрытия смены
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        });

// кнопка очистки статистики
        buttonStatisticsClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StatsActivity.this);
                builder.setTitle("Очитстить статистику?").setMessage("Вы уверены? Все данные будут очищены!").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataBase.statisticsDao().clear();
                                textStatistics.setText("За месяц: ");
                                textPerDay.setText("Среднее в день: ");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    // метод закрытия смены
    private void completedShift() {
        int completedToday = 0;
        List<Address> allAddress = dataBase.mainDAO().getAll();
        for (Address address : allAddress) {
            if (address.isChecked()) {
                completedToday++;
            }
        }
        Statistics statistics = new Statistics();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        statistics.setDate(today);
        statistics.setTaskComplete(completedToday);

        statisticsDAO.insert(statistics);

        totalTaskAllTime = statisticsDAO.getTotalTasks();
        List<Statistics> allStats = statisticsDAO.getAll();
        avgPerDay = allStats.isEmpty() ? 0 : totalTaskAllTime / allStats.size();

        textStatistics.setText("За месяц: " + totalTaskAllTime);
        textPerDay.setText("Среднее в день: " + avgPerDay);

        textTotal.setText("Total: "); // устанавливаем в поля данные
        textCompleted.setText("Complete: ");
        textRemaining.setText("Remaining: ");
        progressBar.setProgress(0); // прогресс - меняеся от кол-ва выполненного

        dataBase.mainDAO().deleteAll();
        buttonCloseItem.setEnabled(false);
    }
}