package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.DataValidException;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by 浩 on 2015-07-06 0006.
 */
public class InsertRender extends FreemarkerSqlRender {
    public InsertRender(TableMetaData metaData) {
        super(metaData);
        try {
            helper(new DataFormatHelper<Date>() {
                @Override
                public Object format(String key, Date param) {
                    return new Timestamp(param.getTime());
                }
            });
            helper(new DataFormatHelper<Object>() {
                @Override
                public Object format(String key, Object param) {
                    FieldMetaData metaDate = getMetaData().getField(key);
                    if (metaDate == null || param == null)
                        return param;
                    if (metaDate.getJavaType() == Date.class) {
                        if (!(param instanceof Date) && DateTimeUtils.validDate(param.toString())) {
                            Date date = DateTimeUtils.formatUnknownString2Date(param.toString());
                            return new Timestamp(date.getTime());
                        }
                    }
                    return param;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Set<String> getFields(final SqlRenderConfig config) {
        Set<String> fields = new LinkedHashSet<String>();
        if (config.getIncludes().size() == 0) {
            config.getIncludes().add("*");
        }
        Set<FieldMetaData> metas = new HashSet<FieldMetaData>(getMetaData().getFields());
        //是否查询所有字段
        boolean all = config.getIncludes().contains("*");
        if (all) {
            for (FieldMetaData meta : metas) {
                fields.add(meta.getName());
            }
        } else {
            for (String s : config.getIncludes()) {
                if (getMetaData().getField(s) != null) {
                    fields.add(s);
                }
            }
        }
        fields.removeAll(config.getExcludes());
        return fields;
    }

    /**
     * 传入配置，返回Sql信息
     *
     * @param config_ SqlRenderConfig配置实体
     * @return SqlInfo
     * @throws Exception
     */
    @Override
    public SqlInfo render(SqlRenderConfig config_) throws Exception {
        final SqlRenderConfig config = config_.clone();
        TableMetaData table = getMetaData();
        StringBuilder field_str = new StringBuilder();
        StringBuilder param_str = new StringBuilder();
        Object data = config.getData();
        Set<String> fields = getFields(config);
        int i = 0;
        for (String fieldName : fields) {
            if (config_.getExcludes().contains(fieldName))
                continue;
            if (!(data instanceof Collection)) {
                Object val = ClassUtil.getValueByAttribute(fieldName, data);
                table.getField(fieldName).valid(val);
            }
            if (i++ != 0) {
                field_str.append(",");
                param_str.append(",");
            }
            field_str.append(fieldName);
            param_str.append("@{").append(fieldName).append("}");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ")
                .append(table.getName())
                .append("(").append(field_str).append(")")
                .append("VALUES(").append(param_str).append(")");

        SqlInfo info;
        if (data instanceof Collection) {
            List datas = new ArrayList((Collection) data);
            if (datas.size() == 0)
                throw new DataValidException("data size is 0");
            info = compileSql(builder.toString(), datas.get(0));
            //批量操作
            for (int i1 = 1; i1 < datas.size(); i1++) {
                for (Object o : (Collection) data) {
                    SqlRenderConfig conf = config.clone();
                    conf.setData(o);
                    info.getBindSql().put("batch insert [" + (i1) + "]", this.render(conf));
                }
            }
        } else {
            info = compileSql(builder.toString(), config.getData());
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
