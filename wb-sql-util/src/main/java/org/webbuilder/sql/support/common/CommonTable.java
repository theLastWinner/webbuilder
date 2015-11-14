package org.webbuilder.sql.support.common;

import org.webbuilder.sql.*;
import org.webbuilder.sql.exception.DeleteException;
import org.webbuilder.sql.exception.InsertException;
import org.webbuilder.sql.exception.QueryException;
import org.webbuilder.sql.exception.UpdateException;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.executor.HashMapWrapper;
import org.webbuilder.sql.support.executor.ObjectWrapper;
import org.webbuilder.sql.support.executor.ScriptObjectWrapper;
import org.webbuilder.sql.support.executor.SqlExecutor;

import java.util.Map;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class CommonTable implements Table {
    public static ObjectWrapper DEFAULT_WRAPPER = new HashMapWrapper();

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
        CommonQuery query = new CommonQuery(template, sqlExecutor);
        //尝试注册脚本对象包装器
        if (metaData.triggerSupport(Constant.TRIGGER_SELECT_WRAPPER)) {
            ScriptObjectWrapper wrapper = new ScriptObjectWrapper(metaData, DEFAULT_WRAPPER);
            query.setObjectWrapper(wrapper);
        }

        return query;
    }

    @Override
    public Update createUpdate() throws UpdateException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.UPDATE);
        Update update = new CommonUpdate(template, sqlExecutor);
        return update;
    }

    @Override
    public Delete createDelete() throws DeleteException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.DELETE);
        Delete update = new CommonDelete(template, sqlExecutor);
        return update;
    }

    @Override
    public Insert createInsert() throws InsertException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.INSERT);
        Insert update = new CommonInsert(template, sqlExecutor);
        return update;
    }
}
