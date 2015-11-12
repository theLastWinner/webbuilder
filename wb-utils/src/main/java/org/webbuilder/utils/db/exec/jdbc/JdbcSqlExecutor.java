package org.webbuilder.utils.db.exec.jdbc;

import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.SqlExecutorException;
import org.webbuilder.utils.db.exec.ExecutorConfig;
import org.webbuilder.utils.db.exec.SqlExecutor;
import org.webbuilder.utils.db.exec.helper.ResultHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-08 0008.
 */
public class JdbcSqlExecutor implements SqlExecutor {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Connection getConnection(ExecutorConfig<?> config) throws Exception {
        if (config.getSession() != null && config.getSession() instanceof Connection) {
            if (config.getMetaData() == null) {
                //没有定义TableMetaData,使用temp
                TableMetaData tableMetaData = new TableMetaData("__temp");
                config.setMetaData(tableMetaData);
            }
            return (Connection) config.getSession();
        }
        throw new SqlExecutorException("jdbc connection illegal!");
    }

    public int exec(ExecutorConfig<?> config) throws Exception {
        SqlInfo info = config.getSqlInfo();
        logSql(config);
        int i = 1;
        Connection connection = getConnection(config);
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        statement.execute();
        for (Map.Entry<String, ExecutorConfig> entry : config.bindSqlConfigs().entrySet()) {
            i += exec(entry.getValue());
        }
        return i;
    }

    public <T> List<T> select(ExecutorConfig<T> config) throws Exception {
        SqlInfo info = config.getSqlInfo();
        logSql(config);
        Connection connection = getConnection(config);
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        ResultSet resultSet = statement.executeQuery();
        //获取查询结果的字段信息
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> headers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        ResultHelper<T> helper = config.getResultHelper();
        List<T> result = new ArrayList<T>();
        while (resultSet.next()) {
            T data = helper.newInstance();
            for (String header : headers) {
                helper.initAttr(header, helper.formatData(resultSet.getObject(header)), data);
            }
            //执行多次查询
            for (Map.Entry<String, ExecutorConfig> entry : config.bindSqlConfigs().entrySet()) {
                entry.getValue().getSqlInfo().initForBind(data);
                TableMetaData.Foreign foreign = config.getMetaData().getforeign(entry.getKey());
                if (foreign != null && foreign.getType() == TableMetaData.Foreign.TYPE.ONE2ONE)
                    helper.initAttr(entry.getKey(), this.selectOne(entry.getValue()), data);
                else
                    helper.initAttr(entry.getKey(), this.select(entry.getValue()), data);
            }
            result.add(data);
            helper.onCreated(data);
        }
        return result;
    }

    public <T> T selectOne(ExecutorConfig<T> config) throws Exception {
        SqlInfo info = config.getSqlInfo();
        logSql(config);
        Connection connection = getConnection(config);
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        //预编译参数
        this.preparedParam(statement, info);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        List<String> headers = new ArrayList<String>();
        for (int i = 1; i <= count; i++) {
            headers.add(metaData.getColumnLabel(i));
        }
        ResultHelper<T> helper = config.getResultHelper();
        T data = helper.newInstance();
        if (resultSet.next()) {
            for (String header : headers) {
                helper.initAttr(header, helper.formatData(resultSet.getObject(header)), data);
            }
            //执行多次查询
            for (Map.Entry<String, ExecutorConfig> entry : config.bindSqlConfigs().entrySet()) {
                entry.getValue().getSqlInfo().initForBind(data);
                TableMetaData.Foreign foreign = config.getMetaData().getforeign(entry.getKey());
                if (foreign != null && foreign.getType() == TableMetaData.Foreign.TYPE.ONE2ONE)
                    helper.initAttr(entry.getKey(), this.selectOne(entry.getValue()), data);
                else
                    helper.initAttr(entry.getKey(), this.select(entry.getValue()), data);
            }
            helper.onCreated(data);
        }
        return data;
    }

    public int insert(ExecutorConfig<?> config) throws Exception {
        return this.exec(config);
    }

    private void preparedParam(PreparedStatement statement, SqlInfo info) throws Exception {
        int index = 1;
        //预编译参数
        for (Object object : info.getParams()) {
            if (object instanceof Date)
                statement.setTimestamp(index++, new java.sql.Timestamp(((Date) object).getTime()));
            else if (object instanceof byte[]) {
                statement.setBlob(index++, new ByteArrayInputStream((byte[]) object));
            } else
                statement.setObject(index++, object);

        }
    }

    public void logSql(ExecutorConfig<?> config) {
        SqlInfo info = config.getSqlInfo();
        if (logger.isDebugEnabled()) {
            logger.debug("execute sql :" + info.getSql());
            logger.debug("params :" + info.paramsString());
        }
    }

    public int update(ExecutorConfig<?> config) throws Exception {
        SqlInfo info = config.getSqlInfo();
        logSql(config);
        Connection connection = getConnection(config);
        PreparedStatement statement = connection.prepareStatement(info.getSql());
        this.preparedParam(statement, info);
        int i = statement.executeUpdate();
        logger.debug(i + " data is updated!");
        return i;
    }

    public int delete(ExecutorConfig<?> config) throws Exception {
        return exec(config);
    }

}
