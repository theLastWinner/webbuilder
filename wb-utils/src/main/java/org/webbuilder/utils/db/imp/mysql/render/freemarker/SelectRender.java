package org.webbuilder.utils.db.imp.mysql.render.freemarker;

import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.render.SqlRender;

/**
 * Created by 浩 on 2015-07-05 0005.
 */
public class SelectRender extends org.webbuilder.utils.db.imp.oracle.render.freemarker.SelectRender {
    @Override
    public String getQuotesEnd() {
        return "`";
    }

    @Override
    public String getQuotesStart() {
        return "`";
    }

    public SelectRender(TableMetaData metaData) {
        super(metaData);
    }

    @Override
    public void init() throws Exception {
        //初始化模板
        StringBuilder builder = new StringBuilder();
        //查询字段
        builder.append(" SELECT ${__SELECTFIELDS!'u.*'} FROM ");
        //查询表
        builder.append(tablesNames());
        //查询条件模板
        builder.append(conditionTemplate());
        //分组判断模板
        builder.append("\n<#if ").append(SqlRender.GROUP_BY_KEY).append("??>\n");
        builder.append("\t GROUP BY ${").append(SqlRender.GROUP_BY_KEY).append("}");
        builder.append("\n</#if>");
        //排序判断模板
        builder.append("\n<#if ").append(SqlRender.SORT_FIELD_KEY).append("??>\n");
        builder.append("\t ORDER BY ${").append(SqlRender.SORT_FIELD_KEY).append("}");
        builder.append(" ${").append(SqlRender.SORT_ORDER_KEY).append("!''}");
        builder.append("\n</#if>");
        //分页判断模板
        builder.append("\n<#if ").append(SqlRender.PAGE_FIRST_RESULT_KEY).append("??>\n");
        builder.append("\t LIMIT ")
                .append("@{").append(SqlRender.PAGE_FIRST_RESULT_KEY).append("},")
                .append("@{").append(SqlRender.PAGE_MAX_RESULTS_KEY).append("}");
        builder.append("\n</#if>");
        builder.append(" ${__for_update???string(' FOR UPDATE','')}");
        setTemplate(builder.toString());
        initTableHash();
    }


}


