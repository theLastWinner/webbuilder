package org.webbuilder.web.core.websocket.message.imp;

import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.message.Message;
import org.webbuilder.web.po.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于ConcurrentHashMap的消息管理器（不能用于集群）
 * <p/>
 * Created by 浩 on 2015-09-08 0008.
 */
public class LocalMessageManager extends BaseMessageManager {

    //消息缓存库
    private static final Map<String, Map<String, List<Message>>> messageBase = new ConcurrentHashMap<>();

    //session缓存库
    private static final Map<String, Map<String, WebSocketSession>> sessionBase = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Message> getMessageList(String userId, String type) {
        Map<String, List<Message>> map = messageBase.get(userId);
        if (map == null) {
            map = new HashMap<>();
            messageBase.put(userId, map);
        }
        return map.get(type);
    }

    private WebSocketSession getSessionByType(String to, String type) {
        Map<String, WebSocketSession> map = sessionBase.get(to);
        if (map == null) {
            map = new HashMap<>();
            sessionBase.put(to, map);
        }
        return map.get(type);
    }

    @Override
    public void send(Message message) {
        boolean sendSuccess = false;
        //发送消息
        WebSocketSession session = getSessionByType(message.getTo(), message.getCmd());
        if (session != null) {
            if (!session.isOpen()) {
                sessionBase.remove(message.getTo());
            } else {
                try {
                    session.sendMessage(new TextMessage(new CmdResponse(message.getCmd(), true, message).toString()));
                    sendSuccess = true;
                    onSend(message);
                    onRead(session, message);
                } catch (IOException e) {
                    onError(session, message, e);
                }
            }
        }
        //发送失败后缓存消息
        if (!sendSuccess) {
            cacheMessage(message);
        }
    }

    private void cacheMessage(Message message) {
        String userId = message.getTo();
        List<Message> messages = getMessageList(userId, message.getCmd());
        synchronized (messages) {
            messages.add(message);
        }
        onSend(message);
    }

    private void cacheMessage(String userId, List<Message> messages) {
        synchronized (messages) {
            for (Message message : messages) {
                List<Message> messages_old = getMessageList(userId, message.getCmd());
                messages_old.add(message);
                onSend(message);
            }
        }
    }

    @Override
    public void sessionClose(WebSocketSession socketSession, String type) {
        final User user = (User) socketSession.getAttributes().get("user");
        if (user == null) return;
        sessionBase.remove(user.getU_id());
    }

    @Override
    public void bindAutoPush(final WebSocketSession socketSession, String type) {
        final User user = (User) socketSession.getAttributes().get("user");
        if (user == null) {
            if (logger.isDebugEnabled())
                logger.debug("bind WebSocketSession fail,because user not login!");
            try {
                socketSession.sendMessage(new TextMessage("请登陆"));
            } catch (IOException e) {
            }
            return;
        }
        WebSocketSession session = getSessionByType(user.getU_id(), type);
        if (session != null) {
            try {
                session.close(CloseStatus.BAD_DATA.withReason("已在其他位置链接。"));
            } catch (IOException e) {
            }
        }
        sessionBase.get(user.getU_id()).put(type, socketSession);
        if (logger.isDebugEnabled())
            logger.debug("bind WebSocketSession to user:{} success!", user.getU_id());
        //首次绑定，推送未读消息
        List<Message> messages = getMessageList(user.getU_id(), type);
        if (messages != null)
            try {
                for (Message message : messages) {
                    onRead(socketSession, message);
                    socketSession.sendMessage(new TextMessage(new CmdResponse(message.getCmd(), true, message).toString()));
                }
                if (logger.isDebugEnabled())
                    logger.debug("send message success,len:{}", messages.size());
                messages.clear();
            } catch (IOException e) {
                //失败了，
                cacheMessage(user.getU_id(), messages);
                logger.error("send message error", e);
            }

    }
}
