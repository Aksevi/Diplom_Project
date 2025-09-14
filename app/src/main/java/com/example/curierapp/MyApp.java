package com.example.curierapp;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey("my_apikey");
        MapKitFactory.initialize(this);
    }
}
