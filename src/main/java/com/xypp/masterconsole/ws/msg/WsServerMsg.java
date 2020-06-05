package com.xypp.masterconsole.ws.msg;

import lombok.Data;

import java.util.Map;

@Data
public class WsServerMsg {

    private String message;

    private Map<String,Object> params;

    public static WsServerMsg msg(String message){
        WsServerMsg wsServerMsg = new WsServerMsg();
        wsServerMsg.setMessage(message);
        return wsServerMsg;
    }

}
