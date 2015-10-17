package org.webbuilder.web.core.websocket.message.imp;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.StorageDriver;
import org.webbuilder.utils.storage.event.Finder;
import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.message.Message;
import org.webbuilder.web.po.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用自定义的消息存储器进行消息推送（适合集群环境下使用）.
 * 由于socketSession不支持序列化，所以使用心跳线程进行消息定时推送
 * Created by 浩 on 2015-09-08 0008.
 */
public class StorageMessageManager extends BaseMessageManager {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    //存储器驱动,此driver应使用队列实现
    private StorageDriver driver;

    private StorageDriver sessionCacheDriver;

    //心跳间隔
    private int waitFor = 500;

    //每次getMessageList的数量
    private int quantity = 20;
    /**
     * 心跳线程池，每创建一个session时，则建立一个对应的心跳线程进行定时消息推送
     */
    private static final Map<String, Map<String, HeartBeatThread>> threadPool = new ConcurrentHashMap<>(64);

    //根据用户id获取一个存储器名称
    private String cacheName(String userId, String type) {
        return "web.socket.".concat(type).concat("->").concat(userId);
    }

    @Override
    public void send(Message message) {
        try {
            if (logger.isDebugEnabled())
                logger.debug("send message to {},now online number:{}", message.getTo(), threadPool.size());
            message.setSend_date(new Date());
            String key = cacheName(message.getTo(), message.getCmd());
            Storage<String, Message> storage = getDriver().getStorage(key, Message.class);
            storage.put(message.getU_id(), message);
            onSend(message);
        } catch (Exception e) {
            onError(null, message, e);
        }
    }

    @Override
    public List<Message> getMessageList(String userId, String type) {
        String key = cacheName(userId, type);
        try {
            Storage<Object, Message> storage = driver.getStorage(key, Message.class);
            List<Message> messages = storage.find(new Finder<Object, Message>() {
                @Override
                public boolean each(int index, Object key, Message val) {
                    if (index > quantity) {//一次获取20条message
                        findOver();
                        return false;
                    }
                    return true;
                }
            });
            return messages;
        } catch (Exception e) {
            logger.error(String.format("getMessageList(%s,%s)", userId, type), e);
        }
        return null;
    }

    private void removeMessage(Message message) throws Exception {
        if (message == null) return;
        String key = cacheName(message.getTo(), message.getCmd());
        Storage<String, Message> storage = driver.getStorage(key, Message.class);
        storage.remove(message.getU_id());
    }

    private Storage<String, String> getSessionCache() {
        Storage<String, String> storage = null;
        try {
            storage = getSessionCacheDriver().getStorage("socket.session.hold");
        } catch (Exception e) {
            logger.error("session cache error!", e);
        }
        return storage;
    }

    private String getSessionId(String user, String type) {
        return getSessionCache().get(user.concat(".").concat(type));
    }

    private void putSessionId(String user, String type, String sessionId) {
        getSessionCache().put(user.concat(".").concat(type), sessionId);
    }

    @Override
    public void bindAutoPush(WebSocketSession socketSession, String type) {
        User user = getUserFromSession(socketSession);
        if (user == null) return;
        //移除已存在的session
        putSessionId(user.getU_id(), type, socketSession.getId());
        Map<String, HeartBeatThread> threadGroup = threadPool.get(user.getU_id());
        if (threadGroup == null) {
            threadGroup = new HashMap<>();
            threadPool.put(user.getU_id(), threadGroup);
        }
        HeartBeatThread thread = new HeartBeatThread(user, socketSession, type);
        thread.setWaitFor(waitFor);
        thread.start();
        threadGroup.put(type, thread);
    }

    @Override
    public void sessionClose(WebSocketSession socketSession, String type) {
        User user = getUserFromSession(socketSession);
        if (user == null) return;
        putSessionId(user.getU_id(), type, "-0");
        Map<String, HeartBeatThread> threadGroup = threadPool.get(user.getU_id());
        if (threadGroup != null) {
            HeartBeatThread thread = threadGroup.get(type);
            if (thread != null) {
                threadPool.remove(user.getU_id());
            }
        }
    }

    private User getUserFromSession(WebSocketSession socketSession) {
        return (User) socketSession.getAttributes().get("user");
    }

    public StorageDriver getDriver() {
        return driver;
    }

    public StorageDriver getSessionCacheDriver() {
        return sessionCacheDriver;
    }

    public void setSessionCacheDriver(StorageDriver sessionCacheDriver) {
        this.sessionCacheDriver = sessionCacheDriver;
    }

    public void setDriver(StorageDriver driver) {
        this.driver = driver;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 心跳线程，用于定时向客户端推送消息
     */
    private class HeartBeatThread extends Thread {
        //客户端会话
        private final WebSocketSession session;

        private final String type;
        //登陆的用户信息
        private final User user;

        /**
         * 带参构造方法，创建线程时，必须指定user 和session
         *
         * @param user    登陆用户
         * @param session 会话
         */
        public HeartBeatThread(User user, WebSocketSession session, String type) {
            this.session = session;
            this.user = user;
            this.type = type;
        }

        @Override
        public void run() {
            while (!isOver()) {
                beat();//心跳
                //判断是否已经断开连接
                if (!session.getId().equals(getSessionId(user.getU_id(), type))) {
                    over();
                    return;
                }
                //获取消息列表
                List<Message> messages = getMessageList(user.getU_id(), type);
                if (messages == null) continue;
                if (messages.size() > 0) {
                    //进行消息推送
                    for (int i = 0, len = messages.size(); i < len; i++) {
                        Message message = messages.get(i);
                        try {
                            session.sendMessage(new TextMessage(new CmdResponse(message.getCmd(), true, message).toString()));
                            onRead(session, message);
                            removeMessage(message);
                        } catch (Exception e) {
                            onError(session, message, e);
                        }
                    }
                }
            }
            if (logger.isDebugEnabled())
                logger.debug("HeartBeatThread for {} over!", user.getUsername());
        }

        private void beat() {
            try {
                Thread.sleep(waitFor);
            } catch (InterruptedException e) {
            }
        }

        private int waitFor = 500;

        private boolean over = false;

        public WebSocketSession getSession() {
            return session;
        }

        public void setWaitFor(int waitFor) {
            this.waitFor = waitFor;
        }

        public boolean isOver() {
            return over;
        }

        public void over() {
            this.over = true;
        }
    }

    public int getWaitFor() {
        return waitFor;
    }

    public void setWaitFor(int waitFor) {
        this.waitFor = waitFor;
    }
}
