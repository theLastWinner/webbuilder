package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

/**
 * 进行count查询的render实例，除了基础模板，其他沿用了SelectRender
 * Created by 浩 on 2015-07-06 0006.
 */
public class TotalRender extends SelectRender {
    public TotalRender(TableMetaData metaData) {
        super(metaData);
    }

    @Override
    public SqlInfo render(SqlRenderConfig config) throws Exception {
        return super.render(config);
    }

    @Override
    public void init() throws Exception {
        //初始化模板
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(0) AS \"total\" FROM ");
        //查询表
        builder.append(tablesNames());
        //查询条件模板
        builder.append(conditionTemplate());
        setTemplate(builder.toString());
        initTableHash();
    }

}
