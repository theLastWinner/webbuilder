package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Insert;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonInsert implements Insert {
    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;

    public CommonInsert(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public boolean insert(InsertParam param) throws Exception {
        SQL sql = sqlTemplate.render(param);
        sqlExecutor.insert(sql);
        return true;
    }
}
