package org.webbuilder.web.core.websocket.message.listener;

import org.webbuilder.web.core.websocket.message.Message;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by 浩 on 2015-09-10 0010.
 */
public interface MessageListener {
    void onSend(Message message);

    void onRead(WebSocketSession session, Message message);

    void onError(WebSocketSession session, Message message, Exception e);
}
