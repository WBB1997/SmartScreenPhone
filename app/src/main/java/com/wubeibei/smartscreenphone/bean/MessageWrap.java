package com.wubeibei.smartscreenphone.bean;


import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class MessageWrap implements ISendable {

    private final String message;

    public static MessageWrap get(String message) {
        return new MessageWrap(message);
    }

    private MessageWrap(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public byte[] parse() {
        byte[] body = message.getBytes(Charset.forName("UTF-8"));
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}