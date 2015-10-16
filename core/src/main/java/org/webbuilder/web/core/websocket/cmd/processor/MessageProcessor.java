package org.webbuilder.web.core.websocket.cmd.processor;

import org.webbuilder.web.core.websocket.cmd.CmdExecutor;
import org.webbuilder.web.core.websocket.cmd.CmdProcessor;
import org.webbuilder.web.core.websocket.cmd.CmdRequest;
import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.message.Message;
import org.webbuilder.web.core.websocket.message.WebSocketMessageManager;
import org.webbuilder.web.core.websocket.message.imp.LocalMessageManager;
import org.webbuilder.web.service.user.UserService;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-08 0008.
 */
public class MessageProcessor implements CmdProcessor {

    /**
     * 点对点发送消息
     */
    public static final String TYPE_P2P = "p2p";

    /**
     * 点对群发送消息
     */
    public static final String TYPE_P2G = "p2g";

    @Resource
    private UserService userService;

    private WebSocketMessageManager messageManager;

    public static String NAME = "message";

    private String name = NAME;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = NAME = name;
    }

    @Override
    public CmdResponse exec(CmdRequest request) {
        Map<String, Object> param = request.getParams();
        String callBack = (String) param.get("callBack");
        if (callBack == null) callBack = request.getCmd();
        Object type = param.get("type");
        try {
            Assert.notNull(type, "param.type is null!");
            switch (type.toString()) {
                case TYPE_P2P:
                    String to = (String) param.get("to");
                    Assert.hasLength(to, "param.to must be hasLength!");
                    Message message = new Message();
                    message.setFrom(request.getUserId());
                    message.setContent(String.valueOf(param.get("message")));
                    message.setTo(to);
                    message.setCmd(callBack);
                    getMessageManager().send(message);
                    break;
                case TYPE_P2G:

                    break;
                case "start":
                    getMessageManager().bindAutoPush(request.getSession(), getName());
                    break;
                case "stop":
                    getMessageManager().sessionClose(request.getSession(), getName());
                    break;
                default:
                    return new CmdResponse(request.getCmd(), false, "cmd not found!");
            }
        } catch (Exception e) {
            return new CmdResponse(request.getCmd().concat("error"), false, e.getMessage());
        }
        return new CmdResponse(request.getCmd().concat(String.valueOf(type)), true, "success");
    }

    @Override
    public void onSessionConnect(WebSocketSession session) throws Exception {

    }

    @Override
    public void onSessionClose(WebSocketSession session) throws Exception {
        getMessageManager().sessionClose(session, getName());
    }

    @Override
    public void init() throws Exception {
        CmdExecutor.registerCmd(this);
    }

    public WebSocketMessageManager getMessageManager() {
        if (messageManager == null)
            messageManager = new LocalMessageManager();
        return messageManager;
    }

    public void setMessageManager(WebSocketMessageManager messageManager) {
        this.messageManager = messageManager;
    }

}
