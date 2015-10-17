package org.webbuilder.utils.storage.driver;

import org.webbuilder.utils.storage.driver.local.LocalStorageDriver;
import org.webbuilder.utils.storage.driver.local.PropertiesStorageDriver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-08-07 0007.
 */
public class StorageDriverManager {

    private static final Map<String, StorageDriver> drivers = new ConcurrentHashMap<>();

    private StorageDriverManager() {
    }
    static{
        //默认对local支持
        try {
            Class.forName(LocalStorageDriver.class.getName());
            Class.forName(PropertiesStorageDriver.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void registerDriver(StorageDriver driver) {
        drivers.put(driver.getName(), driver);
    }

    public static StorageDriver getDriver(String name) {
        return drivers.get(name);
    }

}
