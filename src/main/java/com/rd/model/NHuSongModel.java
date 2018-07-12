package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.husong.NHuSongData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NHuSongModel {
    static Logger log = Logger.getLogger(NHuSongModel.class.getName());
    private static Map<Integer, NHuSongData> husongMap;
    private static List<NHuSongData> husongList;

    public static void loadData(String path) {
        loadZhongKui(path);


    }

    private static void loadZhongKui(String path) {
        final File file = new File(path, "gamedata/husong.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NHuSongData> temp = new HashMap<>();
                List<NHuSongData> list = new ArrayList<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int gailv = value.getInt("gailv");
                        int time = value.getInt("time");
                        List<DropData> rewards = GameCommon.parseDropDataList(value.getString("reward"));
                        List<DropData> jiebiao = GameCommon.parseDropDataList(value.getString("jiebiao"));
                        NHuSongData data =
                                new NHuSongData(id, time, gailv, rewards, jiebiao);
                        temp.put(id, data);
                        list.add(data);
                    }

                    husongMap = temp;
                    husongList = list;
                } catch (IOException e) {
                    log.error("加载护送镖车数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "husongbiaochemodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static NHuSongData getNHuSongDataById(int id) {
        return husongMap.get(id);
    }

    public static List<NHuSongData> getNHuSongDataList() {
        return husongList;
    }

}
