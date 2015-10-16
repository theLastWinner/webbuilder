package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.DataValidException;
import org.webbuilder.utils.db.render.KeyWordsMapper;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.util.*;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class UpdateRender extends InsertRender {
    public UpdateRender(TableMetaData metaData) {
        super(metaData);
    }


    @Override
    public SqlInfo render(SqlRenderConfig config_) throws Exception {
        final SqlRenderConfig config = config_.clone();
        StringBuilder field_str = new StringBuilder();
        TableMetaData table = getMetaData();
        Object data = config.getData();
        if ((data instanceof Collection)) {
            throw new DataValidException("data is collection!");
        }
        Set<String> fields = getFields(config);
        int i = 0;
        List<Object> params = new LinkedList<Object>();
        for (String fieldName : fields) {
            Object val = ClassUtil.getValueByAttribute(fieldName, data);
            if (val == null)//值为null 则不修改，如果要修改为null，请使用""
                continue;
            FieldMetaData metaData = table.getField(fieldName);
            if (!metaData.isCanUpdate())
                continue;
            val = parseValue(fieldName, val);
            metaData.valid(val);
            params.add(val);
            if (i++ != 0) {
                field_str.append(",");
            }
            field_str.append(fieldName).append("=?");
        }
        if (i == 0) {
            throw new DataValidException("not field selected!");
        }
        StringBuilder cdt = new StringBuilder(" WHERE ");
        int x = 0;
        //条件
        for (Map.Entry<String, Object> entry : config.getParams().entrySet()) {
            String fieldName = entry.getKey();
            KeyWordsMapper.Mapper mapper = getKeyWordsMapper().getMapper(entry.getKey());
            String template;
            if (mapper != null) {
                fieldName = mapper.fieldName(fieldName);
                FieldMetaData metaData = table.getField(fieldName);
                if (metaData == null) continue;
                else {
                    template = mapper.template(metaData);
                }
            } else {
                template = fieldName + "=@{" + fieldName + "}";
            }
            if (table.getField(fieldName) == null)
                continue;
            if (x++ != 0) {
                cdt.append(",");
            }
            cdt.append(template);
        }
        //禁止无where条件的删除，防止数据错误
        if (x == 0) {
            throw new DataValidException("not found params!");
        }
        SqlInfo info = compileSql(cdt.toString(), config.getParams());
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ")
                .append(table.getName())
                .append(" u SET ").append(field_str)
                .append(info.getSql());
        params.addAll(Arrays.asList(info.getParams()));
        SqlInfo info1 = createSqlInfo(builder.toString());
        info1.setParams(params.toArray());
        return info1;
    }


    @Override
    public void init() throws Exception {
    }
}
