package com.hm.camerademo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/1/5.
 */
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
