package org.webbuilder.sql.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.webbuilder.sql.*;
import org.webbuilder.sql.param.MethodField;
import org.webbuilder.sql.param.delete.DeleteParam;
import org.webbuilder.sql.param.insert.InsertParam;
import org.webbuilder.sql.param.query.QueryParam;
import org.webbuilder.sql.param.update.UpdateParam;
import org.webbuilder.sql.parser.CommonTableMetaDataParser;
import org.webbuilder.sql.support.OracleDataBaseMetaData;
import org.webbuilder.sql.support.common.CommonDataBase;
import org.webbuilder.sql.support.executor.AbstractJdbcSqlExecutor;
import org.webbuilder.utils.base.Resources;
import org.webbuilder.utils.base.file.FileUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-10 0010.
 */
public class SQLTest {
    DataBase dataBase;

    Connection connection;

    public SQLTest() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@server.142:1521:ORCL", "cqcy", "cqcy");
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void destroy() {
        try {
            connection.rollback();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void init() throws Exception {

        //定义数据库
        DataBaseMetaData dataBaseMetaData = new OracleDataBaseMetaData();
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

        String s_user_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/s_user.html").getAbsolutePath());
        TableMetaData s_user = new CommonTableMetaDataParser().parse(s_user_content, "html");
        s_user.setName("s_user_02");
        s_user.setDataBaseMetaData(dataBaseMetaData);
        s_user.setComment("测试表");
        //定义表结构
        String area_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/area.html").getAbsolutePath());
        TableMetaData area = new CommonTableMetaDataParser().parse(area_content, "html");
        area.setName("area");
        area.setDataBaseMetaData(dataBaseMetaData);

        //添加表到数据库
        dataBaseMetaData.addTable(s_user);
        dataBaseMetaData.addTable(area);

    }

    @Test
    public void testInsert() throws Exception {
        Table table = dataBase.getTable("s_user_02");
        Insert insert = table.createInsert();
        InsertParam param = new InsertParam();
        Map<String, Object> data = new HashMap<>();
        data.put("username", "admin");
        data.put("id", "aaa");
        param.values(data);
        System.out.println(insert.insert(param));
    }


    @Test
    public void testUpdate() throws Exception {
        Table table = dataBase.getTable("s_user_02");
        //创建更新
        Update update = table.createUpdate();
        //-----------------更新条件-------------
        UpdateParam param = new UpdateParam();
        param.set("username", "admin").where("username", "admin");
        update.update(param);
    }

    @Test
    public void testSelect() throws Exception {
        Table table = dataBase.getTable("s_user_02");
        //创建查询
        Query query = table.createQuery();
        //-----------------多条件查询条件-------------
        QueryParam param = new QueryParam();
        String where = "{\"area_id$NOTNULL\":\"1\"," +
                "\"username$LIKE\":{\"value\":\"w\",\"nest\":{\"area.id\":{\"type\":\"or\",\"value\":2}} }}";

        param.select("id", "username", "area.name").where(where).orderBy("id").noPaging();
//        param.addProperty("user",new HashMap<String,Object>(){
//            {
//                put("area_id",111);
//            }
//        });

        System.out.println(query.list(param));
        //进行分页
        param.doPaging(0, 5);
        System.out.println(query.list(param));
        System.out.println(query.single(param));//单个值
        System.out.println(query.total(param));//查询总数
        //------------------自定义函数查询--------------
        param = new QueryParam(false);
        param.include(new MethodField().count("id").as("total"));
        System.out.println(query.single(param));
    }


    @Test
    public void testDelete() throws Exception {
        Table table = dataBase.getTable("s_user_02");
        //创建删除
        Delete delete = table.createDelete();
        //-----------------条件-------------
        DeleteParam param = new DeleteParam();
        param.where("id$NOT", 1);
        delete.delete(param);
    }

    @Test
    public void testCreate() throws Exception {
        dataBase.createTable(dataBase.getTable("s_user_02").getMetaData());
    }

    @Test
    public void testAlter() throws Exception {
        String s_user_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/s_user.html").getAbsolutePath());
        TableMetaData s_user = new CommonTableMetaDataParser().parse(s_user_content, "html");
        s_user.setName("s_user_02");
        s_user.setComment("测试表");
        FieldMetaData metaData = new FieldMetaData("test_f", String.class, "varchar2(256)");
        metaData.setComment("测试字段");
        s_user.addField(metaData);
        dataBase.alterTable(s_user);
    }
}