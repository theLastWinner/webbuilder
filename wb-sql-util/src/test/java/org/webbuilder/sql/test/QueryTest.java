package org.webbuilder.sql.test;


import org.junit.Before;
import org.junit.Test;
import org.webbuilder.sql.*;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.keywords.dialect.oracle.OracleKeywordsMapper;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.param.QueryParam;
import org.webbuilder.sql.parser.ExecuteConditionParser;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.common.CommonDataBase;
import org.webbuilder.sql.support.common.CommonSqlTemplateRender;
import org.webbuilder.sql.support.executor.AbstractJdbcSqlExecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by 浩 on 2015-11-10 0010.
 */
public class QueryTest {
    DataBase dataBase;

    @Before
    public void init() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        DataBaseMetaData dataBaseMetaData = new DataBaseMetaData() {
            SqlTemplateRender render = new CommonSqlTemplateRender();
            KeywordsMapper mapper = new OracleKeywordsMapper();

            @Override
            public String getName() {
                return "orcl";
            }

            @Override
            public SqlTemplateRender getRender() {
                return render;
            }

            @Override
            public KeywordsMapper getKeywordsMapper() {
                return mapper;
            }
        };
        dataBase = new CommonDataBase(dataBaseMetaData, new AbstractJdbcSqlExecutor() {
            @Override
            public Connection getConnection() {
                try {
                    return DriverManager.getConnection("jdbc:oracle:thin:@server.142:1521:ORCL", "cqcy", "cqcy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            public void resetConnection(Connection connection) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


        TableMetaData s_user = new TableMetaData();
        s_user.setName("s_user");
        s_user.setDataBaseMetaData(dataBaseMetaData);
        s_user.addField(new FieldMetaData("id", String.class, "varchar2(100)"));
        s_user.addField(new FieldMetaData("username", String.class, "varchar2(100)"));
        s_user.addField(new FieldMetaData("area_id", Integer.class, "number(4,0)"));

        TableMetaData.Correlation correlation = new TableMetaData.Correlation();
        ExecuteCondition condition = new ExecuteCondition();
        condition.setField("area_id");
        condition.setSql(true);
        condition.setValue("area.id");
        correlation.addCondition(condition);
        correlation.setTargetTable("area");

        s_user.addCorrelation(correlation);

        TableMetaData area = new TableMetaData();
        area.setName("area");
        area.setDataBaseMetaData(dataBaseMetaData);
        area.addField(new FieldMetaData("id", String.class, "varchar2(100)"));
        area.addField(new FieldMetaData("name", String.class, "varchar2(100)"));

        dataBaseMetaData.addTable(s_user);
        dataBaseMetaData.addTable(area);

    }

    @Test
    public void testSelect() throws Exception {
        Table table = dataBase.getTable("s_user");
        Query query = table.createQuery();
        QueryParam param = new QueryParam(false);
        Set<ExecuteCondition> conditions =
                ExecuteConditionParser.parseByJson("{\"area_id$NOTNULL\":\"1\",\"username$LIKE\":{\"value\":\"w\",\"nest\":{\"area.id\":{\"type\":\"or\",\"value\":2}} }}");
        param.setConditions(conditions);

        param.setPaging(false);
        param.include("username");
        System.out.println(query.list(param));
        param.doPaging(0, 5);
        System.out.println(query.list(param));
        System.out.println(query.single(param));
        System.out.println(query.total(param));

    }
}