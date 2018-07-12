package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.combat.data.CombatDungeonData;
import com.rd.common.GameCommon;
import com.rd.common.ParseCommon;
import com.rd.define.TeamDef;
import com.rd.model.data.CrossDunData;
import com.rd.model.data.LADDisasterData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月11日上午11:16:45
 */
public class TeamModel {

    private static Logger log = Logger.getLogger(TeamModel.class);

    private static Map<Byte, CrossDunData> crossDunMap;

    private static Map<Integer, LADDisasterData> ladDisasterMap;

    public TeamModel() {

    }

    public static void loadData(String path) {
        loadCrossDungeonData(path);
        loadLADDisasterData(path);
    }

    private static void loadCrossDungeonData(String path) {
        File file = new File(path, "/gamedata/teamcopy.json");
        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, CrossDunData> tmp = new HashMap<>();

                    String content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject object = new JSONObject(content);
                    Iterator<String> it = object.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        JSONObject item = object.getJSONObject(key);

                        byte id = Byte.parseByte(key);
                        short needLevel = item.getNumber("need_lv").shortValue();
                        int boss = item.getInt("bossId");
                        int[] monster = ParseCommon.parseCommaInt(item.getString("monsterId"));
                        List<DropData> reward1 = GameCommon.parseDropDataList(item.getString("reward_1"));
                        List<DropData> reward2 = GameCommon.parseDropDataList(item.getString("reward_2"));
                        tmp.put(id, new CrossDunData(id, needLevel, boss, monster, reward1, reward2));
                    }
                    crossDunMap = tmp;
                } catch (IOException e) {
                    log.error("加载跨服组队副本出错");
                    e.printStackTrace();
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return "teamcopy";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadLADDisasterData(String path) {
        final File file = new File(path, "gamedata/teambreak.json");
        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, LADDisasterData> tmp = new HashMap<>();
                    String content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject json = new JSONObject(content);
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject value = json.getJSONObject(key);

                        String[] id = value.getString("id").split("_");
                        int boss1 = value.getInt("bossId1");
                        int[] monster1 = ParseCommon.parseCommaInt(value.getString("monsterId1"));
                        int boss2 = value.getInt("bossId2");
                        int[] monster2 = ParseCommon.parseCommaInt(value.getString("monsterId2"));
                        int boss3 = value.getInt("bossId3");
                        int[] monster3 = ParseCommon.parseCommaInt(value.getString("monsterId3"));
                        List<DropData> reward1s = ParseCommon.parseSemicolonDropDataList(value.getString("reward_1"));

                        List<DropData> reward2s = ParseCommon.parseSemicolonDropDataList(value.getString("reward_2"));
                        DropData rewardAssist = GameCommon.parseDropData(value.getString("help_reward"));
                        DropData[] rewardBox = ParseCommon.parseSemicolonDropData(value.getString("box_reward"));

                        int laddId = TeamDef.getLaddId(Integer.parseInt(id[0]), Integer.parseInt(id[1]));

                        CombatDungeonData[] monster = new CombatDungeonData[3];
                        monster[0] = new CombatDungeonData(boss1, monster1);
                        monster[1] = new CombatDungeonData(boss2, monster2);
                        monster[2] = new CombatDungeonData(boss3, monster3);

                        tmp.put(laddId, new LADDisasterData(laddId, monster, reward1s, reward2s, rewardAssist, rewardBox));
                    }
                    ladDisasterMap = tmp;
                } catch (IOException e) {
                    log.error("加载生死劫数据出错！");
                    e.printStackTrace();
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return "teambreak";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Map<Byte, CrossDunData> getCrossDunMap() {
        return crossDunMap;
    }

    public static Map<Integer, LADDisasterData> getLADDisasterData() {
        return ladDisasterMap;
    }
}
