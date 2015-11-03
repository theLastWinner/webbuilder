package org.webbuilder.web.controller.script;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;
import org.webbuilder.utils.script.engine.ExecuteResult;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.controller.GenericController;
import org.webbuilder.web.po.role.Role;
import org.webbuilder.web.po.script.DynamicScript;
import org.webbuilder.web.service.script.DynamicScriptExecutor;
import org.webbuilder.web.service.script.DynamicScriptService;

import javax.annotation.Resource;

/**
 * 动态脚本控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-10-27 14:42:34
 */
@RestController
@RequestMapping(value = "/script")
@AccessLogger("动态脚本")
@Authorize(role = Role.SYS_ROLE_ADMIN)
public class DynamicScriptController extends GenericController<DynamicScript, String> {

    //默认服务类
    @Resource
    private DynamicScriptService dynamicScriptService;
    @Resource
    private DynamicScriptExecutor dynamicScriptExecutor;

    @Override
    public DynamicScriptService getService() {
        return this.dynamicScriptService;
    }

    @RequestMapping(value = "/{id:.+}/exec", method = {RequestMethod.GET})
    @Authorize(role = {}, module = {"script"}, level = {"EXEC"})
    public ResponseMessage exec(@PathVariable("id") String id,
                                @RequestParam(value = "param", defaultValue = "{}") String param) {
        ResponseMessage message;
        try {
            ExecuteResult result = dynamicScriptExecutor.exec(id, JSON.parseObject(param));
            message = new ResponseMessage(true, result);
        } catch (Exception e) {
            message = new ResponseMessage(false, e);
        }
        return message;
    }

    @RequestMapping(value = "/compile", method = {RequestMethod.GET})
    public ResponseMessage compileAll() {
        ResponseMessage message;
        try {
            dynamicScriptService.compileAll();
            message = new ResponseMessage(true, "success");
        } catch (Exception e) {
            message = new ResponseMessage(false, e);
        }
        return message;
    }

    @RequestMapping(value = "/compile/{id:.+}", method = {RequestMethod.GET})
    public ResponseMessage compile(@PathVariable("id") String id) {
        ResponseMessage message;
        try {
            dynamicScriptService.compile(id);

            message = new ResponseMessage(true, "success");
        } catch (Exception e) {
            message = new ResponseMessage(false, e);
        }
        return message;
    }
}
