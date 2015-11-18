package org.webbuilder.sql.support.common.oracle;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;
import org.webbuilder.utils.base.StringUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-11-17 0017.
 */
public class OracleTableCreateRender implements SqlTemplate {

    private TableMetaData tableMetaData;

    private List<SqlAppender> template = new ArrayList<>();

    public OracleTableCreateRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    @Override
    public String getTemplate() {
        return template.toString();
    }

    @Override
    public TYPE getType() {
        return TYPE.CREATE;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        CommonSql commonSql = new CommonSql();
        for (int i = 0; i < template.size(); i++) {
            SqlAppender sqlAppender = template.get(i);
            if (i == 0) {
                commonSql.setSql(sqlAppender.toString());
            } else {
                BindSQL bindSQL = new BindSQL();
                bindSQL.setToField("__");
                bindSQL.setSql(new CommonSql(sqlAppender.toString()));
                commonSql.getBinds().add(bindSQL);
            }
        }
        return commonSql;
    }

    protected String buildPrimaryKeyName(String fieldName) {
        return StringUtil.concat(tableMetaData.getName(), "_", fieldName);
    }

    @Override
    public void reload() throws SqlRenderException {
        SqlAppender appender = new SqlAppender();
        appender.addSpc("create", "table", tableMetaData.getName(), "(");
        boolean isFirst = true;
        List<SqlAppender> comments = new LinkedList<>();
        List<SqlAppender> primarykeys = new LinkedList<>();

        for (FieldMetaData fieldMetaData : tableMetaData.getFields()) {
            if (!isFirst) {
                appender.addEdSpc(",");
            }
            appender.addSpc(fieldMetaData.getName(), fieldMetaData.getDataType());
            if (fieldMetaData.isNotNull()) {
                appender.addSpc("not null");
            }
            isFirst = false;
            //注释
            if (!StringUtil.isNullOrEmpty(fieldMetaData.getComment())) {
                SqlAppender comment = new SqlAppender();
                comment.addSpc("comment on column");
                comment.addEdSpc(getTableMetaData().getName(), ".", fieldMetaData.getName());
                comment.addSpc("is", "'", fieldMetaData.getComment(), "'");
                comments.add(comment);
            }
            //主键
            if (fieldMetaData.isPrimaryKey()) {
                SqlAppender primarykey = new SqlAppender();
                primarykey.addSpc("alter table ", tableMetaData.getName(),
                        String.format(" add constraint %s primary key (%s)", buildPrimaryKeyName(fieldMetaData.getName()), fieldMetaData.getName()));
                primarykeys.add(primarykey);
            }
        }
        appender.addEdSpc(")");
        template.add(appender);
        if (tableMetaData.getComment() != null) {
            template.add(new SqlAppender().addSpc(String.format("comment on table %s  is '%s'", tableMetaData.getName(), tableMetaData.getComment())));
        }
        template.addAll(comments);
        template.addAll(primarykeys);
    }

}
