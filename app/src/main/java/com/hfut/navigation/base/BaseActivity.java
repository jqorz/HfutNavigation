package com.hfut.navigation.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        init();
    }

    protected void getIntentData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    protected abstract void init();

    protected abstract int getLayoutResId();


}
