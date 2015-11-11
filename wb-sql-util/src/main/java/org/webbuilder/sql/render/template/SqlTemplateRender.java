package org.webbuilder.sql.render.template;

import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;

/**
 * sql渲染器，根据各种条件，渲染出sql模板
 * Created by 浩 on 2015-11-06 0006.
 */
public interface SqlTemplateRender {

    SqlTemplate render(SqlRenderParam param) throws SqlRenderException;

    void init(TableMetaData tableMetaData) throws SqlRenderException;
}
