package com.winning.demo.webSocket;

import com.winning.demo.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{appname}/{key}",configurator = CustomSpringConfigurator.class)
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);
    private String key = "";
    private String appName = "";
    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;
    //你要注入的service或者dao
    //private WsjService wsjService;
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("key") String key,@PathParam("appname") String appName) {
        this.session = session;
        this.key = key;
        this.appName = appName;
        //加入set中
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        LOGGER.info("有新连接"+key+"加入！当前在线人数为" + getOnlineCountByApp(appName));
        try {
                sendMessage(key+"连接成功");
        } catch (IOException e) {
            LOGGER.info("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        LOGGER.info("有一连接关闭！当前在线人数为" + getOnlineCountByApp(this.appName));
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info("来自客户端"+this.key+"的消息:" + message);

        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                Map map = new HashMap();
                map.put("message","i am alive");
                item.sendMessage(GsonUtils.createGsonString(map));
            } catch (IOException e) {
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
        LOGGER.info("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        try {
            this.session.getBasicRemote().sendText(message);
        }catch (NullPointerException e){
            LOGGER.info("当前无用户在线 ");
        }

    }


    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        System.out.println(message);
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 向单个用户发送消息
     * @param message  消息内容
     * @param key  用户标识
     * @throws IOException
     */
    public String sendMessageToUser(String message,String key) throws IOException {
        if (Objects.equals(key,null)){
            return "客户端为空";
        }
        boolean hasUser = false;
        for (WebSocketServer item : webSocketSet) {
            try {
                if (Objects.equals(item.key,key)){
                    hasUser = true;
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
        if (!hasUser){
            return "客户端不存在";
        }
        return "发送成功";
    }

    /**
     * 获取一个app的在线人数
     * @param appName appname
     */
    public synchronized int getOnlineCountByApp(String appName){
        int appOnlineCount = 0;
        for (WebSocketServer item : webSocketSet) {
            if (Objects.equals(item.appName,appName)){
                appOnlineCount++;
            }
        }
        return appOnlineCount;
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