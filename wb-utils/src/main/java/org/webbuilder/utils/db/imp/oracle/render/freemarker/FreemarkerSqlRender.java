package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringTemplateUtils;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;
import org.webbuilder.utils.db.render.KeyWordsMapper;
import org.webbuilder.utils.db.render.SqlRender;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;
import org.webbuilder.utils.db.render.conf.SqlRenderHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-07-05 0005.
 */
public abstract class FreemarkerSqlRender extends SqlRender {
    private static final KeyWordsMapper KEY_WORDS_MAPPER = new OracleKeyWordsMapper();

    public String getQuotesStart(){
        return "\"";
    }

    public String getQuotesEnd(){
        return "\"";
    }
    @Override
    public KeyWordsMapper getKeyWordsMapper() {
        if (super.getKeyWordsMapper() == null)
            return KEY_WORDS_MAPPER;
        else
            return super.getKeyWordsMapper();
    }

    //获取预编译参数正则表达式：@{}
    private static final Pattern pat = Pattern.compile("(?<=@\\{)(.+?)(?=\\})");
    //默认模板
    private String template;
    //缓存
    private Map<String, SqlInfo> cache = new ConcurrentHashMap<String, SqlInfo>();
    //是否使用缓存
    public static boolean USE_CACHE = true;
    //缓存最大数量
    public static int MAX_CACHE_SIZE = 100;
    //ValueParserHelper 池，用于自定义解析值，如日期格式化，数组转换
    protected Map<Class<?>, DataFormatHelper> dataFormatHelperMap = new ConcurrentHashMap<Class<?>, DataFormatHelper>();

    //默认ValueParserHelper，未找到对应类型的helper时使用此helper
    private static final DataFormatHelper<Object> defaultHelper = new DataFormatHelper<Object>() {
        public Object format(String key, Object value) {
            if (value == null)
                return null;
            //使用KeyWordsMapper进行值替换
            KeyWordsMapper.Mapper mapper = KEY_WORDS_MAPPER.getMapper(key);
            if (mapper == null)
                return value;
            FieldMetaData fieldMetaData = new FieldMetaData(mapper.fieldName(key), value.getClass());
            return mapper.value(fieldMetaData, value);
        }
    };
    //默认的日期格式helper
    private static DataFormatHelper<Date> dateValueParserHelper = null;
    //默认的数字数组格式helper
    private static DataFormatHelper<Number[]> numberArrValueParserHelper = null;
    //默认的字符串数组格式helper
    private static DataFormatHelper<String[]> strArrValueParserHelper = null;

    static {
        //日期自动格式化为 yyyy-MM-dd HH:mm:ss 格式
        dateValueParserHelper = new DataFormatHelper<Date>() {
            @Override
            public Object format(String key, Date param) {
                return DateTimeUtils.format(param, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
            }
        };
        //数字数组转为字符串: 1,2,3
        numberArrValueParserHelper = new DataFormatHelper<Number[]>() {
            @Override
            public Object format(String key, Number[] number) {
                String[] valStr = new String[number.length];
                for (int i = 0; i < number.length; i++) {
                    valStr[i] = String.valueOf(number[i]);
                }
                return arr2String(valStr);
            }
        };
        //字符串数组转为字符串: 'aa',1,
        strArrValueParserHelper = new DataFormatHelper<String[]>() {
            @Override
            public Object format(String key, String[] valStr) {
                return arr2String(valStr);
            }
        };
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        cache.clear();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sqlRender cache clear!");
        }
    }

    /**
     * 载入缓存，当数量已经达到最大值，先清空再载入
     *
     * @param key     缓存key
     * @param sqlInfo SqlInfo实体
     */
    public void putCache(String key, SqlInfo sqlInfo) {
        if (cache.size() >= MAX_CACHE_SIZE)
            clearCache();
        cache.put(key, sqlInfo);
    }

