package org.cn.android.common;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenning on 17-9-25.
 */

public class FileUtil {

    /**
     * 创建文件夹
     *
     * @param path Path
     * @return a new file
     */
    public static File createFile(String path) {
        return createFile(path, false);
    }

    /**
     * 创建文件
     *
     * @param path  Path
     * @param clean 如果文件存在,是先否删除
     * @return a new file
     */
    public static File createFile(String path, boolean clean) {
        File file = createParentFolder(path);
        if (file == null) {
            return file;
        }
        if (clean && file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    /**
     * 创建文件夹
     *
     * @param path Path
     * @return a new folder
     */
    public static File createFolder(String path) {
        File file = createParentFolder(path);
        if (file == null) {
            return file;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 递归创建文件夹
     *
     * @param path Path
     * @return a parent folder
     */
    public static File createParentFolder(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            File file = new File(path);
            File parent = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
            if (!parent.exists()) {
                createParentFolder(parent.getPath());
                parent.mkdirs();
            }
            return file;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static boolean deleteFile(File file) {
        boolean result = true;
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }
            for (File f : childFile) {
                result &= deleteFile(f);
            }
            result &= file.delete();
        }
        return result;
    }
}
