package org.webbuilder.generator.jframe.panel;

import org.webbuilder.generator.bean.Field;
import org.webbuilder.generator.bean.GeneratorConfig;
import org.webbuilder.generator.jframe.Main;
import org.webbuilder.generator.service.GeneratorService;
import org.webbuilder.generator.service.imp.CommonGeneratorServiceImp;
import org.webbuilder.utils.office.excel.io.ExcelIO;
import org.webbuilder.utils.office.excel.io.Header;
import org.webbuilder.utils.office.excel.io.ReadExcelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;

/**
 * Created by 浩 on 2015-07-27 0027.
 */
public class GenerPanel extends JPanel {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final GeneratorService service = new CommonGeneratorServiceImp();
    protected Map<String, Component> ComponentBase = new HashMap<>();

    private ButtonGroup autoCreate = new ButtonGroup();
    final String columnNames[] = {"名称", "java类", "数据类型", "备注", "默认值", "主键", "不能为空", "是否只读", "是搜索条件","列表显示"};
    private JTable table = null;
    protected Component components[][] = null;

    public JLabel createLabel(String name) {
        JLabel label = new JLabel(name, SwingConstants.RIGHT);
        label.setFont(Main.baseFont);
        label.setSize(100, 20);
        return label;
    }

    public JTextField createInput(String name) {
        JTextField textField = new JTextField();
        textField.setFont(Main.baseFont_min);
        textField.setSize(100, 25);
        ComponentBase.put(name, textField);
        return textField;
    }

    public GenerPanel() {
        this.setLayout(null);
    }

    public List<Map> getTableData(JTable table) {
        TableModel tm = table.getModel();
        int rows = table.getRowCount();
        int cols = table.getColumnCount();
        List<Map> list = new LinkedList();
        for (int i = 0; i < rows; i++) {
            Map map = new LinkedHashMap();
            list.add(map);
            for (int j = 0; j < cols; j++) {
                map.put(columnNames[j], tm.getValueAt(i, j));
            }
        }
        return list;
    }

    public void putData(JTable table, List<Map> datas) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(model.getRowCount() - 1);
        }
        for (Map data : datas) {
            model.addRow(data.values().toArray());
        }
    }

    //返回操作
    public void c_z() {
        if (cache == null || cache.getOld() == null) {
            logger.error("未找到缓存");
            return;
        }
        putData(table, cache.getData());
        cache.getOld().setNext(cache);
        cache = cache.getOld();
    }

    //撤销返回操作
    public void c_y() {
        if (cache == null || cache.getNext() == null) {
            logger.error("未找到缓存");
            return;
        }
        putData(table, cache.getNext().getData());
        cache = cache.getNext();
    }

    public void onSelected() {
        ((JScrollPane) ComponentBase.get("console")).setViewportView(Main.getConsole());
    }

    public void init() {
        final Object[][] cellData = new Object[][]{};
        final DefaultTableModel model_ = new DefaultTableModel(cellData, columnNames);
        model_.addRow(new Object[]{"u_id", "String", "varchar2(256)", "主键", "", true, true, true, true,true});
        table = new JTable(model_) {
            {
                getColumn("java类").setCellEditor(new DefaultCellEditor(new JComboBox() {{
                    this.addItem("int");
                    this.addItem("double");
                    this.addItem("String");
                    this.addItem("boolean");
                    this.addItem("java.util.Date");
                }}));
                setSize(Main.WIDTH - 70, 190);
                setRowMargin(2);
                setFont(Main.baseFont_min);
                setRowHeight(18);
                setSelectionBackground(new Color(227, 227, 227));
                addKeyListener(new KeyAdapter() {
                    boolean ctrl = false;
                    boolean z = false;
                    boolean y = false;

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                            ctrl = true;
                        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                            z = true;
                        } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                            y = true;
                        }
                        if (ctrl && z) {
                            c_z();
                        } else if (ctrl && y) {
                            c_y();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                            ctrl = false;
                        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                            z = false;
                        } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                            y = false;
                        }
                    }
                });
            }
        };
        cache = new Cache(getTableData(table), null);
