package org.webbuilder.utils.base.file;

import java.io.File;

public interface CallBack {
    void isFile(File file);

    void isDir(File dir);

    void readError(File file, Throwable e);
}
