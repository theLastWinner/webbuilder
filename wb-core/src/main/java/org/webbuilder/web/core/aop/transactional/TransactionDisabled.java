package org.webbuilder.web.core.aop.transactional;

import java.lang.annotation.*;

/**
 * 禁用事务注解，注解后，将不进行事务控制
 * Created by 浩 on 2015-10-29 0029.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TransactionDisabled {
}
