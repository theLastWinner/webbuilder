package org.webbuilder.utils.db.def;


import org.webbuilder.utils.db.imp.oracle.OracleDataBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-07-05 0005.
 */
public class DataBaseStorage {
    private DataBaseStorage() {
    }

    private static final Map<String, DataBase> BASE = new ConcurrentHashMap<>();

    public static OracleDataBase DEFAULT_DATABASE = new OracleDataBase("DEFAULT");

    public static DataBase getDataBase() {
        return DEFAULT_DATABASE;
    }

    public static DataBase getDataBase(String name) {
        return BASE.get(name);
    }

    public static DataBase register(DataBase dataBase) {
         BASE.put(dataBase.getName(), dataBase);
        return dataBase;
    }


}
