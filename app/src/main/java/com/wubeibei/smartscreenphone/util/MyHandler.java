package com.wubeibei.smartscreenphone.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by fangju on 2018/12/8
 */
public class MyHandler extends Handler {
    private Context mContext;

    public MyHandler(){

    }

    public MyHandler(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
