package org.webbuilder.web.controller.form;

import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.controller.GenericController;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.service.form.FormService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 自定义表单控制器，继承自GenericController,使用rest+json
 * Created by generator(by 周浩) 2015-8-1 16:31:30
 */
@RestController
@RequestMapping(value = "/form", produces = ResponseMessage.CONTENT_TYPE_JSON)
@AccessLogger("表单管理")
public class FormController extends GenericController<Form, String> {

    //默认服务类
    @Resource
    private FormService formService;

    @Override
    public FormService getService() {
        return this.formService;
    }

}
