package org.webbuilder.sql.support.common;

import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.render.template.SqlRenderParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.common.template.SelectTemplateRender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class CommonSqlTemplateRender implements SqlTemplateRender {

    protected Map<String, Map<SqlTemplate.TYPE, SqlTemplate>> cache = new ConcurrentHashMap<>();


    @Override
    public SqlTemplate render(SqlRenderParam param) throws SqlRenderException {
        TableMetaData tableMetaData = param.getTableMetaData();
        String tableName = tableMetaData.getName();
        SqlTemplate sqlTemplate = getTemplate(tableMetaData.getName(), param.getType());
        if (sqlTemplate == null) {
            switch (param.getType()) {
                case SELECT:
                    SelectTemplateRender selectTemplateRender = new SelectTemplateRender(tableMetaData);
                    selectTemplateRender.setKeywordsMapper(tableMetaData.getDataBaseMetaData().getKeywordsMapper());
                    cacheTemplate(tableName, sqlTemplate = selectTemplateRender);
                    break;
            }
        }
        return sqlTemplate;
    }

    protected SqlTemplate getTemplate(String tableName, SqlTemplate.TYPE type) {
        Map<SqlTemplate.TYPE, SqlTemplate> templateMap = cache.get(tableName);
        if (templateMap != null) {
            return templateMap.get(type);
        } else {
            templateMap = new HashMap<>();
            cache.put(tableName, templateMap);
        }
        return null;
    }


    private SqlTemplate cacheTemplate(String tableName, SqlTemplate template) {
        Map<SqlTemplate.TYPE, SqlTemplate> templateMap = cache.get(tableName);
        templateMap.put(template.getType(), template);
        template.reload();
        return template;
    }

    @Override
    public void init(TableMetaData tableMetaData) {

    }
}
