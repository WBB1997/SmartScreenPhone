package com.wubeibei.smartscreenphone.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    private static JSONUtil instance = null;

    private JSONUtil(){

    }

    public static JSONUtil getInstance(){
        if(instance == null){
            instance = new JSONUtil();
        }
        return instance;
    }

    /**
     * 判断此JSON字符串是否有效
     * @param jsonStr
     * @return
     */
    public boolean isValid(String jsonStr){
        try {
            JSONObject.parseObject(jsonStr);
        } catch (JSONException e) {
            try {
                JSONArray.parseArray(jsonStr);
            } catch (JSONException e1) {
                return false;
            }
        }
        return true;
    }
}
