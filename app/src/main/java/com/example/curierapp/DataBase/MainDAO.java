package com.example.curierapp.DataBase;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.curierapp.Models.Address;

import java.util.List;

//1️⃣ @Dao — аннотация Room, которая говорит: "Этот интерфейс — это контракт с базой данных".
//Тут мы описываем только методы, Room сам сгенерирует реализацию.
@Dao
public interface MainDAO {

    //2️⃣ Добавление записи. Принимает Address — это наша модель адреса, скорее всего помеченная @Entity.
    @Insert(onConflict = REPLACE) //onConflict = REPLACE — если в таблице уже есть запись с таким же id, она будет заменена (удобно для обновлений).
    void insert (Address address); //@Insert — говорит Room: "Добавь этот объект в таблицу".

    // 3️⃣ Получение всех записей
    @Query("SELECT * FROM address ORDER BY id DESC") //@Query — пишем обычный SQL-запрос. "SELECT * FROM address" — достаём все поля из таблицы address. "ORDER BY id DESC" — сортируем по id от большего к меньшему (новые записи сверху).
    List<Address> getAll(); //Возвращаем список объектов Address.

    // 4️⃣ Обновление записи
    @Query("UPDATE address SET address = :address, phone = :phone, comment = :comment, date = :date, checked = :checked WHERE id = :id") //Обновляем сразу несколько полей: address, comment, date, checked. :address и остальные — это параметры метода. Room подставит их в запрос. WHERE id = :id — обновляем только ту запись, у которой id совпадает.
    void update(String address, String phone, String comment, String date, boolean checked,  int id);

    // 5️⃣ Удаление записи. @Delete — Room удалит переданный объект из базы. Главное, чтобы в Address был указан id — по нему идёт удаление.
    @Delete
    void delete(Address address);
}
