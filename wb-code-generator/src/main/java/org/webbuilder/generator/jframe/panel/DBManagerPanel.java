package org.webbuilder.generator.jframe.panel;


import org.webbuilder.generator.jframe.Main;
import org.webbuilder.generator.service.database.ConnectionBuilder;
import org.webbuilder.utils.base.StringTemplateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 浩 on 2015-07-30 0030.
 */
public class DBManagerPanel extends GenerPanel {

    protected Properties properties = new Properties();


    public DBManagerPanel() {
        super();
        try {
            new File("config").mkdir();
            properties.load(new FileInputStream("config/db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final JComboBox dbType = new JComboBox() {{
        this.addItem("oracle");
        this.addItem("mysql");
        setFont(Main.baseFont_min);
        setSize(150, 21);
    }};

    private Map<String, Map<String, String>> dbProperty = new HashMap() {{
        put("mysql", new HashMap<String, String>() {{
            put("driver", "com.mysql.jdbc.Driver");
            put("url", "jdbc:mysql://${ip!'localhost'}:${port!'3306'}/${name}?useUnicode=true&characterEncoding=${charset!'utf8'}");
            put("test", "select 1");
        }});

        put("oracle", new HashMap<String, String>() {{
            put("driver", "oracle.jdbc.driver.OracleDriver");
            put("url", "jdbc:oracle:thin:@${ip!'localhost'}:${port!'1521'}:${name}");
            put("test", "select 1 from dual");
        }});
    }};

    @Override
    public void init() {

        components = new Component[][]{{
                createLabel("数据库类型:"),
                dbType,
                createLabel("数据库名称:"),
                createInput("name"),
        }, {
                createLabel("数据库地址:"),
                createInput("ip"),
                createLabel("端口:"),
                createInput("port")
        }, {
                createLabel("用户名:"),
                createInput("username"),
                createLabel("密码:"),
                createInput("password")
        }, {new JButton("清空控制台") {{
            setSize(150, 25);
            setFont(Main.baseFont_min);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.clearConsole();
                }
            });
        }},}, {
                new JScrollPane() {{
                    ComponentBase.put("console", this);
                    setSize(Main.WIDTH - 50, 300);
                    setToolTipText("控制台");
                    //setViewportView(Main.getConsole());
                }}
        }};
        this.add(new JButton("测试链接") {
                     {
                         setMargin(new Insets(0, 0, 0, 0));
                         setBounds(560, 20, 100, 70);
                         setFont(Main.baseFont);
                         addActionListener(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   Connection connection = null;
                                                   try {
                                                       Map<String, String> config = getDbConfig();
                                                       connection = ConnectionBuilder.getConnection(config);
                                                       connection.prepareStatement(config.get("test")).execute();
                                                       logger.info("测试通过!");
                                                   } catch (Exception e1) {
                                                       logger.error("测试未通过", e);
                                                   } finally {
                                                       if (connection != null)
                                                           try {
                                                               connection.close();
                                                           } catch (SQLException e1) {
                                                           }
                                                   }

                                               }
                                           }
                         );
                     }
                 }
        );

        this.add(new JButton("保存配置") {
            {
                setMargin(new Insets(0, 0, 0, 0));
                setBounds(670, 20, 100, 70);
                setFont(Main.baseFont);
                addActionListener(new ActionListener() {
                                      @Override
                                      public void actionPerformed(ActionEvent e) {
                                          Map<String, String> config = getDbConfig();
                                          try {
                                              OutputStream fos = new FileOutputStream("config/db.properties");
                                              properties.putAll(config);
                                              properties.store(fos,"--");
                                              fos.flush();
                                              fos.close();
                                              logger.info("保存成功");
                                          } catch (Exception e1) {
                                              logger.error("",e1);
                                          }
                                      }
                                  }
                );
            }
        });

        ComponentBase.get("ip").
                setSize(150, 20);
        ComponentBase.get("port").
                setSize(150, 20);

        ComponentBase.get("name").
                setSize(150, 20);
        ComponentBase.get("username").
                setSize(150, 20);
        ComponentBase.get("password").
                setSize(150, 20);
        dbType.setSelectedItem(properties.getProperty("type", "oracle"));

        ((JTextField) ComponentBase.get("ip")).setText(properties.getProperty("ip", "localhost"));
        ((JTextField) ComponentBase.get("port")).setText(properties.getProperty("port", "1521"));
        ((JTextField) ComponentBase.get("name")).setText(properties.getProperty("name", ""));
        ((JTextField) ComponentBase.get("username")).setText(properties.getProperty("username", ""));
        ((JTextField) ComponentBase.get("password")).setText(properties.getProperty("password", ""));

        buildComponents();
    }


    public Map<String, String> getDbConfig() {
        String name = ((JTextField) ComponentBase.get("name")).getText();
        String ip = ((JTextField) ComponentBase.get("ip")).getText();
        String port = ((JTextField) ComponentBase.get("port")).getText();
        String username = ((JTextField) ComponentBase.get("username")).getText();
        String password = ((JTextField) ComponentBase.get("password")).getText();
        String type = String.valueOf(dbType.getSelectedItem());
        Map<String, String> config = new LinkedHashMap<>();
        config.put("type", type);
        config.put("name", name);
        config.put("ip", ip);
        config.put("port", port);
        config.put("username", username);
        config.put("password", password);
        Map<String, String> dbTypeConfig = dbProperty.get(type);
        try {
            String driver = StringTemplateUtils.compileAndGenerate(dbTypeConfig.get("driver"), (Map) config);
            String url = StringTemplateUtils.compileAndGenerate(dbTypeConfig.get("url"), (Map) config);
            config.put("driver", driver);
            config.put("url", url);
            config.put("test", dbTypeConfig.get("test"));
        } catch (Exception e1) {
            logger.error("获取链接配置错误!");
        }
        return config;
    }

}
