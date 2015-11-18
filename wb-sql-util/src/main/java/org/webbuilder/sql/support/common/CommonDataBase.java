package org.webbuilder.sql.support.common;

import org.webbuilder.sql.*;
import org.webbuilder.sql.exception.CreateException;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.alter.AlterParam;
import org.webbuilder.sql.param.query.QueryParam;
import org.webbuilder.sql.render.template.SqlRenderParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.HashMapWrapper;
import org.webbuilder.sql.support.executor.SqlExecutor;
import org.webbuilder.utils.base.StringUtil;

import java.util.Map;

/**
 * 通用的数据库
 * Created by 浩 on 2015-11-09 0009.
 */
public class CommonDataBase implements DataBase {

    /**
     * 数据库元数据
     */
    private DataBaseMetaData metaData;

    /**
     * sql执行器
     */
    private SqlExecutor sqlExecutor;

    /**
     * 构造方法，必须设置元数据和sql执行器，否则该数据库将无法正常工作
     *
     * @param metaData    元数据
     * @param sqlExecutor sql执行器
     */
    public CommonDataBase(DataBaseMetaData metaData, SqlExecutor sqlExecutor) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
    }


    @Override
    public DataBaseMetaData getMetaData() {
        return metaData;
    }

    /**
     * 获取Table对象，用于进行相关的操作
     *
     * @param name 表名
     * @return 表实体
     */
    @Override
    public Table getTable(String name) {
        return new CommonTable(getMetaData().getTableMetaData(name), sqlExecutor, this);
    }

    @Override
    public Table createTable(TableMetaData tableMetaData) throws Exception {
        getMetaData().addTable(tableMetaData);
        Table table = this.getTable(tableMetaData.getName());
        Query query = table.createQuery();
        try {
            query.total(new QueryParam());
            throw new CreateException("该表已存在");
        } catch (CreateException e) {
            throw e;
        } catch (Exception e) {
        }
        tableMetaData.setDataBaseMetaData(getMetaData());
        SqlRenderParam param = new SqlRenderParam();
        param.setType(SqlTemplate.TYPE.CREATE);
        param.setTableMetaData(tableMetaData);
        SqlTemplate template = getMetaData().getRender().render(param);
        SQL sql = template.render(new SqlRenderConfig());
        sqlExecutor.exec(sql);

        return getTable(tableMetaData.getName());
    }

    @Override
    public Table alterTable(TableMetaData tableMetaData) throws Exception {
        Table table = this.getTable(tableMetaData.getName());
        AlterParam alterParam = new AlterParam(tableMetaData);
        Query query = table.createQuery();
        try {
            int i = query.total(new QueryParam());
            if (i == 0) {
                alterParam.setRemoveField(true);
            }
        } catch (Exception e) {
        }
        tableMetaData.setDataBaseMetaData(getMetaData());
        SqlRenderParam param = new SqlRenderParam();
        param.setType(SqlTemplate.TYPE.ALTER);
        //old
        param.setTableMetaData(getMetaData().getTableMetaData(tableMetaData.getName()));
        SqlTemplate template = getMetaData().getRender().render(param);
        SQL sql = template.render(alterParam);
        sqlExecutor.exec(sql);
        getMetaData().addTable(tableMetaData);
        return getTable(tableMetaData.getName());
    }
}
