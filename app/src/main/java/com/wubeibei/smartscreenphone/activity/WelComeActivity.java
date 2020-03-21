package com.wubeibei.smartscreenphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.util.BaseHandler;

public class WelComeActivity extends BaseActivity {
    private final int MSG_WHAT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        mHandler.sendEmptyMessageDelayed(MSG_WHAT, 2000);//延时时间
    }

    @Override
    public void onBackPressed() {
        // 阻止返回键
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    private BaseHandler mHandler = new BaseHandler(this, msg -> {
        if (msg.what == MSG_WHAT) {
            Intent intent = new Intent(WelComeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    });
}
