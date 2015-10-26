/**
 * 使用seajs 进行全局js控制
 * Created by 浩 on 2015-07-26 0026.
 */
//应用名称，如果不指定则自动拼接,在app为ROOT时，应该设置为""
var appName = null;

//获取应用根路径
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPath + (appName == null ? appName = projectName : appName) );
}
//服务前后缀，与后台服务相关
var _SERVICE_PREFIX = getRootPath() + "/api";
var _SERVICE_SUFFIX = "";//.json

/**
 * 拼接服务地址 ，传入相对地址，进行前缀后缀的追加。生成真实的service请求路径
 * @param service 相对地址 如/config/system
 * @returns {string} 绝对路径
 */
function getServicePath(service) {
    if (service.indexOf("/") != 0)service = "/" + service;
    return _SERVICE_PREFIX + service + _SERVICE_SUFFIX;
}
//项目路径
var ROOT_PATH = getRootPath();
//调试模式
var debug = true;
//默认日志对象
var logger = new Object();
logger.info = function (s) {
    if (window.console && debug)
        window.console.log(s);
}
logger.debug = function (s) {
    logger.info(s);
}
logger.warn = function (s) {
    logger.info(s);
}
logger.error = function (s) {
    logger.info(s);
}
logger.fatal = function (s) {
    logger.info(s);
}

//初始化
function $ContentInit() {
    seajs.config({
        base: ROOT_PATH + "/plugins",
        //支持的插件
        alias: {
            "log": "log/log",
            "jquery": "jquery/jquery-2.1.4.min",
            "template": "template/template",
            "designer": "designer/form/FromDesigner",
            "MD5": "utils/MD5",
            "request": "utils/Request",
            "config": "utils/Config",
            "upload": "webuploader/webuploader",
            "socket": "socket/socket"
        }
    });
    if (debug) {
        seajs.use("log", function (LogFactory) {
            logger = new LogFactory(LogFactory.DEBUG, LogFactory.consoleLogger);
        });
    }

}
document.write("<script src='" + ROOT_PATH + "/plugins/seajs/sea.js" + "'></script>");
document.write("<script >$ContentInit();</script>");