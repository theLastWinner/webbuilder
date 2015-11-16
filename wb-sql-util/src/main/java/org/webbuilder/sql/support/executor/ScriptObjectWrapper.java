package org.webbuilder.sql.support.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.Constant;
import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.trigger.TriggerResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public class ScriptObjectWrapper implements ObjectWrapper<Object> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private TableMetaData tableMetaData;

    private ObjectWrapper defaultWrapper;

    private DataBase dataBase;

    private Table table;

    public ScriptObjectWrapper(TableMetaData tableMetaData, ObjectWrapper defaultWrapper) {
        this.tableMetaData = tableMetaData;
        this.defaultWrapper = defaultWrapper;
    }

    @Override
    public Object newInstance() {
        if (tableMetaData.triggerSupport(Constant.TRIGGER_SELECT_WRAPPER_INSTANCE)) {
            try {
                Map<String, Object> root = new LinkedHashMap<>();
                root.put("table", table);
                root.put("dataBase", dataBase);
                return tableMetaData.on(Constant.TRIGGER_SELECT_WRAPPER_INSTANCE, root).getData();
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
                root.put("table", table);
                root.put("dataBase", dataBase);
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
                root.put("table", table);
                root.put("dataBase", dataBase);
                TriggerResult result = tableMetaData.on(Constant.TRIGGER_SELECT_WRAPPER, root);
                if (result.isSuccess())
                    return;
            } catch (TriggerException e) {
                logger.warn("{} 触发器 {} 执行异常!已使用默认Wrapper", tableMetaData.toString(), Constant.TRIGGER_SELECT_WRAPPER, e);
            }
        }
        defaultWrapper.wrapper(instance, index, attr, value);
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }
}
