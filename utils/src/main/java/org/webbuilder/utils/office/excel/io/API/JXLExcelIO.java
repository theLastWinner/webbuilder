package org.webbuilder.utils.office.excel.io.API;

import org.webbuilder.utils.base.ClassUtil;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.office.excel.io.ExcelApi;
import org.webbuilder.utils.office.excel.io.ExcelIOConfig;
import org.webbuilder.utils.office.excel.io.Header;
import org.webbuilder.utils.office.excel.io.WriteExcelConfig;
import jxl.*;
import jxl.format.Colour;
import jxl.write.*;
import jxl.write.Number;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class JXLExcelIO implements ExcelApi {

    private static Logger logger = LoggerFactory.getLogger(JXLExcelIO.class);

    @Override
    public void read(InputStream input, ExcelIOConfig config) throws Exception {
        logger.info("读取excel.工作簿数量:" + config.getSheets().length);
        Workbook wb = Workbook.getWorkbook(input);
        // 遍历工作簿
        for (int sheetId : config.getSheets()) {
            // 获取行工作簿
            Sheet unitSheet = wb.getSheet(sheetId);
            int unitRows = unitSheet.getRows();

            int unitColumns = unitSheet.getColumns();
            // 遍历数据行(第一行为表头,不获取)
            for (int i = 1; i < unitRows; i++) {
                config.getRowCallBack().before(i, unitRows);
                Cell[] cell = unitSheet.getRow(i);
                // 遍历单元格
                for (int j = 0; j < unitColumns; j++) {
                    config.getCellCallBack().before(i, unitRows);
                    // 表头
                    String name = unitSheet.getColumn(cell[j].getColumn())[0].getContents();
                    // 单元格值（以字符串方式获取）
                    Object value = cell[j].getContents();
                    // 这里只处理了Date类型的数据，其他如number等，由于单元格格式并不一定，所以不处理其他格式，直接以String传入
                    if (cell[j].getType() == CellType.DATE) {
                        DateCell d = (DateCell) cell[j];
                        value = d.getDate();
                    }
                    // 调用成功回调
                    config.getCellCallBack().success(name, value);
                }
            }
        }
    }

    @Override
    public void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList) throws Exception {
        // TODO Auto-generated method stub
        WritableWorkbook workbook = null;
        workbook = Workbook.createWorkbook(stream);
        WritableSheet sheet = workbook.createSheet("sheet0", 0);
        // 将JSON转换为excel数据
        sheet = encodeSheet(sheet, headers, dataList, null);
        workbook.write();
        workbook.close();
    }

    /**
     * 传入要组建的表数据（这里是单个表格WritableSheet对象，不进行整表的创建）
     *
     * @param sheet    要组件的单个表格对象
     * @param headers  表头（就是数据的第一行） 有2种数据 1 header 代表表头（第一行）的内容,field 代表这一列数据的字段（标识） 与dataList中的key相对应
     * @param dataList 表数据 map中的key 与headers集合中field对应
     * @return
     * @throws Exception
     */
    public static WritableSheet encodeSheet(WritableSheet sheet, List<Header> headers, List<Map<?, ?>> dataList, WriteExcelConfig config) throws Exception {
        logger.info("创建excel;表头数量:" + headers.size() + "数据数量:" + dataList.size());
        int columnsize = 10;
        // 单元格格式
        for (int i = 0; i < headers.size(); i++) {
            Header hd = headers.get(i);
            // 表头
            String header = hd.getTitle();
            columnsize = header.length() + 7;
            // 字段标识
            String field = hd.getFiled();
            header = replaceString(header);// 替换非法字符
            sheet.addCell(new Label(i, 0, header));// 添加表头
            // 添加表头对应的数据
            for (int j = 0; j < dataList.size(); j++) {
                // sheet.addCell(new Label(i, j*2+1, header)); //每行数据对应一个表头
                Map<?, ?> map = dataList.get(j);
                Object value = "";
                if (header.contains(".") && value instanceof Map<?, ?>) {
                    value = ClassUtil.getValueByAttribute(header, value);
                } else {
                    value = map.get(field);
                }
                sheet.addCell(getWritableCellByType(i, j + 1, hd.getDataType(), value, hd));
                if (value != null)
                    if (value.toString().length() + 7 > columnsize) {
                        columnsize = value.toString().length() + 7;
                    }
            }
            sheet.setColumnView(i, columnsize);// 自动增加宽度
        }
        // 合并
        if (config != null) {
            List<String> list = config.getMergeColumns();
            Map<String, Integer> cols = new HashMap<String, Integer>();
            for (Header header : headers) {
                if (list.contains(header.getFiled())) {
                    cols.put(header.getFiled(), headers.indexOf(header));
                }
            }
            prepareMerges(dataList, cols, config);
            for (WriteExcelConfig.Merge merge : config.getMerges()) {
                sheet.mergeCells(merge.getRowFrom(), merge.getColFrom(), merge.getRowTo(), merge.getColTo());
            }
        }
        return sheet;
    }

    public static String replaceString(String str) {
        return str.replace("\\n", "").replace("\\t", "").replace(" ", "").replace("\r\n", "").replace("\n", "").replace("\r", "").replace("\t", "");
    }

    /**
     * 根据对象类型 创建对应的excel类型，支持 数值，时间
     *
     * @param c     cell
     * @param r     row
     * @param type  类型
     * @param value 值
     * @return
     */
    public static WritableCell getWritableCellByType(int c, int r, Class<?> type, Object value, Header header) {
        if (value == null)
            value = "";
        if (type == null)
            type = String.class;
        // 数值
        if (type.getName().equals(Integer.class.getName()) || type.getName().equals(int.class.getName()) || type.getName().equals(Double.class.getName()) || type.getName().equals(double.class.getName()) || type.getName().equals(float.class.getName()) || type.getName().equals(Float.class.getName()) || value instanceof Integer || value instanceof Double || value instanceof Float) {
            if (!StringUtil.isNumber(value)) {
                String vstr = value.toString();
                // 设置字体为白色
                if (vstr.contains("$:hide")) {
                    WritableFont font2 = new WritableFont(WritableFont.ARIAL);
                    try {
                        font2.setColour(Colour.WHITE);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    WritableCellFormat cFormat2 = new WritableCellFormat(font2);
                    value = vstr.substring(0, vstr.indexOf("$:hide"));
                    return new Number(c, r, Double.parseDouble(value.toString()), cFormat2);
                }
                value = "0";
            }
            return new Number(c, r, Double.parseDouble(value.toString()));
        }
        // 时间
        if (type.getName().equals(Date.class.getName()) || value instanceof Date) {
            // String d = DateTimeUtils.format((Date) value, header.getDateFormater());
            return new DateTime(c, r, (Date) value);
            // new Label(c, r, d);
        }
        if (StringUtil.isNullOrEmpty(value)) {
            value = "";
        }
        // 普通文本
        return new Label(c, r, value.toString());
    }

    /**
     * 编译需要合并的列
     *
     * @param dataList 数据集合
     * @param cols     需要合并的列<列名, 列索引>
     * @param config   配置对象
     */
    private static void prepareMerges(List<Map<?, ?>> dataList, Map<String, Integer> cols, WriteExcelConfig config) {

        // 列所在所有/////列计数器///////////上一次合并的列位置
        int index = 0, countNumber = 0, lastMergeNumber = 0;
        // 遍历要合并的列名
        for (String header : cols.keySet()) {
            index = cols.get(header);// 列所在索引
            countNumber = lastMergeNumber = 0;
            Object lastData = null;// 上一行数据
            // 遍历列
            for (Map<?, ?> data : dataList) {
                Object val = data.get(header);
                // 如果当前行和上一行相同 ，合并列数+1
                if (val.equals(lastData) || lastData == null) {
                    countNumber++;
                } else {
                    // 与上一行不一致，代表本次合并结束
                    config.addMerge(lastMergeNumber + 1, index, countNumber, index);
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

    @Override
    public void write(OutputStream stream, List<Header> headers, List<Map<?, ?>> dataList, WriteExcelConfig config) throws Exception {
        // TODO Auto-generated method stub
        WritableWorkbook workbook = null;
        workbook = Workbook.createWorkbook(stream);
        WritableSheet sheet = workbook.createSheet("sheet0", 0);
        // 将JSON转换为excel数据
        sheet = encodeSheet(sheet, headers, dataList, config);
        workbook.write();
        workbook.close();
    }
}
