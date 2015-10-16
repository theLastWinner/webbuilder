package org.webbuilder.utils.db.imp.oracle;

import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.def.parser.TableParser;
import org.webbuilder.utils.db.exception.TableParseException;
import oracle.jdbc.OracleResultSetMetaData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 浩 on 2015-07-09 0009.
 */
public class OracleTableParser extends TableParser {
    @Override
    public TableMetaData parse(Object session, String tableName) throws TableParseException {
        if (!(session instanceof Connection))
            throw new TableParseException("session must instanceof java.sql.Connection");
        Connection conn = (Connection) session;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet pkRSet = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
            List pk_list = new ArrayList();
            while (pkRSet.next()) {//得到主键字段
                pk_list.add(pkRSet.getObject(4).toString().toLowerCase());
            }
            TableMetaData tableMetaData = new TableMetaData(tableName);
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE ROWNUM=1");
            statement.execute();
            OracleResultSetMetaData metaData = (OracleResultSetMetaData) statement.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                FieldMetaData field = new FieldMetaData(metaData.getColumnName(i).toLowerCase(), Class.forName(metaData.getColumnClassName(i)));
                field.setLength(metaData.getPrecision(i));
                field.setDataType(metaData.getColumnTypeName(i).toLowerCase());
                field.setPrimaryKey(pk_list.contains(field.getName()));
                tableMetaData.getFields().add(field);
            }

            return tableMetaData;
        } catch (Exception e) {
            throw new TableParseException("parse table" + tableName + " is error!", e);
        }
    }
}
