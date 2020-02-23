package com.wubeibei.smartscreenphone.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {
    private Context mContext;
    private static PreferencesUtil instance = null;
    private SharedPreferences.Editor editor = null;
    private SharedPreferences preferences = null;

    private PreferencesUtil(Context mContext) {
        this.mContext = mContext;
        preferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = mContext.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
    }

    public static PreferencesUtil getInstance(Context mContext) {
        if (instance == null) {
            instance = new PreferencesUtil(mContext);
        }
        return instance;
    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    public String getPreferences(String key) {
        String value = preferences.getString(key, "");
        return value;
    }

    /**
     * 存数据
     *
     * @param key
     * @param value
     */
    public void savePreferences(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

}
