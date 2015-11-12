package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Delete;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.param.delete.DeleteParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonDelete implements Delete {
    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;

    public CommonDelete(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public int delete(DeleteParam param) throws Exception {
        SQL sql = sqlTemplate.render(param);
        int i = sqlExecutor.delete(sql);
        return i;
    }
}
