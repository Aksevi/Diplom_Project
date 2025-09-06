package com.example.curierapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

public class AddressCard extends AppCompatActivity {

    TextView addressText, phoneText, commentText;
    Button backBtn, ordersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_card);

        addressText = findViewById(R.id.address_text);
        phoneText = findViewById(R.id.phone_text);
        commentText = findViewById(R.id.comment_text);
        backBtn = findViewById(R.id.back_btn);
        ordersBtn = findViewById(R.id.orders_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent intent = new Intent(AddressCard.this, MapActivity.class);
                startActivity(intent);*/
                finish();
            }
        });

        ordersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressCard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Получаем ID адреса из Intent
        int addressId = getIntent().getIntExtra("address_id", -1); // "address_id" см в MapActivity
        if (addressId != -1) {
            loadAddress(addressId); // метод загрузки данных ниже
        }
    }

    private void loadAddress(int id) {

        RoomDB dataBase = RoomDB.getInstance(this); // Получаем доступ к базе

        // Берём объект Address по ID
        Address address = dataBase.mainDAO().getById(id); // добавить метод getById в DAO

        if (address != null) {
            addressText.setText(address.getAddress());
            phoneText.setText(address.getPhone());
            commentText.setText(address.getComment());
        }

        // обрабатываем нажатие на номер телефона для звонка
        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL); //Открывает приложение звонков с уже подставленным номером.
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });
    }
}





