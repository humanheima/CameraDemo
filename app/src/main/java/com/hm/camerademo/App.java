package com.hm.camerademo;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Application context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    public static Context getInstance() {
        return context;
    }
}
