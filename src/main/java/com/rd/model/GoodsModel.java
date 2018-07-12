package com.rd.model;

import com.rd.bean.comm.BaseRandomData;
import com.rd.bean.goods.Equip;
import com.rd.bean.goods.data.*;
import com.rd.bean.player.SimplePlayer;
import com.rd.define.EAttrType;
import com.rd.define.EGoodsQuality;
import com.rd.model.data.RedModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

public class GoodsModel {

    static Logger log = Logger.getLogger(GoodsModel.class.getName());

    private static Map<Short, EquipData> equipDataMap;
    private static Map<Short, List<EquipData>> equipLvMap;
    private static Map<Short, ItemData> itemDataMap;
    private static Map<Short, BoxData> boxDataMap;
    private static Map<Short, AuctionBoxData> auctionBoxMap;
    private static Map<Short, CallBackGoodsData> callBackGoodsMap;

    private static byte mountPosSize = 0;

    private GoodsModel() {

    }

    public static void loadGoods(String path) {
        loadMasterEquip(path);
//		loadServantEquip(path);
        loadItem(path);
        loadBox(path);
        loadAuctionBox(path);
        loadCallBack(path);
    }

    private static void loadAuctionBox(String path) {
        final File file = new File(path, "gamedata/auctionBox.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, AuctionBoxData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (Element ele : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(ele, "id"));
                        long lastTime = Integer.parseInt(XmlUtils.getAttribute(ele, "lastTime")) * DateUtil.SECOND;
                        String timeStr = XmlUtils.getAttribute(ele, "drop");
                        List<BaseRandomData<Short>> dropList = new ArrayList<>();
                        for (String pair : timeStr.split(StringUtil.SEMIC)) {
                            String[] param = pair.split(StringUtil.COMMA);
                            Short itemId = Short.parseShort(param[0]);
                            Integer weight = Integer.valueOf(param[1]);
                            dropList.add(new BaseRandomData<Short>(itemId, weight) {
                            });
                        }
                        AuctionBoxData data = new AuctionBoxData(id, lastTime, dropList);
                        tmpMap.put(data.getId(), data);
                    }
                    auctionBoxMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载拍卖宝箱模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "auctionBoxModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadMasterEquip(String path) {
        final File file = new File(path, "gamedata/equipment.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, EquipData> tmpMap = new HashMap<>();
                    Map<Short, List<EquipData>> tmpLv = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "equipment");
                    for (int i = 0; i < elements.length; i++) {
                        EquipData data = new EquipData();
                        data.setGoodsId(Short.parseShort(XmlUtils.getAttribute(elements[i], "id")));
                        data.setLevel(Short.parseShort(XmlUtils.getAttribute(elements[i], "level")));
                        data.setOccupation(Byte.parseByte(XmlUtils.getAttribute(elements[i], "occupation")));
                        data.setPosition(Byte.parseByte(XmlUtils.getAttribute(elements[i], "position")));
                        data.setName(XmlUtils.getAttribute(elements[i], "name"));
                        data.setAttr(EAttrType.getAttr(elements[i]));
                        tmpMap.put(data.getGoodsId(), data);
                        if (!tmpLv.containsKey(data.getLevel()))
                            tmpLv.put(data.getLevel(), new ArrayList<>());
                        tmpLv.get(data.getLevel()).add(data);
                    }
                    equipDataMap = tmpMap;
                    equipLvMap = tmpLv;
                } catch (Exception e) {
                    log.error("加载主角装备模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "masterEquipModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadItem(String path) {
        final File file = new File(path, "gamedata/item.json");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                String content;
                try {
                    Map<Short, ItemData> tmpMap = new HashMap<>();
                    content = FileUtils.readFileToString(file, "UTF-8");

                    JSONObject jsonObject = new JSONObject(content);
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        JSONObject value = jsonObject.getJSONObject(key);

                        ItemData data = new ItemData();
                        data.setGoodsId(Short.parseShort(key));
                        data.setName(value.getString("name"));
                        data.setLastTime(value.getInt("lastTime") * 1000);
                        tmpMap.put(data.getGoodsId(), data);
                    }
                    itemDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载道具模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "itemModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadBox(String path) {
        final File file = new File(path, "gamedata/box.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, BoxData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "box");
                    for (Element ele : elements) {
                        BoxData data = new BoxData();
                        data.setName(XmlUtils.getAttribute(ele, "name"));
                        data.setGoodsId(Short.parseShort(XmlUtils.getAttribute(ele, "id")));
                        data.setLevelLimit(Short.parseShort(XmlUtils.getAttribute(ele, "levelLimit")));
                        data.setVipLimit(Byte.valueOf(XmlUtils.getAttribute(ele, "vipLimit")));
                        data.setGainId(Short.valueOf(XmlUtils.getAttribute(ele, "gainId")));
                        data.setStartTime(XmlUtils.getAttribute(ele, "startTime"));
                        data.setEndTime(XmlUtils.getAttribute(ele, "endTime"));
                        data.setType(Byte.valueOf(XmlUtils.getAttribute(ele, "useType")));
                        data.setLastTime(Integer.valueOf(XmlUtils.getAttribute(ele, "lastTime")));
                        String dcs = XmlUtils.getAttribute(ele, "useMax");
                        if (!StringUtil.isEmpty(dcs))
                            data.setDayCount(Integer.valueOf(dcs));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(ele, "rewards")));
                        data.setHaveNum(Integer.parseInt(XmlUtils.getAttribute(ele, "haveNum")));
                        tmpMap.put(data.getGoodsId(), data);
                    }
                    boxDataMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载宝箱模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "boxModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadCallBack(String path) {
        final File file = new File(path, "gamedata/huishou.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, CallBackGoodsData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (Element ele : elements) {
                        CallBackGoodsData data = new CallBackGoodsData();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(ele, "id")));
                        data.setItem(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "item")));
                        data.setReward(StringUtil.getRewardDropData(XmlUtils.getAttribute(ele, "reward")));
                        tmpMap.put(data.getId(), data);
                    }
                    callBackGoodsMap = tmpMap;
                } catch (Exception e) {
                    log.error("加载回收道具模型数据出错...");
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "callBackGoodsModel";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static EquipData getEquipDataById(short id) {
        return equipDataMap.get(id);
    }

    public static int[] getEquipBaseAttrs(byte quality, short id) {
        if (quality <= EGoodsQuality.ORANGE.getValue()) {
            EquipData data = GoodsModel.getEquipDataById(id);
            if (data != null)
                return data.getAttr();
        } else {
            RedModelData data = OrangeModel.getRed(id);
            if (data != null)
                return data.getAttr();
        }
        return new int[EAttrType.ATTR_SIZE];
    }

    public static byte getEquipType(short id, byte quality) {
        byte type = 0;
        if (quality <= EGoodsQuality.ORANGE.getValue()) {
            EquipData data = GoodsModel.getEquipDataById(id);
            if (data != null)
                type = data.getPosition();
        } else {
            RedModelData data = OrangeModel.getRed(id);
            if (data != null)
                type = data.getType();
        }
        return type;
    }

    public static int[] getEquipAddAttrs(Equip equip, double qualityAddRate) {
        int[] addAttrs = new int[EAttrType.ATTR_SIZE];
        if (equip.getQ() <= EGoodsQuality.ORANGE.getValue()) {
            EquipData equipData = GoodsModel.getEquipDataById(equip.getG());
            for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                int value = equipData.getAttr()[i];
                if (value > 0) {
                    addAttrs[i] = (int) (value * qualityAddRate * equip.getF() / 100);
                }
            }
        } else {
            RedModelData data = OrangeModel.getRed(equip.getG());
            if (data != null)
                return data.getAddAttr();
        }
        return addAttrs;
    }

    public static BoxData getBoxDataById(short id) {
        return boxDataMap.get(id);
    }

    public static short getEquipId(short level, byte occupation, byte position) {
        for (EquipData data : equipDataMap.values()) {
            if (data.getLevel() == level && data.getOccupation() == occupation && data.getPosition() == position) {
                return data.getGoodsId();
            }
        }
        return 0;
    }

    public static ItemData getItemDataById(short id) {
        return itemDataMap.get(id);
    }

    public static List<CallBackGoodsData> getCallBackGoods() {
        return new ArrayList<CallBackGoodsData>(callBackGoodsMap.values());
    }

    public static EquipData getRandomDataByLv(SimplePlayer sp) {
        return getRandomDataByLv(sp.getLvConvert());
    }

    public static EquipData getRandomDataByLv(short lvConvert) {
        List<EquipData> list = equipLvMap.get(lvConvert);
        if (list == null || list.size() == 0)
            return null;
        return list.get(GameUtil.getRangedRandom(0, list.size() - 1));
    }

    public static EquipData getEquipData(int lv, byte type, byte occ) {
        List<EquipData> list = equipLvMap.get((short) lv);
        for (EquipData data : list) {
            if (data.getPosition() == type && data.getOccupation() == occ)
                return data;
        }
        return null;
    }

    public static byte getMountPosSize() {
        return mountPosSize;
    }

    public static AuctionBoxData getAuctionBox(Short id) {
        return auctionBoxMap.get(id);
    }
}
