package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.model.data.copy.NSJGCopyData;
import com.rd.model.data.copy.cailiao.NCaiLiaoCopyData;
import com.rd.model.data.copy.geren.NGeRenBossData;
import com.rd.model.data.copy.mizang.NMiZangCopyData;
import com.rd.model.data.copy.mizang.NMiZangStarData;
import com.rd.model.data.copy.tianmen.NTianMenDBData;
import com.rd.model.data.copy.tianmen.NTianMenData;
import com.rd.model.data.copy.zhongkui.NZhongKuiData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 副本
 *
 * @author MyPC
 */
public class NCopyModel {
    static Logger log = Logger.getLogger(NCopyModel.class.getName());
    private static Map<Integer, NCaiLiaoCopyData> cLCopyDataMap;
    private static Map<Integer, NSJGCopyData> sjgCopy;
    private static Map<Integer, NTianMenData> tmCopy;
    private static Map<Integer, NTianMenDBData> tmdbCopy;
    private static List<NTianMenDBData> tmdbCopyList;
    private static Map<Integer, NMiZangCopyData> mZCopyMap;
    private static Map<Integer, List<NMiZangCopyData>> mZCopyMapStarList;

    private static Map<Integer, NMiZangStarData> mZStarCopyMap;

    private static Map<Short, NGeRenBossData> geRenCopy;
    private static Map<Integer, NGeRenBossData> geRenCopyIdMap;

    private static Map<Integer, NZhongKuiData> zhongkuiMap;

    public static void loadData(String path) {
        loadDungeonMaterial(path);
        loadSJGCopy(path);
        loadTianMenCopy(path);
        loadTianMenDaBiaoCopy(path);
        loadGeRenBossCopy(path);
        loadMiZangCopy(path);
        loadMiZangStar(path);
        loadzhongkuiCopy(path);

    }


