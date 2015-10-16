package org.webbuilder.utils.db.def;

import org.webbuilder.utils.db.def.parser.TableParser;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public abstract class DataBase implements Serializable {
    private String name;

    public DataBase(String name) {
        this.name = name;
    }

    public abstract DataBaseType getType();

    public abstract TableMetaData getTable(String name);

    public abstract TableMetaData putTable(TableMetaData metaData) throws Exception;

    public abstract TableMetaData createTable(TableMetaData metaData, Object session) throws Exception;

    public abstract TableMetaData updateTable(TableMetaData metaData, Object session) throws Exception;

    public abstract TableMetaData dropTable(TableMetaData metaData, Object session) throws Exception;

    public abstract TableParser getParser() throws Exception;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
