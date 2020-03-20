package com.wubeibei.smartscreenphone.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ActivityCollector {
    private static Stack<Activity> activities = new Stack<>();

    // 创建一个Activity时自动调用
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    //销毁一个Activity时自动调用
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}