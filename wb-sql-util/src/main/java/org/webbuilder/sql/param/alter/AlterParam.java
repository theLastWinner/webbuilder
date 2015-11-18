package org.webbuilder.sql.param.alter;

import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.param.SqlRenderConfig;

/**
 * Created by 浩 on 2015-11-18 0018.
 */
public class AlterParam extends SqlRenderConfig {

    private TableMetaData newTable;

    private boolean removeField;

    public AlterParam(TableMetaData newTable) {
        this.newTable = newTable;
    }

    public TableMetaData getNewTable() {
        return newTable;
    }

    public boolean isRemoveField() {
        return removeField;
    }

    public void setRemoveField(boolean removeField) {
        this.removeField = removeField;
    }
}
