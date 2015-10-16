package org.webbuilder.utils.base;

import org.webbuilder.utils.base.file.CallBack;
import org.webbuilder.utils.base.file.FileUtil;
import javassist.*;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.File;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {

    public static final String[] baseClassName = {byte.class.getName(), double.class.getName(), float.class.getName(), byte.class.getName(), int.class.getName(), char.class.getName(), boolean.class.getName(), long.class.getName(), short.class.getName(), Long.class.getName(), Short.class.getName(), Class.class.getName(), Byte.class.getName(), Double.class.getName(), Float.class.getName(), Byte.class.getName(), Integer.class.getName(), Charset.class.getName(), Boolean.class.getName(),
            String.class.getName(), Date.class.getName(), List.class.getName(), ArrayList.class.getName(), LinkedList.class.getName()};

    /**
     * 根据属性名获取getter、setter方法
     *
     * @param getOrSet get方法或者set方法
     * @param field    属性名称
     * @return 如：getOrSet为 set field为name 返回 setName
     */
    public static String encodeGetSetMethod(String getOrSet, String field) {
        StringBuilder builder = new StringBuilder();
        builder.append(getOrSet);
        builder.append(field.substring(0, 1).toUpperCase());
        builder.append(field.substring(1, field.length()));
        return builder.toString();
    }

    public static String getAttributeByGetMehodName(String field) {
        StringBuilder builder = new StringBuilder();
        field = field.substring(3);
        builder.append(field.substring(0, 1).toLowerCase());
        builder.append(field.substring(1, field.length()));
        return builder.toString();
    }

    public static boolean isBaseClass(Class<?> type) {
        String typeName = type.getName();
        for (String class1 : ClassUtil.baseClassName) {
            if (typeName.equals(class1)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isBaseObj(String className) throws Exception {
        for (int i = 0, size = baseClassName.length; i < size; i++) {
            if (baseClassName[i].equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static String getReturnTypeClassName(Method method) {
        Class<?> returnType = method.getReturnType();
        return getClassFullName(returnType);
    }

    public static String getClassFullName(Class<?> class1) {
        String className = class1.getPackage().getName() + "." + class1.getSimpleName();
        return className;
    }

    public static List<Method> getGetMethod(Class<?> classT) {
        return getMethodByIndexOf(classT, "get");
    }

    public static List<Method> getSetMethod(Class<?> classT) {
        return getMethodByIndexOf(classT, "set");
    }

    public static List<Method> getMethodByIndexOf(Class<?> classT, String indexOf) {
        Method[] m = classT.getMethods();
        List<Method> methods = new ArrayList<Method>();
        Method method = null;
        for (int i = 0, size = m.length; i < size; i++) {
            method = m[i];
            if (method.getName().indexOf(indexOf) == 0) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static String getGetOrSetMethodByAttribute(String getOrSet, String attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append(getOrSet);
        builder.append(attribute.substring(0, 1).toUpperCase());
        builder.append(attribute.substring(1, attribute.length()));
        return builder.toString();
    }

    public static Method getGetMethodByField(Class<?> type, Field field) {
        try {
            if (field.getType().getName().equals(Boolean.class.getName()) || field.getType().getName().equals(boolean.class.getName())) {
                Method method = type.getMethod(getGetOrSetMethodByAttribute("is", field.getName()));
                if (method != null)
                    return method;
            }
            return type.getMethod(getGetOrSetMethodByAttribute("get", field.getName()));
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getSetMethodByField(Class<?> type, Field field, Class<?> paramType) {
        try {
            return type.getMethod(getGetOrSetMethodByAttribute("set", field.getName()), paramType);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValueByField(Object classType, Field field, Class<T> resType) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = ClassUtil.getGetMethodByField(classType.getClass(), field);
        return (T) method.invoke(classType);
    }

    /**
     * 通过反射，复制对象。复制时会遍历to参数的
     *
     * @param <T>    复制对象类型为泛型
     * @param to     复制到的对象
     * @param source 复制源
     * @return 复制后的对象
     * @throws Exception 复制失败错误信息
     */
    public static <T> T copyObject(T to, T source) throws Exception {
        List<Method> methods = getSetMethod(to.getClass());
        for (Method sourceMethod : methods) {
            String atrName = getAttributeByGetMehodName(sourceMethod.getName());
            String fsetMethod = null;
            if (sourceMethod.getParameterTypes()[0].getName().equalsIgnoreCase(boolean.class.getName())) {
                fsetMethod = getGetOrSetMethodByAttribute("is", atrName);
            } else {
                fsetMethod = getGetOrSetMethodByAttribute("get", atrName);
            }
            Method fromMethod = source.getClass().getMethod(fsetMethod);
            Object value = fromMethod.invoke(source);
            String setMethod = getGetOrSetMethodByAttribute("set", atrName);
            try {
                Method sourceSetMentod = to.getClass().getMethod(setMethod, fromMethod.getReturnType());
                sourceSetMentod.invoke(to, value);
            } catch (Exception e) {
                // 调取方法错误时跳过此方法
                continue;
            }
        }
        return to;
    }

    public static void scanClass(String[] paths, final ScanCallBack callBack) {
        for (final String path : paths) {
            FileUtil.readFile(path, true, new CallBack() {
                @Override
                public void readError(File file, Throwable e) {
                    callBack.error(file.getAbsolutePath(), e);
                }

                @Override
                public void isFile(File file) {
                    String abs = file.getAbsolutePath();
                    String repath = abs.substring(path.length(), abs.length()).replace("\\","/");
                    if (abs.endsWith(".class")) {
                        if(abs.contains("/"))
                             repath = repath.substring(repath.indexOf("/"), repath.length() - 6);
                        String className = repath.replace(File.separator, ".");
                        loadClass(className);
                    } else if (abs.endsWith(".jar")) {
                        try {
                            JarFile jarFile = new JarFile(file);
                            Enumeration<JarEntry> enumeration = jarFile.entries();
                            while (enumeration.hasMoreElements()) {
                                JarEntry str = enumeration.nextElement();
                                String ename = str.getName();
                                if (ename.endsWith(".class")) {
                                    ename = ename.replace(File.separator, ".").replace("/", ".").substring(0, ename.length() - 6);
                                    loadClass(ename);
                                } else {
                                    callBack.other(ename);
                                }
                            }
                        } catch (Exception e) {
                            callBack.error(repath, e);
                        }
                    } else {
                        callBack.other(repath);
                    }
                }

                public void loadClass(String className) {
                    callBack.isClass(className);
                }

                @Override
                public void isDir(File dir) {

                }
            });
        }
    }

    public interface ScanCallBack {
        void isClass(String className);

        void other(String name);

        void error(String clsName, Throwable e);
    }

    /**
     * 传入2个参数，参数 String aname 为属性名，Object obj 为目标对象 返回 目标对象属性名的值 ，该属性必须有getter方法
     *
     * @param aname 属性名 如: father; father.name (obj对象中father属性的name属性值)
     * @param obj   目标对象
     * @return 目标对象中指定属性的值
     */
    public static Object getValueByAttribute(String aname, Object obj) throws Exception {
        if (obj instanceof String)
            return obj;
        if (!aname.contains(".")) {
            if (obj instanceof Map<?, ?>) {
                return ((Map<?, ?>) obj).get(aname);
            }
            Field field = null;
            try {
                field = obj.getClass().getDeclaredField(aname);
            } catch (Exception e) {
            }
            String methodName = "";
            if (field != null && (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))) {
                if (aname.startsWith("is"))
                    methodName = aname;
                else
                    methodName = getGetOrSetMethodByAttribute("is", aname);
            } else {
                methodName = getGetOrSetMethodByAttribute("get", aname);
            }

            if (obj == null)
                return null;
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        }
        // 获取第一个属性的值为对象
        String fst = aname.split("[.]")[0];
        Object vaObject = null;
        if (obj instanceof Map<?, ?>) {
            vaObject = ((Map<?, ?>) obj).get(fst);
        } else {
            vaObject = getValueByAttribute(fst, obj);
        }
        // 获取剩余属性的值
        String lst = aname.substring(aname.indexOf(".") + 1, aname.length());
        return getValueByAttribute(lst, vaObject);
    }

    @SuppressWarnings("unchecked")
    public static <T> T String2Obj(String str, Class<T> type) {
        Object object = str;
        if (type == Integer.class || type == int.class) {
            if (StringUtil.isInt(str)) {
                object = Integer.parseInt(str);
            } else {
                object = 0;
            }
        }

        if (type == Double.class || type == double.class) {
            if (StringUtil.isNumber(str)) {
                object = Double.parseDouble(str);
            } else {
                object = 0;
            }
        }
        if (type == Float.class || type == float.class) {
            if (StringUtil.isNumber(str)) {
                object = Float.parseFloat(str);
            } else {
                object = 0;
            }
        }
        if (type == Date.class) {
            object = DateTimeUtils.formatUnknownString2Date(str);
        }

        return (T) object;
    }

    public static String toString(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        for (Field field : fields) {
            if (index++ != 0) {
                buffer.append(",");
            }
            buffer.append(field.getName() + "=");
            try {
                buffer.append(getValueByAttribute(field.getName(), object));
            } catch (Exception e) {
                buffer.append("unknow");
            }
        }
        return buffer.toString();

    }

    public static String getMehodsString(Class<?>... classes) throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                buffer.append(getMethodInfo(clazz, method) + " ");
            }
        }
        return buffer.toString();
    }

    /**
     * 获取类的关键字 包括可访问的属性和方法
     *
     * @param classes 类列表
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getClassKW(Class<?>... classes) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        for (Class<?> clazz : classes) {
            List<String> methods_str = new ArrayList<String>();
            Method[] methods = clazz.getDeclaredMethods();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                methods_str.add(field.getName());
            }
            for (Method method : methods) {
                methods_str.add(getMethodInfo(clazz, method));
            }
            data.put(clazz.getSimpleName(), methods_str);
        }
        return data;
    }

    public static boolean equals(Object arg0, Object arg1) {
        if (arg0 == null && arg1 == null) {
            return true;
        }
        if (arg0 == null || arg1 == null) {
            return false;
        }
        if (StringUtil.isNumber(arg0) && StringUtil.isNumber(arg1)) {
            return Double.parseDouble(arg0.toString()) == Double.parseDouble(arg1.toString());
        }
        return arg0.equals(arg1) || arg0.toString().equals(arg1.toString());
    }

    public static String getMethodInfo(Class<?> clazz, Method method) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(method.getName());
        String[] types = null;
        try {
            types = getMethodParamNames(clazz, method.getName(), method.getParameterTypes());
        } catch (Exception e) {
            return getMethodInfoSp(clazz, method);
        }
        buffer.append("(");
        int index = 0;
        for (String type : types) {
            if (index++ > 0)
                buffer.append(",");
            buffer.append(type);
        }
        buffer.append(");");
        return buffer.toString();
    }

    public static String getMethodInfoSp(Class<?> clazz, Method method) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(method.getName());
        Class<?>[] params = method.getParameterTypes();
        buffer.append("(");
        int index = 0;
        for (Class<?> type : params) {
            if (index++ > 0)
                buffer.append(",");
            buffer.append(type.getSimpleName());
        }
        buffer.append(");");
        return buffer.toString();
    }

    /**
     * <p>
     * 获取方法参数名称
     * </p>
     *
     * @param cm
     * @return
     * @throws Exception
     */
    protected static String[] getMethodParamNames(CtMethod cm) throws Exception {
        CtClass cc = cm.getDeclaringClass();
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            throw new Exception(cc.getName());
        }
        String[] paramNames = null;
        paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }

    /**
     * 获取方法参数名称，按给定的参数类型匹配方法
     *
     * @param clazz
     * @param method
     * @param paramTypes
     * @return
     * @throws Exception
     */
    public static String[] getMethodParamNames(Class<?> clazz, String method, Class<?>... paramTypes) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        CtMethod cm = null;
        cc = pool.get(clazz.getName());
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++)
            paramTypeNames[i] = paramTypes[i].getName();
        cm = cc.getDeclaredMethod(method, pool.get(paramTypeNames));
        return getMethodParamNames(cm);
    }

    /**
     * 获取方法参数名称，匹配同名的某一个方法
     *
     * @param clazz
     * @param method
     * @return
     * @throws Exception
     * @throws NotFoundException 如果类或者方法不存在
     * @throws Exception         如果最终编译的class文件不包含局部变量表信息
     */
    public static String[] getMethodParamNames(Class<?> clazz, String method) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc;
        CtMethod cm = null;
        try {
            cc = pool.get(clazz.getName());
            cm = cc.getDeclaredMethod(method);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return getMethodParamNames(cm);
    }

    public static Class<?>[] getMethodParamTypes(Class<?> clazz, String method) throws Exception {
        Method[] methods = clazz.getMethods();
        List<Class<?>> classes = new LinkedList<Class<?>>();
        for (Method method2 : methods) {
            if (method2.getName().equals(method)) {
                return method2.getParameterTypes();
            }
        }
        return null;
    }


    public static String[] getClassNameAndMethodName(String str) throws Exception {
        String[] strs = new String[2];
        strs[0] = str.substring(0, str.lastIndexOf("."));
        strs[1] = str.substring(str.lastIndexOf(".") + 1, str.length());
        return strs;

    }


    public static Class<?> getGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("Index outof bounds");
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    public static Class<?> getGenericType(Class clazz) {
        return getGenericType(clazz, 0);
    }

}
