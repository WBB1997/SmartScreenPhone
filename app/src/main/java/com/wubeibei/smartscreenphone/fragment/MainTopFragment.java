package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.BaseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.wubeibei.smartscreenphone.command.SignalName.BMS_SOC;
import static com.wubeibei.smartscreenphone.command.SignalName.OBU_LocalTimeHour;
import static com.wubeibei.smartscreenphone.command.SignalName.OBU_LocalTimeMinute;


public class MainTopFragment extends Fragment {
    private static final String TAG = "MainTopFragment";
    private final int TIME_FLAG_LOCAL = 10080;//本地时间
    private ImageView networkIv;//网络信号
    private TextView timeTv;//时间
    private int hh = 12;
    private int mm = 0;
    private ImageView batteryIv;//电池图片
    private TextView batteryTv;//电池文字
    private TimeThread timeThread = null;//时间线程

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_top_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkIv = view.findViewById(R.id.topFragment_network_iv);
        timeTv = view.findViewById(R.id.topFragment_time_tv);
        batteryIv = view.findViewById(R.id.topFragment_battery_iv);
        batteryTv = view.findViewById(R.id.topFragment_battery_tv);
        timeThread = new TimeThread();
        timeThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeThread.setFlag(false);
        EventBus.getDefault().unregister(this);
    }

    // 接收Server发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("modify")) {
            JSONObject data = jsonObject.getJSONObject("data");
            String signal = data.getString("signal_name");
            if (signal.equals(OBU_LocalTimeMinute.toString())) {//本地时间
                mm = (int) data.getDoubleValue("value");
                timeTv.setText(transTime(mm,hh));
            }else if (signal.equals(OBU_LocalTimeHour.toString())){
                hh = (int) data.getDoubleValue("value");
                timeTv.setText(transTime(mm,hh));
            }
            if (signal.equals(BMS_SOC.toString())) {//动力电池剩余电量SOC
                int value = (int) data.getDoubleValue("value");
                if (value > 100) {
                    value = 100;
                }
                batteryTv.setText(value + "%");
                if (value >= 0 && value < 17) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_1);
                } else if (value >= 17 && value < 37) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_2);
                } else if (value >= 37 && value < 54) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_3);
                } else if (value >= 54 && value < 71) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_4);
                } else if (value >= 71 && value < 90) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_5);
                } else if (value >= 90) {
                    batteryIv.setBackgroundResource(R.drawable.ic_battery_5);
                }
            }
        }
    }

    /**
     * 转换时间
     */
    private String transTime(int hour, int minute){
        StringBuilder builder = new StringBuilder();
        hour %= 24;
        minute %= 60;
        String finalHour = String.format(Locale.CHINA,"%02d",hour);
        String finalMinute = String.format(Locale.CHINA,"%02d",minute);
        builder.append(finalHour);
        builder.append(":");
        builder.append(finalMinute);
        return builder.toString();
    }

    private BaseHandler topHandler = new BaseHandler(this, new BaseHandler.HandlerCallback() {
        @Override
        public void handleMessage(Message msg) {
            //本地时间更新
            if (msg.what == TIME_FLAG_LOCAL) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
                String date = sdf.format(new Date());
                timeTv.setText(date);
            }
        }
    });

    class TimeThread extends Thread {
        private boolean flag = true;//默认开启线程

        void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            while (flag){
                try {
                    sendText(TIME_FLAG_LOCAL);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 发送更新时间命令给UI线程
         */
        private void sendText(int what){
            Message msg = topHandler.obtainMessage();
            msg.what = what;
            topHandler.sendMessage(msg);
        }
    }
}
