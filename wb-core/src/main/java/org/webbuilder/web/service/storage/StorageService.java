package org.webbuilder.web.service.storage;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.StorageDriver;
import org.webbuilder.utils.storage.driver.StorageDriverManager;
import org.webbuilder.utils.storage.driver.local.LocalStorageDriver;
import org.webbuilder.utils.storage.driver.local.PropertiesStorageDriver;
import org.springframework.stereotype.Service;
import org.webbuilder.web.core.aop.transactional.TransactionDisabled;

/**
 * Created by 浩 on 2015-08-11 0011.
 */
@Service
public class StorageService {

    private String default_name = "local";

    public StorageDriver getDriver() {
        return getDriver(getDefault_name());
    }

    public StorageDriver getConfigDriver() {
        return getDriver(PropertiesStorageDriver.NAME);
    }

    public StorageDriver getLocalDriver() {
        return getDriver(LocalStorageDriver.NAME);
    }

    public StorageDriver getDriver(String name) {
        return StorageDriverManager.getDriver(name);
    }

    public <K, V> Storage<K, V> getStorage(String name) throws Exception {
        return getDriver().getStorage(name);
    }

    public <K, V> Storage<K, V> getStorage(Class<V> type) throws Exception {
        return getDriver().getStorage(type);
    }

    public <K, V> Storage<K, V> getStorage(String name, Class<V> type) throws Exception {
        return getDriver().getStorage(name, type);
    }

    public String getDefault_name() {
        return default_name;
    }

    public void setDefault_name(String default_name) {
        this.default_name = default_name;
    }

}
