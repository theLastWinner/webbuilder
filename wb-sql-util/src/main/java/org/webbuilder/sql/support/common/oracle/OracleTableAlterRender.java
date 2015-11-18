package org.webbuilder.sql.support.common.oracle;

import org.webbuilder.sql.BindSQL;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.SQL;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.exception.SqlRenderException;
import org.webbuilder.sql.param.SqlAppender;
import org.webbuilder.sql.param.SqlRenderConfig;
import org.webbuilder.sql.param.alter.AlterParam;
import org.webbuilder.sql.render.template.SqlTemplate;
import org.webbuilder.sql.support.common.CommonSql;
import org.webbuilder.utils.base.DateTimeUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-11-18 0018.
 */
public class OracleTableAlterRender implements SqlTemplate {

    private TableMetaData tableMetaData;

    public OracleTableAlterRender(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public void setTableMetaData(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    @Override
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    @Override
    public String getTemplate() {
        return "";
    }

    @Override
    public TYPE getType() {
        return TYPE.ALTER;
    }

    @Override
    public SQL render(SqlRenderConfig config) {
        AlterParam alterParam = ((AlterParam) config);
        TableMetaData newTable = alterParam.getNewTable();
        List<SqlAppender> newFieldSqlList = new LinkedList<>();
        List<SqlAppender> removeFieldSqlList = new LinkedList<>();
        List<SqlAppender> commentSqlList = new LinkedList<>();
        List<SqlAppender> changedSqlList = new LinkedList<>();

        for (FieldMetaData newField : newTable.getFields()) {
            //新增的字段
            if (!tableMetaData.hasField(newField.getName())) {
                SqlAppender newFieldSql = new SqlAppender();
                newFieldSql.addSpc("alter table ", tableMetaData.getName(), String.format(" add %s", newField.getName()));
                newFieldSql.addSpc(newField.getDataType());
                if (newField.isNotNull()) {
                    newFieldSql.addEdSpc("not null");
                }
                newFieldSqlList.add(newFieldSql);
                if (newField.getComment() != null) {
                    commentSqlList.add(new SqlAppender().addSpc(String.format("comment on column %s.%s is '%s'", tableMetaData.getName(), newField.getName(), newField.getComment())));
                }
            } else {
                FieldMetaData old = tableMetaData.getField(newField.getName());
                if (!old.getDataType().equals(newField.getDataType()) || (old.isNotNull() != newField.isNotNull())) {
                    SqlAppender changed = new SqlAppender();
                    changed.addSpc(String.format("alter table %s modify %s", tableMetaData.getName(), newField.getName()));
                    if (!old.getDataType().equals(newField.getDataType())) {
                        changed.addSpc(newField.getDataType());
                    }
                    if ((!old.isNotNull()) && newField.isNotNull()) {
                        changed.addSpc(" not null");
                    }
                    changedSqlList.add(changed);
                }
                if (newField.getComment() != null && !newField.getComment().equals(old.getComment())) {
                    commentSqlList.add(new SqlAppender().addSpc(String.format("comment on column %s.%s is '%s'", tableMetaData.getName(), newField.getName(), newField.getComment())));
                }
            }
        }
        //执行删除多余的字段
        if (alterParam.isRemoveField()) {
            for (FieldMetaData old : tableMetaData.getFields()) {
                //新表中不包含字段但是在旧表中包含,主键不能删除
                if (!old.isPrimaryKey() && !newTable.hasField(old.getName())) {
                    SqlAppender droped = new SqlAppender();
                    droped.add(String.format("alter table %s drop column %s", tableMetaData.getName(), old.getName()));
                    removeFieldSqlList.add(droped);
                }
            }
        }
        List<SqlAppender> allSql = new LinkedList<>();
        allSql.addAll(newFieldSqlList);
        allSql.addAll(removeFieldSqlList);
        allSql.addAll(commentSqlList);
        allSql.addAll(changedSqlList);

        allSql.add(new SqlAppender().addSpc(
                String.format("comment on table %s  is '%s,更新于:%s。新增字段%d,删除字段%d,变更字段%d'",
                        tableMetaData.getName(), tableMetaData.getComment(),
                        DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
                        , newFieldSqlList.size(), removeFieldSqlList.size(), changedSqlList.size())));

        CommonSql commonSql = new CommonSql();
        for (int i = 0; i < allSql.size(); i++) {
            SqlAppender sql = allSql.get(i);
            if (i == 0) {
                commonSql.setSql(sql.toString());
            } else {
                CommonSql sql_ = new CommonSql();
                sql_.setSql(sql.toString());
                BindSQL bindSQL = new BindSQL();
                bindSQL.setSql(sql_);
                bindSQL.setToField("__");
                commonSql.getBinds().add(bindSQL);
            }
        }
        return commonSql;
    }

    @Override
    public void reload() throws SqlRenderException {

    }
}
