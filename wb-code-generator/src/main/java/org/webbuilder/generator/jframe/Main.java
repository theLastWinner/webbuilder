package org.webbuilder.generator.jframe;

import org.webbuilder.generator.jframe.panel.DBManagerPanel;
import org.webbuilder.generator.jframe.panel.GenerPanel;
import org.webbuilder.generator.service.logger.append.JTextAreaAppender;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by 浩 on 2015-07-27 0027.
 */
public class Main extends JFrame {
    public static final int WIDTH = 800, HEIGHT = 600;

    private JTabbedPane tabbedPane;

    public static GenerPanel gener = new GenerPanel(), dbmanager = new DBManagerPanel();
    private JPanel system = createPanel();

    private static JTextArea console = null;
    public static Font baseFont = new Font("微软雅黑", Font.BOLD, 18);
    public static Font baseFont_min = new Font("微软雅黑", Font.BOLD, 12);

    public JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setFont(baseFont);
        panel.setLayout(null);
        return panel;
    }

    public static void clearConsole() {
        if (console != null)
            console.setText(">控制台准备就绪!\n");
    }

    public static JTextArea getConsole() {
        return Main.console;
    }

    public Main() {
        console = new JTextArea() {{
            setSize(Main.WIDTH - 50, 150);
            setText(">控制台准备就绪!\n");
            setAutoscrolls(true);
            setEditable(false);
            setFont(Main.baseFont_min);
            setForeground(Color.black);
        }};
        JTextAreaAppender.registerArea(console);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("代码生成器 v1.0 by zh.sqy@qq.com");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);//居中
        this.setResizable(false);
        this.setBackground(new Color(43, 43, 43));
        this.setForeground(new Color(43, 43, 43));
        this.setFont(baseFont);
        Container c = getContentPane();
        tabbedPane = new JTabbedPane();   //创建选项卡面板对象
        //创建面板
        //将标签面板加入到选项卡面板对象上
        gener.init();
        dbmanager.init();
        tabbedPane.setFont(baseFont_min);
        tabbedPane.addTab("代码生成", null, gener, "代码生成");
        tabbedPane.addTab("数据库管理", null, dbmanager, "数据库管理");
       // tabbedPane.addTab("系统设置", null, system, "系统设置");
        gener.onSelected();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = ((JTabbedPane)e.getSource()).getSelectedIndex();
                switch (index){
                    case 0:
                        gener.onSelected();
                        return;
                    case 1:
                        dbmanager.onSelected();
                        return;
                    case 2:
                        return;
                    default:
                        return;
                }
            }
        });
        c.add(tabbedPane);

    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}
