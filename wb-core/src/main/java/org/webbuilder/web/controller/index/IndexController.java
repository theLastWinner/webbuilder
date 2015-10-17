package org.webbuilder.web.controller.index;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.JsonParam;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.core.utils.http.session.HttpSessionManager;
import org.webbuilder.web.core.utils.http.session.impl.StorageHttpSessionManager;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.form.CustomFormService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-29 0029.
 */
@RestController
public class IndexController {

    @Resource
    private HttpSessionManager httpSessionManager;

    @RequestMapping(value = "/online/total", method = RequestMethod.GET)
    @AccessLogger("获取当前在线人数")
    public Object onlineTotal() {
        try {
            int size = httpSessionManager.getUserTotal();
            return new ResponseMessage(true, size);
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/me/module", method = RequestMethod.GET)
    @AccessLogger("获取用户持有的权限")
    @Authorize
    public Object userRoles() {
        try {
            User user = WebUtil.getLoginUser();
            return user.getModules();
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = "/online", method = RequestMethod.GET)
    @Authorize
    @AccessLogger("获取当前在线人员")
    public Object online() {
        try {
            return new ResponseMessage(true, httpSessionManager.getUserIdList());
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }
    }

    @RequestMapping(value = {"/services"}, method = {RequestMethod.GET}, produces = ResponseMessage.CONTENT_TYPE_JSON)
    @Authorize
    @AccessLogger("批量调用服务")
    public Object services(@RequestParam(value = "resources", defaultValue = "[]") String resourcesJson) {
        List<Map> resources = JSON.parseArray(resourcesJson, Map.class);
        Map<String, Object> data = new LinkedHashMap<>();
        for (Map resource : resources) {
            String serviceName = String.valueOf(resource.get("service"));
            if (serviceName == null) {
                continue;
            }
            Object service;
            try {
                service = ContextLoader.getCurrentWebApplicationContext().getBean(serviceName + "Service");
            } catch (Exception e) {
                continue;
            }
            if (service == null) {
                continue;
            }
            Object resultName = resource.get("resultName");
            Object param = resource.get("param");
            if (param == null || !(param instanceof Map))
                param = new HashMap<>();
            if (StringUtil.isNullOrEmpty(resultName)) {
                resultName = serviceName;
            }
            if (service instanceof GenericService) {
                GenericService service_instance = (GenericService) service;
                try {
                    data.put(String.valueOf(resultName), service_instance.select((Map) param));
                } catch (Exception e) {
                    data.put(String.valueOf(resultName), new ResponseMessage(false, e));
                }
            } else if (service instanceof CustomFormService) {
                CustomFormService service_instance = (CustomFormService) service;
                try {
                    service_instance.select(String.valueOf(((Map) param).get("form_id")), (Map) param);
                } catch (Exception e) {
                    data.put(String.valueOf(resultName), new ResponseMessage(false, e));
                }
            }
        }
        return data;
    }
}
