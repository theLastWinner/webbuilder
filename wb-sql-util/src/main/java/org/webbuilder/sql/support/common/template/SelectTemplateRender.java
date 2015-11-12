package org.webbuilder.sql.support.common.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.param.*;
import org.webbuilder.sql.param.query.GroupBy;
import org.webbuilder.sql.param.query.OrderBy;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;
import org.webbuilder.utils.base.StringUtil;

import java.util.*;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class SelectTemplateRender implements SqlTemplate {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected TableMetaData tableMetaData;

    protected KeywordsMapper keywordsMapper;

    public void setKeywordsMapper(KeywordsMapper keywordsMapper) {
        this.keywordsMapper = keywordsMapper;
    }

    public SelectTemplateRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public String getTemplate() {
        return "这个渲染器是运行时渲染，无模板！";
    }

    @Override
    public TYPE getType() {
        return TYPE.SELECT;
    }

    /**
     * 渲染需要查询的字段信息
     *
     * @param config      渲染配置选项
     * @param sqlAppender sql拼接器
     * @return
     */
    protected Set<String> renderSelectFields(SqlRenderConfig config, SqlAppender sqlAppender) {
        Set<IncludeField> includes = config.getIncludes();//指定需要查询的字段
        Set<String> excludes = config.getExcludes();//排除需要查询的字段,指定了includes，此选项忽略
        //需要查询的关联表
        Set<String> needSelectTable = new LinkedHashSet<>();
        //includes未指定或者指定* 则认定查询所有字段
        if (includes.size() == 0 || includes.contains("*")) {
            for (FieldMetaData metaData : tableMetaData.getFields()) {
                if (excludes.contains(metaData.getName())) continue;
                sqlAppender.addEdSpc(tableMetaData.getName(), ".", metaData.getName());
                sqlAppender.addSpc("as");
                sqlAppender.add(keywordsMapper.getSpecifierPrefix());
                sqlAppender.add(metaData.getName());
                sqlAppender.add(keywordsMapper.getSpecifierSuffix());
                sqlAppender.add(",");
            }
        }
        if (includes.size() != 0) {
            for (IncludeField include : includes) {
                include.setMainTable(tableMetaData.getName());
                if (include.isSkipCheck()) {
                    sqlAppender.addSpc(keywordsMapper.getFieldTemplate(include));
                    sqlAppender.add(",");
                    continue;
                }
                String field = include.getField();
                //查询关联表的所有字段
                if (include.isAnotherTable() && "*".equals(field)) {  //多表关联
                    String targetTableName = include.getTargetTable();//关联表
                    TableMetaData.Correlation correlation = tableMetaData.getCorrelation(targetTableName);
                    //未设置关联条件,或者不是1对1关联
                    if (!tableMetaData.hasCorrelation(targetTableName) || !correlation.isOne2one()) {
                        continue;
                    }
                    needSelectTable.add(targetTableName);
                    //元数据
                    TableMetaData targetTableMetaData = tableMetaData.getCorrelationTable(targetTableName);
                    if (targetTableMetaData != null) {
                        //查询关联表的所有字段
                        for (FieldMetaData fieldMetaData : targetTableMetaData.getFields()) {
                            IncludeField tmp_inc = new IncludeField(fieldMetaData.getFullName());
                            tmp_inc.setMainTable(tableMetaData.getName());
                            sqlAppender.addSpc(keywordsMapper.getFieldTemplate(tmp_inc));
                            sqlAppender.add(",");
                        }
                    }
                }
                //能直接进行查询
                if (tableMetaData.hasField(include.getFullField())) {
                    if (include.getTargetTable() != null)
                        needSelectTable.add(include.getTargetTable());
                    sqlAppender.addSpc(keywordsMapper.getFieldTemplate(include));
                    sqlAppender.add(",");
                }
            }
        }
        //删除最后一个逗号
        if (sqlAppender.size() > 0)
            sqlAppender.remove(sqlAppender.size() - 1);
        return needSelectTable;
    }

    public WrapperCondition renderCondition(Set<ExecuteCondition> conditions, SqlAppender sqlAppender) {
        WrapperCondition wrapperCondition = new WrapperCondition();
        wrapperCondition.setTables(new LinkedHashSet<String>());
        wrapperCondition.setParams(new LinkedHashMap<String, Object>());
        int flag = 0;
        for (ExecuteCondition condition : conditions) {
            if (condition.getTable() == null)
                condition.setTable(tableMetaData.getName());
            String field = condition.getFullField();
            if (!tableMetaData.hasField(field)) continue;
            condition.setTableMetaData(tableMetaData);
            if (!field.contains(".")) condition.setTable(tableMetaData.getName());
            if (flag++ == 0) condition.setAppendType("");

            WrapperCondition tmp = keywordsMapper.wrapperCondition(condition);
            wrapperCondition.getTables().addAll(tmp.getTables());
            wrapperCondition.getParams().putAll(tmp.getParams());
            sqlAppender.addSpc(tmp.getTemplate());
        }
        return wrapperCondition;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        if (logger.isInfoEnabled()) {
            logger.info("start build {} sql for {}", getType(), tableMetaData);
        }
        CommonSql sql = new CommonSql();
        sql.setTableMetaData(tableMetaData);
        Map<String, Object> param = new LinkedHashMap<>();
        //需要查询的字段
        SqlAppender select = new SqlAppender();
        Set<String> tables = renderSelectFields(config, select);
        if (select.size() == 0) {
            config.include("*");
            tables.addAll(renderSelectFields(config, select));
        }
        tables.remove(tableMetaData.getName());

        SqlAppender where = new SqlAppender();
        WrapperCondition wrapperCondition = renderCondition(config.getConditions(), where);
        Map<String, Object> param_tmp = wrapperCondition.getParams();
        tables.addAll(wrapperCondition.getTables());
        //排序查询
        SqlAppender orderBy = new SqlAppender();
        tables.addAll(buildOrderBy(config, orderBy));
        //分组查询
        SqlAppender groupBy = new SqlAppender();
        tables.addAll(buildGroupBy(config, groupBy));

        SqlAppender join = new SqlAppender();
        //表链接
        if (tables.size() > 0) {
            param.putAll(buildJoin(tables, join));
        }
        param.putAll(param_tmp);
        SqlAppender sqlAppender = new SqlAppender();
        sqlAppender.addSpc("select");
        sqlAppender.addAll(select);
        sqlAppender.addSpc("from");
        sqlAppender.addSpc(tableMetaData.getName());
        if (join.size() > 0) {
            sqlAppender.addAll(join);
        }
        if (where.size() > 0) {
            sqlAppender.addEdSpc("where");
            sqlAppender.addAll(where);
        }
        if (groupBy.size() > 0) {
            sqlAppender.addEdSpc("group by");
            sqlAppender.addAll(groupBy);
        }
        if (orderBy.size() > 0) {
            sqlAppender.addEdSpc("order by");
            sqlAppender.addAll(orderBy);
        }
        String sql_string = sqlAppender.toString();
        if (config instanceof QueryParam) {
            QueryParam queryParam = (QueryParam) config;
            if (queryParam.isPaging()) {
                sql_string = keywordsMapper.pager(sql_string, queryParam.getPageIndex(), queryParam.getPageSize());
            }
        }
        sql.setSql(sql_string);
        sql.setParams(param);
        return sql;
    }

    protected Map<String, Object> buildJoin(Set<String> tables, SqlAppender appender) {
        Map<String, Object> param = new LinkedHashMap<>();
        for (String table : tables) {
            if (table.equals(tableMetaData.getName())) continue;
            TableMetaData.Correlation correlation = tableMetaData.getCorrelation(table);
            TableMetaData.Correlation.MOD mod = correlation.getMod();
            if (mod == null) continue;
            //初始化表链接 left join {tableName} on {conditions}
            appender.addSpc(mod.toSql());
            appender.addEdSpc(table);
            appender.addSpc("on");
            WrapperCondition param_tmp = renderCondition(correlation.getCondition(), appender);
            param.putAll(param_tmp.getParams());
        }
        return param;
    }


    private Set<OrderBy> initOrderBy(Object object) {
        Set<OrderBy> orderBy = new LinkedHashSet<>();
        if (object instanceof String) {
            orderBy.add(new OrderBy(String.valueOf(object)));
        } else if (object instanceof OrderBy) {
            orderBy.add((OrderBy) object);
        } else if (object instanceof Collection) {
            for (Object o : (Collection) object) {
                orderBy.addAll(initOrderBy(o));
            }
        }
        return orderBy;
    }


    private Set<GroupBy> initGroupBy(Object object) {
        Set<GroupBy> orderBy = new LinkedHashSet<>();
        if (object instanceof String) {
            orderBy.add(new GroupBy(String.valueOf(object)));
        } else if (object instanceof OrderBy) {
            orderBy.add((GroupBy) object);
        } else if (object instanceof Collection) {
            for (Object o : (Collection) object) {
                orderBy.addAll(initGroupBy(o));
            }
        }
        return orderBy;
    }

    protected Set<String> buildGroupBy(SqlRenderConfig config, SqlAppender appender) {
        Set<String> tables = new LinkedHashSet<>();
        Object object = config.getProperty("group_by");
        if (object == null) return tables;
        Set<GroupBy> orderBy = initGroupBy(object);
        for (GroupBy by : orderBy) {
            by.setMainTable(tableMetaData.getName());
            if (by.isAnotherTable()) {
                tables.add(by.getTargetTable());
            }
            appender.addEdSpc(keywordsMapper.getFieldTemplate(by));
            appender.addEdSpc(",");
        }
        if (orderBy.size() > 0)
            appender.remove(appender.size());
        return tables;
    }

    protected Set<String> buildOrderBy(SqlRenderConfig config, SqlAppender appender) {
        Set<String> tables = new LinkedHashSet<>();
        Object object = config.getProperty("order_by");
        Object mod = config.getProperty("order_by_mod");
        if (mod == null) mod = "asc";
        if (object == null) return tables;
        Set<OrderBy> orderBy = initOrderBy(object);
        boolean hasMore = false;
        for (OrderBy by : orderBy) {
            if (hasMore)
                appender.addEdSpc(",");
            by.setMainTable(tableMetaData.getName());
            if (by.isAnotherTable()) {
                tables.add(by.getTargetTable());
            }
            appender.addEdSpc(keywordsMapper.getFieldTemplate(by));
            hasMore = true;
        }
        appender.addEdSpc(String.valueOf(mod));
        return tables;
    }

    @Override
    public void reload() throws SqlRenderException {

    }

}
