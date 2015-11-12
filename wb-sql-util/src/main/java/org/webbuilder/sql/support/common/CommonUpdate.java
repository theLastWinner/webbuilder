package org.webbuilder.sql.support.common;

import org.webbuilder.sql.SQL;
import org.webbuilder.sql.Update;
import org.webbuilder.sql.param.update.UpdateParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.SqlExecutor;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class CommonUpdate implements Update {

    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;

    public CommonUpdate(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public int update(UpdateParam param) throws Exception {
        SQL sql = sqlTemplate.render(param);
        return sqlExecutor.update(sql);
    }
}
