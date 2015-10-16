package org.webbuilder.utils.db.def.parser.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface TableMeta {
    String name();

    String databaseName() default "DEFAULT";

    String[] foreign() default {};
}
