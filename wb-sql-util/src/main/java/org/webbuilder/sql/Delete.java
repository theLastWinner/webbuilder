package org.webbuilder.sql;

import org.webbuilder.sql.param.delete.DeleteParam;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Delete {
    int delete(DeleteParam param) throws Exception;
}
