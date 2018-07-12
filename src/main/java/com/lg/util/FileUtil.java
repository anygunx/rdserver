package com.lg.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by XingYun on 2016/5/10.
 */
public class FileUtil {
    public static void removeFile(String filePath) {
        FileWriter fileWriter = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendToFile(String filePath, String createSql) {
        FileWriter fileWriter = null;
        BufferedWriter bufferWriter = null;
        try {
            File file = new File(filePath);
            fileWriter = new FileWriter(file, true);
            bufferWriter = new BufferedWriter(fileWriter);
            if (!file.exists()) {
                file.createNewFile();
            }
            bufferWriter.write(createSql);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferWriter != null) {
                    bufferWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ȡ��ָ����ͬ·��ָ�����µ����������ӿ�ʵ����
     *
     * @param cls
     * @return
     * @throws Exception
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getAllAssignedClass(Class<?> cls, String packet) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> c : getClassesFromPacketFile(cls, packet)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }

    /**
     * ȡ��ָ����ͬ·��ָ�����µ�������
     *
     * @param cls
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getClassesFromPacketFile(Class<?> cls, String packet) throws IOException, ClassNotFoundException {
        String pk = cls.getPackage().getName() + "." + packet;
        String packageDirName = pk.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String realPath = url.getPath();
                System.out.println("RealPath:" + realPath);
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    System.out.println("ClassPath:" + realPath);
                    return getClassesFromPacketFile(new File(realPath), pk);
                } else if ("jar".equals(protocol)) {
                    String path = getJREPath();  //realPath.substring(6, realPath.indexOf("!"));
                    System.out.println("JarPath:" + path);
                    return getClassFromJarFile(path, packageDirName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��jar�ļ��ж�ȡָ��Ŀ¼��������е�class�ļ�
     *
     * @param jarPath
     * @param filePath
     * @return
     */
    public static List<Class<?>> getClassFromJarFile(String jarPath, String filePath) {
        List<Class<?>> clazzs = new ArrayList<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> jarEntryList = new ArrayList<>();
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = ee.nextElement();
            if (entry.getName().startsWith(filePath) && entry.getName().endsWith(".class")) {
                jarEntryList.add(entry);
            }
        }
        for (JarEntry entry : jarEntryList) {
            String className = entry.getName().replace('/', '.');
            className = className.substring(0, className.length() - 6);
            try {
                clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return clazzs;
    }

    /**
     * ����������
     *
     * @param dir
     * @param pk
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> getClassesFromPacketFile(File dir, String pk) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classes.addAll(getClassesFromPacketFile(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));    //.class
        }
        return classes;
    }

    public static String getJREPath() {
        URL url;
        // �õ�jar�����·��
        url = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
        // ��url·��ת�룬��ҪӦ���ں���
        String temp = "";
        try {
            temp = URLDecoder.decode(url.getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return temp;
    }
}
