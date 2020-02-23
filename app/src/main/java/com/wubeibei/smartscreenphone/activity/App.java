package com.wubeibei.smartscreenphone.activity;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.CrashHandler;
import com.wubeibei.smartscreenphone.util.ScreenAdapter;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import net.qiujuer.genius.kit.handler.Run;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class App extends Application {
    private static final String TAG = "App";
    private static ConnectionInfo info = new ConnectionInfo("192.168.1.60", 5118);
    private IConnectionManager connectionManager;
    private static App instance = null;

    public static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenAdapter.setup(this);
        instance = this;
        CrashHandler.getInstance().init(this);
        connectionManager = OkSocket.open(info);
        OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder(connectionManager.getOption());
        okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength() {
                return 4;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(header);
                byteBuffer.order(byteOrder);
                return byteBuffer.getInt();
            }
        });
        //将新的修改后的参配设置给连接管理器
        connectionManager.option(okOptionsBuilder.build());

        // 注册监听器
        connectionManager.registerReceiver(new SocketActionAdapter(){
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                App.showToast("网络连接成功");
                // 弹出提示框
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                byte[] bytes = data.getBodyBytes();
                String string = new String(bytes);
                EventBus.getDefault().post(MessageWrap.get(string));
            }
        });
        connectionManager.connect();
        // 注册EventBus
        EventBus.getDefault().register(this);
    }

    // 普通的发送网络数据
    public void send(String message){
        if (connectionManager.isConnect()) {
            MessageWrap messageWrap = MessageWrap.get(message);
            connectionManager.send(messageWrap);
        }else {
            App.showToast("暂无网络连接");
        }
    }

    // 发送修改信号值数据
    public void send_modify(String msg_id, String signal_name, double value){
        if (connectionManager.isConnect()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("msg_id", msg_id);
                data.put("signal_name", signal_name);
                data.put("value", value);
                jsonObject.put("action", "modify");
                jsonObject.put("data",data);
                MessageWrap messageWrap = MessageWrap.get(jsonObject.toJSONString());
                connectionManager.send(messageWrap);
            }catch (JSONException e) {
                e.printStackTrace();
                App.showToast(e.getMessage());
            }
        }else {
            App.showToast("暂无网络连接");
        }
    }

    // 发送send数据
    public void send_send(String msg_id){
        if (connectionManager.isConnect()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();

                data.put("msg_id", msg_id);
                jsonObject.put("action", "send");
                jsonObject.put("data",data);

                MessageWrap messageWrap = MessageWrap.get(jsonObject.toJSONString());
                connectionManager.send(messageWrap);
            }catch (JSONException e) {
                e.printStackTrace();
                App.showToast(e.getMessage());
            }
        }else {
            App.showToast("暂无网络连接");
        }
    }

    // 发送修改andSend数据
    public void send_modify_send(String msg_id, String signal_name, double value){
        send_modify(msg_id,signal_name,value);
        send_send(msg_id);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 取消EventBus
        EventBus.getDefault().unregister(this);
    }

    public static void showToast(final String msg) {
        Run.onUiSync(() -> Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(@StringRes int resId) {
        showToast(instance.getString(resId));
    }

}
