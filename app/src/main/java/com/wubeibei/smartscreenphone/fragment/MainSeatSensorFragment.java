package com.wubeibei.smartscreenphone.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wubeibei.smartscreenphone.R;


public class MainSeatSensorFragment extends Fragment {
    private FrameLayout centerSeatsensorLayout;
    private View left1, left2, right1, right2;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_center_seatsensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        centerSeatsensorLayout = view.findViewById(R.id.center_seatsensor_layout);
        centerSeatsensorLayout.getBackground().setAlpha(200);
        left1 = view.findViewById(R.id.seatsensor_item1);
        left2 = view.findViewById(R.id.seatsensor_item2);
        right1 = view.findViewById(R.id.seatsensor_item4);
        right2 = view.findViewById(R.id.seatsensor_item5);
    }

    public void refresh(int id, int data) {

/*
        switch (id) {
            case BCM_Flg_Stat_BeltsSensor1: {//安全带传感器1
                left1.setVisibility(int2Visibility(data));
                break;
            }
            case BCM_Flg_Stat_BeltsSensor2: {//安全带传感器2
                left2.setVisibility(int2Visibility(data));
                break;
            }
            case BCM_Flg_Stat_BeltsSensor3: {//安全带传感器3
                right1.setVisibility(int2Visibility(data));
                break;
            }
            case BCM_Flg_Stat_BeltsSensor4: {//安全带传感器4
                right2.setVisibility(int2Visibility(data));
                break;
            }
        }
*/
        if(isVisibility()){//当前有安全带报警提醒
            centerSeatsensorLayout.setVisibility(View.VISIBLE);
        }else{
            centerSeatsensorLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断是否有座位显示
     * @return 是否显示
     */
    private boolean isVisibility(){
        if(left1.getVisibility() == View.VISIBLE
                || left2.getVisibility() == View.VISIBLE
                || right1.getVisibility() == View.VISIBLE
                || right2.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    private int int2Visibility(int data) {
        int visibility = View.INVISIBLE;
        if (data == 1) {
            visibility = View.VISIBLE;
        }
        return visibility;
    }
}
