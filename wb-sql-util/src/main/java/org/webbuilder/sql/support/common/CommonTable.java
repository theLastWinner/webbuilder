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

    private DataBase dataBase;

    public CommonTable(TableMetaData metaData, SqlExecutor sqlExecutor, DataBase dataBase) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
        this.dataBase = dataBase;
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
        query.setDataBase(dataBase);
        query.setTable(this);
        //注册wrapper触发器
        ScriptObjectWrapper wrapper = new ScriptObjectWrapper(metaData, DEFAULT_WRAPPER);
        wrapper.setTable(this);
        wrapper.setDataBase(dataBase);
        query.setObjectWrapper(wrapper);
        return query;
    }

    @Override
    public Update createUpdate() throws UpdateException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.UPDATE);
        CommonUpdate update = new CommonUpdate(template, sqlExecutor);
        update.setTable(this);
        update.setDataBase(dataBase);
        return update;
    }

    @Override
    public Delete createDelete() throws DeleteException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.DELETE);
        CommonDelete delete = new CommonDelete(template, sqlExecutor);
        delete.setTable(this);
        delete.setDataBase(dataBase);
        return delete;
    }

    @Override
    public Insert createInsert() throws InsertException {
        SqlTemplate template = metaData.getTemplate(SqlTemplate.TYPE.INSERT);
        CommonInsert insert = new CommonInsert(template, sqlExecutor);
        insert.setTable(this);
        insert.setDataBase(dataBase);
        return insert;
    }
}
