package org.webbuilder.sql.support.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.SQL;
import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringTemplateUtils;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.exec.SQLInfo;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public abstract class AbstractJdbcSqlExecutor implements SqlExecutor {

    public abstract Connection getConnection();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Pattern APPEND_PATTERN = Pattern.compile("(?<=$\\{)(.+?)(?=\\})");
    private static final Pattern PREPARED_PATTERN = Pattern.compile("(?<=#\\{)(.+?)(?=\\})");

    public SQLInfo compileSql(SQL sql) {
        SQLInfo sqlInfo = new SQLInfo();
        String sqlTemplate = sql.getSql();
        Map<String, Object> param = sql.getParams();
        Matcher prepared_matcher = PREPARED_PATTERN.matcher(sqlTemplate);
        Matcher append_matcher = APPEND_PATTERN.matcher(sqlTemplate);
        List<Object> params = new LinkedList<>();

        while (append_matcher.matches()) {
            String group = append_matcher.group();
            Object obj = param.get(group);
            if (obj == null)
                try {
                    obj = ClassUtil.getValueByAttribute(group, param);
                } catch (Exception e) {
                }
            sqlTemplate = sqlTemplate.replaceFirst(StringUtil.concat("$\\{", group.replace("$", "\\$"), "\\}"), String.valueOf(obj));
        }
        while (prepared_matcher.find()) {
            String group = prepared_matcher.group();
            sqlTemplate = sqlTemplate.replaceFirst(StringUtil.concat("#\\{", group.replace("$", "\\$"), "\\}"), "?");
            Object obj = param.get(group);
            if (obj == null)
                try {
                    obj = ClassUtil.getValueByAttribute(group, param);
                } catch (Exception e) {
                }
            params.add(obj);
        }
        sqlInfo.setSql(sqlTemplate);
        sqlInfo.setParam(params.toArray());
        return sqlInfo;
    }

    public abstract void resetConnection(Connection connection);

    @Override
    public <T> List<T> list(SQL sql, ObjectWrapper<T> wrapper) throws Exception {
        SQLInfo info = compileSql(sql);
        logSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> headers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        int index = 0;
        List<T> datas = new ArrayList<>();
        while (resultSet.next()) {
            T data = wrapper.newInstance();
            for (String header : headers) {
                Object value = resultSet.getObject(header);
                wrapper.wrapper(data, index++, header, value);
            }
            wrapper.done(data);
            datas.add(data);
        }
        resetConnection(connection);
        return datas;
    }

    @Override
    public <T> T single(SQL sql, ObjectWrapper<T> wrapper) throws Exception {
        SQLInfo info = compileSql(sql);
        logSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> headers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        int index = 0;
        T data = null;
        if (resultSet.next()) {
            data = wrapper.newInstance();
            for (String header : headers) {
                Object value = resultSet.getObject(header);
                wrapper.wrapper(data, index++, header, value);
            }
            wrapper.done(data);
        }
        resetConnection(connection);
        return data;
    }

    @Override
    public void exec(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        logSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        statement.execute();
        if (sql.getBinds() != null) {
            for (BindSQL bindSQL : sql.getBinds()) {
                exec(bindSQL.getSql());
            }
        }
        resetConnection(connection);
    }

    @Override
    public int update(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        logSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        int i = statement.executeUpdate();
        if (logger.isDebugEnabled())
            logger.debug("{} rows is updated!", i);
        resetConnection(connection);
        return i;
    }

    @Override
    public int delete(SQL sql) throws Exception {
        SQLInfo info = compileSql(sql);
        logSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        int i = statement.executeUpdate();
        if (sql.getBinds() != null) {
            for (BindSQL bindSQL : sql.getBinds()) {
                i += delete(bindSQL.getSql());
            }
            return i;
        }
        logger.debug(i + "rows is delete!");
        resetConnection(connection);
        return i;
    }

    @Override
    public int insert(SQL sql) throws Exception {
        exec(sql);
        return sql.size();
    }

    /**
     * 预编译参数
     *
     * @param statement
     * @param info
     * @throws Exception
     */
    private void preparedParam(PreparedStatement statement, SQLInfo info) throws Exception {
        int index = 1;
        //预编译参数
        for (Object object : info.getParam()) {
            if (object instanceof Date)
                statement.setTimestamp(index++, new java.sql.Timestamp(((Date) object).getTime()));
            else if (object instanceof byte[]) {
                statement.setBlob(index++, new ByteArrayInputStream((byte[]) object));
            } else
                statement.setObject(index++, object);
        }
    }

    protected void logSql(SQLInfo info) {
        if (logger.isDebugEnabled()) {
            logger.debug("execute sql :" + info.getSql());
            logger.debug("params :" + info.paramsString());
        }
    }
}
