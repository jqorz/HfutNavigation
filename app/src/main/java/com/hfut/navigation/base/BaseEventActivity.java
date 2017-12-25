package com.hfut.navigation.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

public abstract class BaseEventActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    protected void getIntentData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    protected abstract void init();

    protected abstract int getLayoutResId();


}
