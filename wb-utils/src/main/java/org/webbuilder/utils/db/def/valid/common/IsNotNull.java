package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.db.def.valid.Validator;
import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsNotNull implements Validator {
    public void valid(String errorMsg, Object value) throws DataValidException {
        if (value == null) {
            throw new DataValidException(errorMsg);
        }
    }
}
