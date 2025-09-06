package com.example.curierapp;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey("0e45506f-71a8-4431-8d43-cc279925b01f");
        MapKitFactory.initialize(this);
    }
}
