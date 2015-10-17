package org.webbuilder.web.core.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * Created by 浩 on 2015-09-09 0009.
 */
public interface SocketService {
    Object doService(WebSocketSession session, String name, Map<String, Object> param) throws Exception;
}
