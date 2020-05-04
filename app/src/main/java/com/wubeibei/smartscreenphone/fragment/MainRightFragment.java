package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.App;
import com.wubeibei.smartscreenphone.activity.MainActivity;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.LogUtil;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

import net.qiujuer.genius.kit.handler.Run;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.wubeibei.smartscreenphone.command.MessageName.HMI;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_ACBlowingLevel;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_DemisterStatus;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_DangerAlarmLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_FrontFogLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_HighBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_LeftTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_LowBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_RearFogLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_RightTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_DangerAlarmLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_Demister_Control;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_FANPWM_Control;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_FrontFogLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_HighBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_LeftTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_LoWBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_RearFogLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_RightTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_air_grade;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_air_model;
import static com.wubeibei.smartscreenphone.command.SignalName.VCU_ACWorkingStatus;

public class MainRightFragment extends Fragment {
    private static final String TAG = "MainRightFragment";
    private MainActivity activity;
    private ImageButton lowbeamBtn;//近光灯
    private ImageButton highbeamBtn;//远光灯
    private ImageButton frontFogLightBtn;//前雾灯
    private ImageButton backFogLightBtn;//后雾灯
    private ImageButton errorLightBtn;//警示灯
    private ImageButton leftLightBtn;//左转向灯
    private ImageButton rightLightBtn;//右转向灯
    private Button autoLightBtn;//自动灯
    private ImageButton coolAirBtn;//冷气
    private ImageButton hotAirBtn;//暖气
    private ImageButton offAirBtn;//关闭
    private ImageButton deFogBtn;//除雾
    private SeekBar seekBar;//滑块
    private View rightFragmentDlClickBtn;
    private int field = -1;//属性
    private Object o = null;//状态
    private boolean typeFlag = false;//判断是否改变状态
    private int seekBarIndex = 0;//挡位大小
    private int finalProgress = 0;//最终大小
    private RelativeLayout rightLayout;


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_right_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rightLayout = view.findViewById(R.id.main_activity_right_layout);
        rightLayout.getBackground().setAlpha(200);
        lowbeamBtn = view.findViewById(R.id.rightFragment_lowBeam);
        lowbeamBtn.setOnClickListener(onClickListener);
        highbeamBtn = view.findViewById(R.id.rightFragment_highBeam);
        highbeamBtn.setOnClickListener(onClickListener);
        frontFogLightBtn = view.findViewById(R.id.rightFragment_frontFogLight);
        frontFogLightBtn.setOnClickListener(onClickListener);
        backFogLightBtn = view.findViewById(R.id.rightFragment_backFogLight);
        backFogLightBtn.setOnClickListener(onClickListener);
        errorLightBtn = view.findViewById(R.id.rightFragment_errorLight);
        errorLightBtn.setOnClickListener(onClickListener);
        leftLightBtn = view.findViewById(R.id.rightFragment_leftLight);
        leftLightBtn.setOnClickListener(onClickListener);
        rightLightBtn = view.findViewById(R.id.rightFragment_rightLight);
        rightLightBtn.setOnClickListener(onClickListener);
        autoLightBtn = view.findViewById(R.id.rightFragment_autoLight);
        autoLightBtn.setOnClickListener(onClickListener);
        coolAirBtn = view.findViewById(R.id.rightFragment_coolAir);
        coolAirBtn.setOnClickListener(onClickListener);
        hotAirBtn = view.findViewById(R.id.rightFragment_hotAir);
        hotAirBtn.setOnClickListener(onClickListener);
        offAirBtn = view.findViewById(R.id.rightFragment_offAir);
        offAirBtn.setOnClickListener(onClickListener);
        offAirBtn.setActivated(true);
        deFogBtn = view.findViewById(R.id.rightFragment_deFog);
        deFogBtn.setOnClickListener(onClickListener);
        seekBar = view.findViewById(R.id.rightFragment_seekBar);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar.setOnTouchListener(onTouchListener);
        rightFragmentDlClickBtn = view.findViewById(R.id.rightFragment_dl_clickBtn);
        rightFragmentDlClickBtn.setOnClickListener(onClickListener);
    }

    /**
     * 滑动条事件监听
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtil.d(TAG, String.valueOf(progress));
            if (fromUser) {
                if (progress >= 0 && progress < 10) {
                    seekBarIndex = 0;
                    finalProgress = 0;
                } else if (progress >= 10 && progress < 30) {
                    seekBarIndex = 1;
                    finalProgress = 20;
                } else if (progress >= 30 && progress < 50) {
                    seekBarIndex = 2;
                    finalProgress = 40;
                } else if (progress >= 50 && progress < 70) {
                    seekBarIndex = 3;
                    finalProgress = 60;
                } else if (progress >= 70 && progress < 90) {
                    seekBarIndex = 4;
                    finalProgress = 80;
                } else if (progress >= 90 && progress <= 100) {
                    seekBarIndex = 5;
                    finalProgress = 100;
                }
                //发送最终数据至CAN(1-5档)
                App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_grade.toString(), seekBarIndex);
                // 风扇PWM占比控制信号
                App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_FANPWM_Control.toString(), progress);
                // 一起发送
                App.getInstance().sendCommand(HMI.toString());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(finalProgress);
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            activity.rightDrawerLayout.requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        protected void onSingleClick(View v) {
            switch (v.getId()) {
                // 划出右面板
                case R.id.rightFragment_dl_clickBtn: {
                    activity.rightDrawerLayout.closeDrawer(GravityCompat.END);
                    break;
                }
                case R.id.rightFragment_leftLight: {//左转
                    if (!leftLightBtn.isActivated() && rightLightBtn.isActivated()) {
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_RightTurningLamp.toString(), 1);
                    }
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_LeftTurningLamp.toString(), transInt(!leftLightBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_rightLight: {//右转
                    if (!rightLightBtn.isActivated() && leftLightBtn.isActivated()) {
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_LeftTurningLamp.toString(), 1);
                    }
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_RightTurningLamp.toString(), transInt(!rightLightBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_lowBeam: {//近光灯
                    if (!lowbeamBtn.isActivated() && highbeamBtn.isActivated()) {//点击后近光灯为开
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_HighBeam.toString(), 1);
                    }
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_LoWBeam.toString(), transInt(!lowbeamBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_highBeam: {//远光灯
                    if (!highbeamBtn.isActivated() && lowbeamBtn.isActivated()) {//点击后远光灯为开
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_LoWBeam.toString(), 1);
                    }
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_HighBeam.toString(), transInt(!highbeamBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_frontFogLight: {//前雾灯开关
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_FrontFogLamp.toString(), transInt(!frontFogLightBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_backFogLight: {//后雾灯
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_RearFogLamp.toString(), transInt(!backFogLightBtn.isActivated()) + 1);
                    break;
                }
                case R.id.rightFragment_coolAir: {//冷气
                    coolAirBtn.setActivated(!coolAirBtn.isActivated());
                    if (coolAirBtn.isActivated()) {//冷气为开
                        seekBar.setEnabled(true);
                        hotAirBtn.setActivated(false);
                        offAirBtn.setActivated(false);
                        // 如果开冷气，那么要发送开冷气信号
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_model.toString(), 0);
                    }
                    if (!coolAirBtn.isActivated() && !hotAirBtn.isActivated()) {//冷气暖气为关
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_model.toString(), 2);
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_grade.toString(), 0);
                        offAirBtn.setActivated(true);
                        seekBar.setEnabled(false);
                        seekBar.setProgress(0);
                    }
                    break;
                }
                case R.id.rightFragment_hotAir: {//暖气
                    hotAirBtn.setActivated(!hotAirBtn.isActivated());
                    if (hotAirBtn.isActivated()) {//暖气为开
                        seekBar.setEnabled(true);
                        coolAirBtn.setActivated(false);
                        offAirBtn.setActivated(false);
                        // 如果开暖气，那么要发送开暖气信号
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_model.toString(), transInt(hotAirBtn.isActivated()));
                    }

                    if (!coolAirBtn.isActivated() && !hotAirBtn.isActivated()) {//冷气暖气都为关
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_model.toString(), 2);
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_grade.toString(), 0);
                        offAirBtn.setActivated(true);
                        seekBar.setEnabled(false);
                        seekBar.setProgress(0);
                    }
                    break;
                }
                case R.id.rightFragment_offAir: {//关闭
                    if (!offAirBtn.isActivated()) {
                        offAirBtn.setActivated(true);
                        coolAirBtn.setActivated(false);
                        hotAirBtn.setActivated(false);
                        seekBar.setEnabled(false);
                        seekBar.setProgress(0);
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_model.toString(), 2);
                        App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_air_grade.toString(), 0);
                    }
                    break;
                }
//                case R.id.rightFragment_deFog: {//除雾
//                    deFogBtn.setActivated(!deFogBtn.isActivated());
//                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_Demister_Control.toString(), transInt(deFogBtn.isActivated()) + 1);
//                    break;
//                }
                case R.id.rightFragment_errorLight: {//双闪
                    App.getInstance().modifyCommand(HMI.toString(), HMI_Dig_Ord_DangerAlarmLamp.toString(), transInt(!errorLightBtn.isActivated()) + 1);
                    break;
                }
            }
            App.getInstance().sendCommand(HMI.toString());
        }
    };


    // 接收Server发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("instruction")) {
            JSONObject data = jsonObject.getJSONObject("data");
            String signal = data.getString("signal_name");
            Run.onUiSync(()->{
                if (signal.equals(BCM_Flg_Stat_LeftTurningLamp.toString())) {//左转
                    int value = (int) data.getDoubleValue("value");
                    leftLightBtn.setActivated(value == 1);
                    if (value == 1) {
                        rightLightBtn.setActivated(false);
                    }
                } else if (signal.equals(BCM_Flg_Stat_RightTurningLamp.toString())) {//右转
                    int value = (int) data.getDoubleValue("value");
                    rightLightBtn.setActivated(value == 1);
                    if (value == 1) {
                        leftLightBtn.setActivated(false);
                    }
                } else if (signal.equals(BCM_Flg_Stat_HighBeam.toString())) {//远光灯
                    int value = (int) data.getDoubleValue("value");
                    highbeamBtn.setActivated(value == 1);
                    if (value == 1) {
                        lowbeamBtn.setActivated(false);
                    }
                } else if (signal.equals(BCM_Flg_Stat_LowBeam.toString())) {//近光灯
                    int value = (int) data.getDoubleValue("value");
                    lowbeamBtn.setActivated(value == 1);
                    if (value == 1) {
                        highbeamBtn.setActivated(false);
                    }
                } else if (signal.equals(BCM_Flg_Stat_RearFogLamp.toString())) {//后雾灯
                    int value = (int) data.getDoubleValue("value");
                    backFogLightBtn.setActivated(value == 1);
                } else if (signal.equals(BCM_Flg_Stat_FrontFogLamp.toString())) {//前雾灯
                    int value = (int) data.getDoubleValue("value");
                    frontFogLightBtn.setActivated(value == 1);
                } else if (signal.equals(BCM_Flg_Stat_DangerAlarmLamp.toString())) {// 双闪
                    int value = (int) data.getDoubleValue("value");
                    errorLightBtn.setActivated(value == 1);
                } else if (signal.equals(BCM_DemisterStatus.toString())) {//除雾状态
                    int value = (int) data.getDoubleValue("value");
                    deFogBtn.setActivated(value == 1);
                } else if (signal.equals(BCM_ACBlowingLevel.toString())) {//空调风量档位
                    int value = (int) data.getDoubleValue("value");//接收档位
                    if (value != 6) {
                        seekBarIndex = value;//当前档位
                        seekBar.setProgress(seekBarIndex * 20);//设置滑动条
                    }
                } else if (signal.equals(VCU_ACWorkingStatus.toString())) {
                    int value = (int) data.getDoubleValue("value");
                    switch (value) {
                        case 0: //制冷
                            coolAirBtn.setActivated(true);
                            hotAirBtn.setActivated(false);
                            offAirBtn.setActivated(false);
                            break;
                        case 1: //制热
                            coolAirBtn.setActivated(false);
                            hotAirBtn.setActivated(true);
                            offAirBtn.setActivated(false);
                            break;
                        case 2: //均关闭
                            coolAirBtn.setActivated(false);
                            hotAirBtn.setActivated(false);
                            offAirBtn.setActivated(true);
                            seekBar.setEnabled(false);
                            break;
                    }
                }
            });

        }

    }

    //将boolean转换成int
    private int transInt(boolean flag) {
        if (flag) {
            return 1;
        }
        return 0;
    }
}
