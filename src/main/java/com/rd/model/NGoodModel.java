package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.data.NEquipData;
import com.rd.enumeration.EAttr;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NGoodModel {
    private static Logger logger = Logger.getLogger(NGoodModel.class);

    private static Map<Short, NEquipData> equipDataMap;
    private static Map<Short, List<NEquipData>> equipLvMap;


    /**
     * 经脉
     *
     * @param path
     */
    public static void loadPulseModelData(String path) {
        final File file = new File(path, "gamedata/equipment.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Short, NEquipData> temp = new HashMap<>();
                Map<Short, List<NEquipData>> tmpLv = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        short id = Short.parseShort(key);
                        int pos = value.getInt("pos");
                        int level = value.getInt("level");
                        int quality = value.getInt("quality");
                        int[] attr = EAttr.getIntAttr(value);
                        List<DropData> breakItem = null;// GameCommon.parseDropDataList(value.getString("break_item"));

                        NEquipData data = new NEquipData((short) level, (byte) quality, (byte) pos, attr, breakItem);
                        data.setGoodsId(id);
                        temp.put(id, data);
                        if (!tmpLv.containsKey(data.getLevel()))
                            tmpLv.put(data.getLevel(), new ArrayList<>());
                        tmpLv.get(data.getLevel()).add(data);
                    }
                    equipDataMap = temp;
                    equipLvMap = tmpLv;
                } catch (IOException e) {
                    logger.error("加装备数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "equipModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    public static NEquipData getNEquipDataById(short equipId) {
        return equipDataMap.get(equipId);
    }


}
