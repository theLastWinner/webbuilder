package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.DataValidException;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.util.ArrayList;
import java.util.List;

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
        List<SqlInfo> alters = new ArrayList<SqlInfo>();
        List<SqlInfo> commonts = new ArrayList<SqlInfo>();
        //验证被修改的字段
        for (FieldMetaData field_new : metaData_new.getFields()) {
            field_new.setDataType(getMetaData().getDataTypeMapper().dataType(field_new));
            StringBuilder sql = new StringBuilder();
            FieldMetaData field_old = getMetaData().getField(field_new.getName());
            //新增字段
            if (field_old == null) {
                if (field_new.isPrimaryKey()) {//主键
                    commonts.add(createSqlInfo(createPrimaryKey(field_new.getName())));
                }
                if (field_new.getRemark() != null) {//备注
                    commonts.add(createSqlInfo(getFieldRemarkTemplate(field_new)));
                }
                sql.append("alter table ").append(getMetaData().getName())
                        .append(" add ").append(field_new.getName()).append(" ").append(field_new.getDataType());
                if (!StringUtil.isNullOrEmpty(field_new.getDefaultValue()))
                    sql.append("default ").append(field_new.getDefaultValue());
                if (field_new.isNotNull())
                    sql.append(" NOT NULL");
                alters.add(createSqlInfo(sql.toString()));
            } else {
                field_old.setDataType(getMetaData().getDataTypeMapper().dataType(field_old));
                //备注
                if (field_new.getRemark() != null && !field_new.getRemark().equals(field_old.getRemark()))
                    commonts.add(createSqlInfo(getFieldRemarkTemplate(field_new)));
                //不相同则进行修改
                if (!field_old.equalsFull(field_new)) {
                    //新增主键
                    if (!field_old.isPrimaryKey() && field_new.isPrimaryKey()) {
                        commonts.add(createSqlInfo(createPrimaryKey(field_new.getName())));
                    }
                    //删除主键
                    if (field_old.isPrimaryKey() && !field_new.isPrimaryKey()) {
                        commonts.add(createSqlInfo(new StringBuilder(" alter table ")
                                .append(getMetaData().getName())
                                .append(" drop ").append(" constraint ")
                                .append(" pk_").append(getMetaData().getName()).append("_").append(field_new.getName())
                                .toString()));
                    }
                    sql.append("alter table ").append(getMetaData().getName()).append(" modify ")
                            .append(field_new.getName()).append(" ").append(field_new.getDataType());
                    if (!StringUtil.isNullOrEmpty(field_new.getDefaultValue()))
                        sql.append(" default ").append(field_new.getDefaultValue());
                    if (!field_old.isNotNull() && field_new.isNotNull())
                        sql.append(" NOT NULL");
                    alters.add(createSqlInfo(sql.toString()));
                }
            }
        }
        alters.addAll(commonts);
        for (int i = 0; i < alters.size(); i++) {
            info.getBindSql().put("alters_" + i, alters.get(i));
        }
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
