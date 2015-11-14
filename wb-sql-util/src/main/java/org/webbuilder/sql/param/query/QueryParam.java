package org.webbuilder.sql.param.query;

import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.parser.ExecuteConditionParser;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class QueryParam extends SqlRenderConfig {

    private boolean paging = false;

    private int pageIndex = 0;

    private int pageSize = 50;

    public QueryParam() {
    }

    public QueryParam groupBy(String field) {
        addProperty("group_by", field);
        return this;
    }

    public QueryParam orderBy(boolean desc, String field, String... fields) {
        Set<String> orderBies = new LinkedHashSet<>();
        orderBies.add(field);
        orderBies.addAll(Arrays.asList(fields));
        addProperty("order_by", orderBies);
        addProperty("order_by_mod", desc ? "desc" : "asc");
        return this;
    }

    public QueryParam orderBy(String field) {
        orderBy(false, field);
        return this;
    }

    public void copy(QueryParam queryParam) {
        super.copy(queryParam);
        queryParam.setPaging(this.paging);
        queryParam.setPageIndex(this.getPageIndex());
        queryParam.setPageSize(this.getPageSize());

    }

    public QueryParam(boolean paging) {
        this.paging = paging;
    }

    public QueryParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        paging = true;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public QueryParam noPaging() {
        setPaging(false);
        return this;
    }

    public void doPaging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        if (pageIndex < 0) this.pageIndex = 0;
        if (pageSize <= 0) this.pageSize = 1;
        setPaging(true);
    }

    public QueryParam select(String field, String... fields) {
        this.include(field, fields);
        return this;
    }

    public QueryParam where(String conditionJson) {
        this.getConditions().addAll(ExecuteConditionParser.parseByJson(conditionJson));
        return this;
    }

    public QueryParam where(Map<String, Object> conditionMap) {
        this.getConditions().addAll(ExecuteConditionParser.parseByMap(conditionMap));
        return this;
    }
}
