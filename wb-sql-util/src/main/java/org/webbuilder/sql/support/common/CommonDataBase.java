package org.webbuilder.sql.support.common;

import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.DataBaseMetaData;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.support.executor.SqlExecutor;

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
    public Table createTable(TableMetaData tableMetaData) {
        return null;
    }
}
