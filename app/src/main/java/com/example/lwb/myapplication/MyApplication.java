package com.example.lwb.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by lwb on 2017/4/20.
 */

public class MyApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static  Context getContext(){
        return mContext;
    }
}
