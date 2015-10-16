package org.webbuilder.web.core.websocket.cmd;


import org.springframework.web.socket.WebSocketSession;

/**
 * 命令执行器
 * Created by 浩 on 2015-09-08 0008.
 */
public interface CmdProcessor {
    /**
     * 获取命令名称
     *
     * @return 命令名称
     */
    String getName();

    /**
     * 执行命令
     *
     * @param request 命令请求
     * @return 执行结果
     * @throws Exception 异常
     */
    CmdResponse exec(CmdRequest request) throws Exception;

    /**
     * 当session创建时，调用此方法
     *
     * @param session WebSocketSession 实例
     * @throws Exception
     */
    void onSessionConnect(WebSocketSession session) throws Exception;

    /**
     * 当session关闭时，调用此方法
     *
     * @param session WebSocketSession 实例
     * @throws Exception
     */
    void onSessionClose(WebSocketSession session) throws Exception;

    /**
     * 初始化方法，用于自动注册命令等操作
     *
     * @throws Exception
     */
    void init() throws Exception;
}
