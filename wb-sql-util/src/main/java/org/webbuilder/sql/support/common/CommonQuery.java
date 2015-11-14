package org.webbuilder.sql.support.common;

import org.webbuilder.sql.Constant;
import org.webbuilder.sql.Query;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.query.QueryParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.executor.HashMapWrapper;
import org.webbuilder.sql.support.executor.ObjectWrapper;
import org.webbuilder.sql.support.executor.SqlExecutor;
import org.webbuilder.utils.base.StringUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-10 0010.
 */
public class CommonQuery extends TriggerExecutor implements Query {

    private SqlTemplate sqlTemplate;
    private SqlExecutor sqlExecutor;
    private ObjectWrapper objectWrapper;
    private static ObjectWrapper<Map<String, Object>> DEFAULT_WRAPPER = new HashMapWrapper();

    public CommonQuery(SqlTemplate sqlTemplate, SqlExecutor sqlExecutor) {
        this.sqlTemplate = sqlTemplate;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public <T> List<T> list(QueryParam param) throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("param", param);
        root.put("method", "list");
        tryExecuteTrigger(Constant.TRIGGER_SELECT_BEFORE, root);
        SQL sql = sqlTemplate.render(param);
        List<T> data = sqlExecutor.list(sql, getObjectWrapper());
        root.put("data", data);
        tryExecuteTrigger(Constant.TRIGGER_SELECT_DONE, root, false);
        return data;
    }

    @Override
    public <T> T single(QueryParam param) throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("param", param);
        root.put("method", "single");
        tryExecuteTrigger(Constant.TRIGGER_SELECT_BEFORE, root);
        QueryParam tmp = new QueryParam();
        param.copy(tmp);
        tmp.doPaging(0, 1);
        SQL sql = sqlTemplate.render(param);
        T data = (T) sqlExecutor.single(sql, getObjectWrapper());
        tryExecuteTrigger(Constant.TRIGGER_SELECT_DONE, root, false);
        return data;
    }

    @Override
    public int total(QueryParam param) throws Exception {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("param", param);
        root.put("method", "total");
        tryExecuteTrigger(Constant.TRIGGER_SELECT_BEFORE, root);
        QueryParam tmp = new QueryParam(false);
        tmp.setConditions(param.getConditions());
        tmp.setProperties(param.getProperties());
        IncludeField field = new IncludeField("1");
        field.setAs("total");
        field.setMethod("count");
        field.setSkipCheck(true);
        tmp.include(field);
        tmp.setPaging(false);
        SQL sql = sqlTemplate.render(tmp);
        Map<String, Object> res = sqlExecutor.single(sql, DEFAULT_WRAPPER);
        int total = 0;
        if (res != null) {
            total = StringUtil.toInt(res.get("total"));
        }
        root.put("data", total);
        tryExecuteTrigger(Constant.TRIGGER_SELECT_DONE, root, false);
        return total;
    }

    public SqlExecutor getSqlExecutor() {
        return sqlExecutor;
    }

    public void setSqlExecutor(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public ObjectWrapper getObjectWrapper() {
        if (objectWrapper == null)
            objectWrapper = DEFAULT_WRAPPER;
        return objectWrapper;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        this.objectWrapper = objectWrapper;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return sqlTemplate.getTableMetaData();
    }

}
