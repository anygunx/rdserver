package com.rd.dao.db;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import java.sql.Connection;
import java.sql.DriverManager;

public class ProxoolDB {
    private static final Logger logger = Logger.getLogger(ProxoolDB.class);

    public ProxoolDB() {
    }

    /**
     * 初始化数据库连接池
     */
    public static void init(String path) {
        try {
            Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
            JAXPConfigurator.configure(path + "database/proxool.xml", false); // false 表示不验证XML
            ProxoolFacade.disableShutdownHook(); // 手动释放数据库连接池资源
            /**
             * org.logicalcobwebs.proxool.ProxoolException: Attempt to refer to a unregistered pool by its alias 'machine'
             * 上述异常是由于数据库连接池资源被回收,导致获取数据库连接异常,故改为手动回收资源
             *
             * ProxoolFacade.disableShutdownHook(); // 手动释放数据库连接池资源
             * ProxoolFacade.enableShutdownHook(); // 自动释放数据库连接池资源
             * ProxoolFacade.isShutdownHookEnabled(); // 释放数据库连接池资源状态(自动/手动)
             */
        } catch (Exception e) {
            logger.error("Load MySQL Database Config Error...", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("proxool.round");
        } catch (Exception e) {
            logger.error("Get MySQL Database Connection Error...", e);
        }
        return conn;
    }

    /**
     * 是否数据库连接池资源
     */
    public static void shutdown() {
        ProxoolFacade.shutdown();
    }

}
