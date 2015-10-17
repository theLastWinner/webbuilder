package org.webbuilder.web.core.websocket.cmd;

import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class CmdRequest implements Serializable {
    private String cmd;

    private String userId;

    private transient WebSocketSession session;

    private Map<String, Object> params = new LinkedHashMap<>();

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}
