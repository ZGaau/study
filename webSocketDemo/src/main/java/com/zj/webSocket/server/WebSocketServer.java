package com.zj.webSocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * WebSocket处理类
 */
@Component
@ServerEndpoint(value = "/ws/asset")
public class WebSocketServer {

    @PostConstruct
    public void init() {
        System.out.println("------WebSocket加载------");
    }

    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    // 用来记录与服务器成功连接的客户端个数
    private static final AtomicInteger onLineCount = new AtomicInteger(0);
    // 用来存放每个客户端对应的Session对象, 使用concurrent包下的线程安全set
    private static CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session 存在服务器的session
     */
    @OnOpen
    public void onOpen(Session session) {
        sessionSet.add(session);

        Integer cnt = onLineCount.incrementAndGet(); //在线数加1
        logger.info("有连接加入,当前连接数为,{}", cnt);
        //发送消息
        SendMessage(session, "连接成功");
    }

    /**
     * 连接关闭调用的方法
     *
     * @param session 存在服务器的session
     */
    @OnClose
    public void onClose(Session session) {
        sessionSet.remove(session);

        int cnt = onLineCount.decrementAndGet();
        logger.info("有连接关闭,当前连接数为,{}", cnt);

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param session 存在服务器的session
     * @param messge  客户端发来的消息
     */
    @OnMessage
    public void onMessage(Session session, String messge) {
        logger.info("来自客户端的消息{}," + messge);

        //发消息通知客户端
        SendMessage(session, "收到消息,消息内容:" + messge);
    }

    /**
     * 出现错误后调用的方法
     *
     * @param session 存在服务器的session
     * @param error   出现的错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误,{},Session ID: {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 抽取发送消息方法，实践表明，每次浏览器刷新，session会发生变化。
     *
     * @param session
     * @param message
     */
    public static void SendMessage(Session session, String message) {
        try {
//            session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)",message,session.getId()));
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 指定Session发送消息
     *
     * @param sessionId
     * @param message
     * @throws IOException
     */
    public static void SendMessage(String sessionId, String message) throws IOException {
        Session session = null;
        for (Session s : sessionSet) {
            if (s.getId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session != null) {
            SendMessage(session, message);
        } else {
            logger.info("没有找到你指定ID的会话：{}", sessionId);
        }
    }

    /**
     * 抽取群发消息方法
     *
     * @param message
     * @throws IOException
     */
    public static void BroadCastInfo(String message) throws IOException {
        for (Session session : sessionSet) {
            if (session.isOpen()) {
                SendMessage(session, message);
            }
        }
    }

}


