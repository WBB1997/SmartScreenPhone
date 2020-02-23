package com.wubeibei.smartscreenphone.util;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public class BaseHandler extends Handler {
    private static final String TAG = "BaseHandler";
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    private HandlerCallback callback;

    private BaseHandler(){}

    public BaseHandler(Activity activity, HandlerCallback callback){
        this.callback = callback;
        activityWeakReference = new WeakReference<>(activity);
    }

    public BaseHandler(Fragment fragment,HandlerCallback callback){
        this.callback = callback;
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
        if(activityWeakReference != null && activityWeakReference.get() != null && !activityWeakReference.get().isFinishing()){
            callback.handleMessage(msg);
        }else if(fragmentWeakReference != null || fragmentWeakReference.get() != null || !fragmentWeakReference.get().isRemoving()){
            callback.handleMessage(msg);
        }else{
            Log.e(TAG,"Activity or Fragment is gone");
        }
    }

    public interface HandlerCallback{
        void handleMessage(Message msg);
    }
}
