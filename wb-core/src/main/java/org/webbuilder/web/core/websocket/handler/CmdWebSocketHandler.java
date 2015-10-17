package org.webbuilder.web.core.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.webbuilder.web.core.websocket.cmd.CmdExecutor;
import org.webbuilder.web.core.websocket.cmd.CmdRequest;
import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.exception.WebSocketException;
import org.webbuilder.web.po.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 基于命令的socket处理器
 * Created by 浩 on 2015-09-08 0008.
 */
public class CmdWebSocketHandler extends TextWebSocketHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = getUser(session);
        if (user == null) return;
        if (logger.isInfoEnabled())
            logger.info("handleMessage,id:{} msg={}", session.getId(), message.getPayload());
        CmdResponse response;
        try {
            //请求命令(json格式):{'cmd':'message','params':{type:'p2p', 'to':'admin', 'message': 'aaa' }}
            CmdRequest request = JSON.parseObject(message.getPayload(), CmdRequest.class);
            request.setSession(session);
            request.setUserId(user.getU_id());
            response= CmdExecutor.exec(request);
        } catch (WebSocketException e) {
            response = new CmdResponse("error",false, e.getMessage());
        } catch (JSONException e) {
            response = new CmdResponse("error",false, "request data error!");
        } catch (Exception e) {
            response = new CmdResponse("error",false, "system error!");
        }
        //命令执行结果回掉
        session.sendMessage(new TextMessage(response.toString()));
    }

    private User getUser(WebSocketSession session) {
        return (User) session.getAttributes().get("user");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = getUser(session);
        if (user == null) {
            session.close(CloseStatus.BAD_DATA.withReason("请登陆!"));
            return;
        }
        CmdExecutor.connectionSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = getUser(session);
        if (user == null) return;
        CmdExecutor.sessionClose(session);
    }

}
