package cn.yan.util;

import java.io.File;

public class FileUtil {
    /**
     * 列出目录下的文件
     * @param root
     */
    public static void listDir(String root){
        File file = new File(root);
        File[] files = file.listFiles();
    }

}
