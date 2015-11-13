package org.webbuilder.sql.parser;

import org.webbuilder.sql.TableMetaData;

/**
 * Created by 浩 on 2015-11-13 0013.
 */
public interface TableMetaDataParser {
    TableMetaData parse(String content, String type) throws Exception;
}
