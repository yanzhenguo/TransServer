package cn.shu.util;

import java.io.File;

public class FileUtil {
    /**
     * 查找文件夹下的文件列表
     * @param root
     */
    public static void listDir(String root){
        File file = new File(root);
        File[] files = file.listFiles();
    }

}
