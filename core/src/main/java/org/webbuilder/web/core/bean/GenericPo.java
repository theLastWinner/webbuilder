package org.webbuilder.web.core.bean;

import org.webbuilder.utils.base.MD5;
import org.webbuilder.utils.db.exception.DataValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.Serializable;
import java.util.*;

/**
 * Created by 浩 on 2015-07-20 0020.
 */
public class GenericPo<PK> implements Serializable {

    private PK u_id;

    public PK getU_id() {
        return u_id;
    }

    @Override
    public int hashCode() {
        return getU_id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public void setU_id(PK u_id) {
        this.u_id = u_id;
    }

    public static String createUID() {
        return MD5.encode(String.valueOf(System.nanoTime()) + String.valueOf(Math.random()));
    }

    public static final ValidResults valid(Object object) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> set = validator.validate(object);
        ValidResults results = new ValidResults();
        if (set.size() != 0) {
            for (ConstraintViolation<Object> violation : set) {
                results.addResult(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return results;
    }

    public ValidResults valid() {
        return valid(this);
    }

}
