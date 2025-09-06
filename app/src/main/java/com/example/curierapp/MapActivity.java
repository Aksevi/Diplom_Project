package com.example.curierapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
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
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.Animation;
import com.yandex.runtime.image.ImageProvider;



import android.os.Handler;
import android.os.Looper;


import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private RoomDB dataBase;
    private List<Address> addressList;
    private FloatingActionButton backBtn;
    private com.yandex.mapkit.map.Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        backBtn = findViewById(R.id.back_btn);
        map = mapView.getMap();

        backBtn.setOnClickListener(v -> finish()); // закрываем активность и возвращаемся в MainActivity

        // Проверка разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        }

        dataBase = RoomDB.getInstance(this);
        addressList = dataBase.mainDAO().getAll();
        if (addressList == null) addressList = new ArrayList<>();

        map.setMapType(MapType.MAP);

        // Изначальная загрузка маркеров
        updateMarkers();
    }

    // Метод для добавления маркеров на карту
    private void updateMarkers() {
        // Сохраняем текущую камеру
        CameraPosition currentCamera = map.getCameraPosition();

        // Очищаем старые маркеры
        map.getMapObjects().clear();

        // Берём свежие данные из базы (а не старый список)
        addressList = dataBase.mainDAO().getAll();

        List<Point> points = new ArrayList<>();

        for (Address addr : addressList) {

            // 👉 Фильтр: показываем только НЕ выполненные
            if (addr.isChecked()) {
                continue; // пропускаем, если адрес выполнен
            }


            Point point = getLocationFromAddress(addr.getAddress());
            if (point != null) {
                Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.metka);
                PlacemarkMapObject placemark = map.getMapObjects().addPlacemark(
                        point,
                        ImageProvider.fromBitmap(markerBitmap)
                );

        for (int i = 0; i < addressList.size(); i++) {
            Address addr = addressList.get(i);

            if (addr.isChecked()) continue; // пропускаем выполненные

            Point point = getLocationFromAddress(addr.getAddress());
            if (point != null) {
                // Создаем Bitmap с номером
                Bitmap markerBitmap = createNumberMarker(i + 1); // i+1 — порядковый номер

                PlacemarkMapObject placemark = map.getMapObjects().addPlacemark(
                        point,
                        ImageProvider.fromBitmap(markerBitmap)
                );

                placemark.setUserData(addr);

                placemark.addTapListener((mapObject, tapPoint) -> {
                    Address tappedAddr = (Address) mapObject.getUserData();
                    if (tappedAddr != null) {
                        Intent intent = new Intent(MapActivity.this, AddressCard.class);
                        intent.putExtra("address_id", tappedAddr.getId());
                        startActivity(intent);
                    }
                    return true;
                });

                points.add(point);
            }
        }

        // Если карта была пустой изначально, центрируем на все точки
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

            double centerLat = (north + south) / 2;
            double centerLon = (east + west) / 2;



            map.move(
                    new CameraPosition(new Point(centerLat, centerLon), 12f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 1f),
                    null
            );
        } else if (currentCamera != null) {
            // Восстанавливаем камеру без изменения масштаба
            map.move(currentCamera, new Animation(Animation.Type.SMOOTH, 0f), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();

        // Обновляем маркеры после возвращения из AddressCard
        updateMarkers();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    private Point getLocationFromAddress(String strAddress) {
        String city = "Вологда"; // город по умолчанию
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
    private Bitmap createNumberMarker(int number) {
        int size = 80; // размер маркера в пикселях
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintCircle = new Paint();
        paintCircle.setColor(Color.RED); // фон кружка
        paintCircle.setAntiAlias(true);
        canvas.drawCircle(size/2, size/2, size/2, paintCircle);

        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE); // цвет цифры
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);

        // корректируем позицию текста по вертикали
        Paint.FontMetrics fm = paintText.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        canvas.drawText(String.valueOf(number), size/2, size/2 + textHeight/4, paintText);

        return bitmap;
    }
}
*/



public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private RoomDB dataBase;
    private List<Address> addressList;
    private FloatingActionButton backBtn;
    private com.yandex.mapkit.map.Map map;

    private PlacemarkMapObject courierPlacemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        backBtn = findViewById(R.id.back_btn);
        map = mapView.getMap();

        backBtn.setOnClickListener(v -> finish());

        // Проверка разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        } else {
            // Если разрешения уже есть, запускаем трекинг
            startCourierTracking();
        }

        dataBase = RoomDB.getInstance(this);
        addressList = dataBase.mainDAO().getAllSorted();
        if (addressList == null) addressList = new ArrayList<>();

        map.setMapType(MapType.MAP);

        // Изначальная загрузка маркеров
        updateMarkersAsync();
    }

    // Асинхронное обновление маркеров
    private void updateMarkersAsync() {
        new Thread(() -> {
            List<Point> points = new ArrayList<>();
            int number = 1; // счётчик меток только для unchecked адресов
            for (Address addr : addressList) {
                if (addr.isChecked()) continue;

                Point point = getLocationFromAddress(addr.getAddress());
                if (point != null) {
                    points.add(point); // собираем точки
                    int markerNumber = number; // сохраняем текущий номер
                    runOnUiThread(() -> addPlacemark(point, addr, markerNumber));
                    number++;
                }
            }

            // Центрируем карту после того, как все точки собраны
            if (!points.isEmpty()) {
                runOnUiThread(() -> moveCameraToMarkers(points)); // <-- вызываем метод с автоматическим zoom
            }
        }).start();
    }



    private void addPlacemark(Point point, Address addr, int number) {
        Bitmap markerBitmap = createNumberMarker(number);
        PlacemarkMapObject placemark = map.getMapObjects().addPlacemark(point, ImageProvider.fromBitmap(markerBitmap));
        placemark.setUserData(addr);
        placemark.addTapListener((mapObject, tapPoint) -> {
            Address tappedAddr = (Address) mapObject.getUserData();
            if (tappedAddr != null) {
                Intent intent = new Intent(MapActivity.this, AddressCard.class);
                intent.putExtra("address_id", tappedAddr.getId());
                startActivity(intent);
            }
            return true;
        });
    }

    private void centerMap(List<Point> points, CameraPosition currentCamera) {
        if (points.isEmpty()) return;

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

        double centerLat = (north + south) / 2;
        double centerLon = (east + west) / 2;

        map.move(
                new CameraPosition(new Point(centerLat, centerLon), 12f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1f),
                null
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        updateMarkersAsync();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    private Point getLocationFromAddress(String strAddress) {
        String city = "Вологда";
        String fullAddress = city + ", " + strAddress;
        String apiKey = "324da223-ae3a-4e6b-921d-8bb19c22b79e";
        String urlStr = "https://geocode-maps.yandex.ru/1.x/?apikey=" + apiKey +
                "&geocode=" + Uri.encode(fullAddress) + "&format=json";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject pos = json.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("Point");

            String[] coords = pos.getString("pos").split(" "); // долгота широта
            double lon = Double.parseDouble(coords[0]);
            double lat = Double.parseDouble(coords[1]);
            return new Point(lat, lon);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP_DEBUG", "Разрешение получено!");
                startCourierTracking(); // запускаем трекинг сразу после разрешения
            } else {
                Log.d("MAP_DEBUG", "Разрешение не получено!");
                Toast.makeText(this, "Нужны разрешения для работы карты", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap createNumberMarker(int number) {
        int size = 80;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintCircle = new Paint();
        paintCircle.setColor(Color.RED);
        paintCircle.setAntiAlias(true);
        canvas.drawCircle(size/2, size/2, size/2, paintCircle);

        Paint paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);

        Paint.FontMetrics fm = paintText.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        canvas.drawText(String.valueOf(number), size/2, size/2 + textHeight/4, paintText);

        return bitmap;
    }

    private void moveCameraToMarkers(List<Point> points) {
        if (points.isEmpty()) return;

        // Находим границы всех точек
        double north = points.get(0).getLatitude(), south = north;
        double east = points.get(0).getLongitude(), west = east;
        for (Point p : points) {
            if (p.getLatitude() > north) north = p.getLatitude();
            if (p.getLatitude() < south) south = p.getLatitude();
            if (p.getLongitude() > east) east = p.getLongitude();
            if (p.getLongitude() < west) west = p.getLongitude();
        }

        // Центр карты
        double centerLat = (north + south) / 2;
        double centerLon = (east + west) / 2;

        // Простейший подбор zoom по размаху точек
        double latDiff = north - south;
        double lonDiff = east - west;
        float zoom = 12f; // базовое значение

        // Чем больше разброс точек, тем меньше zoom
        double maxDiff = Math.max(latDiff, lonDiff);
        if (maxDiff > 0.05) zoom = 10f;
        if (maxDiff > 0.1) zoom = 8f;
        if (maxDiff > 0.5) zoom = 5f;

        map.move(new CameraPosition(new Point(centerLat, centerLon), zoom, 0, 0));
    }

    private void startCourierTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return; // разрешение ещё не получено
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000, // обновление каждые 2 секунды
                2,    // минимум 2 метра
                location -> runOnUiThread(() -> updateCourierMarker(location))
        );
    }

    private void updateCourierMarker(Location location) {
        Point point = new Point(location.getLatitude(), location.getLongitude());

        if (courierPlacemark == null) {
            // создаём маркер при первом обновлении
            Bitmap bitmap = createCourierMarker();
            courierPlacemark = map.getMapObjects().addPlacemark(point, ImageProvider.fromBitmap(bitmap));
        } else {
            // просто сдвигаем маркер
            courierPlacemark.setGeometry(point);
        }

        // можно центрировать карту на курьера (необязательно)
//         map.move(new CameraPosition(point, 15f, 0, 0));
    }

    private Bitmap createCourierMarker() {
        int size = 80;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paintCircle = new Paint();
        paintCircle.setColor(Color.BLUE); // синий курьер
        paintCircle.setAntiAlias(true);
        canvas.drawCircle(size / 2, size / 2, size / 2, paintCircle);

        return bitmap;
    }
}

