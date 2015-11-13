package org.webbuilder.sql.support.common.template;

import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.WrapperCondition;
import org.webbuilder.sql.param.update.SetField;
import org.webbuilder.sql.param.update.UpdateParam;
import org.webbuilder.sql.support.common.CommonSql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-12 0012.
 */
public class UpdateTemplateRender extends SelectTemplateRender {

    public UpdateTemplateRender(TableMetaData tableMetaData) {
        super(tableMetaData);
    }

    @Override
    public TYPE getType() {
        return TYPE.UPDATE;
    }

    protected Map<String, Object> renderFields(Set<SetField> fields, SqlAppender appender) {
        Map<String, Object> param = new LinkedHashMap<>();
        for (SetField field : fields) {
            field.setMainTable(tableMetaData.getName());
            if (field.isSkipCheck()) {
                appender.addEdSpc(field.getField(), String.format("=${%s}", field.getField()));
                param.put(field.getField(), field.getValue());
                appender.add(",");
            } else if (tableMetaData.hasField(field.getField())) {
                FieldMetaData fieldMetaData = tableMetaData.getField(field.getField());
                if (fieldMetaData != null && fieldMetaData.isCanUpdate()) {
                    appender.addEdSpc(field.getField(), String.format("=#{%s}", field.getField()));
                    param.put(field.getField(), field.getValue());
                    appender.add(",");
                }
            }
        }
        if (appender.size() > 0 && ",".equals(appender.getLast()))
            appender.removeLast();
        return param;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        if (!(config instanceof UpdateParam)) {
            throw new SqlRenderException("config not instance of UpdateParam");
        }
        SqlAppender appender = new SqlAppender();
        Map<String, Object> param_map = new LinkedHashMap<>();

        SqlAppender fields = new SqlAppender();
        param_map.putAll(renderFields((Set) config.getIncludes(), fields));
        if (fields.size() == 0) {
            throw new SqlRenderException("Not set the column to be updated!");
        }

        SqlAppender where = new SqlAppender();
        WrapperCondition condition = renderCondition(config.getConditions(), where);
        if (where.size() == 0) {
            throw new SqlRenderException("No update condition is set!");
        }

        Set<String> tables = condition.getTables();
        param_map.putAll(condition.getParams());
        SqlAppender join = new SqlAppender();
        param_map.putAll(buildJoin(tables, join));

        appender.addSpc("update", tableMetaData.getName(), "set");
        appender.addAll(fields);
//        appender.addSpc("from", tableMetaData.getName());
//        if (join.size() > 0) {
//            appender.addAll(join);
//        }
        appender.addSpc("where").addAll(where);
        CommonSql sql = new CommonSql();
        sql.setTableMetaData(tableMetaData);
        sql.setParams(param_map);
        sql.setSql(appender.toString());
        return sql;
    }

    @Override
    public void reload() throws SqlRenderException {

    }
}
