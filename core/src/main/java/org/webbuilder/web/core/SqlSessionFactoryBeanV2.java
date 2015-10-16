package org.webbuilder.web.core;

import org.apache.ibatis.io.VFS;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 对SqlSessionFactoryBean拓展，使其支持别名扫描
 * Created by 浩 on 2015-07-21 0021.
 */
public class SqlSessionFactoryBeanV2 extends SqlSessionFactoryBean {
    private Resource[] typeAliasesResources;

    @Override
    public void setTypeAliasesPackage(String typeAliasesPackage) {
        if (typeAliasesPackage != null) {
            String[] environment = StringUtils.tokenizeToStringArray(typeAliasesPackage, ",; \t\n");
            Set<Class> classes = new HashSet<>();
            for (String mapperLocation : environment) {
                classes.addAll(find(mapperLocation));
            }
            this.setTypeAliases(classes.toArray(new Class[classes.size()]));
        }
    }

    public void setTypeAliasesResources(Resource[] resource) {
        System.out.println(resource);
    }

    public Set<Class> find(String packageName) {
        String path = packageName.replace('.', '/');
        Set<Class> classess = new LinkedHashSet<>();
        try {
            List ioe = VFS.getInstance().list(path);
            Iterator i$ = ioe.iterator();
            while (i$.hasNext()) {
                String child = (String) i$.next();
                if (child.endsWith(".class")) {
                    child = child.substring(0, child.lastIndexOf(".")).replace("/", ".");
                    classess.add(Class.forName(child));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classess;
    }

}
