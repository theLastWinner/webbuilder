package org.webbuilder.web.service.basic;

import org.webbuilder.web.service.basic.sql.SqlExecutor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-09 0009.
 */
@Service
public class BasicSqlService {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    public SqlExecutor createExecutor(final String sql, final Map<String, Object> condition) {
        return new SqlExecutor(sqlSessionTemplate, sql).setCondition(condition);
    }


}
