package org.webbuilder.sql.support.common;

import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.trigger.TriggerResult;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public abstract class TriggerExecutor {

    public abstract TableMetaData getTableMetaData();

    private DataBase dataBase;

    private Table table;

    public boolean isSkipTrigger(SqlRenderConfig config) {
        return String.valueOf(config.get("skipTrigger")).equalsIgnoreCase("true");
    }

    public Object tryExecuteTrigger(String triggerName, Map<String, Object> root) throws Exception {
        return tryExecuteTrigger(triggerName, root, false);
    }

    public Object tryExecuteTrigger(String triggerName, Map<String, Object> root, boolean skipError) throws Exception {
        try {
            root.put("table", table);
            root.put("dataBase", dataBase);
            if (getTableMetaData().triggerSupport(triggerName)) {
                TriggerResult res = getTableMetaData().on(triggerName, root);
                if (!res.isSuccess()) {
                    throw new TriggerException(res.getMessage());
                }
                return res.getData();
            }
        } catch (Exception e) {
            if (!skipError) throw e;
        }
        return null;
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
