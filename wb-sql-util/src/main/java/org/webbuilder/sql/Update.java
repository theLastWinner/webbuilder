package org.webbuilder.sql;

import org.webbuilder.sql.param.update.UpdateParam;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Update {
    int update(UpdateParam param) throws Exception;

}
