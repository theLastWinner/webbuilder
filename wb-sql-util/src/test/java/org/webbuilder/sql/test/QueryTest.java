package org.webbuilder.sql.test;


import org.junit.After;
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

    Connection connection;

    public QueryTest() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@server.142:1521:ORCL", "cqcy", "cqcy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void init() throws Exception {

        //定义数据库
        DataBaseMetaData dataBaseMetaData = new DataBaseMetaData() {
            //默认的sql渲染器
            SqlTemplateRender render = new CommonSqlTemplateRender();
            //oracle关键字映射器
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
                return connection;
            }

            @Override
            public void resetConnection(Connection connection) {
                // try {
                //  connection.close();
                // } catch (SQLException e) {
                //    e.printStackTrace();
                // }
            }
        });

        //定义表结构---- 表结构可通过解析html，xml，json等自动生成
        TableMetaData s_user = new TableMetaData();
        s_user.setName("s_user");
        s_user.setDataBaseMetaData(dataBaseMetaData);
        s_user.addField(new FieldMetaData("id", String.class, "varchar2(100)"));
        s_user.addField(new FieldMetaData("username", String.class, "varchar2(100)"));
        s_user.addField(new FieldMetaData("area_id", Integer.class, "number(4,0)"));

        //定义表结构
        TableMetaData area = new TableMetaData();
        area.setName("area");
        area.setDataBaseMetaData(dataBaseMetaData);
        area.addField(new FieldMetaData("id", String.class, "varchar2(100)"));
        area.addField(new FieldMetaData("name", String.class, "varchar2(100)"));

        //定义表关联条件
        TableMetaData.Correlation correlation = new TableMetaData.Correlation();
        ExecuteCondition condition = new ExecuteCondition();
        condition.setSql(true);//直接拼接sql方式  area_id=area.id
        condition.setField("area_id");
        condition.setValue("area.id");
        correlation.addCondition(condition);
        correlation.setTargetTable("area");
        s_user.addCorrelation(correlation);


        //添加表到数据库
        dataBaseMetaData.addTable(s_user);
        dataBaseMetaData.addTable(area);

    }

    @Test
    public void testSelect() throws Exception {
        Table table = dataBase.getTable("s_user");
        //创建查询
        Query query = table.createQuery();
        //构造查询条件
        QueryParam param = new QueryParam(false);
        String where = "{\"area_id$NOTNULL\":\"1\"," +
                "\"username$LIKE\":{\"value\":\"w\",\"nest\":{\"area.id\":{\"type\":\"or\",\"value\":2}} }}";
        //将json转为查询条件列表
        Set<ExecuteCondition> conditions = ExecuteConditionParser.parseByJson(where);

        param.setConditions(conditions);
        param.include("id", "username", "area.name");
        //不分页查询，默认是分页的
        param.setPaging(false);
        System.out.println(query.list(param));
        //进行分页
        param.doPaging(0, 5);
        System.out.println(query.list(param));
        System.out.println(query.single(param));//单个值
        System.out.println(query.total(param));//查询总和

    }
}