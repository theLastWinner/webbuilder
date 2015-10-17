package org.webbuilder.web.core.websocket.message.imp;

import org.webbuilder.web.core.websocket.message.Message;
import org.webbuilder.web.core.websocket.message.WebSocketMessageManager;
import org.webbuilder.web.core.websocket.message.listener.MessageListener;
import org.springframework.web.socket.WebSocketSession;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-09-10 0010.
 */
public abstract class BaseMessageManager implements WebSocketMessageManager {

    protected List<MessageListener> listeners = new LinkedList<>();


    @Override
    public List<MessageListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<MessageListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void onSend(Message message) {
        for (MessageListener listener : getListeners()) {
            listener.onSend(message);
        }
    }

    public void onRead(WebSocketSession session, Message message) {
        for (MessageListener listener : getListeners()) {
            listener.onRead(session, message);
        }
    }

    public void onError(WebSocketSession session, Message message, Exception e) {
        for (MessageListener listener : getListeners()) {
            listener.onError(session, message, e);
        }
    }

}
