package com.wubeibei.smartscreenphone.activity;

import android.app.Application;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wubeibei.smartscreenphone.bean.MessageWrap;
import com.wubeibei.smartscreenphone.util.CrashHandler;
import com.wubeibei.smartscreenphone.util.LogUtil;
import com.wubeibei.smartscreenphone.util.ScreenAdapter;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
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
//    private static ConnectionInfo info = new ConnectionInfo("192.168.1.60", 5118);
//    private static ConnectionInfo info = new ConnectionInfo("192.168.0.100", 5118);
    private static ConnectionInfo info = new ConnectionInfo("10.0.2.2", 5118);
    private IConnectionManager connectionManager;
    private static App instance = null;
    private boolean login = false; // 是否登录

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
                App.showToast(action);
                // 如果已经登录过了，重连时就发送重新登陆指令
                if(login){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("action","relogin");
                    send(jsonObject.toJSONString());
                }
            }

            // 从服务器收到消息
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                receivingHandler(info, action, data);
            }

            // 发送消息回调
            @Override
            public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            }

            // 断开连接
            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                App.showToast(action);
            }
        });
        connectionManager.connect();
    }

    // 接收服务器发来的消息
    private void receivingHandler(ConnectionInfo info, String action, OriginalData data) {
        byte[] bytes = data.getBodyBytes();
        String string = new String(bytes);
        LogUtil.d(TAG, string);
        //转发给其他模块
        EventBus.getDefault().post(MessageWrap.get(string));
    }

    // 发送普通网络数据
    public void send(String message){
        if (connectionManager.isConnect()) {
            MessageWrap messageWrap = MessageWrap.get(message);
            connectionManager.send(messageWrap);
        }else{
            App.showToast("网络连接错误，请检查网络连接");
        }
    }

    // 连接服务器
    public void connect(){
        connectionManager.connect();
    }

    // 判断是否有网络连接
    public boolean isConnection(){
        return connectionManager.isConnect();
    }

    // 发送修改信号值数据
    public void modifyCommand(String msg_name, String signal_name, double value){
        if (connectionManager.isConnect()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("msg_name", msg_name);
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
        }else{
            App.showToast("网络连接错误，请检查网络连接");
        }
    }

    // 发送send数据
    public void sendCommand(String msg_name){
        if (connectionManager.isConnect()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();

                data.put("msg_name", msg_name);
                jsonObject.put("action", "send");
                jsonObject.put("data",data);

                MessageWrap messageWrap = MessageWrap.get(jsonObject.toJSONString());
                connectionManager.send(messageWrap);
            }catch (JSONException e) {
                e.printStackTrace();
                App.showToast(e.getMessage());
            }
        }else {
            App.showToast("网络连接错误，请检查网络连接");
        }
        connectionManager.connect();
    }

    // 发送ModifyandSend数据
    public void modifysendcommand(String msg_name, String signal_name, double value){
        modifyCommand(msg_name,signal_name,value);
        sendCommand(msg_name);
    }

    // 弹出提示框
    public static void showToast(final String msg) {
        Run.onUiSync(() -> Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show());
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    @Override
    public void onTerminate() {
        connectionManager.disconnect();
        super.onTerminate();
    }
}
