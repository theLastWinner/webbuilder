package org.webbuilder.web.core.dao.interceptor.dialect;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.dao.interceptor.SqlWrapper;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public class OracleSqlWrapper extends AbstractSqlWrapper {
    @Override
    public String wrapper(SqlWrapper.WrapperConf conf) {
        StringBuilder builder = new StringBuilder("SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM (");
        builder.append(this.formatSql(conf.getSql())); //sql格式化
        if (!StringUtil.isNullOrEmpty(conf.getSortField())) {
            builder.append(" ORDER BY ").append(conf.getSortField());
            if (!StringUtil.isNullOrEmpty(conf.getSortOrder())) {
                builder.append(" ").append(conf.getSortOrder().toUpperCase().equals("DESC") ? "DESC" : "ASC");
            }
        }
        builder.append(") row_ )");
        int startWith = conf.getMaxResults() * (conf.getPageIndex() + 1);
        builder.append("WHERE rownum_ <= ").append(startWith).append(" and rownum_ > ").append(conf.getFirstResult());
        return builder.toString();
    }

}
