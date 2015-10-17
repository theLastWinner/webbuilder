package org.webbuilder.utils.storage.instance;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.event.StorageListener;
import org.webbuilder.utils.storage.event.Finder;
import org.webbuilder.utils.storage.exception.StorageException;

import java.io.StringReader;
import java.util.*;

/**
 * Created by 浩 on 2015-6-13.
 */
public class PropertiesStorage extends Storage<String, String> {

    private Properties properties;

    private String content;

    public PropertiesStorage(String name, Properties properties) {
        super(name);
        this.properties = properties;
    }

    public PropertiesStorage(String name, String content) {
        super(name);
        this.content = content;
    }

    @Override
    public Set<String> keySet() {
        return (Set) properties.keySet();
    }

    @Override
    public List<String> find(Finder<String, String> finder) {
        Map<String, String> res = new HashMap();
        int count = 0;
        for (Map.Entry kvEntry : properties.entrySet()) {
            if (finder.isOver())
                break;
            if (finder.each(++count,   (String)kvEntry.getKey(), (String) kvEntry.getValue())) {
                res.put((String) kvEntry.getKey(), (String) kvEntry.getValue());
            }
        }
        List<String> data = (List) new LinkedList<>(res.values());
        return data;
    }

    @Override
    public String get(String key) {
        String val = (String) properties.get(key);
        if (val == null) {
            for (StorageListener<String, String> e : getListeners()) {
                val = e.onNotFoundVal(key);
                if (logger.isInfoEnabled())
                    logger.info(getName() + " key:" + key + " not found! do event!");
                if (val != null) break;
            }
            if (val != null)
                put(key, val);
            else
                return null;
        }
        return val;
    }

    @Override
    public boolean put(String key, String val) {
        properties.put(key, val);
        return true;
    }

    @Override
    public boolean remove(String key) {
        properties.remove(key);
        return true;
    }

    @Override
    public int clear() {
        int size = size();
        properties.clear();
        return size;
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public void init() throws Exception {
        if (StringUtil.isNullOrEmpty(getName())) {
            throw new StorageException("config name can't be null!");
        }
        if (properties != null && StringUtil.isNullOrEmpty(getContent()))
            return;
        properties = new Properties();
        properties.load(new StringReader(getContent()));
        for (StorageListener e : getListeners()) {
            e.onLoad(this);
        }
        if (logger.isDebugEnabled())
            logger.debug("init " + getName() + " success!");
    }


    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
