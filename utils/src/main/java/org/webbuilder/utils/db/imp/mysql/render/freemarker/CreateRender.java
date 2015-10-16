package org.webbuilder.utils.db.imp.mysql.render.freemarker;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.imp.oracle.render.freemarker.FreemarkerSqlRender;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-07 0007.
 */
public class CreateRender extends FreemarkerSqlRender {
    public CreateRender(TableMetaData metaData) {
        super(metaData);
    }

    /**
     * 传入配置，返回Sql信息
     *
     * @param config SqlRenderConfig配置实体
     * @return SqlInfo
     * @throws Exception
     */
    @Override
    public SqlInfo render(SqlRenderConfig config) throws Exception {
        SqlInfo sqlInfo = new SqlInfo(getTemplate());//基本建表模板
        Map<String, SqlInfo> primarySqls = new LinkedHashMap<String, SqlInfo>();
        for (FieldMetaData field : getMetaData().getFields()) {
            if (field.isPrimaryKey())
                primarySqls.put("pk_" + field.getName(), new SqlInfo(createPrimaryKey(field.getName())));
        }
        sqlInfo.getBindSql().putAll(primarySqls);
        return sqlInfo;
    }

    public String createPrimaryKey(String fieldName) {
        //alter table tableName add constraint pk_name primary key (fieldName);
        StringBuilder builder = new StringBuilder();
        builder.append("alter table `")
                .append(getMetaData().getName())
                .append("` add primary key (`")
                .append(fieldName).append("`)");
        return builder.toString();
    }

    public String getFileTemplate(FieldMetaData field) throws Exception {
        //进行字段数据类型验证
        if (getMetaData().getDataTypeMapper().valid(field)) {
            StringBuilder builder = new StringBuilder();
            builder.append(field.getName()).append(" ").append(getMetaData().getDataTypeMapper().dataType(field));
            if (field.isNotNull())
                builder.append(" NOT NULL");
            if (!StringUtil.isNullOrEmpty(field.getDefaultValue()))
                builder.append(" default ").append(field.getDefaultValue());
            if(!StringUtil.isNullOrEmpty(field.getRemark()))
                builder.append(" COMMENT '").append(field.getRemark()).append("'");
            return builder.toString();
        }
        return null;
    }

    /**
     * 初始化工作，当此Render被注册到Factory时自动调用
     *
     * @throws Exception 初始化异常
     */
    @Override
    public void init() throws Exception {
        StringBuilder builder = new StringBuilder();
        TableMetaData metaData = getMetaData();
        builder.append("CREATE TABLE ").append(metaData.getName()).append("(");
        int index = 0;
        for (FieldMetaData field : metaData.getFields()) {
            if (index++ != 0)
                builder.append(",");
            builder.append(getFileTemplate(field));
        }
        builder.append(")");
        setTemplate(builder.toString());
    }

}
