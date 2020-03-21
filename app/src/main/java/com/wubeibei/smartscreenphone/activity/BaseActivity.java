package com.wubeibei.smartscreenphone.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.wubeibei.smartscreenphone.util.ActivityCollector;

/**
 * Created by fangju on 2018/11/24
 */
public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Create ",getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Destroy ",getClass().getSimpleName());
        ActivityCollector.removeActivity(this);
    }
}


