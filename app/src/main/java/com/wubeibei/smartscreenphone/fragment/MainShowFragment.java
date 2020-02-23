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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.App;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.BaseHandler;

import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import static com.wubeibei.smartscreenphone.command.MessageName.HMI;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_ACBlowingLevel;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_DemisterStatus;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_DangerAlarmLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_HighBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_LeftTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_LowBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_RearFogLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.BCM_Flg_Stat_RightTurningLamp;
import static com.wubeibei.smartscreenphone.command.SignalName.HIM_Dig_Ord_TotalOdmeter;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_Alam;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_LoWBeam;
import static com.wubeibei.smartscreenphone.command.SignalName.VCU_ACWorkingStatus;
import static com.wubeibei.smartscreenphone.command.SignalName.Wheel_Speed_ABS;
import static com.wubeibei.smartscreenphone.command.SignalName.can_RemainKm;
import static com.wubeibei.smartscreenphone.command.SignalName.can_num_PackAverageTemp;


public class MainShowFragment extends Fragment {
    private static final String TAG = "MainShowFragment";
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
    public MainShowFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    // 接收Server发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("modify")) {
            JSONObject data = jsonObject.getJSONObject("data");
            String signal = data.getString("signal_name");
            if (signal.equals(Wheel_Speed_ABS.toString())) {//车速信号
                double speed = data.getDouble("value");
                if (speed < 0) {
                    return;
                }
                newSpeed = speed;//新速度
                if (newSpeed <= MIN_SPEED)
                    App.getInstance().send_modify_send(HMI.toString(), HMI_Dig_Ord_Alam.toString(), 1);
                else
                    App.getInstance().send_modify_send(HMI.toString(), HMI_Dig_Ord_Alam.toString(), 2);
                if (newSpeed < 100) {
                    speedTv.setText(String.format(Locale.CHINA, "%.1f", newSpeed));
                } else {
                    speedTv.setText(String.format(Locale.CHINA, "%.0f", newSpeed));
                }
                showTotalMile(totalMile);
                avgSpeedTv.setText(String.valueOf((int) calculateAvgSpeed(newSpeed)));
            }
            if (signal.equals(can_num_PackAverageTemp.toString())) {//电池包平均温度
                double value = data.getDouble("value");
                temperatureTv.setText(String.valueOf((int) value));
            }
            if (signal.equals(HIM_Dig_Ord_TotalOdmeter.toString())) {//总里程（只来自主机）
                totalMile = data.getIntValue("value");
                showTotalMile(totalMile);
            }
            if (signal.equals(can_RemainKm.toString())) {//剩余里程数
                int value = (int) data.getDoubleValue("data");
                if (value < 0) {//剩余里程小于0
                    return;
                }
                int percentage = 100;//百分比
                if (totalRemainKmFlag) {//当第一次收到剩余里程数
                    if (value <= 0) {//剩余里程等于0
                        return;
                    }
                    totalRemainKm = value;
                    totalRemainKmFlag = false;
                } else {
                    if (value >= lastRemainKm) {//当前里程数大于等于上一次里程数则忽略
                        return;
                    }
                    percentage = (int) ((1 - ((totalRemainKm - value) / totalRemainKm)) * 100);
                }
                if (percentage < 0) {
                    return;
                }
                lastRemainKm = value;//上一次的剩余里程数
                taskProgressTv.setText(String.valueOf(percentage));
            }
        }
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
