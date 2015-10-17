package org.webbuilder.web.core.websocket.exception;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class WebSocketException extends Exception {
    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