//        final JTextArea console = new JTextArea() {{
//            setSize(Main.WIDTH - 50, 150);
//            setText(">控制台准备就绪!\n");
//            setAutoscrolls(true);
//            setEditable(false);
//            setFont(Main.baseFont_min);
//            setForeground(Color.black);
//        }};
//        JTextAreaAppender.registerArea(console);
        //组件2维数组
        components = new Component[][]{
                {
                        new JButton("添加") {{
                            setSize(80, 25);
                            setFont(Main.baseFont_min);
                            addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    model_.addRow(new Object[]{"", "String", "varchar2(256)", "新建字段", "", false, false, false, true,true});
                                }
                            });
                        }},
                        new JButton("删除") {{
                            setSize(80, 25);
                            setFont(Main.baseFont_min);
                            addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    //删除之前先保存数据
                                    Cache cache_ = new Cache(getTableData(table), cache);
                                    if (cache != null)
                                        cache_.setNext(cache);
                                    cache = cache_;

                                    int selections[] = table.getSelectedRows();
                                    if (selections.length > 0)
                                        try {
                                            int lastIndex = selections[0];
                                            for (int i = selections.length; i > 0; i--) {
                                                model_.removeRow(table.getSelectedRow());
                                            }
                                            if (table.getRowCount() > 0)
                                                table.setRowSelectionInterval(lastIndex - 1, lastIndex - 1);
                                        } catch (Exception e1) {
                                            logger.info("未选择行");
                                        }
                                }
                            });
                        }}, new JButton("导入excel") {{
                    setSize(80, 25);
                    setFont(Main.baseFont_min);
                    setMargin(new Insets(0, 0, 0, 0));
                    addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            chooser.setFileFilter(new FileFilter() {
                                @Override
                                public boolean accept(File f) {
                                    if(f.isDirectory())return true;
                                    return f.getName().endsWith("xls") || f.getName().endsWith("xlsx");
                                }

                                @Override
                                public String getDescription() {
                                    return "excel文档";
                                }
                            });
                            chooser.setFont(Main.baseFont_min);
                            chooser.showOpenDialog(null);
                            File f = chooser.getSelectedFile();
                            if(f==null)
                                return;
                            if (f.getName() != "xls" && f.getName() != "xlsx") {
                                try {
                                    List<LinkedHashMap> datas = ExcelIO.read(new FileInputStream(f), LinkedHashMap.class, new ReadExcelConfig<LinkedHashMap>(){
                                        @Override
                                        public void headerNotFound(String header, Object val, LinkedHashMap nowObj) {
                                            nowObj.put(header,val);
                                        }
                                    });
                                    putData(table,(List) datas);
                                    logger.info("导入成功!");
                                } catch (Exception e1) {
                                    logger.error("加载文件失败", e1);
                                }
                            } else {
                                logger.info("格式错误，只支持xls和xlsx格式的文件！");
                            }

                        }
                    });
                }}
                }
                ,
                {
                        new JScrollPane() {{
                            setSize(Main.WIDTH - 50, 200);
                            setViewportView(table);
                        }},

                },
                {
                        createLabel("输出目录:"),
                        createInput("output"),
                        new JButton("选择") {{
                            setSize(50, 25);
                            setFont(Main.baseFont_min);
                            setMargin(new Insets(0, 0, 0, 0));
                            addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JFileChooser chooser = new JFileChooser();
                                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    chooser.setFont(Main.baseFont_min);
                                    chooser.showOpenDialog(null);
                                    File f = chooser.getSelectedFile();
                                    if (f != null)
                                        getInput("output").setText(f.getAbsolutePath());
                                }
                            });
                        }},
                        createLabel("包  名:"),
                        createInput("packageName")
                },
                {
                        createLabel("模块名称:"),
                        createInput("module"),
                        createLabel("类  名:"),
                        createInput("className"),
                        createLabel("备注:"),
                        createInput("classRemark"),

                }, {
                createLabel("数据库表名:"),
                createInput("tableName"),
                createLabel("自动建表:"),
                new JRadioButton() {{
                    setText("是");
                    setSize(40, 20);
                    setActionCommand("true");
                    ComponentBase.put("autoCreate_true", this);
                }}
                , new JRadioButton() {{
            setText("否");
            setActionCommand("false");
            setSize(40, 20);
            ComponentBase.put("autoCreate_false", this);
            setSelected(true);
        }},
                new JButton("生成代码") {{
                    setSize(120, 25);
                    setFont(Main.baseFont_min);
                    addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            List<Map> datas = getTableData(table);
                            final GeneratorConfig conf = new GeneratorConfig();
                            conf.setDbConfig(((DBManagerPanel) Main.dbmanager).getDbConfig());
                            conf.setDatabaseType(conf.getDbConfig().get("type"));
                            String output = ((JTextField) ComponentBase.get("output")).getText();
                            String packageName = ((JTextField) ComponentBase.get("packageName")).getText();
                            String module = ((JTextField) ComponentBase.get("module")).getText();
                            String className = ((JTextField) ComponentBase.get("className")).getText();
                            String classRemark = ((JTextField) ComponentBase.get("classRemark")).getText();
                            String tableName = ((JTextField) ComponentBase.get("tableName")).getText();
                            boolean autoCreate = ((JRadioButton) ComponentBase.get("autoCreate_true")).isSelected();
                            conf.setAutoCreate(autoCreate);
                            conf.setOutput(new File(output));
                            conf.setTableName(tableName);
                            conf.setPackageName(packageName);
                            conf.setModule(module);
                            conf.setRemark(classRemark);
                            conf.setClassName(className);
                            logger.info(datas.toString());
                            for (Map<String, String> data : datas) {
                                Field field = new Field();
                                field.setName(data.get("名称"));
                                field.setJavaTypeName(data.get("java类"));
                                field.setDataType(data.get("数据类型"));
                                field.setRemark(data.get("备注"));
                                field.setReadOnly("true".equals(String.valueOf(data.get("是否只读"))));
                                field.setCanSearch("true".equals(String.valueOf(data.get("是搜索条件"))));
                                field.setPrimaryKey("true".equals(String.valueOf(data.get("主键"))));
                                field.setNotNull("true".equals(String.valueOf(data.get("不能为空"))));
                                field.setList("true".equals(String.valueOf(data.get("列表显示"))));
                                field.setDefaultValue(data.get("默认值"));
                                try {
                                    field.valid();
                                } catch (Exception e1) {
                                    logger.error(e1.getMessage());
                                    return;
                                }
                                conf.getFields().add(field);
                            }

                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        conf.valid();
                                        service.generate(conf);
                                        logger.info("生成成功!");
                                    } catch (Exception e1) {
                                        logger.error("生成代码失败:" + e1.getMessage());
                                        e1.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    });
                }},
                new JButton("清空控制台") {{
                    setSize(120, 25);
                    setFont(Main.baseFont_min);
                    addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Main.clearConsole();
                        }
                    });
                }},

        },
                {
                        new JScrollPane() {{
                            ComponentBase.put("console", this);
                            setSize(Main.WIDTH - 50, 150);
                            setToolTipText("控制台");
                            //setViewportView(Main.getConsole());
                        }}
                }
        };
        getInput("packageName").setText("org.webbuilder");
        getInput("output").setSize(260, 25);
        autoCreate.add(getRadio("autoCreate_true"));
        autoCreate.add(getRadio("autoCreate_false"));
        buildComponents();
    }

    protected void buildComponents() {
        int x = 20, y = 20;
        for (Component[] component : components) {
            int maxH = 0;
            //横向:
            for (Component componentX : component) {
                if (maxH < componentX.getHeight())
                    maxH = componentX.getHeight();
                componentX.setLocation(x, y);
                x += componentX.getWidth() + 10;
                this.add(componentX);
            }
            x = 20;
            y += maxH + 5;
        }
    }

    public <T extends Component> T getComponent(String name) {
        return (T) ComponentBase.get(name);
    }

    public JTextField getInput(String name) {
        return (JTextField) ComponentBase.get(name);
    }

    public JRadioButton getRadio(String name) {
        return (JRadioButton) ComponentBase.get(name);
    }

    private Cache cache;

    public class Cache {
        private List data;

        private Cache old;

        private Cache next;

        public Cache(List data, Cache old) {
            this.data = data;
            this.old = old;
        }

        public List getData() {
            return data;
        }

        public void setData(List data) {
            this.data = data;
        }

        public Cache getOld() {
            return old;
        }

        public void setOld(Cache old) {
            this.old = old;
        }

        public Cache getNext() {
            return next;
        }

        public void setNext(Cache next) {
            this.next = next;
        }
    }
}
