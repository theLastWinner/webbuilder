package org.webbuilder.web.core.websocket.cmd;

import org.webbuilder.web.core.websocket.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class CmdExecutor {
    private static final Map<String, CmdProcessor> base = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(CmdExecutor.class);

    private CmdExecutor() {
    }

    public static CmdProcessor registerCmd(CmdProcessor processor) {
        return base.put(processor.getName(), processor);
    }

    public static void removeCmd(String name) {
        base.remove(name);
    }

    public static CmdProcessor getCmdProcessor(String name) {
        return base.get(name);
    }

    public static void connectionSession(WebSocketSession session) {
        for (CmdProcessor processor : base.values()) {
            try {
                processor.onSessionConnect(session);
            } catch (Exception e) {
                logger.error("connectionSession error", e);
            }
        }
    }

    public static void sessionClose(WebSocketSession session) {
        for (CmdProcessor processor : base.values()) {
            try {
                processor.onSessionClose(session);
            } catch (Exception e) {
                logger.error("sessionClose error", e);
            }
        }
    }

    public static CmdResponse exec(CmdRequest request) throws Exception {
        CmdProcessor processor = getCmdProcessor(request.getCmd());
        if (processor == null) throw new WebSocketException("cmd ".concat(request.getCmd()).concat(" not found!"));
        return processor.exec(request);
    }
}
