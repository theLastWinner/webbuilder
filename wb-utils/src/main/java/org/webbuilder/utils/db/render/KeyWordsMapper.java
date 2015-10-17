package org.webbuilder.utils.db.render;


import org.webbuilder.utils.db.def.FieldMetaData;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public abstract class KeyWordsMapper {
    public Map<String, Mapper> key_mapper = new ConcurrentHashMap<>();

    public void registerMapper(KeyWordsMapper.Mapper mapper) {
        key_mapper.put(mapper.getKw(), mapper);
    }

    public abstract KeyWordsMapper.Mapper getMapperByKey(String key);

    public abstract KeyWordsMapper.Mapper getMapper(String key);

    public abstract static class Mapper {

        public Mapper() {
        }

        public Mapper(String kw) {
            this.kw = kw;
        }

        public abstract String getKw();

        private Class<?>[] types = {String.class, Number.class, Date.class};

        private String kw;

        public Class<?>[] getTypes() {
            return types;
        }

        public boolean canUse(FieldMetaData<?> field) {
            for (Class<?> aClass : getTypes()) {
                try {
                    field.getJavaType().asSubclass(aClass);
                    return true;
                } catch (Exception e) {
                }
            }
            return false;
        }

        public void setTypes(Class<?>... types) {
            this.types = types;
        }

        public String template(FieldMetaData<?> field) {
            return template(field, null);
        }

        public abstract String template(FieldMetaData<?> field, String name);

        public abstract String fieldName(String key);

        public Object value(FieldMetaData<?> field, Object value) {
            return value;
        }

    }
}
