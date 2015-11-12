package org.webbuilder.sql.keywords;

import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.WrapperCondition;

/**
 * 关键字映射器
 * Created by 浩 on 2015-11-06 0006.
 */
public interface KeywordsMapper {

    /**
     * sql关键字区分前缀 如 oracle的",mysql的`
     */
    String getSpecifierPrefix();

    /**
     * sql关键字区分后缀 如 oracle的",mysql的`
     */
    String getSpecifierSuffix();

    /**
     * 进行分页
     *
     * @param sql       未分页的sql语句
     * @param pageIndex 开始页
     * @param pageSize  每页行数
     * @return 分页后的sql
     */
    String pager(String sql, int pageIndex, int pageSize);

    /**
     * 根据查询条件，包装对应的sql语句，如: name$LIKE 包装成 name like '%'||?||'%'
     *
     * @param executeCondition
     * @return
     */
    WrapperCondition wrapperCondition(ExecuteCondition executeCondition);

    /**
     * 获取字段的模板，如查询字段 user.name ，返回 user.name as "user.name"
     *
     * @param include 字段
     * @return 字段模板
     */
    String getFieldTemplate(IncludeField include);

}
