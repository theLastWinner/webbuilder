package org.webbuilder.web.core.dao.interceptor.dialect;

import org.webbuilder.utils.base.StringUtil;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public class MysqlSqlWrapper extends AbstractSqlWrapper {
    @Override
    public String wrapper(WrapperConf conf) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.formatSql(conf.getSql())); //sql格式化
        if (!StringUtil.isNullOrEmpty(conf.getSortField())) {
            builder.append(" order by ").append(conf.getSortField());
            if (!StringUtil.isNullOrEmpty(conf.getSortOrder())) {
                builder.append(" ").append(conf.getSortOrder().toLowerCase().equals("desc") ? "desc" : "asc");
            }
        }
        builder.append(" limit ").append(conf.getFirstResult()).append(",").append(conf.getMaxResults());
        return builder.toString();
    }

}
