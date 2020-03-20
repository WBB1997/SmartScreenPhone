package com.wubeibei.smartscreenphone.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.ActivityCollector;
import com.wubeibei.smartscreenphone.util.LogUtil;
import com.wubeibei.smartscreenphone.util.ScreenAdapter;
import com.wubeibei.smartscreenphone.view.CommonDialog;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

import net.qiujuer.genius.kit.handler.Run;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_BASE_HEIGHT;
import static com.wubeibei.smartscreenphone.util.ScreenAdapter.MATCH_UNIT_DP;

public class MainActivity extends BaseActivity{
    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager = null;
    private FragmentTransaction transaction = null;
    private boolean autoDriveModel = false;//默认关闭自动驾驶模式
    public static boolean target = false;//默认没跳转
//    private int currentDriveModel = DRIVE_MODEL_AUTO_AWAIT;//当前驾驶状态默认为待定
//    private int clickDriveModel = DRIVE_MODEL_AUTO_AWAIT;//点击驾驶模式
    public DrawerLayout rightDrawerLayout = null;
    private LinearLayout rightDLClickBtn = null;
    private long firstOnBackClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdapter.match(this, 400, MATCH_BASE_HEIGHT, MATCH_UNIT_DP);
        setContentView(R.layout.main_activity);
        initView();
        //申请相关权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        }
        //有权限的话什么都不做
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //初始化布局
    private void initView() {
        rightDrawerLayout = findViewById(R.id.right_drawerLayout);
        rightDrawerLayout.setScrimColor(Color.TRANSPARENT);
        rightDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//侧滑栏打开手势滑动
        rightDrawerLayout.addDrawerListener(rightDrawerListener);
        rightDLClickBtn = findViewById(R.id.right_drawerLayout_click_btn);
        rightDLClickBtn.setOnClickListener(onClickListener);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.commit();
    }

    // 接收Server发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        LogUtil.d(TAG, jsonObject.toJSONString());
        if (action.equals("message")) {
            int status = jsonObject.getIntValue("status");
            switch (status) {
                // 弹出提示框，是否重新登录或者退出程序
                case 0:
                    Run.onUiSync(() ->showDialog(MainActivity.this, jsonObject.getString("msg")));
                    break;
                default:
                    break;
            }
        }
    }

    //弹出对话框
    private void showDialog(@NonNull Context context, @NonNull String str) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setMessage(str).setNegtive("重新登录")
                .setPositive("退出")
                .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                App.showToast("退出");
            }

            @Override
            public void onNegtiveClick() {
                App.showToast("重新登陆");
            }
        }).show();
    }

    /**
     * 对特定Fragment进行隐藏或显示
     * @param fragment Fragment
     * @param flag     是否隐藏
     */
    private void hideFragment(Fragment fragment, boolean flag) {
        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (flag) {
                transaction.hide(fragment);
            } else {
                transaction.show(fragment);
            }
            transaction.commit();
        }
    }

    // 右边弹出模块
    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        public void onSingleClick(View v) {
            rightDrawerLayout.openDrawer(GravityCompat.END);
        }
    };

    private DrawerLayout.SimpleDrawerListener rightDrawerListener = new DrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {
            if (v == 0) {
                rightDLClickBtn.setVisibility(View.VISIBLE);
            } else {
                rightDLClickBtn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onDrawerClosed(@NonNull View view) {
            rightDLClickBtn.setVisibility(View.VISIBLE);
        }
    };

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

    // 开启权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    App.showToast("权限开启成功");
                } else {
                    ActivityCollector.finishAll();
                    System.exit(1);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
