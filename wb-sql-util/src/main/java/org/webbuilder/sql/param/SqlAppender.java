package org.webbuilder.sql.param;

import java.util.LinkedList;

/**
 * Created by 浩 on 2015-11-07 0007.
 */
public class SqlAppender extends LinkedList<String> {


    public SqlAppender add(String... str) {
        for (String s : str) {
            this.add(s);
        }
        return this;
    }

    public SqlAppender addEdSpc(String... str) {
        for (String s : str) {
            this.add(s);
        }
        this.add(" ");
        return this;
    }

    /**
     * 接入sql语句，并自动加入空格
     *
     * @param str
     * @return
     */
    public SqlAppender addSpc(String... str) {
        for (String s : str) {
            this.add(s);
            this.add(" ");
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String str : this) {
            builder.append(str);
        }
        return builder.toString();
    }
}
