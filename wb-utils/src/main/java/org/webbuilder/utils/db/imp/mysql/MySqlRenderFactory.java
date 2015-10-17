package org.webbuilder.utils.db.imp.mysql;

import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.imp.mysql.render.freemarker.*;
import org.webbuilder.utils.db.imp.oracle.render.freemarker.DeleteRender;
import org.webbuilder.utils.db.imp.oracle.render.freemarker.InsertRender;
import org.webbuilder.utils.db.imp.oracle.render.freemarker.TotalRender;
import org.webbuilder.utils.db.imp.oracle.render.freemarker.UpdateRender;
import org.webbuilder.utils.db.render.KeyWordsMapper;
import org.webbuilder.utils.db.render.SqlRender;
import org.webbuilder.utils.db.render.SqlRenderFactory;
import org.webbuilder.utils.db.render.SqlRenderType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ORACLE数据库render工厂类，用于注册和获取对ORACLE数据库进行各种操作的render实例
 * Created by 浩 on 2015-07-04 0004.
 */
public class MySqlRenderFactory implements SqlRenderFactory {

    //SqlRenderType对应的实例映射集合
    private static final Map<SqlRenderType, Class<? extends SqlRender>> mapper = new ConcurrentHashMap<SqlRenderType, Class<? extends SqlRender>>();

    private static final KeyWordsMapper keyWordsMapper = new MySqlKeyWordsMapper();
    {
        //默认支持的render
        registerRenderMapper(SqlRenderType.SELECT, SelectRender.class);
        registerRenderMapper(SqlRenderType.TOTAL, TotalRender.class);
        registerRenderMapper(SqlRenderType.ALTER, AlterRender.class);
        registerRenderMapper(SqlRenderType.CREATE, CreateRender.class);
        registerRenderMapper(SqlRenderType.INSERT, InsertRender.class);
        registerRenderMapper(SqlRenderType.UPDATE, UpdateRender.class);
        registerRenderMapper(SqlRenderType.DELETE, DeleteRender.class);
    }

    /**
     * 注册一个render
     *
     * @param type  render 类型
     * @param clazz render 类
     */
    public void registerRenderMapper(SqlRenderType type, Class<? extends SqlRender> clazz) {
        mapper.put(type, clazz);
    }

    /**
     * 根据render类型获取一个render
     *
     * @param type
     * @param metaData
     * @return
     * @throws Exception
     */
    public SqlRender getRender(SqlRenderType type, TableMetaData metaData) throws Exception {
        Class<? extends SqlRender> renderClass = mapper.get(type);
        if (renderClass == null)
            throw new ClassNotFoundException(type + " 对应的SqlRender未找到!");
        SqlRender sqlRender = renderClass.getConstructor(TableMetaData.class).newInstance(metaData);
        sqlRender.setKeyWordsMapper(keyWordsMapper);
        metaData.setRenderFactory(this);
        return sqlRender;
    }

    public List<SqlRenderType> supportRenders() throws Exception {
        return new ArrayList<>(mapper.keySet());
    }


}
