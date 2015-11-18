package org.webbuilder.sql;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface DataBase extends Serializable {

    DataBaseMetaData getMetaData();

    Table getTable(String name);

    Table createTable(TableMetaData tableMetaData) throws Exception;

    Table alterTable(TableMetaData tableMetaData) throws Exception;

}
