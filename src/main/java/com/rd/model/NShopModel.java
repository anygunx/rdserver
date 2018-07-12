package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.shop.NShopData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NShopModel {
    static Logger log = Logger.getLogger(NShopModel.class.getName());
    private static Map<String, NShopData> shopMap;

    public static void loadModelData(String path) {

        Map<String, NShopData> tempMap = new HashMap<>();
        loadShopModelData(path, "Equipshop", tempMap);
        loadShopModelData(path, "Clubshop", tempMap);
        loadShopModelData(path, "Datishop", tempMap);
        loadShopModelData(path, "Equipshop", tempMap);
        loadShopModelData(path, "Friendshop", tempMap);
        loadShopModelData(path, "Goldshop", tempMap);
        loadShopModelData(path, "Guanghshop", tempMap);
        loadShopModelData(path, "Husongshop", tempMap);
        loadShopModelData(path, "Moneyshop", tempMap);
        loadShopModelData(path, "Petshop", tempMap);
        loadShopModelData(path, "Pifushop", tempMap);
        loadShopModelData(path, "Pvpshop", tempMap);
        loadShopModelData(path, "Skinshop", tempMap);
        loadShopModelData(path, "Weiwangshop", tempMap);
        loadShopModelData(path, "Xianlvshop", tempMap);
        loadShopModelData(path, "Clubreward", tempMap);
        loadShopModelData(path, "Pvpreward", tempMap);
        loadShopModelData(path, "Cailiaoshop", tempMap);
        loadShopModelData(path, "Quanminshop", tempMap);
        shopMap = tempMap;

    }

    /**
     * @param path
     */
    private static void loadShopModelData(String path, String name, Map<String, NShopData> shopMap) {
        final File file = new File(path, "gamedata/" + name + ".json");

        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {


                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        int shop_type = value.getInt("shop_type");

                        short level = 0;
                        if (name.equals("equipshop")) {
                            level = (short) value.getInt("level");
                        }

                        int num = value.getInt("num");
                        int condition = value.getInt("condition");
                        DropData cost = GameCommon.parseDropData(value.getString("cost"));
                        DropData reward = GameCommon.parseDropData(value.getString("reward"));
                        NShopData data = new NShopData(id, (byte) shop_type, level, reward, (byte) num, cost, (byte) condition);
                        shopMap.put(getKey(shop_type, id), data);
                    }

                } catch (IOException e) {
                    log.error("加装备商店数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "ShopModelNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);


    }

    public static NShopData getShopMap(int type, int id) {
        return shopMap.get(getKey(type, id));
    }


    private static String getKey(int type, int id) {
        return type + "_" + id;
    }

}
