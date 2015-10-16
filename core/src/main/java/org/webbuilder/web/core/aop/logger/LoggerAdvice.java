package org.webbuilder.web.core.aop.logger;

import com.alibaba.fastjson.JSON;
import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.web.core.FastJsonHttpMessageConverter;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.logger.LogInfo;
import org.webbuilder.web.po.user.User;
import org.webbuilder.web.core.logger.LoggerService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * 请求日志切入通知
 */
@Aspect
public class LoggerAdvice {

    private LoggerService loggerService;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private Method getMethod(ProceedingJoinPoint pjp) {
        if (pjp.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            return signature.getMethod();
        }
        return null;
    }

    private String getMethodName(ProceedingJoinPoint pjp) {
        StringBuilder methodName = new StringBuilder(pjp.getSignature().getName()).append("(");
        Method method = getMethod(pjp);
        Class[] args = method.getParameterTypes();
        for (int i = 0, len = args.length; i < len; i++) {
            if (i != 0) methodName.append(",");
            methodName.append(args[i].getSimpleName());
        }
        return methodName.append(")").toString();
    }

    private String getDesc(ProceedingJoinPoint pjp, AccessLogger log) {
        Class<?> clazz = pjp.getTarget().getClass();
        AccessLogger clazzDesc = clazz.getAnnotation(AccessLogger.class);
        String desc = log.value();
        if (clazzDesc != null) {
            return clazzDesc.value().concat("-").concat(desc);
        }
        return desc;
    }

    static final Random random = new Random();

    /**
     * 环绕切入，记录请求日志,切入注解了SystemLog的方法
     *
     * @param pjp
     */
    @Around(value = "@annotation(log)")
    public Object logInfo(ProceedingJoinPoint pjp, AccessLogger log) {
        LogInfo logInfo = new LogInfo();
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Class<?> clazz = pjp.getTarget().getClass();
            logInfo.setU_id(MD5.encode(String.valueOf(System.nanoTime()).concat(String.valueOf(random.nextInt()))));
            logInfo.setDesc(getDesc(pjp, log));//方法描述
            logInfo.setClass_name(clazz.getName());//映射类名
            logInfo.setIp(WebUtil.getIpAddr(request));//ip地址
            logInfo.setMethod(request.getMethod().concat(".").concat(getMethodName(pjp)));//方法：GET.select()
            logInfo.setHeaders(JSON.toJSONString(WebUtil.getHeaders(request)));//http请求头
            logInfo.setReferer(request.getHeader("referer"));//referer
            logInfo.setUri(WebUtil.getUri(request, false));//请求相对路径
            logInfo.setUrl(WebUtil.getBasePath(request).concat(logInfo.getUri()));//请求绝对路径
            logInfo.setUser_agent(request.getHeader("User-agent"));//客户端标识
            logInfo.setParams(JSON.toJSONString(WebUtil.getParams(request)));
            User user = WebUtil.getLoginUser();
            if (user != null)
                logInfo.setUser_id(user.getU_id());//当前登录的用户
        } catch (Exception e) {
            logger.error("create logInfo error", e);
            logInfo.setResponse(StringUtil.throwable2String(e));
        }
        Object obj = null;
        try {//计算请求时间
            logInfo.setRequest_time(System.currentTimeMillis());
            obj = pjp.proceed();
            if (obj != null) {
                if (obj instanceof ResponseMessage) {
                    ResponseMessage res = (ResponseMessage) obj;
                    if (res.getSourceData() instanceof Throwable) {
                        logInfo.setException(StringUtil.throwable2String((Throwable) res.getData()));
                    }
                    logInfo.setCode(res.getCode());
                } else {
                    logInfo.setCode("200");
                }
                logInfo.setResponse(FastJsonHttpMessageConverter.toJson(obj));
            }
            logInfo.setResponse_time(System.currentTimeMillis());
        } catch (Throwable e) {
            logger.error("logger aop proceed error", e);
            logInfo.setException(StringUtil.throwable2String(e));
            logInfo.setCode("500");
        }
        //输入日志
        if (getLoggerService() != null) {
            try {
                getLoggerService().log(logInfo);
            } catch (Exception e) {
                logger.error("write access logger error", e);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(JSON.toJSONString(logInfo));
            }
        }
        return obj;
    }


    public LoggerService getLoggerService() {
        return loggerService;
    }

    public void setLoggerService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }
}
