package org.webbuilder.utils.office.excel.io;

import org.webbuilder.utils.office.excel.io.API.POIExcelIO;

public class ExcelIOBuilder {
    private ExcelIOBuilder() {
    }

    private static ExcelApi API = new POIExcelIO();

    public static ExcelApi getApi() {
        return API;
    }

    public static void initApi(ExcelApi api) {
        API = api;
    }
}
