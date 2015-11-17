package org.webbuilder.sql;

import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.render.template.SqlRenderParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.render.template.SqlTemplateRender;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public abstract class DataBaseMetaData {

    private Map<String, TableMetaData> tables = new ConcurrentHashMap<>();

    public Set<TableMetaData> showTables() {
        return new HashSet<>(tables.values());
    }

    public TableMetaData getTable(String tableName) {
        return tables.get(tableName);
    }

    public TableMetaData addTable(TableMetaData tableMetaData) {
        tableMetaData.setDataBaseMetaData(this);
        tables.put(tableMetaData.getName(), tableMetaData);
        return tableMetaData;
    }

    public abstract String getName();

    public abstract SqlTemplateRender getRender();

    public SqlTemplate getTemplate(SqlTemplate.TYPE type, TableMetaData tableMetaData) {
        SqlTemplateRender render = this.getRender();
        SqlRenderParam param = new SqlRenderParam();
        param.setTableMetaData(tableMetaData);
        param.setType(type);
        return render.render(param);
    }

    public abstract KeywordsMapper getKeywordsMapper();


    public TableMetaData getTableMetaData(String tableName) {
        return tables.get(tableName);
    }


}
