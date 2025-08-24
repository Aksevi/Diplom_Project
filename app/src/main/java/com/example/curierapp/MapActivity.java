package com.example.curierapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.Animation;
import com.yandex.runtime.image.ImageProvider;


import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private RoomDB dataBase;
    private List<Address> addressList;
    private FloatingActionButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey("0e45506f-71a8-4431-8d43-cc279925b01f");
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapview);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Проверка разрешений для локации
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }

        // Загрузка адресов из БД
        dataBase = RoomDB.getInstance(this);
        addressList = dataBase.mainDAO().getAll();
        if (addressList == null) addressList = new ArrayList<>();

        com.yandex.mapkit.map.Map map = mapView.getMap();
        List<Point> points = new ArrayList<>();

        // Добавляем маркеры и собираем точки для BoundingBox
        for (Address addr : addressList) {
            Point point = getLocationFromAddress(addr.getAddress());
            Log.d("MAP_DEBUG", addr.getAddress() + " -> " + point); // <-- лог
            if (point != null) {
                map.getMapObjects().addPlacemark(
                        point,
                        com.yandex.runtime.image.ImageProvider.fromResource(this, R.drawable.dot_marker_big)
                );
                points.add(point);
            }
        }

        // Центрируем карту на все точки
        if (!points.isEmpty()) {
            double north = points.get(0).getLatitude();
            double south = points.get(0).getLatitude();
            double east = points.get(0).getLongitude();
            double west = points.get(0).getLongitude();

            for (Point p : points) {
                if (p.getLatitude() > north) north = p.getLatitude();
                if (p.getLatitude() < south) south = p.getLatitude();
                if (p.getLongitude() > east) east = p.getLongitude();
                if (p.getLongitude() < west) west = p.getLongitude();
            }

            // Рассчитываем центр
            double centerLat = (north + south) / 2;
            double centerLon = (east + west) / 2;

            // Ставим камеру на центр
            map.move(
                    new CameraPosition(new Point(centerLat, centerLon), 10f, 0.0f, 0.0f), // 10f — пример масштаба
                    new Animation(Animation.Type.SMOOTH, 1f),
                    null
            );
        }
    }


    // --- метод для получения координат из адреса ---
    private Point getLocationFromAddress(String strAddress) {
        String city = "Вологда"; // или город по умолчанию
        String fullAddress = city + ", " + strAddress;


        android.location.Geocoder coder = new android.location.Geocoder(this);
        try {
            List<android.location.Address> list = coder.getFromLocationName(fullAddress, 1);
            if (list == null || list.isEmpty()) return null;

            android.location.Address location = list.get(0);
            return new Point(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP_DEBUG", "Разрешение получено!");
            } else {
                Log.d("MAP_DEBUG", "Разрешение не получено!");
                Toast.makeText(this, "Нужны разрешения для работы карты", Toast.LENGTH_LONG).show();
            }
        }
    }

}