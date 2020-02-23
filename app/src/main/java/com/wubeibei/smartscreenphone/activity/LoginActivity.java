package com.wubeibei.smartscreenphone.activity;

import android.app.Dialog;
import android.content.Context;
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
import com.wubeibei.smartscreenphone.util.LogUtil;
import com.wubeibei.smartscreenphone.util.PreferencesUtil;
import com.wubeibei.smartscreenphone.util.ScreenAdapter;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

import org.greenrobot.eventbus.Subscribe;

import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_BASE_HEIGHT;
import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_UNIT_DP;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private EditText userNameEt, passWordEt;//用户名、密码输入框
    private Dialog loginDialog = null;
    private boolean isFirst = true;//默认第一次调用该页面
    private boolean isShow = false;//默认不锁屏
    private boolean loginFlag = false;//默认登陆失败

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdapter.match(this, 400, MATCH_BASE_HEIGHT, MATCH_UNIT_DP);
        setContentView(R.layout.login_activity);
        this.mContext = this;
        isFirst = getIntent().getBooleanExtra("isFirst", true);
        userNameEt = findViewById(R.id.login_activity_username_et);
        passWordEt = findViewById(R.id.login_activity_password_et);
        //确认按钮
        Button submitBtn = findViewById(R.id.login_activity_submit_btn);
        submitBtn.setOnClickListener(onClickListener);
        loginDialog = CustomProgress.getDialog(mContext, getResources().getString(R.string.login_loading), false, null);
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
                    if (!loginDialog.isShowing()) {
                        loginDialog.show();
                        JSONObject jsonObject = new JSONObject();
                        JSONObject data = new JSONObject();
                        data.put("password", passWord);
                        jsonObject.put("action", "login");
                        jsonObject.put("data", data);
                        LogUtil.d(TAG,jsonObject.toJSONString());
                        App.getInstance().send(jsonObject.toJSONString());
                    } else {
                        App.showToast(getResources().getString(R.string.network_retry));
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFirst) {//第一次登陆有效
            ActivityCollector.finishAll();
            System.exit(1);
        }
    }

    // 接收Client发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("login")) {
            boolean flag = jsonObject.getBooleanValue("data");
            if (!flag) {
                String msg = jsonObject.getString("msg");
                App.showToast(msg);
            } else {
                toActivity();
            }
        }
    }

    private void toActivity() {
        if (!isFirst) {
            Intent i = new Intent();
            i.putExtra("isShow",isShow);
            i.putExtra("loginFlag", loginFlag);
            setResult(RESULT_OK, i);
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
