package org.webbuilder.sql.support.common.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.param.IncludeField;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.insert.InsertField;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class InsertTemplateRender implements SqlTemplate {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected TableMetaData tableMetaData;

    protected KeywordsMapper keywordsMapper;

    public void setKeywordsMapper(KeywordsMapper keywordsMapper) {
        this.keywordsMapper = keywordsMapper;
    }

    public InsertTemplateRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public String getTemplate() {
        return "";
    }

    @Override
    public TYPE getType() {
        return TYPE.INSERT;
    }


    @Override
    public SQL render(SqlRenderConfig config) {
        if (!(config instanceof InsertParam)) {
            throw new SqlRenderException("config not instance of InsertParam");
        }
        InsertParam param = ((InsertParam) config);
        SqlAppender sqlAppender = new SqlAppender();
        SqlAppender fields = new SqlAppender();
        SqlAppender params = new SqlAppender();
        Map<String, Object> param_map = new LinkedHashMap<>();

        for (IncludeField field : param.getIncludes()) {
            InsertField field1 = ((InsertField) field);
            if (tableMetaData.hasField(field.getField())) {
                fields.add(",");
                params.add(",");
                fields.add(field.getField());
                params.add(String.format("#{%s}", field.getField()));
                param_map.put(field.getField(), field1.getValue());
            }
        }
        if (fields.size() == 0) {
            throw new SqlRenderException("Not set the column to be insert!");
        }
        fields.removeFirst();
        params.removeFirst();

        sqlAppender.addSpc("insert", "into", tableMetaData.getName());
        sqlAppender.add("(");
        sqlAppender.addAll(fields);
        sqlAppender.addEdSpc(")");
        sqlAppender.addSpc("values");
        sqlAppender.add("(");
        sqlAppender.addAll(params);
        sqlAppender.add(")");

        CommonSql sql = new CommonSql();
        sql.setTableMetaData(tableMetaData);
        sql.setSql(sqlAppender.toString());
        sql.setParams(param_map);
        return sql;
    }

    @Override
    public void reload() throws SqlRenderException {

    }
}
