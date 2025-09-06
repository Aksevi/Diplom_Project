package com.example.curierapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.curierapp.Adapter.AddressListAdapter;
import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddAddressActivity extends AppCompatActivity {

    EditText editTextAddress, editTextPhone, editTextComment; //поля ввода адреса, телефона, комментария
    ImageView imageViewSave; //иконка для кнопки "Сохранить".
    //    RoomDB db; //объект базы данных.
    Address address; // объект  адреса
    boolean isOldAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // доступ к xml
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextComment = findViewById(R.id.edit_text_comment);
        imageViewSave = findViewById(R.id.image_view_save);

        // Получаем экземпляр базы данных через Singleton (один объект на всё приложение).
//        db = Room.databaseBuilder(getApplicationContext(), RoomDB.class, "CurierApp.db").allowMainThreadQueries().build();
//        db = RoomDB.getInstance(this);

        address = new Address();
        try {
            address = (Address) getIntent().getSerializableExtra("old_address");
            editTextAddress.setText(address.getAddress());
            editTextPhone.setText(address.getPhone());
            editTextComment.setText(address.getComment());
            isOldAddress = true;
        } catch (
                Exception e) { //Если old_note не передан — ловим исключение, и notes остаётся пустой (новая заметка).
            e.printStackTrace();
        }


        //жмем на кнопку «Сохранить»
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressText = editTextAddress.getText().toString().trim(); //Забираем текст из полей и чистим от лишних пробелов.
                String phoneText = editTextPhone.getText().toString().trim();
                String commentText = editTextComment.getText().toString().trim();

                if (addressText.isEmpty()) { //Проверка заполненности
                    Toast.makeText(AddAddressActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                Date date = new Date();

                if (!isOldAddress) { // Если это новая заметка (!isOldNote) — создаём новый объект Notes.
                    address = new Address();
                }

                address.setAddress(addressText);
                address.setPhone(phoneText);
                address.setComment(commentText);
                address.setDate(formatter.format(date));

                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(AddAddressActivity.RESULT_OK, intent);
                finish();
            }
        });
    }
}

/*    //4. saveAddress() — логика сохранения
    private void saveAddress() {
        String addressText = editTextAddress.getText().toString().trim(); //Забираем текст из полей и чистим от лишних пробелов.
        String phoneText = editTextPhone.getText().toString().trim();
        String commentText = editTextComment.getText().toString().trim();

        if (addressText.isEmpty() || phoneText.isEmpty()){ //Проверка заполненности
            Toast.makeText(this, "Enter address or Phone", Toast.LENGTH_SHORT).show();
            return;
        }
    //Создаём объект модели
        Address address = new Address();
        address.setAddress(addressText);
        address.setPhone(phoneText);
        address.setComment(commentText);

        //Генерируем дату и время
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        long timestamp = System.currentTimeMillis(); // вот оно — текущее время;
        String dateString = sdf.format(new Date(timestamp));
        address.setDate(dateString);

        address.setDate(dateString); // дата и время

        //Сохраняем в базу
        db.mainDAO().insert(address); // Запись в базу данных. mainDAO() абстрактный метод в RoomDB.java

        //Сообщение и закрытие
        Toast.makeText(this, "Адрес сохранён", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK); //setResult(RESULT_OK) - это служебный метод дочерней Activity. Он говорит, что эта Activity завершилась успешно и возвращает этот код в родительскую Activity.
        finish(); // Закрыть экран

    }
}*/

/*
//Создаём объект модели
Address address = new Address();
        address.setAddress(addressText);
        address.setPhone(phoneText);
        address.setComment(commentText);

//Генерируем дату и время
SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
long timestamp = System.currentTimeMillis(); // вот оно — текущее время;
String dateString = sdf.format(new Date(timestamp));
        address.setDate(dateString);

        address.setDate(dateString); // дата и время

//Сохраняем в базу
        db.mainDAO().insert(address); // Запись в базу данных. mainDAO() абстрактный метод в RoomDB.java

//Сообщение и закрытие
        Toast.makeText(this, "Адрес сохранён", Toast.LENGTH_SHORT).show();

setResult(RESULT_OK); //setResult(RESULT_OK) - это служебный метод дочерней Activity. Он говорит, что эта Activity завершилась успешно и возвращает этот код в родительскую Activity.
finish(); // Закрыть экран

    }
            }*/
