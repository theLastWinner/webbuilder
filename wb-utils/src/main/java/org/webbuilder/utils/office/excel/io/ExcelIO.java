package org.webbuilder.utils.office.excel.io;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.office.excel.annotation.Excel;
import org.webbuilder.utils.office.excel.callback.CallBack;
import org.webbuilder.utils.office.excel.callback.CallBackAdapter;
import org.webbuilder.utils.office.excel.callback.ObjcetCreater;
import org.webbuilder.utils.office.excel.html.Excel2Html;
import org.webbuilder.utils.office.excel.io.API.POIExcelIO;
import org.webbuilder.utils.base.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * excel 读写工具类，用于对excel和java对象集合的互转处理
 *
 * @author ZhouHao
 */
public final class ExcelIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelIO.class);

    /**
     * 读取excel基础方法，在读取时，只进行回调操作
     *
     * @param input
     * @param config Excel输入输出配置对象，进行回调，格式化，过滤等配置
     */
    private static void read(InputStream input, ExcelIOConfig config) {
        try {
            if (ExcelIOBuilder.getApi() == null)
                ExcelIOBuilder.initApi(new POIExcelIO());
            ExcelIOBuilder.getApi().read(input, config);
        } catch (Exception e) {
            LOGGER.error("读取excel异常:", e);
        }
    }

    /**
     * 读取excel为对象集合
     *
     * @param <T>   对象集合中对象泛型
     * @param <O>
     * @param input excel输入流
     * @param type  对象类型
     * @return excel解析后的对象集合
     */
    public static <T, O> List<T> read(final InputStream input, final Class<T> type, final ReadExcelConfig<T> readConfig) {
        final ObjcetCreater<?> creaters[] = readConfig.getCreaters();
        CallBack rowCallBack = new CallBackAdapter();

        final Map<Integer, T> datas_tmp = new LinkedHashMap<Integer, T>();
        // 进行对象属性，注解缓存
        final Field[] fields = type.getDeclaredFields();
        final Map<String, Excel[]> annotations = AnnotationUtil.getAnnotationsMapByFields(fields, Excel.class);
        final Map<String, Field> tmps = new HashMap<String, Field>();
        // 填充缓存
        for (Field field : fields) {
            Object[] excels = annotations.get(field.getName());
            if (excels != null) {
                Excel e = (Excel) excels[0];
                if (ClassUtil.isBaseClass(field.getType())) {
                    tmps.put(e.headerName(), field);
                    tmps.put(field.getName(), field);
                } else {
                    String[] cus = e.customRole().split(",");
                    for (String rol : cus) {
                        String[] r = rol.split("=");
                        tmps.put(r[0], field);
                    }
                }
            } else {
                tmps.put(field.getName(), field);
            }
        }
        // excel回调
        final CallBack cellCallBack = new CallBackAdapter() {
            private static final long serialVersionUID = -612826252323164298L;
            int tmp = -1;// 当前行的临时缓存
            T now = null;// 当前在处理的对象(行)
            T next = null;// 下一个要处理的对象(行)
            O o_tmp = null;
            boolean isLast = false;
            ObjcetCreater<O> creater_tmp = null;

            @SuppressWarnings("unchecked")
            @Override
            public Object success(String header, Object val) {
                Field field = tmps.get(header.trim());
                if (field != null) {
                    Method method = ClassUtil.getSetMethodByField(type, field, field.getType());
                    Object[] excels = annotations.get(field.getName());
                    Excel excel = null;
                    if (excels != null) {
                        excel = (Excel) excels[0];
                    }
                    try {
                        // 自定义值替换
                        ReadExcelConfig.ChangeValue changeValue = readConfig.getChangeValue(field.getName());
                        Object tmp = null;
                        if (changeValue != null)
                            tmp = changeValue.getValue(val);
                        if (tmp != null)
                            val = tmp;

                        // 基础类型 直接调用set方法进行填充
                        if (ClassUtil.isBaseClass(field.getType()) && !field.getType().equals(List.class) && !field.getType().equals(ArrayList.class)) {
                            method.invoke(now, ClassUtil.String2Obj(String.valueOf(val), field.getType()));
                        } else {
                            // 自定义类型 调用typeIsCustom方法进行处理后返回数据
                            method.invoke(now, typeIsCustom(header, val, field, excel));
                        }
                    } catch (Exception e) {
                        exception(header, e);
                    }
                } else {
                    readConfig.headerNotFound(header, val, now);
                }
                if (isLast)
                    datas_tmp.put(tmp + 1, now);
                return null;
            }

            @SuppressWarnings("unchecked")
            public Object typeIsCustom(String header, Object val, Field field, Excel excel) {
                // 自定义规则
                String[] role = excel.customRole().split(",");
                if (role.length <= 0)
                    return null;
                HashMap<String, String> map = new HashMap<String, String>();
                for (String r : role) {
                    String[] rs = r.split("=");
                    if (rs.length >= 2)
                        map.put(rs[0], rs[1]);
                }
                // 调用对应类型的自定义对象创建接口
                for (ObjcetCreater<?> creater : creaters) {
                    if (creater.getType().getName().equals(field.getType().getName())) {
                        creater_tmp = (ObjcetCreater<O>) creater;
                        // 自定义对象实例
                        O v = null;
                        try {
                            v = (O) ClassUtil.getValueByAttribute(field.getName(), now);
                            if (v == null) {
                                v = (O) field.getType().newInstance();
                            }
                        } catch (Exception e) {
                            LOGGER.error("实例化自定义对象实例:" + field.getType() + "异常", e);
                        }
                        return o_tmp = (O) creater.execute(map.get(header), val, v);
                    }
                }
                return super.typeIsCustom(map.get(header), val, excel);
            }

            @Override
            public Object exception(String header, Throwable e) {
                LOGGER.error("回调异常:" + this, e);
                return null;
            }

            @Override
            public Object before(int index, int max) {
                // 索引和缓存不相同，表示现在是下一行数据
                if (index != tmp) {
                    if (creater_tmp != null && o_tmp != null) {
                        if (creater_tmp.getType().getName().equals(o_tmp.getClass().getName())) {
                            creater_tmp.objectCreateSuccess(o_tmp);
                        }
                    }
                    // 创建下一个数据实体
                    if (next == null) {
                        createObject();
                    }
                    // 添加当前处理完的实体
                    if (tmp != -1) {
                        datas_tmp.put(tmp, now);
                    }
                    if (index == max) {
                        isLast = true;
                    }
                    // 更改处理状态
                    now = next;
                    next = null;
                    tmp = index;
                }
                return null;
            }

            private void createObject() {
                if (next == null) {
                    try {
                        next = type.newInstance();
                        // System.out.println("创建实体" + next.getClass() + "@index=" + tmp);
                    } catch (Exception e) {
                        LOGGER.error("创建next失败:" + this, e);
                    }
                }
            }
        };

        ExcelIOConfig config = new ExcelIOConfig(rowCallBack, cellCallBack);
        read(input, config);
        final List<T> datas = new LinkedList<T>(datas_tmp.values());
        return datas;
    }

    @SuppressWarnings("unchecked")
    private static List<Header> getHeaders(Field[] fields, Map<String, Excel[]> annotations, ExcelIOConfig config) {
        final String[] writeFilter = config.getWriteFilter();
        List<Header> headers = new LinkedList<Header>();
        int rowindex = 0;
        for (Field field : fields) {
            config.getRowCallBack().before(rowindex++, fields.length);
            String title = field.getName();
            Object[] ano = annotations.get(field.getName());
            if (ano != null) {
                Excel excel = (Excel) ano[0];
                title = excel.headerName();
                if (writeFilter.length != 0 && (StringUtil.hasStr(writeFilter, field.getName()) || StringUtil.hasStr(writeFilter, title))) {
                    continue;
                }
                if (ClassUtil.isBaseClass(field.getType())) {
                    Header header = new Header(title, field.getName(), field.getType());
                    header.setIndex(excel.index());
                    headers.add(header);
                    config.getRowCallBack().success(title, header);
                } else if (field.getType().getName().equals(List.class.getName()) || field.getType().getName().equals(ArrayList.class.getName())) {
                    // config.getRowCallBack().typeIsCustom(title, field, excel);
                } else {
                    Object heads = config.getRowCallBack().typeIsCustom(title, field, excel);
                    if (heads instanceof List<?>) {
                        headers.addAll((List<Header>) heads);
                    }
                    if (heads instanceof Header) {
                        headers.add((Header) heads);
                    }
                }
            }
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Map<?, ?>> getDatas(List<Header> headers, List<T> datas, Field[] fields, Map<String, Excel[]> annotations, ExcelIOConfig config, WriteExcelConfig writeExcelConfig) {
        List<Map<?, ?>> dataList = new LinkedList<Map<?, ?>>();
        int cellindex = 0;
        for (T data : datas) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            cellindex++;
            for (Field field : fields) {
                Object[] ano = annotations.get(field.getName());
                Excel excel = null;
                if (ano != null) {
                    excel = (Excel) ano[0];
                }
                config.getCellCallBack().before(cellindex, datas.size());
                Method method = ClassUtil.getGetMethodByField(data.getClass(), field);
                if (method == null)
                    continue;
                Object value = null;
                try {
                    if (!ClassUtil.isBaseClass(field.getType())) {
                        // 获取回调值，回调返回值需要是Map<String, Object>类型
                        Object res = config.getCellCallBack().typeIsCustom(field.getName(), data, excel);
                        if (res instanceof Map<?, ?>) {
                            dataMap.putAll((Map<String, Object>) res);
                            // dataList.add(dataMap);
                            continue;
                        }
                    }

                    value = method.invoke(data);
                    WriteExcelConfig.ChangeValue changeValue = writeExcelConfig.getChangeValue(field.getName());
                    Object tmp = null;
                    if (changeValue != null)
                        tmp = changeValue.getValue(value);
                    if (tmp != null)
                        value = tmp;

                    // 对集合进行重新编译
                    if (value instanceof List) {
                        List<?> data_ = (List<?>) value;
                        if (data_.size() > 0) {
                            // 规则转换为map
                            // Map<String, String> roles_map = new HashMap<String, String>();
                            // String role_ = excel.customHeaderRole();
                            // String[] roles = role_.split(",");
                            // for (String r : roles) {
                            // String[] r_ = r.split("=");
                            // roles_map.put(r_[0], r_[1]);
                            // }
                            for (Object object : data_) {
                                // 表头值
                                Object h_v = ClassUtil.getValueByAttribute(excel.KEY_HEADER(), object);
                                // 数据值
                                Object d_v = ClassUtil.getValueByAttribute(excel.KEY_DATA(), object);
                                // 索引值
                                Object i_v = ClassUtil.getValueByAttribute(excel.KEY_INDEX(), object);
                                if (h_v == null || d_v == null) {
                                    continue;
                                }
                                Header header = new Header(h_v.toString(), h_v.toString(), d_v.getClass());
                                if (StringUtil.isInt(i_v)) {
                                    header.setIndex((Integer) i_v);
                                }
                                if (!headers.contains(header))
                                    headers.add(header);
                                dataMap.put(h_v.toString(), d_v);
                            }
                        }
                        continue;
                    }
                    if (value == null) {
                        value = config.getCellCallBack().valueIsNull(field.getName(), field.getType());
                    }
                } catch (Exception e) {
                    value = config.getCellCallBack().exception(field.getName(), e);
                }
                dataMap.put(field.getName(), value);
                config.getCellCallBack().success(field.getName(), value);
            }
            dataList.add(dataMap);
        }
        return dataList;
    }

    /**
     * 自定义配置导出
     *
     * @param <T>    导出对象类型
     * @param out    输出流
     * @param datas  数据集合
     * @param config 配置对象
     * @throws Exception
     */
    private static <T> void write(OutputStream out, List<T> datas, ExcelIOConfig config, WriteExcelConfig writeExcelConfig) throws Exception {
        if (datas.size() == 0)
            return;
        final Field[] fields = datas.get(0).getClass().getDeclaredFields();
        final Map<String, Excel[]> annotations = AnnotationUtil.getAnnotationsMapByFields(fields, Excel.class);
        List<Header> headers = getHeaders(fields, annotations, config);
        List<Map<?, ?>> dataList = getDatas(headers, datas, fields, annotations, config, writeExcelConfig);
        // 表头排序
        Collections.sort(headers);
        // 调用API 进行处理
        write(out, headers, dataList, writeExcelConfig);
    }

    public static <T> void write(OutputStream out, List<T> datas, final WriteExcelConfig writeExcelConfig) throws Exception {
        final ExcelIOConfig config = new ExcelIOConfig();

        // 表头读取回调
        CallBack rowCallBack = new CallBackAdapter() {
            private static final long serialVersionUID = -9083844397250516552L;

            @Override
            public Object typeIsCustom(String header, Object val, Excel excel) {
                // 填充自定义表头如 goodsType.name
                if (val instanceof Field) {
                    String[] roles = excel.customRole().split(",");
                    Field c = (Field) val;
                    List<Header> headers = new LinkedList<Header>();
                    for (String ros : roles) {
                        String[] role = ros.split("=");
                        Header header2 = new Header(role[0], c.getName() + "." + role[1], c.getType());
                        header2.setIndex(excel.index());
                        headers.add(header2);
                    }
                    return headers;
                }
                return null;
            }
        };
        // 单元格读取回调
        CallBack cellCallBack = new CallBackAdapter() {
            private static final long serialVersionUID = -8378044276800396591L;

            @Override
            public Object valueIsNull(String header, Class<?> type) {
                // 值为空时的处理
                // System.out.println(header+" is null");
                return "";
            }

            @Override
            public Object typeIsCustom(String header, Object val, Excel excel) {
                // 自定义对象的处理
                // 自定义对象转换规则
                if (val == null)
                    return null;
                // if (val instanceof List<?>) {
                // List<?> datas = (List<?>) val;
                // if (datas.size() == 0)
                // return null;
                // final Field[] fields = datas.get(0).getClass().getDeclaredFields();
                // final Map<String, Excel[]> annotations = AnnotationUtil.getAnnotationsMapByFields(fields, Excel.class);
                // List<Header> headers = getHeaders(fields, annotations, config);
                // List<Map<?, ?>> dataList = getDatas(headers,datas, fields, annotations, config);
                // return dataList;
                // }
                String[] roles = excel.customRole().split(",");
                HashMap<String, Object> datas = new HashMap<String, Object>();
                for (String ros : roles) {
                    String[] role = ros.split("=");
                    String fieldName = header + "." + role[1];
                    try {
                        Object v = ClassUtil.getValueByAttribute(fieldName, val);
                        WriteExcelConfig.ChangeValue changeValue = writeExcelConfig.getChangeValue(fieldName);

                        if (changeValue != null) {
                            Object newObj = changeValue.getValue(v);
                            if (newObj != null)
                                v = newObj;
                        }
                        // 根据属性名 获取对象的值
                        datas.put(fieldName, v);
                    } catch (Exception e) {
                        LOGGER.error("获取自定义属性值:" + fieldName + "失败", e);
                    }
                }
                return datas;
            }
        };
        config.setCellCallBack(cellCallBack);
        config.setRowCallBack(rowCallBack);
        config.setWriteFilter(writeExcelConfig == null ? null : writeExcelConfig.getFilter());
        write(out, datas, config, writeExcelConfig);
    }

    public static void write(OutputStream out, List<Header> headers, List<Map<?, ?>> dataList, WriteExcelConfig writeExcelConfig) throws Exception {

        ExcelIOBuilder.getApi().write(out, headers, dataList, writeExcelConfig);
    }

    public static void write(OutputStream out, List<Header> headers, List<Map<?, ?>> dataList) throws Exception {
        ExcelIOBuilder.getApi().write(out, headers, dataList, null);
    }

    public static void toHTML(String source, String target) throws Exception {
        Excel2Html toHtml = Excel2Html.create(source, new PrintWriter(new File(target), "utf-8"));
        toHtml.setCompleteHTML(true);
        toHtml.printPage();
    }

    public static void toHTML(String source, PrintWriter writer) throws Exception {
        Excel2Html toHtml = Excel2Html.create(source, writer);
        toHtml.setCompleteHTML(true);
        toHtml.printPage();
    }

}
