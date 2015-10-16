package org.webbuilder.utils.db.imp;


import org.webbuilder.utils.db.def.DataBase;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.render.SqlRenderFactoryBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-07-06 0006.
 */
public abstract class CommonDataBase extends DataBase {
    private final static Map<String, TableMetaData> BASE = new ConcurrentHashMap<String, TableMetaData>();

    public CommonDataBase(String name) {
        super(name);
    }


    public TableMetaData getTable(String name) {
        TableMetaData metaData = BASE.get(name);
        if(metaData==null)
            return null;
        //返回克隆的对象，避免信息被误修改，要需改表信息只能通过putTable
        return metaData.clone();
    }

    public TableMetaData putTable(TableMetaData metaData) throws Exception {
        metaData.setDataBase(this);
        metaData.setRenderFactory(SqlRenderFactoryBuilder.build(getType()));
        return BASE.put(metaData.getName(), metaData);
    }


}
