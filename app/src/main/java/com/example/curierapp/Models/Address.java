package com.example.curierapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// 1️⃣  @Entity — аннотация Room, говорящая: "Эта Java-класс — таблица в базе данных".
@Entity(tableName = "address") // tableName = "address" — имя таблицы в базе.
public class Address implements Serializable { //implements Serializable — позволяет передавать объект между экранами через Intent или сохранять его в файл.

    //2️⃣ Поля — это колонки в таблице
    //Первичный ключ
    @PrimaryKey(autoGenerate = true)// @PrimaryKey(autoGenerate = true) — ключ будет уникальным и генерироваться автоматически.
    @ColumnInfo(name = "id") //@ColumnInfo(name = "id") — имя колонки в базе.
    private int id = 0;
    //Адрес
    @ColumnInfo(name = "address")
    String address = "";
    //Телефон
    @ColumnInfo(name = "phone")
    String phone = "";

    //Координаты (для карты)
    @ColumnInfo(name = "latitude") // координаты ширина нужны для карты
    private double latitude;

    @ColumnInfo(name = "longitude") // координаты долгота нужны для карты
    private double longitude;

    //Комментарий
    @ColumnInfo(name = "comment")
    String comment = "";

    //Дата
    @ColumnInfo(name = "date")
    String date = "";

    //Галочка выполнения
    @ColumnInfo(name = "checked")
    boolean checked = false;

    // индекс в списке
    @ColumnInfo(name = "position")
    private int position;




    //3️⃣ Конструктор. Пустой конструктор нужен Room для создания объекта при загрузке из базы.
    public Address() {
    }

    //4️⃣ Геттеры и сеттеры
    //Методы get... и set... дают доступ к полям:
    //getId(), setId() — работа с ID.
    //getAddress(), setAddress() — работа с адресом.
    //getPhone(), setPhone() — работа с телефоном.
    //getLatitude(), setLatitude() — работа с широтой.
    //getLongitude(), setLongitude() — работа с долготой.
    //getComment(), setComment() — комментарий.
    //getDate(), setDate() — дата.
    //isChecked(), setChecked() — статус выполнения.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
