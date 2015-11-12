package org.webbuilder.sql;

import org.webbuilder.sql.param.insert.InsertParam;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public interface Insert {

    boolean insert(InsertParam param) throws Exception;
}
