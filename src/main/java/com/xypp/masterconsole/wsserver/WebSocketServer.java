package com.xypp.masterconsole.wsserver;

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
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("用户连接:" + url + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户:" + url + ",网络异常!!!!!!");
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
        log.info("用户退出:" + url + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + url + ",报文:" + message);
        //可以群发消息
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromurl", this.url);
                String tourl = jsonObject.getString("tourl");
                //传送给对应tourl用户的websocket
                if (StringUtils.isNotBlank(tourl) && webSocketMap.containsKey(tourl)) {
                    webSocketMap.get(tourl).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的url:" + tourl + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.url + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    @SneakyThrows
    public static void sendInfo(String message, @PathParam("url") String url) throws IOException {
        log.info("发送消息到:" + url + "，报文:" + message);
        if (StringUtils.isNotBlank(url) && webSocketMap.containsKey(url)) {
            webSocketMap.get(url).sendMessage(message);
        } else {
            log.error("用户" + url + ",不在线！");
        }
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
