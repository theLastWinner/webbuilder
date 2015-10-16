package org.webbuilder.web.core.dao.interceptor.dialect;

import org.webbuilder.web.core.dao.interceptor.SqlWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public abstract class AbstractSqlWrapper implements SqlWrapper {
    private static final Pattern clean_sql_pattern = Pattern.compile("\\s{2,}+");
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String formatSql(String sql) {
        return sql;
    }
}
