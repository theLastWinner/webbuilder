package org.webbuilder.generator.service.imp;

import org.webbuilder.generator.bean.Field;
import org.webbuilder.generator.bean.GeneratorConfig;
import org.webbuilder.generator.service.GeneratorService;
import org.webbuilder.generator.service.database.ConnectionBuilder;
import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.Resources;
import org.webbuilder.utils.base.StringTemplateUtils;
import org.webbuilder.utils.base.file.CallBack;
import org.webbuilder.utils.base.file.FileUtil;
import org.webbuilder.utils.db.def.*;
import org.webbuilder.utils.db.imp.mysql.MySqlDataBase;
import org.webbuilder.utils.db.imp.oracle.OracleDataBase;
import org.webbuilder.utils.db.render.SqlRenderType;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;
import org.webbuilder.utils.office.excel.io.ExcelIO;
import org.webbuilder.utils.office.excel.io.Header;
import org.webbuilder.utils.office.excel.io.WriteExcelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;

/**
 * 使用freemarker进行模板生成
 * Created by 浩 on 2015-07-27 0027.
 */
public class CommonGeneratorServiceImp implements GeneratorService {

    public static String CHARSET = "utf8";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Map<String, Class> mapper = new HashMap() {{
        put("int", Integer.class);
        put("String", String.class);
        put("java.util.Date", Date.class);
        put("double", Double.class);
        put("boolean", Boolean.class);
    }};

    public CommonGeneratorServiceImp() {
        DataBaseStorage.register(new MySqlDataBase("mysql"));
        DataBaseStorage.register(new OracleDataBase("oracle"));
    }

    public void initTemplate() throws Exception {
        Resources.setCharset(Charset.forName(CHARSET));
        Properties properties = new Properties();
        properties.load(Resources.getResourceAsReader("config/template.properties"));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            if (key.endsWith("ftl")) {
                logger.info("加载并并以模板:" + value);
                BufferedReader reader = new BufferedReader(Resources.getResourceAsReader(value));
                StringBuffer content = new StringBuffer();
                while (reader.ready()) {
                    content.append(reader.readLine()).append("\n");
                }
                StringTemplateUtils.compileTemplate(key, content.toString());
            }
        }
    }

    @Override
    public void generate(GeneratorConfig config) throws Exception {
        initTemplate();
        Map<String, Object> param = new HashMap<>();
        param.put("config", config);
        param.put("fields", config.getFields());

        String outPath = config.getOutput().getAbsolutePath();
        String outPath_bean = (outPath + "/src/" + config.getPackageName() + "/po/" + config.getModule()).replace(".", "/");
        String outPath_dao = (outPath + "/src/" + config.getPackageName() + "/dao/" + config.getModule()).replace(".", "/");
        String outPath_mapper = (outPath + "/src/" + config.getPackageName() + "/dao/" + config.getModule() + "/mapper/" + config.getDatabaseType()).replace(".", "/");
        String outPath_service = (outPath + "/src/" + config.getPackageName() + "/service/" + config.getModule()).replace(".", "/");
        String outPath_controller = (outPath + "/src/" + config.getPackageName() + "/controller/" + config.getModule()).replace(".", "/");
        logger.info("生成代码中...");
        String bean = StringTemplateUtils.generate("Bean.ftl", param);
        String dao = StringTemplateUtils.generate("Mapper.ftl", param);
        String mapper = StringTemplateUtils.generate("Mapper_" + config.getDatabaseType() + ".ftl", param);
        String service = StringTemplateUtils.generate("Service.ftl", param);
        String controller = StringTemplateUtils.generate("Controller.ftl", param);

        new File(outPath_bean).mkdirs();
        new File(outPath_dao).mkdirs();
        new File(outPath_mapper).mkdirs();
        new File(outPath_service).mkdirs();
        new File(outPath_bean).mkdirs();
        new File(outPath_controller).mkdirs();
        outPath_bean = outPath_bean + "/" + config.getClassName() + ".java";
        outPath_dao = outPath_dao + "/" + config.getClassName() + "Mapper.java";
        outPath_mapper = outPath_mapper + "/" + config.getClassName() + "Mapper.xml";
        outPath_service = outPath_service + "/" + config.getClassName() + "Service.java";
        outPath_controller = outPath_controller + "/" + config.getClassName() + "Controller.java";

        logger.info("写出bean:" + outPath_bean);
        FileUtil.writeString2File(bean, outPath_bean, CHARSET);
        logger.info("写出dao:" + outPath_dao);
        FileUtil.writeString2File(dao, outPath_dao, CHARSET);
        logger.info("写出mapper:" + outPath_mapper);
        FileUtil.writeString2File(mapper, outPath_mapper, CHARSET);
        logger.info("写出service:" + outPath_service);
        FileUtil.writeString2File(service, outPath_service, CHARSET);
        logger.info("写出controller:" + outPath_controller);
        FileUtil.writeString2File(controller, outPath_controller, CHARSET);

        createExcel(config);
        if (config.isAutoCreate()) {
            logger.info("开始自动建立数据库表...");
            createTable(config);
        }
    }

    //归档Excel
    public void createExcel(GeneratorConfig config) throws Exception {
        String outPath = config.getOutput().getAbsolutePath();
        String outPath_bean = (outPath + "/doc/excel/" + config.getPackageName() + "/" + config.getModule()).replace(".", "/");
        new File(outPath_bean).mkdirs();
        outPath_bean = (outPath_bean + "/" + config.getClassName() + ".xls");
        List datas = new LinkedList();
        datas.addAll(config.getFields());
        logger.info("写出excel:" + outPath_bean);
        ExcelIO.write(new FileOutputStream(outPath_bean), datas, new WriteExcelConfig());
    }

    public void createTable(GeneratorConfig config) {
        Map<String, String> dbConf = config.getDbConfig();
        logger.info("使用数据库配置:" + dbConf);
        Set<Field> fields = config.getFields();
        DataBase dataBase = DataBaseStorage.getDataBase(config.getDatabaseType());
        if (dataBase == null) {
            logger.error("获取数据库失败!");
            return;
        }
        TableMetaData metaData = new TableMetaData(config.getTableName());
        for (Field field : fields) {
            Class javaType = mapper.get(field.getJavaTypeName());
            if (javaType == null) {
                logger.error("不支持的类型:" + field.getJavaTypeName());
                return;
            }
            FieldMetaData fieldMetaData = new FieldMetaData(field.getName(), javaType);
            fieldMetaData.setDataType(field.getDataType());
            fieldMetaData.setRemark(field.getRemark());
            fieldMetaData.setNotNull(field.isNotNull());
            fieldMetaData.setPrimaryKey(field.isPrimaryKey());
            try {
                metaData.addField(fieldMetaData);
            } catch (Exception e) {
                logger.error("", e);
                return;
            }
        }
        try {
            dataBase.putTable(metaData);
            metaData = dataBase.getTable(metaData.getName());
            SqlInfo info = metaData.render(SqlRenderType.CREATE).render(new SqlRenderConfig());

            String outPath = config.getOutput().getAbsolutePath();
            String outPath_bean = (outPath + "/doc/sql/" + config.getPackageName() + "/" + config.getModule()).replace(".", "/");
            new File(outPath_bean).mkdirs();
            outPath_bean = (outPath_bean + "/" + config.getClassName() + ".sql");
            FileUtil.writeString2File(info.toString(), outPath_bean, CHARSET);
            Connection connection = ConnectionBuilder.getConnection(dbConf);
            try {
                dataBase.createTable(metaData, connection);
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            logger.error("", e);
            return;
        }
    }


}
