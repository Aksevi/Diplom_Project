package com.example.curierapp.DataBase;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.curierapp.Models.Address;
import com.example.curierapp.Models.Statistics;

//RoomDB — это класс, который создаёт и настраивает базу данных. Он следит, чтобы в приложении была только одна база и отдаёт тебе MainDAO, через который ты работаешь с таблицей Address.
    //1️⃣ Аннотация @Database для Room, чтобы понять, какие таблицы включить в БД. entities = Address.class — указываем, что в базе будет одна таблица — Address. version = 1 — это версия базы, пригодится при миграциях (например, если ты позже добавишь новое поле в таблицу). exportSchema = false — отключает экспорт схемы базы как файла (вполне нормально для небольших проектов).
@Database(entities = {Address.class, Statistics.class}, version = 3, exportSchema = false)
    //2️⃣ Наследование от RoomDatabase. RoomDatabase — это абстрактный класс из библиотеки Room.
public abstract class RoomDB extends RoomDatabase {

    // 3️⃣ Поля класса
    private  static RoomDB database; // database — singleton-экземпляр  базы (один на всё приложение)
    public static final String DATABASE_NAME = "CurierApp.db";//DATABASE_NAME — имя файла, в котором хранится твоя база (CurierApp.db)

    // 4️⃣ Метод getInstance(...) — фабрика базы — она гарантирует, что база создаётся только один раз, даже если ты сто раз вызовешь getInstance(...)
    public static synchronized  RoomDB getInstance(Context context){

        if (database == null){ //Если базы ещё нет — создаём её с помощью Room.databaseBuilder(...)
            // Сам билд базы:
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration(true).build();//context.getApplicationContext() — даёт Room глобальный контекст, чтобы база не протекала памятью. RoomDB.class — указываем, что база будет собрана по этому классу. DATABASE_NAME — имя файла с базой.  allowMainThreadQueries() — разрешает делать запросы к базе на главном потоке. fallbackToDestructiveMigration(true) — если ты поменяешь структуру таблицы, а миграцию не пропишешь, база просто удалится и создастся заново .build() — создаёт объект базы.
        }
        return database;
    }
    //5️⃣ Доступ к DAO абстрактный метод, который говорит Room: Эй, когда я попрошу DAO — верни мне реализацию MainDAO, связанную с таблицей Address.” Room сам сгенерирует  нужный код при компиляции.
    public abstract MainDAO mainDAO();

    public abstract StatisticsDAO statisticsDao(); // добавил  для таблицы  статистики.

}


