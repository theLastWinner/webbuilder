package org.webbuilder.web.core.websocket.cmd.processor;

import org.webbuilder.web.core.service.SocketService;
import org.webbuilder.web.core.websocket.cmd.CmdExecutor;
import org.webbuilder.web.core.websocket.cmd.CmdProcessor;
import org.webbuilder.web.core.websocket.cmd.CmdRequest;
import org.webbuilder.web.core.websocket.cmd.CmdResponse;
import org.webbuilder.web.core.websocket.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-09 0009.
 */
public class ServiceProcessor implements CmdProcessor {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String name = "service";

    @Autowired
    private ApplicationContext context;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CmdResponse exec(CmdRequest request) throws Exception {
        Map<String, Object> params = request.getParams();
        String serviceName = (String) params.get("id");
        if (serviceName == null) throw new WebSocketException("service id is null!");
        String serviceClassName = serviceName;
        CmdResponse response = new CmdResponse();
        response.setCmd(request.getCmd());
        try {
            if (context == null) {
                logger.error("spring applicationContext not init!", new NullPointerException());
            }
            SocketService socketService = ContextLoader.getCurrentWebApplicationContext().getBean(serviceName, SocketService.class);
            Object data = socketService.doService(request.getSession(), String.valueOf(params.get("method")), (Map) params.get("params"));
            response.setSuccess(true);
            response.setData(data);
            return response;
        } catch (Exception e) {
            throw new WebSocketException(String.format("service %s is not found!", serviceClassName));
        }
    }

    @Override
    public void onSessionConnect(WebSocketSession session) throws Exception {

    }

    @Override
    public void onSessionClose(WebSocketSession session) throws Exception {

    }

    @Override
    public void init() throws Exception {
        CmdExecutor.registerCmd(this);
    }
}
