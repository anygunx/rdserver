package com.rd.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 文件工具类
 *
 * @author Created by U-Demon on 2016年10月26日 下午1:58:39
 * @version 1.0.0
 */
public class FileUtil {

    private static Logger logger = Logger.getLogger(FileUtil.class);

    public static final String ROOTPATH;
    public static final String RESOURCE_PATH;
    public static String src_path;

    static {
        ROOTPATH = new File("").getAbsolutePath();
        RESOURCE_PATH = ROOTPATH + File.separatorChar + "resource" + File.separatorChar;

    }

    /**
     * 获取包下所有类
     *
     * @param packageName
     * @return
     */
    public static List<String> getClasses(String packageName) {
        if (System.getProperty("os.name").indexOf("Windows") != -1)
            src_path = ROOTPATH + File.separatorChar + "bin" + File.separatorChar + packageName;
        else
            src_path = ROOTPATH + File.separatorChar;
        logger.info("src_path = " + src_path);
        File file = new File(src_path.replace('.', File.separatorChar));
        List<String> classNames = new ArrayList<>();
        getClassName(file, packageName, classNames);
        return classNames;
    }

    private static void getClassName(File file, String packageName, List<String> className) {
        //文件夹
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                getClassName(childFile, packageName, className);
            }
        }
        //class文件
        else if (file.getName().endsWith(".class")) {
            String filePath = file.getPath().replace(File.separatorChar, '.');
            filePath = filePath.substring(filePath.indexOf(packageName), filePath.length() - 6);
            className.add(filePath);
        }
        //jar文件
        else if (file.getName().endsWith(".jar")) {
            try {
                FileInputStream fis = new FileInputStream(file);
                JarInputStream jis = new JarInputStream(fis, false);
                JarEntry je = null;
                while ((je = jis.getNextJarEntry()) != null) {
                    String jeName = je.getName().replace(File.separatorChar, '.');
                    if (jeName.startsWith(packageName) && jeName.endsWith(".class")) {
                        className.add(jeName.substring(0, jeName.length() - 6));
                    }
                    jis.closeEntry();
                }
                jis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
