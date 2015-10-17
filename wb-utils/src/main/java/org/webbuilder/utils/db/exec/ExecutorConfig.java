package org.webbuilder.utils.db.exec;

import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exec.helper.MapResultHelper;
import org.webbuilder.utils.db.exec.helper.ResultHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * sql执行配置
 * Created by 浩 on 2015-07-08 0008.
 */
public class ExecutorConfig<T> {
    //要执行的sql信息
    private SqlInfo sqlInfo;

    //sqlSession会话，根据SqlExecutor实现进行相应的session初始化，默认应为 java.sql.Connection
    private Object session;
    //结果处理器，用于生成执行结果；主要对查询语句生效
    private ResultHelper<T> resultHelper;
    //执行的此操作的表定义信息
    private TableMetaData metaData;
    //是否使用缓存，具体缓存策略由SqlExecutor实现
    private boolean useCache = true;

    public ExecutorConfig() {
        this(null, null);
    }

    public ExecutorConfig(SqlInfo sqlInfo, Object session) {
        this(sqlInfo, session, null);
    }

    public ExecutorConfig(SqlInfo sqlInfo, Object session, TableMetaData metaData) {
        this(sqlInfo, session, metaData, null);
    }

    public ExecutorConfig(SqlInfo sqlInfo, Object session, TableMetaData metaData, ResultHelper resultHelper) {
        this.sqlInfo = sqlInfo;
        this.session = session;
        this.metaData = metaData;
        this.resultHelper = resultHelper;
    }

    /**
     * 获取关联sql语句执行配置
     *
     * @return <关联表名，执行配置>
     */
    public Map<String, ExecutorConfig> bindSqlConfigs() {
        Map<String, ExecutorConfig> configs = new HashMap();
        for (Map.Entry<String, SqlInfo> entry : getSqlInfo().getBindSql().entrySet()) {
            ExecutorConfig config = new ExecutorConfig(entry.getValue(), getSession());
            config.setMetaData(getMetaData().getDataBase().getTable(entry.getKey()));
            config.setResultHelper(this.getResultHelper());
            configs.put(entry.getKey(), config);
        }
        return configs;
    }


    public SqlInfo getSqlInfo() {
        return sqlInfo;
    }

    public void setSqlInfo(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public Object getSession() {
        return session;
    }

    public void setSession(Object session) {
        this.session = session;
    }

    public ResultHelper<T> getResultHelper() {
        if (resultHelper == null) {
            //使用默认的ResultHelper
            resultHelper = (ResultHelper<T>) new MapResultHelper();
        }//如果resultHelper未初始化table定义，则进行初始化
        if (resultHelper.getMetaData() == null)
            resultHelper.setMetaData(getMetaData());
        return resultHelper;
    }

    public void setResultHelper(ResultHelper resultHelper) {
        this.resultHelper = resultHelper;
    }

    public TableMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(TableMetaData metaData) {
        this.metaData = metaData;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }
}
