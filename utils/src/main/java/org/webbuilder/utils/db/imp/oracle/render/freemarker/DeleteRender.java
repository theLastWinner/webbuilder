package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exception.DataValidException;
import org.webbuilder.utils.db.render.KeyWordsMapper;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.util.Collection;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class DeleteRender extends UpdateRender {
    public DeleteRender(TableMetaData metaData) {
        super(metaData);
    }

    @Override
    public SqlInfo render(SqlRenderConfig config_) throws Exception {
        final SqlRenderConfig config = config_.clone();
        TableMetaData table = getMetaData();
        Object data = config.getData();
        if ((data instanceof Collection)) {
            throw new DataValidException("data is collection!");
        }
        StringBuilder cdt = new StringBuilder("DELETE FROM ").append(table.getName()).append(" WHERE ");
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
        //禁止无where条件的修改，防止数据错误
        if (x == 0) {
            throw new DataValidException("not found params!");
        }
        SqlInfo info = compileSql(cdt.toString(), config.getParams());
        return info;
    }
}
