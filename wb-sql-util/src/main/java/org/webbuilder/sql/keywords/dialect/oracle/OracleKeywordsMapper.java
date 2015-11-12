package org.webbuilder.sql.keywords.dialect.oracle;

import org.webbuilder.sql.keywords.FieldTemplateWrapper;
import org.webbuilder.sql.keywords.dialect.AbstractKeywordsMapper;
import org.webbuilder.sql.keywords.dialect.oracle.wrapper.*;
import org.webbuilder.sql.param.ExecuteCondition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class OracleKeywordsMapper extends AbstractKeywordsMapper {

    private static final Map<ExecuteCondition.QueryType, FieldTemplateWrapper> wrappers = new ConcurrentHashMap<>();

    static {
        addWrapper(new EQWrapper(true));
        addWrapper(new EQWrapper(false));

        addWrapper(new EndWrapper(true));
        addWrapper(new EndWrapper(false));

        addWrapper(new INWrapper(true));
        addWrapper(new INWrapper(false));

        addWrapper(new LIKEWrapper(true));
        addWrapper(new LIKEWrapper(false));

        addWrapper(new NullWrapper(true));
        addWrapper(new NullWrapper(false));

        addWrapper(new StartWrapper(true));
        addWrapper(new StartWrapper(false));

        addWrapper(new ThanWrapper(true));
        addWrapper(new ThanWrapper(false));
    }

    public static void addWrapper(FieldTemplateWrapper wrapper) {
        wrappers.put(wrapper.getType(), wrapper);
    }

    @Override
    protected FieldTemplateWrapper getQueryTypeMapper(ExecuteCondition.QueryType type) {
        return wrappers.get(type);
    }

    @Override
    public String getSpecifierPrefix() {
        return "\"";
    }

    @Override
    public String getSpecifierSuffix() {
        return "\"";
    }

    @Override
    public String pager(String sql, int pageIndex, int pageSize) {
        StringBuilder builder = new StringBuilder("SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM (");
        builder.append(sql);
        builder.append(") row_ )");
        builder.append("WHERE rownum_ <= ").append(pageSize * (pageIndex + 1)).append(" and rownum_ > ").append(pageSize * pageIndex);
        return builder.toString();
    }

}
