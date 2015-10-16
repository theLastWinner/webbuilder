package org.webbuilder.web.controller.module;

import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.controller.GenericController;
import org.webbuilder.web.po.module.Module;
import org.webbuilder.web.service.module.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 系统模块控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-26 11:22:11
 */
@Controller
@RequestMapping(value = "/module")
@AccessLogger("系统模块管理")
@Authorize(module = "module")
public class ModuleController extends GenericController<Module, String> {

    //默认服务类
    @Resource
    private ModuleService moduleService;

    @Override
    public ModuleService getService() {
        return this.moduleService;
    }

    @Override
    @AccessLogger("新增")
    @Authorize(level = "C")
    public Object add(@RequestBody Module object) {
        return super.add(object);
    }


}
