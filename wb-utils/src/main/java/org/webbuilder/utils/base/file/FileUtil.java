package org.webbuilder.utils.base.file;


import org.webbuilder.utils.base.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件工具
 */
public class FileUtil {
    public static final Map<String, String> CONTENT_TYPES = new HashMap<String, String>();

    static {
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("bmp", "application/x-bmp");
        CONTENT_TYPES.put("js", "application/x-javascript");
        CONTENT_TYPES.put("css", "text/css");
        CONTENT_TYPES.put("html", "text/html;charset=utf-8");
        CONTENT_TYPES.put("htm", "text/html;charset=utf-8");
        CONTENT_TYPES.put("txt", "text/html;charset=utf-8");
        CONTENT_TYPES.put("sql", "text/html;charset=utf-8");
    }

    /**
     * 文件名 数字排序
     *
     * @param strs 文件名
     * @return 排序后
     */
    public static String[] sortFile(String[] strs) {
        String file_ = null;
        for (int i = 0; i < strs.length - 1; i++) {
            for (int j = i + 1; j < strs.length; j++) {
                if (compFileName(strs[i], strs[j])) {
                    file_ = strs[i];
                    strs[i] = strs[j];
                    strs[j] = file_;
                }
            }
        }
        return strs;
    }

    public static File[] sortFile(File[] strs) {
        File file_ = null;
        for (int i = 0; i < strs.length - 1; i++) {
            for (int j = i + 1; j < strs.length; j++) {
                if (compFileName(strs[i].getName(), strs[j].getName())) {
                    file_ = strs[i];
                    strs[i] = strs[j];
                    strs[j] = file_;
                }
            }
        }
        return strs;
    }

    /**
     * 对比文件名顺序
     *
     * @param str1 文件名1
     * @param str2 文件名2
     * @return 文件名1是否大于文件名2
     */
    public static boolean compFileName(String str1, String str2) {
        String number1 = getNumberStr(str1);
        String number2 = getNumberStr(str2);
        if (!StringUtil.isNumber(number1) || !StringUtil.isNumber(number2))
            return false;
        return Long.parseLong(number1) > Long.parseLong(number2);
    }

    /**
     * 获取字符串中的数字
     *
     * @param str
     * @return
     */
    public static String getNumberStr(String str) {
        String regex = "\\d*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            if (!"".equals(m.group()))
                builder.append(m.group());
        }
        return builder.toString();
    }

    /**
     * 获取指定目录下的所有文件
     *
     * @param path    目录
     * @param allFile 是否查找子目录下的文件
     * @return allFile为true查询包括子目录的文件否则只返回当前目录的文件, 不返回所有不可读的文件
     */
    public static List<File> getFilesByPath(String path, boolean allFile) {
        List<File> files = new ArrayList<File>();
        try {
            File file = new File(path);
            File filesArr[] = file.listFiles();// 获取当前目录的所有文件
            for (int i = 0; i < filesArr.length; i++) {
                File fileTemp = filesArr[i];
                if (fileTemp.canRead()) { // 文件可读
                    if (fileTemp.isFile()) { // 是文件
                        files.add(fileTemp); // 添加文件到集合
                    }
                    if (allFile && fileTemp.isDirectory()) { // 是文件夹并且要查询子目录
                        // 添加字目录下的文件到集合（使用递归）
                        files.addAll(getFilesByPath(fileTemp.getAbsolutePath(), allFile));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }

    public static String getSuffix(String fileName) {
        if (fileName == null)
            return "";
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        }
        return "";
    }

    /**
     * 创建文件夹
     *
     * @param path
     */
    public static void createPath(String path) {
        File file = new File(path);
        if (!file.canRead()) {
            file.mkdirs();
        }
    }

    /**
     * 读取指定目录的文件
     *
     * @param path     目录
     * @param deep     是否读取子目录
     * @param callBack 读取回调
     */
    public static void readFile(String path, boolean deep, CallBack callBack) {
        File file = new File(path);
        try {
            // 调用回调
            if (file.isFile()) {
                callBack.isFile(file);
            } else {
                callBack.isDir(file);
                File[] files = file.listFiles();
                for (File file2 : files) {
                    // 递归
                    if (deep)
                        readFile(file2.getAbsolutePath(), deep, callBack);
                        // 调用回调
                    else if (file2.isFile())
                        callBack.isFile(file2);
                    else if (file2.isDirectory())
                        callBack.isDir(file2);
                }
            }
        } catch (Exception e) {
            callBack.readError(file, e);
        }
    }

    /**
     * 读取文件为内容String（自动根据文件编码读取）
     *
     * @param file 文件路径
     * @return 文件内容
     * @throws Exception
     */
    public synchronized static String readFile2String(String file) throws Exception {
        final StringBuffer buffer = new StringBuffer();
        readFile(file, new ReadCallBack() {

            @Override
            public void readLine(String line) {
                buffer.append(line + "\r\n");
            }

            @Override
            public void readError(Throwable e) {

            }

            @Override
            public void readOver() {

            }
        });
        return buffer.toString();
    }

    /**
     * 写出字符串到文件
     *
     * @param str    要写出的字符串
     * @param path   保存目录
     * @param encode 文件编码
     * @throws Exception
     */
    public synchronized static void writeString2File(String str, String path, String encode) throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encode));
        try {
            writer.write(str);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    /**
     * java对象序列化到文件中
     *
     * @param obj      实现Serializable接口的对象
     * @param fileName 存储文件名
     * @throws Exception
     */
    public synchronized static void writhObj2File(Serializable obj, String fileName) throws Exception {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(fileName));
            stream.writeObject(obj);
        } finally {
            stream.close();
        }
    }

    /**
     * java对象反序列化
     *
     * @param <T>      对象泛型
     * @param fileName 文件路径
     * @return 反序列化的对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public synchronized static <T> T reateFile2Obj(String fileName) throws Exception {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(fileName));
            return (T) stream.readObject();
        } finally {
            if (stream != null)
                stream.close();
        }
    }

    /**
     * 回掉方式读取文件为内容String（自动根据文件编码读取）
     *
     * @param file 文件路径
     * @return 文件内容
     * @throws Exception
     */
    public synchronized static void readFile(String file, ReadCallBack callBack) throws Exception {
        // 获取文件编码
        String encode = EncodingDetect.getJavaEncode(file);
        // 指定文件编码读取
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
        try {
            try {
                while (bufferedReader.ready()) {
                    // 读取一行 回掉
                    callBack.readLine(bufferedReader.readLine());
                }
            } catch (Exception e) {
                callBack.readError(e);
            }
        } finally {
            bufferedReader.close();
        }
        callBack.readOver();
    }

}
