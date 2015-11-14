package org.webbuilder.sql.test

import org.webbuilder.sql.DataBaseMetaData
import org.webbuilder.sql.keywords.KeywordsMapper
import org.webbuilder.sql.keywords.dialect.oracle.OracleKeywordsMapper
import org.webbuilder.sql.param.query.QueryParam
import org.webbuilder.sql.parser.CommonTableMetaDataParser
import org.webbuilder.sql.render.template.SqlTemplateRender
import org.webbuilder.sql.support.common.CommonDataBase
import org.webbuilder.sql.support.common.CommonSqlTemplateRender
import org.webbuilder.sql.support.executor.AbstractJdbcSqlExecutor
import org.webbuilder.utils.base.Resources
import org.webbuilder.utils.base.file.FileUtil

import java.sql.Connection

//定义数据库
def dataBaseMetaData = new DataBaseMetaData() {
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
def dataBase = new CommonDataBase(dataBaseMetaData, new AbstractJdbcSqlExecutor() {
    @Override
    public Connection getConnection() {
        return null;
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

def s_user_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/s_user.html").getAbsolutePath());
def s_user = new CommonTableMetaDataParser().parse(s_user_content, "html");
s_user.setName("s_user");
s_user.setDataBaseMetaData(dataBaseMetaData);

//定义表结构
def area_content = FileUtil.readFile2String(Resources.getResourceAsFile("tables/area.html").getAbsolutePath());
def area = new CommonTableMetaDataParser().parse(area_content, "html");
area.setName("area");
area.setDataBaseMetaData(dataBaseMetaData);

//添加表到数据库
dataBaseMetaData.addTable(s_user);
dataBaseMetaData.addTable(area);

def table = dataBase.getTable("s_user");
//创建查询
def query = table.createQuery();
//-----------------多条件查询条件-------------
def param = new QueryParam();
def where = [area_id$LIKE: 1];

param.select("id", "username", "area.name").where(where).orderBy("id").noPaging();
param.addProperty("user", [area_id: 1]);

println(query.list(param));