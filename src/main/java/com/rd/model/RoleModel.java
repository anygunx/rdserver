package com.rd.model;

import com.rd.bean.role.RoleData;
import com.rd.enumeration.EAttr;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月23日下午2:39:35
 */
public class RoleModel {

    static Logger log = Logger.getLogger(RoleModel.class.getName());

    private static Map<Short, RoleData> roleDataMap;

    private RoleModel() {

    }

    public static void loadRole(String path) {
        final File file = new File(path, "gamedata/levelup.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, RoleData> tmpMap = new HashMap<>();
                    String content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject json = new JSONObject(content);
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        JSONObject value = json.getJSONObject(key);
                        long exp = value.getLong("exp");
                        int[] attr = EAttr.getIntAttr(value);
                        short lv = Short.parseShort(key);
                        tmpMap.put(lv, new RoleData(lv, exp, attr));
                    }
                    roleDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载主角模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "levelup";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static RoleData getRoleData(short level) {
        return roleDataMap.get(level);
    }

    public static RoleData getRoleData(int level) {
        return roleDataMap.get((short) level);
    }

    public static long getMaxExpByLevel(short level) {
        return roleDataMap.get(level).getExp();
    }
}
