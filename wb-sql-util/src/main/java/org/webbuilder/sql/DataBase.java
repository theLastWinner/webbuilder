package org.webbuilder.sql;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface DataBase {

    DataBaseMetaData getMetaData();

    Table getTable(String name);

    Table createTable(TableMetaData tableMetaData);


}
