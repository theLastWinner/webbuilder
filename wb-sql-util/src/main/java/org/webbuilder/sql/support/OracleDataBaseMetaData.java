package org.webbuilder.sql.support;

import org.webbuilder.sql.DataBaseMetaData;
import org.webbuilder.sql.keywords.KeywordsMapper;
import org.webbuilder.sql.keywords.dialect.oracle.OracleKeywordsMapper;
import org.webbuilder.sql.render.template.SqlTemplateRender;
import org.webbuilder.sql.support.common.CommonSqlTemplateRender;

/**
 * Created by 浩 on 2015-11-17 0017.
 */
public class OracleDataBaseMetaData extends DataBaseMetaData {
    protected SqlTemplateRender sqlTemplateRender = new CommonSqlTemplateRender();
    protected KeywordsMapper keywordsMapper = new OracleKeywordsMapper();
    protected String name = "orcl";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SqlTemplateRender getRender() {
        return sqlTemplateRender;
    }

    @Override
    public KeywordsMapper getKeywordsMapper() {
        return keywordsMapper;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeywordsMapper(KeywordsMapper keywordsMapper) {
        this.keywordsMapper = keywordsMapper;
    }

    public void setSqlTemplateRender(SqlTemplateRender sqlTemplateRender) {
        this.sqlTemplateRender = sqlTemplateRender;
    }
}

