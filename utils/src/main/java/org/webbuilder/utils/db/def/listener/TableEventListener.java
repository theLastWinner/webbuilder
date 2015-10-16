package org.webbuilder.utils.db.def.listener;


import org.webbuilder.utils.db.def.TableMetaData;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public interface TableEventListener extends Serializable {
    void before(TableMetaData metaData) throws Exception;

    void after(TableMetaData metaData) throws Exception;
}
