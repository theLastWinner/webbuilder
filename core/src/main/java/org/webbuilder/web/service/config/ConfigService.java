package org.webbuilder.web.service.config;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.logger.LoggerConfig;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.core.service.SocketService;
import org.webbuilder.web.dao.config.ConfigMapper;
import org.webbuilder.web.po.config.Config;
import org.apache.ibatis.io.Resources;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.socket.WebSocketSession;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * 系统配置服务类
 * Created by generator
 */
@Service
public class ConfigService extends GenericService<Config, String> implements SocketService {
    public static final String CACHE_KEY = "config";
    //默认数据映射接口
    @Resource
    protected ConfigMapper configMapper;

    @Override
    protected ConfigMapper getMapper() {
        return this.configMapper;
    }

    @Override
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public int update(Config data) throws Exception {
        return super.update(data);
    }

    /**
     * 根据配置名称，获取配置内容
     *
     * @param name 配置名称
     * @return 配置内容
     * @throws Exception 异常信息
     */
    @Cacheable(value = CACHE_KEY, key = "'info_content_'+#name")
    public String getContent(String name) throws Exception {
        Config config = getMapper().selectByPk(name);
        if (config == null) return null;
        return config.getContent();
    }

    /**
     * 根据配置名称，获取配置内容，并解析为Properties格式
     *
     * @param name 配置名称
     * @return 配置内容
     * @throws Exception 异常信息
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name")
    public Properties get(String name) throws Exception {
        Config config = getMapper().selectByPk(name);
        if (config == null) return new Properties();
        return config.toMap();
    }

    /**
     * 获取配置中指定key的值
     *
     * @param name 配置名称
     * @param key  key 异常信息
     * @return 指定的key对应的value
     * @throws Exception
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key")
    public String get(String name, String key) throws Exception {
        return get(name).getProperty(key);
    }

    /**
     * 获取配置中指定key的值，并指定一个默认值，如果对应的key未获取到，则返回默认值
     *
     * @param name         配置名称
     * @param key          key 异常信息
     * @param defaultValue 默认值
     * @return 对应key的值，若为null，则返回默认值
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key")
    public String get(String name, String key, String defaultValue) {
        String val;
        try {
            val = this.get(name).getProperty(key);
            if (val == null) {
                logger.error(String.format("获取配置:%s.%s失败,defaultValue:%s", name, key, defaultValue));
                return defaultValue;
            }
        } catch (Exception e) {
            logger.error(String.format("获取配置:%s.%s失败,defaultValue:%s", name, key, defaultValue));
            return defaultValue;
        }
        return val;
    }


    /**
     * 参照 {@link ConfigService#get(String, String)}，将值转为int类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_int'")
    public int getInt(String name, String key) throws Exception {
        return StringUtil.toInt(get(name, key));
    }

    /**
     * 参照 {@link ConfigService#get(String, String)}，将值转为double类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_double'")
    public double getDouble(String name, String key) throws Exception {
        return StringUtil.toDouble(get(name, key));
    }

    /**
     * 参照 {@link ConfigService#get(String, String)}，将值转为long类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_long'")
    public long getLong(String name, String key) throws Exception {
        return StringUtil.toLong(get(name, key));
    }

    /**
     * 参照 {@link ConfigService#get(String, String, String)}，将值转为int类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_int'")
    public int getInt(String name, String key, int defaultValue) {
        return StringUtil.toInt(get(name, key, String.valueOf(defaultValue)));
    }

    /**
     * 参照 {@link ConfigService#get(String, String, String)}，将值转为double类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_double'")
    public double getDouble(String name, String key, double defaultValue) {
        return StringUtil.toDouble(get(name, key, String.valueOf(defaultValue)));
    }

    /**
     * 参照 {@link ConfigService#get(String, String, String)}，将值转为long类型
     */
    @Cacheable(value = CACHE_KEY, key = "'info_'+#name+'_key_'+#key+'_long'")
    public long getLong(String name, String key, long defaultValue) {
        return StringUtil.toLong(get(name, key, String.valueOf(defaultValue)));
    }

    private final EntityResolver entityResolver = new EntityResolver() {
        String emptyDtd = "";

        ByteArrayInputStream bytels = new ByteArrayInputStream(emptyDtd.getBytes());

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(bytels);
        }
    };

    /**
     * 动态从config中加载logback配置文件，必须保证配置管理中已存在配置：logback.json，
     * 如果配置管理中不存在logback.xml默认配置，则使用classpath中的logback.xml配置
     *
     * @throws Exception 当logback.json不存在时，抛出NullPointerException异常
     */
    public void loadConfig4LogBack() throws Exception {
        Config config = this.selectByPk("logback.json");
        Config config_base = this.selectByPk("logback.xml");
        if (config == null) {
            throw new NullPointerException("logback.json is not found!");
        }
        String json = config.getContent(); //json 配置
        List<Map<String, Object>> config_data = JSON.parseObject(json, LinkedList.class);
        SAXReader saxReader = new SAXReader();
        saxReader.setEntityResolver(entityResolver);
        Reader configXML =
                config_base == null
                        ? Resources.getResourceAsReader("logback.xml")
                        : new StringReader(config_base.getContent());
        Document document = saxReader.read(configXML);
        Element root = document.getRootElement();
        for (Map<String, Object> loggerData : config_data) {
            Element logger = root.addElement("logger");
            String name = String.valueOf(loggerData.get("name"));
            String level = String.valueOf(loggerData.get("level"));
            Object appenderRef = loggerData.get("appenderRef");
            if (name == null) continue;
            if (level == null) level = "OFF";
            if (appenderRef == null || !(appenderRef instanceof List)) appenderRef = new ArrayList<>();
            logger.addAttribute("name", name);
            logger.addAttribute("level", level);
            //appender
            List<String> appenders = (List) appenderRef;
            for (String ref : appenders) {
                Element appender = logger.addElement("appender-ref");
                appender.addAttribute("ref", ref);
            }
        }
        LoggerConfig.loadConfigure(document.asXML().getBytes("utf8"));
    }

    @Override
    public Object doService(WebSocketSession session, String name, Map<String, Object> param) throws Exception {

        return null;
    }

}
