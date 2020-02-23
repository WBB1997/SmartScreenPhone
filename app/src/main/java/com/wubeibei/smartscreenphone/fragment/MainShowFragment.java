package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.MainActivity;
import com.wubeibei.smartscreenphone.util.BaseHandler;

import java.util.Locale;


public class MainShowFragment extends Fragment {
    private static final String TAG = "MainShowFragment";
    public final static int SHOW_FRAGMENT = 2;
    private MainActivity activity = null;
    private TextView speedTv = null;//速度
    private TextView temperatureTv = null;//温度
    private TextView totalOdmeterTv = null;//总里程
    private TextView taskProgressTv = null;//任务进度
    private TextView avgSpeedTv = null;//平均时速
    private double totalMile = 0;//总里程
    private volatile double newSpeed = 0;//新速度
    private double lastSpeed = 0;//上一次的速度
    private double totalRemainKm = 0;//任务进度总里程
    private double lastRemainKm = 0;//上一次的剩余里程数
    private boolean totalRemainKmFlag = true;//判断是否是第一次接收到总里程
    private final int MIN_SPEED = 5;//低速报警值
//    private int canType = HOST_TO_CAN;
    private String clazz = "HMI";//所属类名
    private int field = -1;//属性
    private Object o = null;//状态

    public MainShowFragment() {

    }

    public void setTotalRemainKmFlag(boolean totalRemainKmFlag) {
        this.totalRemainKmFlag = totalRemainKmFlag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setHandler(SHOW_FRAGMENT, showHandler);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_show_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        speedTv = view.findViewById(R.id.showFragment_speed_tv);
        temperatureTv = view.findViewById(R.id.showFragment_temperature_tv);
        totalOdmeterTv = view.findViewById(R.id.showFragment_totalOdmeter_tv);
        taskProgressTv = view.findViewById(R.id.showFragment_taskProgress_tv);
        avgSpeedTv = view.findViewById(R.id.showFragment_avgSpeed_tv);
        AssetManager assetManager = getActivity().getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/LCD-N___.TTF");
        speedTv.setTypeface(typeface);
        temperatureTv.setTypeface(typeface);
        showTotalMile(totalMile);
    }

    private BaseHandler showHandler = new BaseHandler(this, new BaseHandler.HandlerCallback() {
        @Override
        public void handleMessage(Message msg) {
            JSONObject object = (JSONObject) msg.obj;
            refresh(object);
        }
    });

    /**
     * 更新界面
     *
     * @param object 数据
     */
    public void refresh(JSONObject object) {
        int id = object.getIntValue("id");
//        if (id == Wheel_Speed_ABS) {//车速信号
//            double speed = object.getDouble("data");
//            if (speed < 0) {
//                return;
//            }
//            newSpeed = speed;//新速度
//            if (newSpeed <= MIN_SPEED) {
//                activity.sendToCAN(canType, clazz, HMI_Dig_Ord_Alam, Ord_Alam_ON);
//            } else {
//                activity.sendToCAN(canType, clazz, HMI_Dig_Ord_Alam, Ord_Alam_OFF);
//            }
//            if (newSpeed < 100) {
//                speedTv.setText(String.format(Locale.CHINA, "%.1f", newSpeed));
//            } else {
//                speedTv.setText(String.format(Locale.CHINA, "%.0f", newSpeed));
//            }
//            showTotalMile(totalMile);
//            avgSpeedTv.setText(String.valueOf((int) calculateAvgSpeed(newSpeed)));
//        }
//        if (id == can_num_PackAverageTemp) {//电池包平均温度
//            double data = object.getDouble("data");
//            temperatureTv.setText(String.valueOf((int) data));
//        }
//        if (id == HMI_Dig_Ord_TotalOdmeter) {//总里程（只来自主机）
//            totalMile = object.getIntValue("data");
//            showTotalMile(totalMile);
//        }
//        if (id == can_RemainKm) {//剩余里程数
//            int data = (int) object.getDoubleValue("data");
//            if (data < 0) {//剩余里程小于0
//                return;
//            }
//            int percentage = 100;//百分比
//            if (totalRemainKmFlag) {//当第一次收到剩余里程数
//                if (data <= 0) {//剩余里程等于0
//                    return;
//                }
//                totalRemainKm = data;
//                totalRemainKmFlag = false;
//            } else {
//                if (data >= lastRemainKm) {//当前里程数大于等于上一次里程数则忽略
//                    return;
//                }
//                percentage = (int) ((1 - ((totalRemainKm - data) / totalRemainKm)) * 100);
//            }
//            if (percentage < 0) {
//                return;
//            }
//            lastRemainKm = data;//上一次的剩余里程数
//            taskProgressTv.setText(String.valueOf(percentage));
//        }
    }

    /**
     * 显示总里程
     *
     * @param totalMile 总里程
     */
    private void showTotalMile(double totalMile) {
        if (totalMile < 100) {
            totalOdmeterTv.setText(String.format(Locale.CHINA, "%.1f", totalMile));
        } else {
            String totalStr = String.valueOf(totalMile);
            if (totalStr.length() > 4) {
                if (totalStr.length() > 5) {
                    totalOdmeterTv.setTextSize(20);
                }
                totalOdmeterTv.setText(String.format(Locale.CHINA, "%.0f", totalMile));
            } else {
                totalOdmeterTv.setText(String.format(Locale.CHINA, "%.0f", totalMile));
            }
        }
    }

    /**
     * 计算总里程
     *
     * @param newSpeed 新速度
     * @return 总里程
     */
    private double calculateTotalMile(double newSpeed) {
        if (newSpeed < 0) {
            return 0;
        }
        return (lastSpeed + newSpeed) / (7200.0);
    }

    /**
     * 计算平均速度
     *
     * @param newSpeed 新速度
     * @return 平均速度
     */
    private double calculateAvgSpeed(double newSpeed) {
        return (lastSpeed + newSpeed) / 2.0;
    }

}
