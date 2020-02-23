package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
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
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.MainActivity;
import com.wubeibei.smartscreenphone.util.BaseHandler;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

public class MainRightFragment extends Fragment {
    private static final String TAG = "MainRightFragment";
    public final static int RIGHT_FRAGMENT = 3;
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
    //发送CAN总线的数据
//    private int canType = HOST_TO_CAN;
    private String clazz = "HMI";//所属类名
    private int field = -1;//属性
    private Object o = null;//状态
    private boolean typeFlag = false;//判断是否改变状态
    private int seekBarIndex = 0;//挡位大小
    private int finalProgress = 0;//最终大小
    private RelativeLayout rightLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
//        if (activity != null) {
//            activity.setHandler(RIGHT_FRAGMENT, rightHandler);
//        }
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
//        lowbeamBtn.setEnabled(false);
//        lowbeamBtn.setOnClickListener(onClickListener);
        highbeamBtn = view.findViewById(R.id.rightFragment_highBeam);
//        highbeamBtn.setOnClickListener(onClickListener);
//        highbeamBtn.setEnabled(false);
        frontFogLightBtn = view.findViewById(R.id.rightFragment_frontFogLight);
//        frontFogLightBtn.setOnClickListener(onClickListener);
        backFogLightBtn = view.findViewById(R.id.rightFragment_backFogLight);
//        backFogLightBtn.setOnClickListener(onClickListener);
        errorLightBtn = view.findViewById(R.id.rightFragment_errorLight);
//        errorLightBtn.setOnClickListener(onClickListener);
        leftLightBtn = view.findViewById(R.id.rightFragment_leftLight);
//        leftLightBtn.setOnClickListener(onClickListener);
        rightLightBtn = view.findViewById(R.id.rightFragment_rightLight);
//        rightLightBtn.setOnClickListener(onClickListener);
        autoLightBtn = (Button) view.findViewById(R.id.rightFragment_autoLight);
//        autoLightBtn.setOnClickListener(onClickListener);
        coolAirBtn = view.findViewById(R.id.rightFragment_coolAir);
//        coolAirBtn.setOnClickListener(onClickListener);
        hotAirBtn = view.findViewById(R.id.rightFragment_hotAir);
//        hotAirBtn.setOnClickListener(onClickListener);
        offAirBtn = view.findViewById(R.id.rightFragment_offAir);
//        offAirBtn.setOnClickListener(onClickListener);
        offAirBtn.setActivated(true);
        deFogBtn = (ImageButton) view.findViewById(R.id.rightFragment_deFog);
//        deFogBtn.setOnClickListener(onClickListener);
        seekBar = view.findViewById(R.id.rightFragment_seekBar);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBar.setOnTouchListener(onTouchListener);
        rightFragmentDlClickBtn = (View) view.findViewById(R.id.rightFragment_dl_clickBtn);
//        rightFragmentDlClickBtn.setOnClickListener(onClickListener);
    }

    /**
     * 滑动条事件监听
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            LogUtil.d(TAG,String.valueOf(progress));
//            if (fromUser) {
//                if (progress >= 0 && progress < 10) {
//                    seekBarIndex = AIR_GRADE_OFF;
//                    finalProgress = 0;
//                } else if (progress >= 10 && progress < 30) {
//                    seekBarIndex = AIR_GRADE_FIRST_GEAR;
//                    finalProgress = 20;
//                } else if (progress >= 30 && progress < 50) {
//                    seekBarIndex = AIR_GRADE_SECOND_GEAR;
//                    finalProgress = 40;
//                } else if (progress >= 50 && progress < 70) {
//                    seekBarIndex = AIR_GRADE_THIRD_GEAR;
//                    finalProgress = 60;
//                } else if (progress >= 70 && progress < 90) {
//                    seekBarIndex = AIR_GRADE_FOURTH_GEAR;
//                    finalProgress = 80;
//                } else if (progress >= 90 && progress <= 100) {
//                    seekBarIndex = AIR_GRADE_FIVE_GEAR;
//                    finalProgress = 100;
//                }
//                //发送最终数据至CAN(1-5档)
//                activity.sendToCAN(canType, clazz, HMI_Dig_Ord_air_grade, seekBarIndex);
//                // 风扇PWM占比控制信号
//                activity.sendToCAN(canType, clazz, HMI_Dig_Ord_FANPWM_Control, progress);
//            }
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
//            switch (v.getId()) {
//                case R.id.rightFragment_dl_clickBtn: {
//                    activity.rightDrawerLayout.closeDrawer(Gravity.END);
//                    break;
//                }
//                case R.id.rightFragment_lowBeam: {//近光灯
//                    lowbeamBtn.setActivated(!lowbeamBtn.isActivated());
//                    if (lowbeamBtn.isActivated()) {//点击后近光灯为开
//                        highbeamBtn.setActivated(false);//远光灯关闭
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_LowBeam;
//                    o = transInt(lowbeamBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_highBeam: {//远光灯
//                    highbeamBtn.setActivated(!highbeamBtn.isActivated());
//                    if (highbeamBtn.isActivated()) {//点击后远光灯为开
//                        lowbeamBtn.setActivated(false);//近光灯关闭
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_HighBeam;
//                    o = transInt(highbeamBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_frontFogLight: {//前雾灯开关
//                    frontFogLightBtn.setActivated(!frontFogLightBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_backFogLight: {//后雾灯
//                    backFogLightBtn.setActivated(!backFogLightBtn.isActivated());
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_RearFogLamp;
//                    o = transInt(backFogLightBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_leftLight: {//左转
//                    leftLightBtn.setActivated(!leftLightBtn.isActivated());
//                    if (leftLightBtn.isActivated()) {
//                        rightLightBtn.setActivated(false);
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_LeftTurningLamp;
//                    o = transInt(leftLightBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_rightLight: {//右转
//                    rightLightBtn.setActivated(!rightLightBtn.isActivated());
//                    if (rightLightBtn.isActivated()) {
//                        leftLightBtn.setActivated(false);
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_RightTurningLamp;
//                    o = transInt(rightLightBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_autoLight: {//自动灯
//                    autoLightBtn.setActivated(!autoLightBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_coolAir: {//冷气
//                    coolAirBtn.setActivated(!coolAirBtn.isActivated());
//                    if (coolAirBtn.isActivated()) {//冷气为开
//                        seekBar.setEnabled(true);
//                        hotAirBtn.setActivated(false);
//                        offAirBtn.setActivated(false);
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_air_model;
//                    o = AIR_MODEL_COOL;//制冷模式
//                    if (!coolAirBtn.isActivated() && !hotAirBtn.isActivated()) {//冷气暖气为关
//                        o = ARI_MODEL_CLOSE;//关闭
//                        seekBarIndex = AIR_GRADE_OFF;
//                        offAirBtn.setActivated(true);
//                        seekBar.setEnabled(false);
//                        seekBar.setProgress(0);
//                    }
//                    break;
//                }
//                case R.id.rightFragment_hotAir: {//暖气
//                    hotAirBtn.setActivated(!hotAirBtn.isActivated());
//                    if (hotAirBtn.isActivated()) {//冷气为开
//                        seekBar.setEnabled(true);
//                        coolAirBtn.setActivated(false);
//                        offAirBtn.setActivated(false);
//                    }
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_air_model;
//                    o = AIR_MODEL_COOL;//制冷模式
//                    if (!coolAirBtn.isActivated() && !hotAirBtn.isActivated()) {//冷气暖气为关
//                        o = ARI_MODEL_CLOSE;//关闭
//                        seekBarIndex = AIR_GRADE_OFF;
//                        offAirBtn.setActivated(true);
//                        seekBar.setEnabled(false);
//                        seekBar.setProgress(0);
//                    }
//                    break;
//                }
//                case R.id.rightFragment_offAir: {//关闭
//                    if (!offAirBtn.isActivated()) {
//                        offAirBtn.setActivated(true);
//                        coolAirBtn.setActivated(false);
//                        hotAirBtn.setActivated(false);
//                        seekBar.setEnabled(false);
//                        seekBar.setProgress(0);
//                        typeFlag = true;
//                        seekBarIndex = AIR_GRADE_OFF;
//                        field = HMI_Dig_Ord_air_model;//空调模式
//                        o = ARI_MODEL_CLOSE;//关闭
//                    }
//                    break;
//                }
//                case R.id.rightFragment_deFog: {//除雾
//                    deFogBtn.setActivated(!deFogBtn.isActivated());
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_Demister_Control;//除雾控制
//                    o = transInt(deFogBtn.isActivated());
//                    break;
//                }
//                case R.id.rightFragment_errorLight: {//双闪
//                    errorLightBtn.setActivated(!errorLightBtn.isActivated());
//                    typeFlag = true;
//                    field = HMI_Dig_Ord_DangerAlarm;
//                    o = transInt(errorLightBtn.isActivated());
//                    break;
//                }
//            }
//            if (typeFlag) {//如果按钮被点击（有效）
//                activity.sendToCAN(canType, clazz, field, o);
//                if (field == HMI_Dig_Ord_air_model) {//如果当前是空调模式
//                    activity.sendToCAN(canType, clazz, HMI_Dig_Ord_air_grade, seekBarIndex);//档位
//                    if (ARI_MODEL_CLOSE == (int) o) {//空调关闭
//                        // 风扇PWM占比控制信号
//                        activity.sendToCAN(canType, clazz, HMI_Dig_Ord_FANPWM_Control, 0);
//                    }
//                }
//                typeFlag = false;
//            }
        }
    };

    /**
     * 更新界面
     *
     * @param object 数据
     */
    public void refresh(JSONObject object) {
        int id = object.getIntValue("id");
        Object data = object.get("data");
//        switch (id) {
//            case BCM_Flg_Stat_LeftTurningLamp: {//左转
//                boolean flag = (boolean) data;
//                leftLightBtn.setActivated(flag);
//                if (flag) {
//                    rightLightBtn.setActivated(false);
//                }
//                break;
//            }
//            case BCM_Flg_Stat_RightTurningLamp: {//右转
//                boolean flag = (boolean) data;
//                rightLightBtn.setActivated(flag);
//                if (flag) {
//                    leftLightBtn.setActivated(false);
//                }
//                break;
//            }
//            case BCM_Flg_Stat_HighBeam: {//远光灯
//                boolean flag = (boolean) data;
//                highbeamBtn.setActivated(flag);
//                if (flag) {
//                    lowbeamBtn.setActivated(false);
//                }
//                break;
//            }
//            case BCM_Flg_Stat_LowBeam: {//近光灯
//                boolean flag = (boolean) data;
//                lowbeamBtn.setActivated(flag);
//                if (flag) {
//                    highbeamBtn.setActivated(false);
//                }
//                break;
//            }
//            case BCM_Flg_Stat_RearFogLamp: {//后雾灯
//                boolean flag = (boolean) data;
//                backFogLightBtn.setActivated(flag);
//                break;
//            }
//            case BCM_Flg_Stat_DangerAlarmLamp: {// 双闪
//                boolean flag = (boolean) data;
//                errorLightBtn.setActivated(flag);
//                break;
//            }
//            case BCM_DemisterStatus: {//除雾状态
//                boolean flag = (boolean) data;
//                deFogBtn.setActivated(flag);
//                break;
//            }
//            case BCM_ACBlowingLevel: {//空调风量档位
//                int index = object.getIntValue("data");//接收档位
//                if (index != AIR_GRADE_SIX_GEAR) {
//                    seekBarIndex = index;//当前档位
//                    seekBar.setProgress(seekBarIndex * 20);//设置滑动条
//                }
//                break;
//            }
//            case VCU_ACWorkingStatus: {
//                int flag = object.getIntValue("data");
//                if (flag == 0) {//制冷
//                    coolAirBtn.setActivated(true);
//                    hotAirBtn.setActivated(false);
//                    offAirBtn.setActivated(false);
//                } else if (flag == 1) {//制热
//                    coolAirBtn.setActivated(false);
//                    hotAirBtn.setActivated(true);
//                    offAirBtn.setActivated(false);
//                } else if (flag == 2) {//均关闭
//                    coolAirBtn.setActivated(false);
//                    hotAirBtn.setActivated(false);
//                    offAirBtn.setActivated(true);
//                    seekBar.setEnabled(false);
//                }
//                break;
//            }
//        }
    }

    private BaseHandler rightHandler = new BaseHandler(this, new BaseHandler.HandlerCallback() {
        @Override
        public void handleMessage(Message msg) {
            JSONObject object;
            try {
                object = (JSONObject) msg.obj;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            refresh(object);
        }
    });

    /**
     * 将boolean转换成int
     *
     * @param flag true/fasle
     * @return 2/1
     */
    private int transInt(boolean flag) {
        if (flag) {
            return 1;
        }
        return 2;
    }

    /**
     * 把int值转换成boolean {0x0:off,0x1:on}
     *
     * @param flag 1/0
     * @return true/false
     */
    private boolean transBoolean(int flag) {
        boolean data = false;
        if (flag == 1) {
            data = true;
        }
        return data;
    }

    public interface OnMainRightFragListener {
        void onRelayIsOpen(boolean open);

        void sendToCAN();
    }
}
