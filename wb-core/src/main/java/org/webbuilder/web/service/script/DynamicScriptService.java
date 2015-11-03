package org.webbuilder.web.service.script;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.webbuilder.utils.script.engine.DynamicScriptEngine;
import org.webbuilder.utils.script.engine.DynamicScriptEngineFactory;
import org.webbuilder.utils.script.engine.ExecuteResult;
import org.webbuilder.web.core.aop.transactional.TransactionDisabled;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.script.DynamicScript;
import org.webbuilder.web.dao.script.DynamicScriptMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态脚本服务类
 * Created by generator
 */
@Service
public class DynamicScriptService extends GenericService<DynamicScript, String> {

    private static final String CACHE_KEY = "dynamic_script";

    //默认数据映射接口
    @Resource
    protected DynamicScriptMapper dynamicScriptMapper;

    @Override
    protected DynamicScriptMapper getMapper() {
        return this.dynamicScriptMapper;
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "#pk")
    public DynamicScript selectByPk(String pk) throws Exception {
        return super.selectByPk(pk);
    }

    @Override
    @CacheEvict(value = CACHE_KEY, key = "#data.u_id")
    public int update(DynamicScript data) throws Exception {
        int i = super.update(data);
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(data.getType());
        engine.compile(data.getU_id(), data.getContent());
        return i;
    }

    @Override
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public int update(List<DynamicScript> datas) throws Exception {
        int i = super.update(datas);
        for (DynamicScript data : datas) {
            DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(data.getType());
            engine.compile(data.getU_id(), data.getContent());
        }
        return i;
    }

    @Override
    @CacheEvict(value = CACHE_KEY, key = "#pk")
    public int delete(String pk) throws Exception {
        return super.delete(pk);
    }

    @TransactionDisabled
    public void compile(String id) throws Exception {
        DynamicScript script = this.selectByPk(id);
        if (script == null) throw new BusinessException(String.format("脚本[%s]不存在", id));
        DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(script.getType());
        try {
            engine.compile(script.getU_id(), script.getContent());
        } catch (Exception e) {
            logger.error("compile error!", e);
        }
    }

    @TransactionDisabled
    public void compileAll() throws Exception {
        List<DynamicScript> list = this.select(new HashMap<String, Object>());
        for (DynamicScript script : list) {
            DynamicScriptEngine engine = DynamicScriptEngineFactory.getEngine(script.getType());
            try {
                engine.compile(script.getU_id(), script.getContent());
            } catch (Exception e) {
                logger.error("compile error!", e);
            }
        }
    }

}
