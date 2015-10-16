package org.webbuilder.utils.db.imp.oracle;

import org.webbuilder.utils.db.def.DataBaseType;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.def.parser.TableParser;
import org.webbuilder.utils.db.exec.ExecutorConfig;
import org.webbuilder.utils.db.imp.CommonDataBase;
import org.webbuilder.utils.db.render.DataTypeMapper;
import org.webbuilder.utils.db.render.SqlRender;
import org.webbuilder.utils.db.render.SqlRenderType;

/**
 * Created by 浩 on 2015-07-06 0006.
 */
public class OracleDataBase extends CommonDataBase {

    private static final TableParser parser = new OracleTableParser();

    private static DataTypeMapper DEFAULT_DATA_TYPE_MAPPER = new OracleDataTypeMapper();

    public OracleDataBase(String name) {
        super(name);
    }

    @Override
    public TableMetaData createTable(TableMetaData metaData, Object session) throws Exception {
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        config.getExecutorConfig().setMetaData(metaData);
        SqlRender render = metaData.render(SqlRenderType.CREATE);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        metaData.setDataUpdated(true);//标记数据已更新
        config.getSqlExecutor().exec(executorConfig);
        putTable(metaData);
        return metaData;
    }

    @Override
    public TableMetaData updateTable(TableMetaData metaData, Object session) throws Exception {
        TableMetaData old = getTable(metaData.getName());
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        SqlRender render = old.render(SqlRenderType.ALTER);
        config.setData(metaData);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        executorConfig.setMetaData(old);
        config.getSqlExecutor().exec(executorConfig);
        metaData.setDataUpdated(true);
        putTable(metaData);
        return old;
    }

    @Override
    public TableMetaData dropTable(TableMetaData metaData, Object session) throws Exception {
        throw new Exception("version lacks!");
    }

    @Override
    public TableParser getParser() throws Exception {
        return parser;
    }

    public DataBaseType getType() {
        return DataBaseType.ORACLE;
    }

    @Override
    public TableMetaData putTable(TableMetaData metaData) throws Exception {
        if (metaData.getDataTypeMapper() == null)
            metaData.setDataTypeMapper(DEFAULT_DATA_TYPE_MAPPER);
        return super.putTable(metaData);
    }
}
