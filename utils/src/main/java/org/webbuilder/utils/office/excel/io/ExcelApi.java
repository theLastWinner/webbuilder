package org.webbuilder.utils.office.excel.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface ExcelApi {
    void read(InputStream input, ExcelIOConfig config) throws Exception;

    void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList) throws Exception;

    void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList, WriteExcelConfig config) throws Exception;
}