    /**
     * 字符串数组转为字符串 如["a",1,2]转为 'a',1,2
     *
     * @param valStr 要转换的数组
     * @return 转换后的字符串
     */
    private static String arr2String(String[] valStr) {
        //把值转为字符串
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String s : valStr) {
            if (index++ != 0)
                builder.append(",");
            if (StringUtil.isNumber(s)) {
                builder.append(s);
            } else {
                builder.append("'").append(s).append("'");
            }
        }
        return builder.toString();
    }


    public FreemarkerSqlRender(TableMetaData metaData) {
        super(metaData);
        LOGGER.debug("init render for :" + metaData);
        try {
            //载入默认helper
            helper(dateValueParserHelper).
                    helper(numberArrValueParserHelper).
                    helper(strArrValueParserHelper);
        } catch (Exception e) {
        }
    }

    @Override
    public SqlRender helper(SqlRenderHelper helper) throws Exception {
        if (helper instanceof DataFormatHelper) {
            DataFormatHelper helper1 = (DataFormatHelper) helper;
            dataFormatHelperMap.put(helper1.getGenericType(), helper1);
        }
        return this;
    }

    /**
     * 使用helper进行值重构
     *
     * @param key   键
     * @param value 要重构的值
     * @return 重构后的值
     */
    public Object parseValue(String key, Object value) {
        if (value == null)
            return value;
        Class type = value.getClass();
        DataFormatHelper helper = dataFormatHelperMap.get(type);
        if (helper == null)
            helper = dataFormatHelperMap.get(Object.class);
        if (helper == null)
            helper = defaultHelper;
        return helper.format(key, value);
    }

    /**
     * 根据参数构建sql对象
     *
     * @param param 参数
     * @return SqlInfo 实例
     * @throws Exception 构建异常
     */
    public SqlInfo build(Map<String, Object> param) throws Exception {
        SqlInfo sqlInfo;
        LOGGER.info("start build sql!");
        String cacheKey = String.valueOf(param.hashCode());
        if (USE_CACHE) {//先从缓存中获取
            sqlInfo = cache.get(cacheKey);
            if (sqlInfo != null) {
                LOGGER.info("build sql success. from cache!");
                return sqlInfo;
            }
        }
        //先使用freemarker进行预处理
        String newSql = StringTemplateUtils.generate(templateKey(), param);
        //循环处理，知道sql中已不包含${} freemarker标签
        while (newSql.contains("${")) {
            newSql = StringTemplateUtils.compileAndGenerate(templateKey() + newSql.hashCode(), newSql, param);
        }
        //去除换行等操作符
        newSql = newSql.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\\s+", " ");
        SqlInfo sql_res = compileSql(newSql, param);
        LOGGER.info("build sql success!");
        putCache(cacheKey, sql_res);
        return sql_res;
    }

    public SqlInfo compileSql(String sql, Object param) {
        List<Object> obj = new ArrayList<Object>();
        String newSql = sql;
        //进行预编译参数处理
        //正则截取 @{}
        Matcher mat = pat.matcher(newSql);
        //循环查找符合正则条件@{}的字符串
        while (mat.find()) {
            String kw = mat.group(0);
            Object val;
            KeyWordsMapper.Mapper mapper = getKeyWordsMapper().getMapper(kw);
            //迭代参数
            if (kw.endsWith("...")) {
                mapper = getKeyWordsMapper().getMapper(kw.replace("...", ""));
                val = mapper.value(getMetaData().getField(mapper.fieldName(kw)), getParamValue(kw.replace("...", ""), param));
                if (val != null) {
                    if (val instanceof Iterable) {
                        Iterable iterable = (Iterable) val;
                        StringBuilder params = new StringBuilder();
                        int i = 0;
                        for (Object val_ : iterable) {
                            if (i++ != 0) params.append(",");
                            params.append("?");
                            obj.add(val_);
                        }
                        newSql = newSql.replaceFirst("@\\{" + kw.replace("$", "\\$") + "\\}", params.toString());
                    }
                    continue;
                }
            }
            //将需要预编译的参数替换为?
            newSql = newSql.replaceFirst("@\\{" + kw.replace("$", "\\$") + "\\}", "?");
            val = mapper.value(getMetaData().getField(mapper.fieldName(kw)), getParamValue(kw, param));
            //将value放入参数列表
            obj.add(parseValue(kw, val));
        }
        SqlInfo sql_res = createSqlInfo(newSql);
        sql_res.setParams(obj.toArray());
        return sql_res;
    }

    public static void main(String[] args) {
        FreemarkerSqlRender render = new SelectRender(null);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("user_ids", Arrays.asList(1, 2, "aa"));
        SqlInfo sqlInfo = render.compileSql("select * from user_s where id in (@{user_ids...})", params);
        System.out.println(sqlInfo);
    }

    /**
     * 根据key获取参数中的值，支持类似 user.id 多层属性获取
     *
     * @param key   Key
     * @param param 参数集合
     * @return 获取到的值，如果值为null，将返回""字符串
     */
    public Object getParamValue(String key, Object param) {
        try {
            return ClassUtil.getValueByAttribute(key, param);
        } catch (Exception e) {
            return "";
        }
    }

    public String templateKey() {
        return "template_" + getTemplate().hashCode();
    }

    public void checkTableChange() throws Exception {
        if (tableChanged()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("table info is changed!");
            }
            initTableHash();
            clearCache();
            init();
        }
    }

    public String getTemplate() {
        return template;
    }

    public SqlRender setTemplate(String template) {
        this.template = template;

        try {
            StringTemplateUtils.compileTemplate(templateKey(), getTemplate());
            if (LOGGER.isInfoEnabled())
                LOGGER.info("编译模板成功 from :" + this.getClass());
        } catch (Exception e) {
            LOGGER.error("编译模板失败", e);
        }
        return this;
    }

    public static abstract class DataFormatHelper<T> implements SqlRenderHelper {
        /**
         * 获取泛型类型
         *
         * @return 泛型的类型
         */
        public Class<?> getGenericType() {
            return ClassUtil.getGenericType(getClass());
        }

        public abstract Object format(String key, T param);
    }

}
