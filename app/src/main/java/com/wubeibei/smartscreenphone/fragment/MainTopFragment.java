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

import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.MainActivity;
import com.wubeibei.smartscreenphone.util.BaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainTopFragment extends Fragment {
    private static final String TAG = "MainTopFragment";
    public final static int TOP_FRAGMENT = 1;
    private final int TIME_FLAG_LOCAL = 10080;//本地时间
    private MainActivity activity = null;
    private ImageView networkIv;//网络信号
    private TextView timeTv;//时间
    private ImageView batteryIv;//电池图片
    private TextView batteryTv;//电池文字
    private TimeThread timeThread = null;//时间线程

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.setHandler(TOP_FRAGMENT,topHandler);
        }
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
    }

    /**
     *更新界面
     */
    public void refresh(JSONObject object){
        int id = object.getIntValue("id");
        Object data = object.get("data");
//        if(id == HAD_GPSPositioningStatus){//GPS状态
//            //TODO GPS状态显示
//        }
//        if(id == OBU_LocalTime){//本地时间
//            if (data != null) {
//                timeTv.setText(transTime((JSONObject)data));
//            }
//        }
//        if(id == BMS_SOC){//动力电池剩余电量SOC
//            int batteryNum = (int) object.getDoubleValue("data");
//            if(batteryNum > 100){
//                batteryNum = 100;
//            }
//            batteryTv.setText(String.valueOf(batteryNum)+"%");
//            if(batteryNum >=0 && batteryNum < 17){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_1);
//            }else if(batteryNum >= 17 && batteryNum < 37){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_2);
//            }else if(batteryNum >=37 && batteryNum < 54){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_3);
//            }else if(batteryNum >= 54 && batteryNum < 71){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_4);
//            }else if(batteryNum >= 71 && batteryNum < 90){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_5);
//            }else if(batteryNum >= 90){
//                batteryIv.setBackgroundResource(R.drawable.ic_battery_5);
//            }
//        }
    }

    /**
     * 转换时间
     * @param object 时间数据
     * @return 时间
     */
    private String transTime(JSONObject object){
        StringBuilder builder = new StringBuilder();
        int hour = object.getIntValue("hour");
        int minute = object.getIntValue("minute");
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
            switch (msg.what){
                case TIME_FLAG_LOCAL:{//本地时间更新
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
                    String date = sdf.format(new Date());
                    timeTv.setText(date);
                    break;
                }
                default:
                    refresh((JSONObject) msg.obj);
                    break;
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
