package org.webbuilder.sql.render.template;

import org.webbuilder.sql.SQL;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlRenderConfig;

/**
 * 渲染后的sql模板
 * Created by 浩 on 2015-11-06 0006.
 */
public interface SqlTemplate {
    String getTemplate();

    TYPE getType();

    SQL render(SqlRenderConfig config);

    void reload() throws SqlRenderException;

    enum TYPE {
        CREATE, ALTER, SELECT, UPDATE, DELETE
    }
}
