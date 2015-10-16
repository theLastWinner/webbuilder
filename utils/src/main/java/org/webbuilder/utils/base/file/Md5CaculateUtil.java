package org.webbuilder.utils.base.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5CaculateUtil {

    private Md5CaculateUtil() {

    }

    private static char[] hexChar = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String getHash(String fileName, String hashType) throws IOException, NoSuchAlgorithmException {
        File f = new File(fileName);
        System.out.println(" -------------------------------------------------------------------------------");
        System.out.println("|当前文件名称:" + f.getName());
        System.out.println("|当前文件大小:" + (f.length() / 1024 / 1024) + "MB");
        System.out.println("|当前文件路径[绝对]:" + f.getAbsolutePath());
        System.out.println("|当前文件路径[---]:" + f.getCanonicalPath());
        System.out.println(" -------------------------------------------------------------------------------");
        InputStream ins = new FileInputStream(f);
        byte[] buffer = new byte[8192];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int len;
        while ((len = ins.read(buffer)) != -1) {
            md5.update(buffer, 0, len);
        }

        ins.close();
//        也可以用apache自带的计算MD5方法
        return DigestUtils.md5Hex(md5.digest());
//        自己写的转计算MD5方法
//        return toHexString(md5.digest());
    }

    public static String getHash2(String fileName) {
        File f = new File(fileName);
        return String.valueOf(f.lastModified());
    }


    protected static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /*
     * 获取MessageDigest支持几种加密算法
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String[] getCryptolmpls(String serviceType) {
        Set result = new HashSet();
//        all providers
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++) {
//            get services provided by each provider
            Set keys = providers[i].keySet();
            for (Iterator it = keys.iterator(); it.hasNext(); ) {
                String key = it.next().toString();
                key = key.split(" ")[0];

                if (key.startsWith(serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 1));
                } else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
                    result.add(key.substring(serviceType.length() + 11));
                }
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }


    public static void main(String[] args) throws Exception, Exception {
//        调用方法
//        String[] names = getCryptolmpls("MessageDigest");
//        for(String name:names){
//            System.out.println(name);
//        }
        long start = System.currentTimeMillis();
        System.out.println("开始计算文件MD5值,请稍后...");
        String fileName = "E:\\项目文档\\行政审批.rar";
////        String fileName = "E:\\SoTowerStudio-3.1.0.exe";
        String hashType = "MD5";
        String hash = getHash(fileName, hashType);
        System.out.println("MD5:" + hash);
        long end = System.currentTimeMillis();
        System.out.println("一共耗时:" + (end - start) + "毫秒");
    }
}
