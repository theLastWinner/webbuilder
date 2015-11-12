package org.webbuilder.sql.support.common.template;

import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.WrapperCondition;
import org.webbuilder.sql.support.common.CommonSql;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class DeleteTemplateRender extends UpdateTemplateRender {
    public DeleteTemplateRender(TableMetaData tableMetaData) {
        super(tableMetaData);
    }

    @Override
    public TYPE getType() {
        return TYPE.DELETE;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        SqlAppender appender = new SqlAppender();
        appender.addSpc("delete", "from", tableMetaData.getName());
        SqlAppender where = new SqlAppender();
        WrapperCondition condition = renderCondition(config.getConditions(), where);
        if (where.size() == 0) {
            throw new SqlRenderException("No update condition is set!");
        }
        appender.add("where");
        appender.addAll(where);
        CommonSql sql = new CommonSql();
        sql.setTableMetaData(tableMetaData);
        sql.setParams(condition.getParams());
        sql.setSql(appender.toString());
        return sql;
    }
}
