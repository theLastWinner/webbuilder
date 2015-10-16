package org.webbuilder.web.service.basic.sql;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.bean.PageUtil;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-09 0009.
 */
public class SqlExecutor {

    private final SqlSession sqlSession;

    private final String basicSql;

    private Map<String, Object> condition;

    public SqlExecutor(SqlSession sqlSession, String basicSql) {
        this.sqlSession = sqlSession;
        this.basicSql = basicSql;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public SqlExecutor setCondition(Map<String, Object> condition) {
        this.condition = condition;
        return this;
    }

    public List<Object> list() throws Exception {
        final List<Object> objects = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>(getCondition());
        condition.put("$sql", basicSql);
        sqlSession.select("BasicMapper.selectSql", condition, new ResultHandler() {
            @Override
            public void handleResult(ResultContext context) {
                objects.add(context.getResultObject());
            }
        });
        return objects;
    }

    public int total() throws Exception {
        StringBuilder newSql = new StringBuilder("select count(0) as total from (").append(basicSql).append(")");
        final List<Integer> objects = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>(getCondition());
        condition.put("$sql", newSql.toString());
        sqlSession.select("BasicMapper.selectSql", condition, new ResultHandler() {
            @Override
            public void handleResult(ResultContext context) {
                if (context.getResultCount() == 1) {
                    Object data = context.getResultObject();
                    if (data instanceof Map) {
                        Map<String, Object> map = (Map) data;
                        Object val;
                        if ((val = map.get("total")) != null || (val = map.get("TOTAL")) != null)
                            objects.add(StringUtil.toInt(val));
                    }
                }
            }
        });
        if (objects.size() > 0) {
            return objects.get(0);
        }
        return 0;
    }

    public Object single() throws Exception {
        final List<Object> objects = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>(getCondition());
        condition.put("$sql", basicSql);
        //只查询一条数据
        condition.put(PageUtil.PAGE_INDEX, 0);
        condition.put(PageUtil.MAX_RESULTS, 1);
        condition.put(PageUtil.FIRST_RESULT, 0);
        sqlSession.select("BasicMapper.selectSql", condition, new ResultHandler() {
            @Override
            public void handleResult(ResultContext context) {
                if (context.getResultCount() != 1) {
                    context.stop();
                }
                objects.add(context.getResultObject());
            }
        });
        if (objects.size() > 0) {
            return objects.get(0);
        }
        return null;
    }

}
