package org.webbuilder.utils.storage.driver.local;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.CommonStorageDriver;
import org.webbuilder.utils.storage.driver.StorageDriverManager;
import org.webbuilder.utils.storage.exception.StorageException;
import org.webbuilder.utils.storage.instance.PropertiesStorage;

/**
 * Created by 浩 on 2015-08-10 0010.
 */
public class PropertiesStorageDriver extends CommonStorageDriver {

    public static final String NAME = "config";

    private PropertiesStorageDriver() {

    }

    private static final PropertiesStorageDriver driver = new PropertiesStorageDriver();

    static {
        StorageDriverManager.registerDriver(driver);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public <K, V> Storage<K, V> registerStorage(Storage<K, V> storage) throws Exception {
        if (!(storage instanceof PropertiesStorage)) {
            throw new StorageException("storage not instanceof PropertiesStorage!");
        }
        return cache.put(storage.getName(), storage);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
