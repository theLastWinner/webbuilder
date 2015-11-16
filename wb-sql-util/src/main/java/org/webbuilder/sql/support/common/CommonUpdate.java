package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Constant;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.Update;
import org.webbuilder.sql.exception.TriggerException;
import org.webbuilder.sql.param.update.UpdateParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;
import org.webbuilder.sql.trigger.TriggerResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonUpdate extends TriggerExecutor implements Update {

    private SqlTemplate sqlTemplate;

    private SqlExecutor sqlExecutor;

    public CommonUpdate(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public int update(UpdateParam param) throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("param", param);
        //尝试执行触发器
        if (!isSkipTrigger(param))
            tryExecuteTrigger(Constant.TRIGGER_UPDATE_BEFORE, root);
        SQL sql = sqlTemplate.render(param);
        int i = sqlExecutor.update(sql);
        root.put("length", i);
        //尝试执行触发器
        if (!isSkipTrigger(param))
            tryExecuteTrigger(Constant.TRIGGER_UPDATE_DONE, root);
        return i;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return sqlTemplate.getTableMetaData();
    }

}
