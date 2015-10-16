package org.webbuilder.utils.db.def.valid;


import org.webbuilder.utils.db.exception.DataValidException;

public interface Validator {
    void valid(String name, Object value) throws DataValidException;
}
