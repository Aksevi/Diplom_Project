package com.example.curierapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "statistics")
public class Statistics {

    //Первичный ключ
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id = 0;

    //дата
    @ColumnInfo(name = "date")
    private String date = "";

    //закрыто заданий
    @ColumnInfo(name = "task_complete")
    private int taskComplete = 0;

    public Statistics() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTaskComplete() {
        return taskComplete;
    }

    public void setTaskComplete(int taskComplete) {
        this.taskComplete = taskComplete;
    }
}