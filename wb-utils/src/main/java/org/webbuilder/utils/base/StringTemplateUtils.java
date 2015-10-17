package org.webbuilder.utils.base;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * create by hao
 * 字符串模板处理工具 (基于freemarker)
 */
public class StringTemplateUtils {

    //freemarker字符串模板加载器
    private static final StringTemplateLoader loader = new StringTemplateLoader();
    //freemarker配置器
    private static final Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_0);

    static {
        //初始化
        freemarkerCfg.setEncoding(Locale.getDefault(), "UTF-8");
        freemarkerCfg.setTemplateLoader(loader);
    }

    /**
     * 调用模板，生成结果
     *
     * @param name 模板名称
     * @param data 参数
     * @return 生成后的数据
     * @throws Exception 异常信息，如模板未找到
     */
    public static String generate(String name, Map<String, Object> data) throws Exception {
        Template template = freemarkerCfg.getTemplate(name);
        StringWriter out = new StringWriter();
        template.process(data, out);
        return out.getBuffer().toString();
    }

    /**
     * 编译模板并生成结果
     *
     * @param name        模板名称
     * @param templateStr 模板内容
     * @param data        参数
     * @return 生成后的结果  异常信息，如模板未找到
     * @throws Exception
     */
    public static String compileAndGenerate(String name, String templateStr, Map<String, Object> data) throws Exception {
        compileTemplate(name, templateStr);
        return generate(name, data);
    }

    /**
     * 使用默认名称编译模板并生成结果
     *
     * @param templateStr 模板内容
     * @param data        参数
     * @return 生成后的结果
     * @throws Exception 异常信息，如模板未找到
     */
    public static String compileAndGenerate(String templateStr, Map<String, Object> data) throws Exception {
        return compileAndGenerate(String.valueOf(templateStr.hashCode()), templateStr, data);
    }

    public static void removeTemplate(String name) throws IOException {
        freemarkerCfg.removeTemplateFromCache(name);
    }

    /**
     * 编译模板并装载模板
     *
     * @param templateStr 模板内容
     * @return 编译是否成功
     * @throws Exception 异常信息，如模板未找到
     */
    public static boolean compileTemplate(String name, String templateStr) throws Exception {
        StringTemplateLoader templateLoader = ((StringTemplateLoader) freemarkerCfg.getTemplateLoader());
        templateLoader.putTemplate(name, templateStr);
        freemarkerCfg.setTemplateLoader(templateLoader);
        return true;
    }

    /**
     * 自定义标记语言进行模板预解析
     *
     * @param data     数据
     * @param template 模板内容
     * @return 处理结果
     * @throws Exception 异常信息，如模板未找到
     */
    public static String generate(Map<String, Object> data, String template) throws Exception {
        //先直接对#()标记进行编译
        Pattern pat = Pattern.compile("(?<=#\\()(.+?)(?=\\))");
        Matcher mat = pat.matcher(template);
        while (mat.find()) {
            String kw = mat.group(0);
            Object val;
            //like user.username
            if (kw.contains(".")) {
                val = ClassUtil.getValueByAttribute(kw.substring(kw.indexOf(".") + 1), data.get(kw.split("[.]")[0]));
            } else {
                val = data.get(kw);
            }
            if (val == null)
                val = "";
            //如果值为空，则不进行替换
            template = template.replaceAll("#\\(" + kw.replace("$", "\\$") + "\\)", String.valueOf(val));
        }
        //使用freemarker引擎进行处理后返回
        return compileAndGenerate(template, data);
    }

}
