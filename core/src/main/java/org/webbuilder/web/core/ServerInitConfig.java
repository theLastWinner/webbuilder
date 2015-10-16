package org.webbuilder.web.core;

import org.webbuilder.utils.db.def.DataBase;
import org.webbuilder.utils.db.def.DataBaseStorage;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.imp.mysql.MySqlDataBase;
import org.webbuilder.utils.db.imp.oracle.OracleDataBase;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.service.form.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 服务器初始化配置
 * Created by 浩 on 2015-07-22 0022.
 */
public class ServerInitConfig implements ApplicationListener {
    private Logger logger = LoggerFactory.getLogger(ServerInitConfig.class);

    @Resource
    private FormService formService;

    public void initCustomForm() {
        try {
            List<Form> forms = formService.select(new HashMap<String, Object>());
            for (Form data : forms) {
                //获取数据库，如果未获取到，则新建
                DataBase dataBase = DataBaseStorage.getDataBase(data.getDb_name());
                if (dataBase == null) {
                    if ("oracle".equals(data.getDb_type())) {
                        dataBase = DataBaseStorage.register(new OracleDataBase(data.getDb_name()));
                    } else if ("mysql".equals(data.getDb_type())) {
                        dataBase = DataBaseStorage.register(new MySqlDataBase(data.getDb_name()));
                    } else {
                        throw new BusinessException("不支持的数据库类型:" + data.getDb_type());
                    }
                }
                TableMetaData metaData = new TableMetaData(data.getTable_name());
                metaData.setDefineContent(data.getContent());
                metaData.setDefineContentType(TableMetaData.DefineContentType.HTML);
                metaData.getFields().addAll(FormService.DEFAULT_FIELD);
                formService.initForeign(metaData,data.getForeigns());
                dataBase.getParser().parse(metaData);
                dataBase.putTable(metaData);
            }
        } catch (Exception e) {
            logger.error("加载表单失败:", e);
        }
    }

    public void init() {
        initCustomForm();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

    }
}
