package com.wubeibei.smartscreenphone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wubeibei.smartscreenphone.R;
import com.wubeibei.smartscreenphone.activity.MainActivity;

/**
 * @Author: FangJu
 * @Date: 2019/9/19
 */
public class MainLeftFragment extends Fragment {
    private View leftFragmentDLClickBtn;
    private FrameLayout mLeftLayout;
    private ImageButton mAirBtn;
    private ImageButton mGlassBtn;
    private OnLeftFragmentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLeftFragmentListener) {
            mListener = (OnLeftFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_left_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLeftLayout = view.findViewById(R.id.main_activity_left_layout);
        mLeftLayout.getBackground().setAlpha(200);
        mAirBtn = view.findViewById(R.id.ib_air_btn);
        mAirBtn.setOnClickListener(onClickListener);
        mGlassBtn = view.findViewById(R.id.ib_glass_btn);
        mGlassBtn.setOnClickListener(onClickListener);
        leftFragmentDLClickBtn = view.findViewById(R.id.leftFragment_dl_clickBtn);
        leftFragmentDLClickBtn.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_air_btn: {
                    if (mListener != null) {
                        mListener.onAirButtonClick();
                    }
                    break;
                }
                case R.id.ib_glass_btn: {
                    mGlassBtn.setActivated(!mGlassBtn.isActivated());
                    if (mListener != null) {
                        mListener.onGlassButtonClick(mGlassBtn.isActivated());
                    }
                    break;
                }
                case R.id.leftFragment_dl_clickBtn: {
                    if (getActivity() != null) {
//                        ((MainActivity) getActivity()).leftDrawerLayout.closeDrawer(Gravity.START);
                    }
                    break;
                }
            }
        }
    };

    public interface OnLeftFragmentListener {
        void onAirButtonClick();

        void onGlassButtonClick(boolean open);
    }
}
