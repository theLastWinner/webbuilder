package org.webbuilder.web.service.script;

import org.springframework.stereotype.Service;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;
import org.webbuilder.web.po.script.DynamicScript;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-29 0029.
 */
@Service
public class DynamicScriptExecutor {

    @Resource
    private DynamicScriptService dynamicScriptService;

    public ExecuteResult exec(String id, Map<String, Object> param) throws Exception {
        DynamicScript data = dynamicScriptService.selectByPk(id);
        if (data == null) {
            ExecuteResult result = new ExecuteResult();
            result.setResult(String.format("script %s not found!", id));
            result.setSuccess(false);
            return result;
        }
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(data.getType());
        return engine.execute(id, param);
    }
}
