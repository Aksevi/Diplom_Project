package com.example.curierapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.curierapp.Models.Statistics;

import java.util.List;

@Dao
public interface StatisticsDAO {

    @Insert
    void insert(Statistics stat);

    @Query("SELECT * FROM statistics")
    List<Statistics> getAll();

    @Query("SELECT SUM(task_complete) FROM statistics")
    int getTotalTasks();

    @Query("DELETE FROM statistics")
    void clear(); //  очистка статистику
}

