package org.webbuilder.sql.support.common;

import org.webbuilder.sql.*;
import org.webbuilder.sql.exception.DeleteException;
import org.webbuilder.sql.exception.QueryException;
import org.webbuilder.sql.exception.UpdateException;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.executor.SqlExecutor;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class CommonTable implements Table {

    private TableMetaData metaData;

    private SqlExecutor sqlExecutor;

    public CommonTable(TableMetaData metaData, SqlExecutor sqlExecutor) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public TableMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(TableMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public Query createQuery() throws QueryException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.SELECT);
        Query query = new CommonQuery(template, sqlExecutor);
        return query;
    }

    @Override
    public Update createUpdate() throws UpdateException {
        return null;
    }

    @Override
    public Delete createDelete() throws DeleteException {
        return null;
    }
}
