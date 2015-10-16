package org.webbuilder.utils.db.render;


import org.webbuilder.utils.db.def.TableMetaData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public interface SqlRenderFactory extends Serializable {
    void registerRenderMapper(SqlRenderType type, Class<? extends SqlRender> renderClass);

    SqlRender getRender(SqlRenderType type, TableMetaData metaData) throws Exception;

    List<SqlRenderType> supportRenders() throws Exception;
}
