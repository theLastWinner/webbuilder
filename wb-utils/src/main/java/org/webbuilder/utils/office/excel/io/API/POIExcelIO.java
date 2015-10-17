package org.webbuilder.utils.office.excel.io.API;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.office.excel.io.ExcelApi;
import org.webbuilder.utils.office.excel.io.ExcelIOConfig;
import org.webbuilder.utils.office.excel.io.WriteExcelConfig;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.utils.office.excel.io.Header;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POIExcelIO implements ExcelApi {
    private final Logger LOGGER = LoggerFactory.getLogger(POIExcelIO.class);

    private Map<String, HSSFCellStyle> styleCache = new HashMap<String, HSSFCellStyle>();

    private void initStyleCache(HSSFWorkbook wb) {
        String key = wb.hashCode() + "";
        synchronized (styleCache) {
            styleCache.put(key + "int", createStyle(wb, Integer.class));
            styleCache.put(key + "double", createStyle(wb, Double.class));
            styleCache.put(key + "float", createStyle(wb, Float.class));
            styleCache.put(key + "string", createStyle(wb, String.class));
            styleCache.put(key + "date", createStyle(wb, Date.class));
        }

    }

    private void removeStyleCache(HSSFWorkbook wb) {
        String key = wb.hashCode() + "";
        synchronized (styleCache) {
            styleCache.remove(key + "int");
            styleCache.remove(key + "double");
            styleCache.remove(key + "float");
            styleCache.remove(key + "string");
            styleCache.remove(key + "date");
        }
    }

    private HSSFCellStyle getCellStyle(HSSFWorkbook wb, String type) {
        String key = wb.hashCode() + "";
        synchronized (styleCache) {
            return styleCache.get(key + type);
        }
    }

    private HSSFCellStyle createStyle(HSSFWorkbook wb, Class<?> type) {
        HSSFDataFormat format = wb.createDataFormat();
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        if (type == Date.class) {
            cellStyle.setDataFormat(format.getFormat(DateTimeUtils.YEAR_MONTH_DAY));
        } else if (type == Integer.class || type == int.class) {
            cellStyle.setDataFormat(format.getFormat("0"));
        } else if (type == Double.class || type == double.class) {
            cellStyle.setDataFormat(format.getFormat("0.00"));
        } else if (type == Float.class || type == float.class) {
            cellStyle.setDataFormat(format.getFormat("0.00"));
        }
        return cellStyle;

    }

    @Override
    public void read(InputStream input, ExcelIOConfig config) throws Exception {
        // POIFSFileSystem fs = new POIFSFileSystem(input);
        // 兼容读取 支持2007 +
        Workbook wbs = WorkbookFactory.create(input);
        for (int sn : config.getSheets()) {
            Sheet sheet = wbs.getSheetAt(sn);// wb.getSheetAt(0);
            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            Row row = sheet.getRow(0);
            int colNum = row.getPhysicalNumberOfCells();
            for (int i = 1; i <= rowNum; i++) {
                config.getRowCallBack().before(i, rowNum);
                row = sheet.getRow(i);
                for (int j = 0; j < colNum; j++) {
                    config.getCellCallBack().before(i, rowNum);
                    Object v = row == null ? null : cell2Objec(row.getCell(j));
                    config.getCellCallBack().success(cell2Objec(sheet.getRow(0).getCell(j)).toString(), v);
                }
            }
        }
    }

    public Object cell2Objec(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case HSSFCell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();
            default:
                return "";
        }

    }

    @Override
    public void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList) throws Exception {
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建Excel的工作sheet,对应到一个excel文档的tab
        HSSFSheet sheet = wb.createSheet("sheet1");
        sheet = encodeSheet(sheet, wb, headers, dataList, wb, null);

        wb.write(stream);
        stream.close();
    }

    /**
     * 传入要组建的表数据（这里是单个表格WritableSheet对象，不进行整表的创建）
     *
     * @param sheet    要组件的单个表格对象
     * @param headers  表头（就是数据的第一行） 有2种数据 1 header 代表表头（第一行）的内容,field 代表这一列数据的字段（标识） 与dataList中的key相对应
     * @param dataList 表数据 map中的key 与headers集合中field对应
     * @param wb2
     * @return
     * @throws Exception
     */
    public HSSFSheet encodeSheet(HSSFSheet sheet, HSSFWorkbook wb, List<Header> headers, List<Map<?, ?>> dataList, HSSFWorkbook wb2, WriteExcelConfig config) throws Exception {
        initStyleCache(wb);
        HSSFRow h_row = sheet.createRow(0);
        // 单元格格式
        for (int i = 0; i < headers.size(); i++) {
            org.webbuilder.utils.office.excel.io.Header hd = headers.get(i);
            // 表头
            String header = hd.getTitle();
            // 字段标识
            String field = hd.getFiled();
            header = replaceString(header);// 替换非法字符
            HSSFCell h_cell = h_row.createCell(i);
            h_cell.setCellValue(new HSSFRichTextString(header));
            for (int j = 0; j < dataList.size(); j++) {
                Map<?, ?> map = dataList.get(j);
                Object value;
                if (field.contains(".")) {
                    value = ClassUtil.getValueByAttribute(field, map);
                } else {
                    value = map.get(field);
                }
                if (value == null) {
                    value = "";
                }
                LOGGER.debug("写出EXCEL: header=" + header + "    field=" + field + "\t value=" + value);
                HSSFRow row = sheet.getRow(j + 1) == null ? sheet.createRow(j + 1) : sheet.getRow(j + 1);
                HSSFCell cell = row.createCell(i);
                initCell(cell, value, wb2);
            }
        }
        if (config != null) {
            List<String> list = config.getMergeColumns();
            Map<String, Integer> cols = new HashMap<String, Integer>();
            for (Header header : headers) {
                if (list.contains(header.getFiled())) {
                    cols.put(header.getFiled(), headers.indexOf(header));
                }
            }
            prepareMerges(dataList, cols, config);
            // 合并单元格
            List<WriteExcelConfig.Merge> merges = config.getMerges();
            for (WriteExcelConfig.Merge merge : merges) {
                LOGGER.info("merge:" + merge.getRowFrom() + "," + merge.getColFrom() + "," + merge.getRowTo() + "," + merge.getColTo());
                try {
                    sheet.addMergedRegion(new CellRangeAddress(merge.getRowFrom(), merge.getColTo(), merge.getColFrom(), merge.getRowTo()));
                } catch (Exception e) {
                    LOGGER.error("POIExcelIo", e);
                }
            }
            // 合并相同列
        }
        removeStyleCache(wb);
        return sheet;
    }

    /**
     * 编译需要合并的列
     *
     * @param dataList 数据集合
     * @param cols     需要合并的列<列名, 列索引>
     * @param config   配置对象
     * @throws Exception
     */
    private static void prepareMerges(List<Map<?, ?>> dataList, Map<String, Integer> cols, WriteExcelConfig config) throws Exception {
        // 列所在索引/////列计数器///////////上一次合并的列位置
        int index = 0, countNumber = 0, lastMergeNumber = 0;
        // 遍历要合并的列名
        for (String header : cols.keySet()) {
            index = cols.get(header);// 列所在索引
            countNumber = lastMergeNumber = 0;
            Object lastData = null;// 上一行数据
            // 遍历列
            for (Map<?, ?> data : dataList) {
                Object val = ClassUtil.getValueByAttribute(header, data);// data.get(header);
                if (val == null)
                    continue;
                // 如果当前行和上一行相同 ，合并列数+1
                if (val.equals(lastData) || lastData == null) {
                    countNumber++;
                } else {
                    // 与上一行不一致，代表本次合并结束
                    config.addMerge(lastMergeNumber + 1, index, index, countNumber);
                    lastMergeNumber = countNumber;// 记录当前合并位置
                    countNumber++;// 总数加1
                }
                // 列末尾需要合并
                if (dataList.indexOf(data) == dataList.size() - 1) {
                    config.addMerge(lastMergeNumber + 1, index, index, dataList.size());
                }
                // 上一行数据
                lastData = val;
            }
        }
    }

    public void initCell(HSSFCell cell, Object value, HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = getCellStyle(workbook, "string");
        if (value instanceof String) {
            String v = value.toString();
            if (v.endsWith("$:hide")) {
                // 标记隐藏的数据，设置字体颜色为白色
                value = v.substring(0, v.indexOf("$:hide"));
                Font font = workbook.createFont();
                font.setColor(HSSFColor.WHITE.index);
                cellStyle.setFont(font);
                cell.setCellStyle(cellStyle);
                return;
            }
        }
        // 尝试转换为数值
        if (StringUtil.isInt(value))
            value = StringUtil.toInt(value);
        else if (StringUtil.isDouble(value))
            value = StringUtil.toDouble(value);
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cellStyle = getCellStyle(workbook, "date");
        } else if (value instanceof Double) {
            double d = (Double) value;
            int i = (int) d;
            if (d == 0.0 || d % i == 0) {
                cellStyle = getCellStyle(workbook, "int");
            } else {
                cellStyle = getCellStyle(workbook, "double");
            }
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cellStyle = getCellStyle(workbook, "int");
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            double d = (Float) value;
            int i = (int) d;
            if (d == 0 || d % i == 0) {
                cellStyle = getCellStyle(workbook, "int");
            } else {
                cellStyle = getCellStyle(workbook, "float");
            }
            cellStyle = getCellStyle(workbook, "float");
            cell.setCellValue((Float) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(new HSSFRichTextString(value.toString()));
        }
        cell.setCellStyle(cellStyle);
    }


    public static String replaceString(String str) {
        return str.replace("\\n", "").replace("\\t", "").replace(" ", "").replace("\r\n", "").replace("\n", "").replace("\r", "").replace("\t", "");
    }

    @Override
    public void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList, WriteExcelConfig config) throws Exception {
        // 创建Excel的工作书册 Workbook,对应到一个excel文档
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建Excel的工作sheet,对应到一个excel文档的tab
        HSSFSheet sheet = wb.createSheet("sheet1");
        sheet = encodeSheet(sheet, wb, headers, dataList, wb, config);
        wb.write(stream);
        stream.close();
    }

}
