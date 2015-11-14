package org.webbuilder.sql.support.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.Constant;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.TriggerException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public class ScriptObjectWrapper implements ObjectWrapper<Object> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private TableMetaData tableMetaData;

    private ObjectWrapper defaultWrapper;

    public ScriptObjectWrapper(TableMetaData tableMetaData, ObjectWrapper defaultWrapper) {
        this.tableMetaData = tableMetaData;
        this.defaultWrapper = defaultWrapper;
    }

    @Override
    public Object newInstance() {
        if (tableMetaData.triggerSupport(Constant.TRIGGER_SELECT_WRAPPER_INSTANCE)) {
            try {
                return tableMetaData.on(Constant.TRIGGER_SELECT_WRAPPER_INSTANCE);
            } catch (Exception e) {
                logger.warn("{} 触发器 {} 执行异常! 已使用默认Wrapper!", tableMetaData.toString(), Constant.TRIGGER_SELECT_WRAPPER_INSTANCE, e);
            }
        } else {
            if (logger.isDebugEnabled())
                logger.debug("{} 触发器 {} 不支持或者未注册! 已使用默认Wrapper!", tableMetaData.toString(), Constant.TRIGGER_SELECT_WRAPPER_INSTANCE);
        }
        return defaultWrapper.newInstance();
    }

    @Override
    public void done(Object instance) {
        if (tableMetaData.triggerSupport(Constant.TRIGGER_SELECT_WRAPPER_DONE)) {
            try {
                Map<String, Object> root = new LinkedHashMap<>();
                root.put("instance", instance);
                tableMetaData.on(Constant.TRIGGER_SELECT_WRAPPER_DONE, root);
            } catch (TriggerException e) {
                logger.warn("{} 触发器 {} 执行异常!", tableMetaData.toString(), Constant.TRIGGER_SELECT_WRAPPER_DONE, e);
            }
        }
    }

    @Override
    public void wrapper(Object instance, int index, String attr, Object value) {
        if (tableMetaData.triggerSupport(Constant.TRIGGER_SELECT_WRAPPER)) {
            try {
                Map<String, Object> root = new LinkedHashMap<>();
                root.put("index", index);
                root.put("attr", attr);
                root.put("instance", instance);
                root.put("value", value);
                tableMetaData.on(Constant.TRIGGER_SELECT_WRAPPER, root);
            } catch (TriggerException e) {
                logger.warn("{} 触发器 {} 执行异常!已使用默认Wrapper", tableMetaData.toString(), Constant.TRIGGER_SELECT_WRAPPER, e);
            }
            defaultWrapper.wrapper(instance, index, attr, value);
        }
    }

}
