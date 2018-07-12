package com.rd.model;

import com.rd.bean.fighter.FighterData;
import com.rd.bean.fighter.HeroData;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.define.GameDefine;
import com.rd.define.SkillDefine;
import com.rd.model.data.PvpRankData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FighterModel {

    static Logger log = Logger.getLogger(FighterModel.class.getName());

    private static Map<Byte, HeroData> heroDataMap;

    private static Map<Short, FighterData> fighterDataMap;

    private static List<PvpRankData> pvpRankDataList;

    private FighterModel() {

    }

    public static void loadData(String path) {
        loadFighter(path);
        loadPvpRank(path);
    }

    private static void loadFighter(String path) {
        final File file = new File(path, "gamedata/fighter.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, HeroData> tmpMap1 = new HashMap<>();
                    Map<Short, FighterData> tmpMap2 = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "fighter");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        if (id <= GameDefine.OCCUPATION_NUM) {
                            HeroData data = new HeroData();
                            data.setId((byte) (id - 1));
                            data.setAttr(EAttrType.getShortAttr(elements[i]));
                            String[] arrSkill = (XmlUtils.getAttribute(elements[i], "skill")).split(",");
                            byte[] arrTemp = new byte[SkillDefine.SKILL_NUM];
                            for (int j = arrSkill.length - 1; j >= 0; --j) {
                                arrTemp[SkillDefine.SKILL_NUM - 1 - j] = Byte.parseByte(arrSkill[j]);
                            }
                            data.setArrSkillId(arrTemp);
                            tmpMap1.put(data.getId(), data);
                        } else {
                            FighterData data = new FighterData();
                            data.setId(id);
                            data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                            data.setName(XmlUtils.getAttribute(elements[i], "name"));
                            data.setHp(Long.parseLong(XmlUtils.getAttribute(elements[i], "hp")));
                            data.setAtk(Integer.parseInt(XmlUtils.getAttribute(elements[i], "attack")));
                            data.setPower(Long.parseLong(XmlUtils.getAttribute(elements[i], "power")));
                            tmpMap2.put(data.getId(), data);
                        }
                    }
                    heroDataMap = tmpMap1;
                    fighterDataMap = tmpMap2;
                } catch (Exception e) {
                    log.error("加载战士模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "fighterModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadPvpRank(String path) {
        final File file = new File(path, "gamedata/zaoyubang.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    List<PvpRankData> tempList = new ArrayList<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        PvpRankData data = new PvpRankData();
                        data.setRankMin(Short.parseShort(XmlUtils.getAttribute(elements[i], "rankMin")));
                        data.setRankMax(Short.parseShort(XmlUtils.getAttribute(elements[i], "rankMax")));
                        data.setRewardList(GameCommon.parseDropDataList(XmlUtils.getAttribute(elements[i], "reward")));
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "content"));
                        tempList.add(data);
                    }
                    pvpRankDataList = tempList;
                } catch (Exception e) {
                    log.error("加载遭遇战排行榜模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "pvpRankModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static HeroData getHeroDataById(byte id) {
        return heroDataMap.get(id);
    }

    public static FighterData getFighterDataById(short id) {
        return fighterDataMap.get(id);
    }

    public static PvpRankData getPvpRankData(int rank) {
        for (PvpRankData data : pvpRankDataList) {
            if (data.getRankMin() <= rank && rank <= data.getRankMax())
                return data;
        }
        return pvpRankDataList.get(pvpRankDataList.size() - 1);
    }
}
