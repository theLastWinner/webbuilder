package org.webbuilder.utils.db.render;

import org.webbuilder.utils.db.def.DataBaseType;
import org.webbuilder.utils.db.imp.mysql.MySqlRenderFactory;
import org.webbuilder.utils.db.imp.oracle.OracleRenderFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public class SqlRenderFactoryBuilder {

    private static final Map<DataBaseType, SqlRenderFactory> STORAGE = new ConcurrentHashMap<DataBaseType, SqlRenderFactory>();

    private SqlRenderFactoryBuilder() {
    }

    public static SqlRenderFactory register(DataBaseType type, SqlRenderFactory factory) {
        return STORAGE.put(type, factory);
    }

    public static SqlRenderFactory build(DataBaseType type) {
        return STORAGE.get(type);
    }

    static {
        STORAGE.put(DataBaseType.ORACLE, new OracleRenderFactory());
        STORAGE.put(DataBaseType.MYSQL, new MySqlRenderFactory());
    }
}
