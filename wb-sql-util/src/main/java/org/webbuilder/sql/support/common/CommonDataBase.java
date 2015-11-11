package org.webbuilder.sql.support.common;

import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.DataBaseMetaData;
import org.webbuilder.sql.Table;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.support.executor.SqlExecutor;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class CommonDataBase implements DataBase {

    private DataBaseMetaData metaData;

    private SqlExecutor sqlExecutor;

    public CommonDataBase(DataBaseMetaData metaData, SqlExecutor sqlExecutor) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
    }


    @Override
    public DataBaseMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Table getTable(String name) {
        return new CommonTable(getMetaData().getTableMetaData(name), sqlExecutor);
    }

    @Override
    public Table createTable(TableMetaData tableMetaData) {
        return null;
    }
}
