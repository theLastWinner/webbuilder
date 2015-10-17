package org.webbuilder.web.core.websocket.cmd.processor;

import org.webbuilder.web.core.logger.LoggerAppender;
import org.webbuilder.web.core.logger.LoggerAppenderStorage;
import org.webbuilder.web.core.websocket.cmd.CmdExecutor;
import org.webbuilder.web.core.websocket.cmd.CmdProcessor;
import org.webbuilder.web.core.websocket.cmd.CmdRequest;
import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.message.Message;
import org.webbuilder.web.core.websocket.message.WebSocketMessageManager;
import org.webbuilder.web.core.websocket.message.imp.LocalMessageManager;
import org.webbuilder.web.po.user.User;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-10 0010.
 */
public class LoggerProcessor implements CmdProcessor {

    private String name = "logger";

    private WebSocketMessageManager messageManager;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CmdResponse exec(final CmdRequest request) throws Exception {
        Map<String, Object> param = request.getParams();
        String type = (String) param.get("type");
        String callBack = (String) param.get("callBack");
        if (callBack == null) callBack = request.getCmd();

        if ("start".equals(type)) {
            getMessageManager().bindAutoPush(request.getSession(), getName());
            LoggerAppenderStorage.registAppender(new LoggerWriter(request.getUserId()));
            return new CmdResponse(callBack, true, "start logger");
        } else if ("stop".equals(type)) {
            LoggerAppenderStorage.cancelAppender(request.getUserId());
            return new CmdResponse(callBack, true, "stop logger");
        }
        return new CmdResponse(callBack, false, String.format("unknow type %s", type));
    }

    @Override
    public void onSessionConnect(WebSocketSession session) throws Exception {

    }

    @Override
    public void onSessionClose(WebSocketSession session) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user != null) {
            LoggerAppenderStorage.cancelAppender(user.getU_id());
            getMessageManager().sessionClose(session, getName());
        }
    }

    private class LoggerWriter implements LoggerAppender {
        private String name;

        public LoggerWriter(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void append(String log) {
            Message message = new Message();
            message.setTo(name);
            message.setContent(log);
            message.setCmd("logger");
            message.setFrom("logger");
            message.setSend_date(new Date());
            getMessageManager().send(message);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.hashCode() == obj.hashCode();
        }
    }

    public WebSocketMessageManager getMessageManager() {
        if (messageManager == null) messageManager = new LocalMessageManager();
        return messageManager;
    }

    public void setMessageManager(WebSocketMessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Override
    public void init() throws Exception {
        CmdExecutor.registerCmd(this);
    }
}
