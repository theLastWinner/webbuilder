package org.webbuilder.web.core.dao.interceptor;

import org.webbuilder.web.core.dao.interceptor.dialect.MysqlSqlWrapper;
import org.webbuilder.web.core.dao.interceptor.dialect.OracleSqlWrapper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class }) })
public class PagerInterceptor implements Interceptor {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final Map<String, SqlWrapper> wrappers = new ConcurrentHashMap<>();

    public String dialect = "oracle";

    static {
        wrappers.put("oracle", new OracleSqlWrapper());
        wrappers.put("mysql", new MysqlSqlWrapper());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) target;
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            String sql = statementHandler.getBoundSql().getSql();
            Object obj = statementHandler.getParameterHandler().getParameterObject();
            if (obj instanceof Map) {
                Map<String, Object> param = (Map) obj;
                SqlWrapper.WrapperConf conf = SqlWrapper.WrapperConf.fromMap(param);
                if (conf != null) {
                    SqlWrapper wrapper = wrappers.get(getDialect());
                    if (wrapper == null) {
                        logger.error("dialect {} not support!", getDialect());
                    } else {
                        conf.setSql(sql);
                        String newSql = wrapper.wrapper(conf);
                        metaStatementHandler.setValue("delegate.boundSql.sql", newSql);
                    }
                }
            }
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
