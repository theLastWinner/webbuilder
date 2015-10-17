package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class IsInt extends IsNumber {
    @Override
    public void valid(String errorMsg, Object value) throws DataValidException {
        super.valid(errorMsg, value);
        if (!StringUtil.isInt(value)) {
            throw new DataValidException(errorMsg);
        }
    }
}
