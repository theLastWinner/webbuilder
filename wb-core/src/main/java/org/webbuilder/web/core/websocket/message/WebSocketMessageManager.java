package org.webbuilder.web.core.websocket.message;

import org.webbuilder.web.core.websocket.message.listener.MessageListener;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * WebSocket 消息管理器，用于进行消息发送和推送
 * Created by 浩 on 2015-09-08 0008.
 */
public interface WebSocketMessageManager {
    /**
     * 发送一条消息
     *
     * @param message 消息对象
     */
    void send(Message message);

    /**
     * 根据用id获取未读消息列表
     *
     * @param userId 用户id
     * @return 消息列表
     */
    List<Message> getMessageList(String userId,String type);

    /**
     * 绑定一个session
     *
     * @param socketSession
     */
    void bindAutoPush(WebSocketSession socketSession,String type);

    /**
     * 关闭session
     *
     * @param socketSession
     */
    void sessionClose(WebSocketSession socketSession,String type);

    List<MessageListener> getListeners();

    void addListener(MessageListener listener);

    void removeListener(MessageListener listener);

}
