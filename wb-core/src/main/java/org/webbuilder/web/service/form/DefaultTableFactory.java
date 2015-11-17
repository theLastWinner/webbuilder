package org.webbuilder.web.service.form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.webbuilder.sql.DataBase;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.parser.CommonTableMetaDataParser;
import org.webbuilder.sql.parser.TableMetaDataParser;
import org.webbuilder.utils.base.file.FileUtil;
import org.webbuilder.web.core.aop.transactional.TransactionDisabled;
import org.webbuilder.web.po.form.Form;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-17 0017.
 */
@Service
public class DefaultTableFactory {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataBase dataBase;

    private TableMetaDataParser parser = new CommonTableMetaDataParser();

    private org.springframework.core.io.Resource[] localFiles;

    @Resource
    private FormService formService;

    @TransactionDisabled
    public void init() {
        initLocalFiles();
        initDb();
    }

    @TransactionDisabled
    public void initLocalFiles() {
        if (getLocalFiles() == null) return;
        for (org.springframework.core.io.Resource localFile : localFiles) {
            try {
                File file = localFile.getFile();
                if (file.getName().endsWith(".html")) {
                    String content = FileUtil.readFile2String(file.getAbsolutePath());
                    TableMetaData tableMetaData = parser.parse(content, "html");
                    if (tableMetaData.getName() == null) {
                        tableMetaData.setName(file.getName().split("[.]")[0]);
                    }
                    dataBase.getMetaData().addTable(tableMetaData);
                    logger.debug("init table success {}", file);
                }
            } catch (Exception e) {
                logger.error("init table error", e);
            }
        }
    }

    @TransactionDisabled
    public void initDb() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("db_name", dataBase.getMetaData().getName());
            List<Form> forms = formService.select(map);
            for (Form form : forms) {
                TableMetaData tableMetaData = parser.parse(form.getContent(), "html");
                tableMetaData.setName(form.getName());
                dataBase.getMetaData().addTable(tableMetaData);
                logger.debug("init table success {}", form.getU_id());
            }
        } catch (Exception e) {
            logger.error("init table error", e);
        }
    }

    @TransactionDisabled
    public DataBase getDataBase() {
        return dataBase;
    }

    @TransactionDisabled
    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @TransactionDisabled
    public void setParser(TableMetaDataParser parser) {
        this.parser = parser;
    }

    @TransactionDisabled
    public TableMetaDataParser getParser() {
        return parser;
    }

    @TransactionDisabled
    public org.springframework.core.io.Resource[] getLocalFiles() {
        return localFiles;
    }

    @TransactionDisabled
    public void setLocalFiles(org.springframework.core.io.Resource[] localFiles) {
        this.localFiles = localFiles;
    }
}
