package com.xypp.masterconsole.ws;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xypp.masterconsole.ws.msg.WsClientMsg;
import com.xypp.masterconsole.ws.msg.WsServerMsg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


/**
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@ServerEndpoint("/ws/endpoint/{url}")
@Component
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收url
     */
    private String url = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("url") String url) {
        this.session = session;
        this.url = url;
        if (webSocketMap.containsKey(url)) {
            webSocketMap.remove(url);
            webSocketMap.put(url, this);
            //加入set中
        } else {
            webSocketMap.put(url, this);
            addOnlineCount();
        }

        log.info("客户端连接:" + url + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage(WsServerMsg.msg("服务端OK"));
        } catch (IOException e) {
            log.error("客户端:" + url + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(url)) {
            webSocketMap.remove(url);
            //从set中删除
            subOnlineCount();
        }
        log.info("客户端退出:" + url + ",当前在线客户端数:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("客户端消息:" + url + ",报文:" + message);

/*        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                WsClientMsg wsClientMsg = JSON.parseObject(message, WsClientMsg.class);
                String id = wsClientMsg.getId();
                //传送给对应tourl客户端的websocket
                if (StringUtils.isNotBlank(id) && webSocketMap.containsKey(id)) {
                    webSocketMap.get(id).sendMessage();
                } else {
                    log.error("请求的url:" + id + "不在该服务器上");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客户端错误:" + this.url + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(WsServerMsg serverMsg) throws IOException {
        this.session.getBasicRemote().sendText(JSON.toJSONString(serverMsg));
    }


    /**
     * 发送自定义消息
     */
    @SneakyThrows
    public static boolean sendServerMsg(WsServerMsg serverMsg, @PathParam("url") String url) throws IOException {
        boolean isSuccess = false;
        log.info("发送消息到:" + url + "，报文:" + serverMsg);
        if (StringUtils.isNotBlank(url) && webSocketMap.containsKey(url)) {
            webSocketMap.get(url).sendMessage(serverMsg);
        } else {
            isSuccess = false;
            log.error("客户端" + url + ",不在线！");
        }
        return isSuccess;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
