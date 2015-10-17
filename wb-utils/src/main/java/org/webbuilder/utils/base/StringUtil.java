package org.webbuilder.utils.base;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String throwable2String(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }


    /**
     * 获取汉字全拼
     *
     * @param str 汉字字符串
     * @return 汉字字符串全拼
     */
    public static String getPingYin(String str) {
        char[] t1 = str.toCharArray();
        String[] t2;
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else {
                    t4 += Character.toString(t1[i]);
                }
            }
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }

    /**
     * 获取中文字符串首字母
     *
     * @param str 字符串
     * @return 字符串首字母
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 将字符串转移为ASCII码
     *
     * @param str 字符串
     * @return 字符串ASCII码
     */
    public static String getCnASCII(String str) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = str.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 是否包含中文字符
     *
     * @param str 要判断的字符串
     * @return 是否包含中文字符
     */
    public static boolean isChineseChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 对象是否为无效值
     *
     * @param obj 要判断的对象
     * @return 是否为有效值（不为null 和 "" 字符串）
     */
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj.toString());
    }


    /**
     * 参数是否是有效数字 （整数或者小数）
     *
     * @param str 参数（对象将被调用string()转为字符串类型）
     * @return 是否是数字
     */
    public static boolean isNumber(Object str) {
        return isInt(str) || isDouble(str);
    }

    /**
     * 参数是否是有效整数
     *
     * @param str 参数（对象将被调用string()转为字符串类型）
     * @return 是否是整数
     */
    public static boolean isInt(Object str) {
        if (isNullOrEmpty(str))
            return false;
        if (str instanceof Integer)
            return true;
        return str.toString().matches("[-+]?\\d+");
    }

    /**
     * 字符串参数是否是double
     *
     * @param str 参数（对象将被调用string()转为字符串类型）
     * @return 是否是double
     */
    public static boolean isDouble(Object str) {
        if (isNullOrEmpty(str))
            return false;
        if (str instanceof Double || str instanceof Float)
            return true;
        return str.toString().matches("[-+]?\\d+\\.\\d+");
    }

    /**
     * 字符串参数是否是boolean
     *
     * @param str 字符串
     * @return 是否是boolean
     */
    public static boolean isBoolean(Object str) {
        return "true".equals(String.valueOf(str));
    }

    /**
     * 判断字符串数组strArr中是否包含一个字符串str
     *
     * @param strArr 字符串数组
     * @param str    字符串
     * @return 如果有返回true没有返回false
     */
    public static boolean hasStr(String strArr[], String str) {
        if (!StringUtil.isNullOrEmpty(strArr)) {
            for (int i = 0; i < strArr.length; i++) {
                if (strArr[i].equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断数组strArr中是否包含一个元素
     *
     * @param strArr 字符串数组
     * @param obj    字符串
     * @return 如果有返回true没有返回false
     */
    public static boolean containObjInArr(Object strArr[], Object obj) {
        return Arrays.asList(strArr).contains(obj);
    }

    public static boolean containIntInArr(int arr[], int x) {
        if (!StringUtil.isNullOrEmpty(arr)) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == x) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String urlEncoder(String url, String charset) throws UnsupportedEncodingException {
        for (char c : url.toCharArray()) {
            if (StringUtil.isChineseChar(c + "")) {
                url = url.replace(c + "", URLEncoder.encode(c + "", charset));
            }
        }
        return url.replaceAll(" ", "%20");
    }

    public static int toInt(Object object) {
        if (object instanceof Integer)
            return (Integer) object;
        if (isInt(object)) {
            return Integer.parseInt(object.toString());
        }
        if (isDouble(object)) {
            return (int) Double.parseDouble(object.toString());
        }
        return 0;
    }

    public static long toLong(Object object) {
        if (object instanceof Long)
            return (Long) object;
        if (isInt(object)) {
            return Long.parseLong(object.toString());
        }
        if (isDouble(object)) {
            return (long) Double.parseDouble(object.toString());
        }
        return 0;
    }

    public static double toDouble(Object object) {
        if (object instanceof Double)
            return (Double) object;
        if (isNumber(object)) {
            return (int) Double.parseDouble(object.toString());
        }
        return 0;
    }

}
