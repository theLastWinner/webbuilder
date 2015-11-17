package org.webbuilder.web.core;

import org.webbuilder.web.service.form.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.webbuilder.web.service.script.DynamicScriptService;

import javax.annotation.Resource;

/**
 * 服务器初始化配置
 * Created by 浩 on 2015-07-22 0022.
 */
public class ServerInitConfig implements ApplicationListener {
    private Logger logger = LoggerFactory.getLogger(ServerInitConfig.class);

    @Resource
    private FormService formService;

    @Resource
    private DynamicScriptService dynamicScriptService;


    public void init() {
        try {
            dynamicScriptService.compileAll();
        } catch (Exception e) {
            logger.error("compile script error!", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

    }
}
