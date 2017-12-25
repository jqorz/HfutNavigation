package com.hfut.navigation.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());//加载so文件
    }
}
