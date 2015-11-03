package org.webbuilder.generator.service.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-30 0030.
 */
public class ConnectionBuilder {

    protected  static Logger logger = LoggerFactory.getLogger(ConnectionBuilder.class);

    public static Connection getConnection(Map<String, String> config) {
        logger.info("数据库配置:" + config);
        try {
            Class.forName(config.get("driver"));
            Connection connection = DriverManager.getConnection(config.get("url"),config.get("username"), config.get("password"));
            return connection;
        } catch (Exception e) {
            logger.error("获取链接失败!", e);
        }
        return null;
    }
}
