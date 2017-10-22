package cn.shu.util;

import java.io.File;

public class FileUtil {
    /**
     * �г�Ŀ¼�µ��ļ�
     * @param root
     */
    public static void listDir(String root){
        File file = new File(root);
        File[] files = file.listFiles();
    }

}
