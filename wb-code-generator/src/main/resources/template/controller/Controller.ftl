package ${config.packageName}.controller.${config.module};

import ${config.packageName}.po.${config.module}.${config.className};
import ${config.packageName}.service.${config.module}.${config.className}Service;
import org.webbuilder.web.core.controller.GenericController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;

/**
* ${config.remark!''}控制器，继承自GenericController,使用rest+json
* Created by generator ${.now}
*/
@Controller
@RequestMapping(value = "/${config.className?uncap_first}")
public class ${config.className}Controller extends GenericController<${config.className},String> {

    //默认服务类
    @Resource
    private ${config.className}Service ${config.className?uncap_first}Service;

    @Override
    public ${config.className}Service getService(){
        return this.${config.className?uncap_first}Service;
    }


}
