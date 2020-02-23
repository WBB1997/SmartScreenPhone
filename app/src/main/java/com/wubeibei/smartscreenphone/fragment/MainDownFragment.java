package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.App;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

import org.greenrobot.eventbus.Subscribe;

import static com.wubeibei.smartscreenphone.command.MessageName.HMI;
import static com.wubeibei.smartscreenphone.command.SignalName.HMI_Dig_Ord_Driver_model;
import static com.wubeibei.smartscreenphone.command.SignalName.RCU_Dig_Ord_SystemStatus;

public class MainDownFragment extends Fragment {
    private static final String TAG = "MainDownFragment";
    public final static int DOWN_FRAGMENT = 4;
    private Button longDriveBtn = null;//远程驾驶
    private Button autoDriveBtn = null;//自动驾驶
    private Button awaitBtn = null;//待机状态
    private final String clazz = "HMI";//所属类名
    private int field = -1;//属性
    private Object o = null;//状态
    private boolean typeFlag = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_down_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        longDriveBtn = view.findViewById(R.id.downFragment_longDrive_btn);
        autoDriveBtn = view.findViewById(R.id.downFragment_autoDrive_btn);
        awaitBtn = view.findViewById(R.id.downFragment_await_btn);
        longDriveBtn.setOnClickListener(onClickListener);
        autoDriveBtn.setOnClickListener(onClickListener);
        awaitBtn.setOnClickListener(onClickListener);
        awaitBtn.setActivated(true);
        awaitBtn.setEnabled(true);
    }

    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.downFragment_longDrive_btn: {
                    App.getInstance().send_modify_send(HMI.toString(), HMI_Dig_Ord_Driver_model.toString(), 1);
                    changeType(true, false, false);
                    break;
                }
                case R.id.downFragment_autoDrive_btn: {
                    App.getInstance().send_modify_send(HMI.toString(), HMI_Dig_Ord_Driver_model.toString(), 0);
                    changeType(false, true, false);
                    break;
                }
                case R.id.downFragment_await_btn: {
                    App.getInstance().send_send(HMI.toString());
                    changeType(false, false, true);
                    break;
                }
            }
        }
    };

    // 接收Server发来的指令
    @Subscribe
    public void messageEventBus(MessageWrap messageWrap) {
        JSONObject jsonObject = JSON.parseObject(messageWrap.getMessage());
        String action = jsonObject.getString("action");
        if (action.equals("modify")) {
            JSONObject data = jsonObject.getJSONObject("data");
            String signal = data.getString("signal_name");
            if (signal.equals(RCU_Dig_Ord_SystemStatus.toString())) {
                int value = (int) data.getDoubleValue("value");
                changeType(value == 2, value == 0, value == 1 || value == 3);
                if (value == 1 || value == 3) {
                    App.showToast(value == 1 ? "自动驾驶系统故障" : "远程驾驶系统故障");
                } else {
                    App.showToast(value == 2 ? "自动驾驶系统运行正常" : "远程驾驶系统运行正常");
                }
            }
        }
    }


    /**
     * 改变按钮状态
     *
     * @param longFlag  远程驾驶状态
     * @param autoFlag  自动驾驶状态
     * @param awaitFlag 待机状态
     */
    public void changeType(boolean longFlag, boolean autoFlag, boolean awaitFlag) {
        longDriveBtn.setActivated(longFlag);
        autoDriveBtn.setActivated(autoFlag);
        awaitBtn.setActivated(awaitFlag);
        if (longFlag || autoFlag) {
            awaitBtn.setEnabled(true);
        }
    }
}