    private static void loadDungeonMaterial(String path) {
        final File file = new File(path, "gamedata/cailiaocopy.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NCaiLiaoCopyData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        short needLv = (short) (value.getInt("need_lv"));//("need_lv");

                        int boosId = value.getInt("bossId");
                        String Monsterid = value.getString("monsterId");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        short price = (short) (value.getInt("price"));
                        Map<Byte, Byte> numMap = new HashMap<>();
                        String nums = value.getString("nums");
                        if (nums != null && nums.trim().length() > 0) {
                            String[] numStrs = nums.split(";");
                            for (String str : numStrs) {
                                String[] s = str.split(",");
                                numMap.put(Byte.parseByte(s[0]), Byte.parseByte(s[1]));
                            }


                        }
                        NCaiLiaoCopyData data =
                                new NCaiLiaoCopyData(id, needLv, boosId, arry, reward, price, numMap);


                        temp.put(id, data);
                    }

                    cLCopyDataMap = temp;
                } catch (IOException e) {
                    log.error("加载材料副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "dungeonMaterialmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadSJGCopy(String path) {
        final File file = new File(path, "gamedata/shuijinggong.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NSJGCopyData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        Integer id = Integer.parseInt(key);
                        int boosId = value.getInt("bossid");
                        String Monsterid = value.getString("monsterid");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        NSJGCopyData data =
                                new NSJGCopyData(id, boosId, arry, reward);
                        temp.put(id, data);
                    }

                    sjgCopy = temp;
                } catch (IOException e) {
                    log.error("加载水晶宫副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "copymodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadTianMenCopy(String path) {
        final File file = new File(path, "gamedata/tianmen.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NTianMenData> temp = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int boosId = value.getInt("bossid");
                        String Monsterid = value.getString("monsterid");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        NTianMenData data =
                                new NTianMenData(id, boosId, arry, reward);
                        temp.put(id, data);
                    }

                    tmCopy = temp;
                } catch (IOException e) {
                    log.error("加载天门副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "tianmencopymodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadTianMenDaBiaoCopy(String path) {
        final File file = new File(path, "gamedata/tianmendabiao.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NTianMenDBData> temp = new HashMap<>();
                List<NTianMenDBData> tempList = new ArrayList<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        int copyId = value.getInt("copy");
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        NTianMenDBData data =
                                new NTianMenDBData(id, copyId, reward);
                        temp.put(id, data);
                        tempList.add(data);
                    }

                    tmdbCopy = temp;
                    tmdbCopyList = tempList;
                    Collections.sort(tmdbCopyList);
                } catch (IOException e) {
                    log.error("加载天门达标副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "tianmendabiaocopymodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadMiZangCopy(String path) {
        final File file = new File(path, "gamedata/mizangcopy.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NMiZangCopyData> temp = new HashMap<>();

                Map<Integer, List<NMiZangCopyData>> tempList = new HashMap<>();
                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);


                        int id = Integer.parseInt(key);

                        int type = value.getInt("mission");
                        int boosId = value.getInt("bossid");
                        String Monsterid = value.getString("monsterid");
                        int level = value.getInt("lv");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));
                        List<DropData> first = GameCommon.parseDropDataList(value.getString("first"));
                        NMiZangCopyData data =
                                new NMiZangCopyData(type, id, boosId, reward, first, arry, level);
                        temp.put(id, data);
                        List<NMiZangCopyData> list = tempList.get(type);
                        if (list == null) {
                            list = new ArrayList<>();
                            tempList.put(type, list);
                        }
                        list.add(data);

                    }
                    mZCopyMap = temp;
                    mZCopyMapStarList = tempList;

                    for (List<NMiZangCopyData> te : tempList.values()) {
                        Collections.sort(te);

                    }

                } catch (IOException e) {
                    log.error("加载密藏副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "tianmendabiaocopymodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    private static void loadMiZangStar(String path) {
        final File file = new File(path, "gamedata/mizangstar.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                Map<Integer, NMiZangStarData> temp = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);
                        int id = Integer.parseInt(key);
                        byte star_1 = (byte) (value.getInt("star_1"));
                        byte star_2 = (byte) (value.getInt("star_2"));
                        byte star_3 = (byte) (value.getInt("star_3"));

                        List<DropData> reward_1 = GameCommon.parseDropDataList(value.getString("reward_1"));
                        List<DropData> reward_2 = GameCommon.parseDropDataList(value.getString("reward_2"));
                        List<DropData> reward_3 = GameCommon.parseDropDataList(value.getString("reward_3"));
                        Map<Byte, List<DropData>> map = new HashMap<>();
                        map.put(star_1, reward_1);
                        map.put(star_2, reward_2);
                        map.put(star_3, reward_3);

                        NMiZangStarData data =
                                new NMiZangStarData(id, map);
                        temp.put(id, data);
                    }

                    mZStarCopyMap = temp;
                } catch (IOException e) {
                    log.error("加载密藏副本Star数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "mizangstarcopymodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadGeRenBossCopy(String path) {
        final File file = new File(path, "gamedata/gerenboss.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            //id":"1","need_lv":30,"bossID":1001,"monsterID":"1,1,1,1,1,
            //1,1,1,1","order":1,"show":"2,12,30","reward"
            @Override
            public void onResourceChange(File file) {
                Map<Short, NGeRenBossData> temp = new HashMap<>();
                Map<Integer, NGeRenBossData> tempId = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);
                        short needLv = (short) (value.getInt("need_lv"));//("need_lv");

                        int boosId = value.getInt("bossID");
                        String Monsterid = value.getString("monsterID");
                        //byte order=(byte)value.getInt("order");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));


                        NGeRenBossData data =
                                new NGeRenBossData(id, needLv, boosId, arry, reward);


                        temp.put(needLv, data);
                        tempId.put(id, data);
                    }

                    geRenCopy = temp;
                    geRenCopyIdMap = tempId;
                } catch (IOException e) {
                    log.error("加载个人副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "dungeonMaterialmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    private static void loadzhongkuiCopy(String path) {
        final File file = new File(path, "gamedata/zhongkuicopy.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            //id":"1","need_lv":30,"bossID":1001,"monsterID":"1,1,1,1,1,
            //1,1,1,1","order":1,"show":"2,12,30","reward"
            @Override
            public void onResourceChange(File file) {

                Map<Integer, NZhongKuiData> tempId = new HashMap<>();

                String content;
                try {
                    content = FileUtils.readFileToString(file, "UTF-8");
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        int id = Integer.parseInt(key);


                        int boosId = value.getInt("bossid");
                        int exp = value.getInt("exp");
                        String Monsterid = value.getString("monsterid");
                        //byte order=(byte)value.getInt("order");
                        int[] arry = null;
                        if (Monsterid != null && Monsterid.trim().length() > 0) {
                            arry = StringUtil.getIntList(Monsterid);
                        }
                        List<DropData> reward = GameCommon.parseDropDataList(value.getString("reward"));

                        NZhongKuiData data =
                                new NZhongKuiData(id, boosId, arry, exp, reward);


                        tempId.put(id, data);
                    }


                    zhongkuiMap = tempId;
                } catch (IOException e) {
                    log.error("加载钟馗副本数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "dungeonMaterialmodel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }


    public static NCaiLiaoCopyData getCLCopyDataMap(int id) {
        return cLCopyDataMap.get(id);
    }

    public static NSJGCopyData getSJGCopyDataMap(int id) {
        return sjgCopy.get(id);
    }

    public static NTianMenData getTianMenCopyDataMap(int id) {
        return tmCopy.get(id);
    }

    public static Map<Integer, NTianMenData> getTianMenMap() {
        return tmCopy;
    }

    public static NTianMenDBData getTianMenDBCopyDataMap(int copyid) {
        return tmdbCopy.get(copyid);
    }

    public static Map<Integer, NTianMenDBData> getTianMenDBCopyDataMap() {
        return tmdbCopy;
    }

    public static List<NTianMenDBData> getNTianMenDBDataList() {
        return tmdbCopyList;
    }

    public static NGeRenBossData getNGeRenBossDataByLevel(short level) {
        return geRenCopy.get(level);
    }

    public static NGeRenBossData getNGeRenBossDataById(int copyId) {
        return geRenCopyIdMap.get(copyId);
    }


    public static NMiZangCopyData getNMiZangCopyDataById(int copyId) {
        return mZCopyMap.get(copyId);
    }

    public static Map<Integer, NMiZangCopyData> getNMiZangCopyDataMap() {
        return mZCopyMap;
    }

    public static NMiZangStarData getNMiZangStarDataById(int copyId) {
        return mZStarCopyMap.get(copyId);
    }

    public static Map<Integer, NMiZangStarData> getNMiZangStarDataMap() {
        return mZStarCopyMap;
    }


    public static List<NMiZangCopyData> getNMiZangCopyDataByStarId(int startId) {
        return mZCopyMapStarList.get(startId);
    }


    public static NZhongKuiData getNZhongKuiDataById(int id) {

        return zhongkuiMap.get(id);
    }


    public static Map<Integer, NZhongKuiData> getNZhongKuiDataMap() {

        return zhongkuiMap;
    }


}
