package org.webbuilder.sql.keywords;

import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.WrapperCondition;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface KeywordsMapper {
    String getSpecifierPrefix();

    String getSpecifierSuffix();

    String pager(String sql, int pageIndex, int pageSize);

    WrapperCondition wrapperCondition(ExecuteCondition executeCondition);

    String getFieldTemplate(IncludeField include);

}
