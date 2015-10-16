package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsNotEmpty extends IsNotNull {
    @Override
    public void valid(String errorMsg, Object value) throws DataValidException {
        super.valid(errorMsg, value);
        if ("" .equals(value)) {
            throw new DataValidException(errorMsg);
        }
    }
}
