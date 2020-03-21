package com.wubeibei.smartscreenphone.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.dialog.CustomProgress;
import com.wubeibei.smartscreenphone.util.ActivityCollector;
import com.wubeibei.smartscreenphone.util.ScreenAdapter;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_BASE_HEIGHT;
import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_UNIT_DP;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private EditText passWordEt;//用户名、密码输入框
    private Dialog loginDialog = null;
    private long firstOnBackClickTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdapter.match(this, 400, MATCH_BASE_HEIGHT, MATCH_UNIT_DP);
        setContentView(R.layout.login_activity);
        passWordEt = findViewById(R.id.login_activity_password_et);
        //确认按钮
        Button submitBtn = findViewById(R.id.login_activity_submit_btn);
        submitBtn.setOnClickListener(onClickListener);
        loginDialog = CustomProgress.getDialog(this, getResources().getString(R.string.login_loading), true, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.login_activity_submit_btn: {
                    //密码
                    String passWord = passWordEt.getText().toString().trim();
                    if (TextUtils.isEmpty(passWord)) {
                        App.showToast("密码不能为空！");
                        return;
                    }
                    // 如果网络未连接
                    if (App.getInstance().isConnection()) {
                        // 打开加载界面
                        loginDialog.show();
                        // 构造发送的数据
                        JSONObject jsonObject = new JSONObject();
                        JSONObject data = new JSONObject();
                        data.put("password", passWord);
                        jsonObject.put("action", "login");
                        jsonObject.put("data", data);
                        // 发送
                        App.getInstance().send(jsonObject.toJSONString());
                    }else {
                        App.getInstance().connect();
                        App.showToast("网络连接错误，请稍后再试！");
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        long secondOnBackClickTime = System.currentTimeMillis();
        if (secondOnBackClickTime - firstOnBackClickTime > 2000) {
            App.showToast("再按一次返回键退出");
            firstOnBackClickTime = System.currentTimeMillis();
        } else {
            ActivityCollector.finishAll();
            System.exit(1);
        }
    }

    // 接收服务器发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("login")) {
            boolean flag = jsonObject.getBooleanValue("data");
            // 登录失败
            if (!flag) {
                String msg = jsonObject.getString("msg");
                App.showToast(msg);
                if(loginDialog.isShowing())
                    loginDialog.cancel();
                // 登录成功
            } else {
                if (loginDialog.isShowing()) {
                    toActivity();
                    App.getInstance().setLogin(true);
                    loginDialog.cancel();
                }
            }
        }
    }

    private void toActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
