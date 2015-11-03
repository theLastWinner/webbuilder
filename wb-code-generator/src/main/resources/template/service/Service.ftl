package ${config.packageName}.service.${config.module};

import org.webbuilder.web.core.service.GenericService;
import ${config.packageName}.po.${config.module}.${config.className};
import ${config.packageName}.dao.${config.module}.${config.className}Mapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
* ${config.remark!''}服务类
* Created by generator ${create_time!''}
*/
@Service
public class ${config.className}Service extends GenericService<${config.className},String> {

    //默认数据映射接口
    @Resource
    protected ${config.className}Mapper ${config.className?uncap_first}Mapper;

    @Override
    protected ${config.className}Mapper getMapper(){
        return this.${config.className?uncap_first}Mapper;
    }

}
