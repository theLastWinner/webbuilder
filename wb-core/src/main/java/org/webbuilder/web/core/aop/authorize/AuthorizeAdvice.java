package org.webbuilder.web.core.aop.authorize;

import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.core.bean.ResponseMessage;
import org.webbuilder.web.core.utils.WebUtil;
import org.webbuilder.web.po.user.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * aop方式进行授权验证
 * Created by 浩 on 2015-09-28 0028.
 */
@Aspect
public class AuthorizeAdvice {

    @Around(value = "@annotation(authorize)")
    public Object authorize(ProceedingJoinPoint pjp, Authorize authorize) {
        Object obj;
        try {
            if (!validAuthorize(pjp, authorize)) {
                return new ResponseMessage(false, "无访问权限!", "502");
            }
            obj = pjp.proceed();

        } catch (Throwable e) {
            return new ResponseMessage(false, e);
        }
        return obj;
    }

    private boolean validAuthorize(ProceedingJoinPoint pjp, Authorize authorize) {
        User user = WebUtil.getLoginUser();

        return false;
    }
}
