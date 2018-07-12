package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.copy.quanmin.QianMinBossData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NBossModel {
    static Logger log = Logger.getLogger(NBossModel.class.getName());
    private static Map<Byte, QianMinBossData> quanMinBossDataMap;

    public static void loadData(String path) {
        loadDungeonMaterial(path);

    }


    private static void loadDungeonMaterial(String path) {
        final File file = new File(path, "gamedata/quanminboss.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }


            @Override
            public void onResourceChange(File file) {
                Map<Byte, QianMinBossData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        byte id = Byte.parseByte(key);
                        short needLv = (short) (value.getInt("need_lv"));//("need_lv");

                        int bossID = value.getInt("bossID");
                        String Monsterid = value.getString("monsterID");
                        int[] monsterID = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            monsterID = StringUtil.getIntList(Monsterid);
                        }
                        DropData rebirthitem = GameCommon.parseDropData(value.getString("rebirthitem"));
                        DropData rebirthcost = GameCommon.parseDropData(value.getString("rebirthcost"));


                        int rebirthtime = value.getInt("rebirthtime");

                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));


                        QianMinBossData data =
                                new QianMinBossData(id, needLv, bossID, monsterID, reward, rebirthitem, rebirthcost, rebirthtime);


                        temp.put(id, data);
                    }

                    quanMinBossDataMap = temp;
                } catch (IOException e) {
                    log.error("加载材料副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "quanminMaterialmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    public static Map<Byte, QianMinBossData> getQianMinBossDataMap() {
        return quanMinBossDataMap;
    }

}
