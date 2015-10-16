package org.webbuilder.web.core.utils;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created by 浩 on 2015-08-31 0031.
 */
public class DynamicClassLoader extends URLClassLoader {
    public DynamicClassLoader(URL[] urls) {
        super(urls);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    private static final DynamicClassLoader instance = new DynamicClassLoader(new URL[0], DynamicClassLoader.class.getClassLoader());

    public static <T> Class<T> loadClass(File path, String name) throws Exception {
//        WebappClassLoader loader = (WebappClassLoader) DynamicClassLoader.class.getClassLoader();
//        if (path.canRead())
//            loader.addRepository(path.toURI().toURL().toString());
        // return (Class<T>) loader.loadClass(name, true);
        return null;
    }

    public static DynamicClassLoader getInstance() {
        return instance;
    }


    public static void main(String[] args) {
        try {
            Class.forName("org.apache.lucene.LucenePackage");
        } catch (ClassNotFoundException e) {
            System.out.println("未获取到");
        }
        System.out.println(System.getProperty("java.class.path"));

        try {
            while (true) {
                try {
                    Class c = DynamicDeployBeans.class.getClassLoader().loadClass("org.apache.lucene.search.AutomatonQuery");
                    System.out.println(c);
                } catch (Exception e) {
                    System.out.println("加载失败!" + e.getMessage());
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class clazz = Class.forName("org.apache.lucene.LucenePackage");
            System.out.println(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
