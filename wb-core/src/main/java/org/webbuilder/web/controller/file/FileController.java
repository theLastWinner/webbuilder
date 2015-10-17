package org.webbuilder.web.controller.file;

import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.base.file.FileUtil;
import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.bean.ResponseData;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.RandomUtil;
import org.webbuilder.web.po.resource.Resources;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.service.config.ConfigService;
import org.webbuilder.web.service.resource.ResourcesService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件管理控制器，用于上传和下载资源文件，使用restful。
 * Created by 浩 on 2015-08-28 0028.
 */
@RestController
@RequestMapping(value = "/file")
@AccessLogger("文件管理")
public class FileController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ConfigService configService;

    @Resource
    private ResourcesService resourcesService;

    //文件名中不允许出现的字符 \ / : | ? < > "
    private static final Pattern fileNameKeyWordPattern = Pattern.compile("(\\\\)|(/)|(:)(|)|(\\?)|(>)|(<)|(\")");

    /**
     * 下载文件,支持断点下载
     *
     * @param id   要下载资源文件的id
     * @param name 自定义文件名，该文件名不能存在非法字符
     * @return 失败时，会返回失败原因信息{@link ResponseMessage}
     */
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    @AccessLogger("下载文件")
    public ResponseMessage downLoad(@PathVariable("id") String id, String name, HttpServletResponse response, HttpServletRequest request) {
        try {
            Resources resources = resourcesService.selectByPk(id);
            if (resources == null || resources.getStatus() != 1) {
                response.setStatus(404);
                return new ResponseMessage(false, "资源不存在！", "404");
            } else {
                if (!"file".equals(resources.getType()))
                    return new ResponseMessage(false, "该资源不是文件！", "400");
                String fileBasePath = configService.get("upload", "basePath", "/upload/").trim();
                File file = new File(fileBasePath.concat(resources.getPath().concat("/".concat(resources.getMd5()))));
                if (!file.canRead()) {
                    response.setStatus(404);
                    return new ResponseMessage(false, "资源不存在！", "404");
                }
                //获取contentType，默认application/octet-stream
                String contentType = FileUtil.CONTENT_TYPES.get(resources.getSuffix());
                if (contentType == null)
                    contentType = "application/octet-stream";
                if (StringUtil.isNullOrEmpty(name))//未自定义文件名，则使用上传时的文件名
                    name = resources.getName();
                if (!name.contains("."))//如果未指定文件拓展名，则追加默认的文件拓展名
                    name = name.concat(".").concat(resources.getSuffix());
                //关键字剔除
                name = fileNameKeyWordPattern.matcher(name).replaceAll("");
                int skip = 0;
                long fSize = file.length();
                //尝试判断是否为断点下载
                try {
                    //获取要继续下载的位置
                    String Range = request.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", "");
                    skip = StringUtil.toInt(Range);
                } catch (Exception e) {
                }
                response.setContentLength((int) fSize);//文件大小
                response.setContentType(contentType);
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(name, "utf-8"));
                //try with resource
                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                     BufferedOutputStream stream = new BufferedOutputStream(response.getOutputStream())) {
                    //断点下载
                    if (skip > 0) {
                        inputStream.skip(skip);
                        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                        String contentRange = new StringBuffer("bytes ").append(skip).append("-").append(fSize - 1).append("/").append(fSize).toString();
                        response.setHeader("Content-Range", contentRange);
                    }
                    byte b[] = new byte[2048 * 10];
                    while ((inputStream.read(b)) != -1) {
                        stream.write(b);
                    }
                    stream.flush();
                } catch (Exception e) {
                    logger.debug(String.format("download file error%s", e.getMessage()));
                }
                return null;
            }
        } catch (Exception e) {
            return new ResponseMessage(false, e);
        }

    }

    /**
     * 上传文件，进行md5一致性校验，不保存重复文件。成功后返回文件信息{uid,md5,name}
     *
     * @param files   文件列表
     * @param session HttpSession
     * @return 上传结果
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @AccessLogger("上传文件")
    public Object upload(@RequestParam("file") CommonsMultipartFile[] files, HttpSession session) {
        if (logger.isInfoEnabled())
            logger.info(String.format("start upload , file number:%s", files.length));
        //配置中的文件上传根路径
        String fileBasePath = configService.get("upload", "basePath", "/upload").trim();
        //文件存储的相对路径，以日期分隔，每天创建一个新的目录
        String filePath = "/file/".concat(DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY));
        //文件存储绝对路径
        String absPath = fileBasePath.concat(filePath);
        File path = new File(absPath);
        if (!path.exists()) path.mkdirs(); //创建目录
        //生成的资源对象
        List<Resources> resourcesList = new LinkedList<>();
        for (int i = 0; i < files.length; i++) {
            CommonsMultipartFile file = files[i];
            if (!file.isEmpty()) {
                if (logger.isInfoEnabled())
                    logger.info(String.format("start write file:%s", file.getOriginalFilename()));
                int pre = (int) System.currentTimeMillis();
                try {
                    String fileName = files[i].getOriginalFilename();
                    String newName = MD5.encode(String.valueOf(System.nanoTime())); //临时文件名 ,纳秒的md5值
                    String fileAbsName = absPath.concat("/").concat(newName);
                    //try with resource
                    try (BufferedInputStream in = new BufferedInputStream(files[i].getInputStream());
                         //写出文件
                         BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fileAbsName))) {
                        byte[] buffer = new byte[2048 * 10];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        os.flush();
                    }
                    File newFile = new File(fileAbsName);
                    //获取文件的md5值
                    String md5;
                    try (FileInputStream inputStream = new FileInputStream(newFile)) {
                        md5 = DigestUtils.md5Hex(inputStream);
                    }
                    //判断文件是否已经存在
                    Resources resources = resourcesService.selectByMd5(md5);
                    if (resources != null) {
                        resourcesList.add(resources);
                        newFile.delete();//文件已存在则删除临时文件不做处理
                        logger.debug(String.format("存在重复文件md5:%s", md5));
                        continue;
                    }
                    //使用md5作为文件名
                    newName = md5;
                    newFile.renameTo(new File(absPath.concat("/").concat(newName)));
                    //获取已经上传的文件md5
                    long finalTime = System.currentTimeMillis() - pre;
                    if (logger.isInfoEnabled())
                        logger.info(String.format("write file:%s,success use time:%sms", file.getOriginalFilename(), finalTime));
                    //存放资源信息。
                    resources = new Resources();
                    resources.setStatus(1);
                    resources.setPath(filePath);
                    resources.setMd5(md5);
                    resources.setCreate_date(new Date());
                    resources.setType("file");
                    resources.setName(fileName);
                    User user;
                    if ((user = (User) session.getAttribute("user")) != null) {
                        resources.setCreator_id(user.getU_id());
                    }
                    resourcesService.insert(resources);
                    resourcesList.add(resources);
                } catch (Exception e) {
                    return new ResponseMessage(false, e);
                }
            }
        }//响应上传成功的资源信息
        return new ResponseData(new ResponseMessage(true, resourcesList)).includes(Resources.class, "u_id", "name", "md5");
    }
}
