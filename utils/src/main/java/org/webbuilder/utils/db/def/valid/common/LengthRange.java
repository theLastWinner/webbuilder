package org.webbuilder.utils.db.def.valid.common;


import org.webbuilder.utils.db.exception.DataValidException;

/**
 * Created by 浩 on 2015-07-10 0010.
 */
public class LengthRange extends IsNotNull {

    private int min = 0;

    private int max = 0;

    public LengthRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void valid(String errorMsg, Object value) throws DataValidException {
        super.valid(errorMsg, value);
        String val = value.toString();
        int len = val.length();
        if (len < min || len > max) {
            throw new DataValidException(errorMsg);
        }
    }
}
