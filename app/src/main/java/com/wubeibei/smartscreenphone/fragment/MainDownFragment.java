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

import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.MainActivity;
import com.wubeibei.smartscreenphone.view.CustomOnClickListener;

public class MainDownFragment extends Fragment {
    private static final String TAG = "MainDownFragment";
    public final static int DOWN_FRAGMENT = 4;
    private MainActivity activity = null;
    private Button longDriveBtn = null;//远程驾驶
    private Button autoDriveBtn = null;//自动驾驶
    private Button awaitBtn = null;//待机状态
    //发送CAN总线的数据
//    private int canType = HOST_TO_CAN;
    private final String clazz = "HMI";//所属类名
    private int field = -1;//属性
    private Object o = null;//状态
    private boolean typeFlag = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_down_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        longDriveBtn = view.findViewById(R.id.downFragment_longDrive_btn);
        autoDriveBtn = view.findViewById(R.id.downFragment_autoDrive_btn);
        awaitBtn = view.findViewById(R.id.downFragment_await_btn);
//        longDriveBtn.setOnClickListener(onClickListener);
//        autoDriveBtn.setOnClickListener(onClickListener);
//        awaitBtn.setOnClickListener(onClickListener);
        awaitBtn.setActivated(true);
        awaitBtn.setEnabled(true);
    }

    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        public void onSingleClick(View v) {
//            field = HMI_Dig_Ord_Driver_model;
//            switch (v.getId()){
//                case R.id.downFragment_longDrive_btn:{
//                    activity.onBtnClick(DRIVE_MODEL_REMOTE);
////                    changeType(true,false,false);
////                    o = DRIVE_MODEL_REMOTE;
////                    typeFlag = true;
//                    break;
//                }
//                case R.id.downFragment_autoDrive_btn:{
//                    activity.onBtnClick(DRIVE_MODEL_AUTO);
////                    changeType(false,true,false);
////                    o = DRIVE_MODEL_AUTO;
////                    typeFlag = true;
//                    break;
//                }
//                case R.id.downFragment_await_btn:{
////                    changeType(false,false,true);
////                    o = DRIVE_MODEL_AUTO_AWAIT;
//                    break;
//                }
//            }
//            if(typeFlag){
//                activity.sendToCAN(canType,clazz,field,o);
//                typeFlag = false;
//            }
        }
    };

    /**
     * 改变按钮状态
     * @param longFlag 远程驾驶状态
     * @param autoFlag 自动驾驶状态
     * @param awaitFlag 待机状态
     */
    public void changeType(boolean longFlag,boolean autoFlag,boolean awaitFlag){
        longDriveBtn.setActivated(longFlag);
        autoDriveBtn.setActivated(autoFlag);
        awaitBtn.setActivated(awaitFlag);
        if(longFlag || autoFlag){
            awaitBtn.setEnabled(true);
        }
    }
}
