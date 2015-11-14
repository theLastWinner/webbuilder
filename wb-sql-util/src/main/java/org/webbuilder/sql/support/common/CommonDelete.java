package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Constant;
import org.webbuilder.sql.Delete;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.param.delete.DeleteParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;
import org.webbuilder.sql.trigger.TriggerResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonDelete extends TriggerExecutor implements Delete {
    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;

    public CommonDelete(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public int delete(DeleteParam param) throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("param", param);
        //尝试执行触发器
        tryExecuteTrigger(Constant.TRIGGER_DELETE_BEFORE, root);
        SQL sql = sqlTemplate.render(param);
        int i = sqlExecutor.delete(sql);
        root.put("data", i);
        tryExecuteTrigger(Constant.TRIGGER_DELETE_DONE, root);
        return i;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return sqlTemplate.getTableMetaData();
    }

}
