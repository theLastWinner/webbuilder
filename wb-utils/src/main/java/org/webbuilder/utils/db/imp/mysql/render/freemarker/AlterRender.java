package org.webbuilder.utils.db.imp.mysql.render.freemarker;

import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.DataValidException;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

/**
 * Created by 浩 on 2015-07-07 0007.
 */
public class AlterRender extends CreateRender {
    public AlterRender(TableMetaData metaData) {
        super(metaData);
    }

    /**
     * 传入配置，返回Sql信息,只进行 增加字段，修改字段，为了数据安全 不删除字段，若需删除，请手动删除
     *
     * @param config SqlRenderConfig配置实体
     * @return SqlInfo
     * @throws Exception
     */
    @Override
    public SqlInfo render(SqlRenderConfig config) throws Exception {
        config = config.clone();
        if (config.getData() == null || !(config.getData() instanceof TableMetaData))
            throw new DataValidException(" data is null or not is TableMetaData!");

        TableMetaData metaData_new = (TableMetaData) config.getData();
        SqlInfo info = createSqlInfo(" select 1 as a from " + getMetaData().getName() + " where 1=2");
        StringBuilder sqls = new StringBuilder("ALTER TABLE `").append(metaData_new.getName()).append("` ");
        int index = 0;
        //验证被修改的字段
        for (FieldMetaData field_new : metaData_new.getFields()) {
            field_new.setDataType(getMetaData().getDataTypeMapper().dataType(field_new));
            FieldMetaData field_old = getMetaData().getField(field_new.getName());
            if (index != 0)
                sqls.append(",");
            //新增字段
            if (field_old == null) {
                sqls.append(" ADD COLUMN ");
                sqls.append(field_new.getName()).append(" ");
                sqls.append(field_new.getDataType());
                if (field_new.getDefaultValue() != null) {
                    String spit = "";
                    try {
                        field_new.getJavaType().asSubclass(Number.class);
                        spit = "'";
                    } catch (Exception e) {
                    }
                    sqls.append(" DEFAULT ").append(spit).append(field_new.getDefaultValue()).append(spit);
                }
                if (field_new.isNotNull())
                    sqls.append(" NOT ");
                sqls.append(" NULL COMMENT ").append(field_new.getRemark());
                index++;
            } else {
                //修改字段
                if (!field_old.getDataType().equals(field_new.getDataType())
                        || field_old.getRemark().equals(field_new.getRemark())) {
                    sqls.append(" CHANGE ").append("'").append(field_new.getName()).append("'")
                            .append(" '").append(field_new.getName()).append("' ").append(field_new.getDataType());
                    if (field_new.getDefaultValue() != null) {
                        String spit = "";
                        try {
                            field_new.getJavaType().asSubclass(Number.class);
                            spit = "'";
                        } catch (Exception e) {
                        }
                        sqls.append(" DEFAULT ").append(spit).append(field_new.getDefaultValue()).append(spit);
                    }
                    if (field_new.isNotNull())
                        sqls.append(" NOT ");
                    sqls.append(" NULL ");
                    sqls.append(" NULL COMMENT '").append(field_new.getRemark()).append("'");
                }
            }
        }
        info.getBindSql().put("__", new SqlInfo(sqls.toString()));
        return info;
    }

    /**
     * 初始化工作，当此Render被注册到Factory时自动调用
     *
     * @throws Exception 初始化异常
     */
    @Override
    public void init() throws Exception {

    }
}
