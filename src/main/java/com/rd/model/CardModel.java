package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.define.EAttrType;
import com.rd.model.data.CardModelData;
import com.rd.model.data.CardSuitLvModelData;
import com.rd.model.data.CardSuitModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 卡牌
 */
public class CardModel {
    private static final Logger logger = Logger.getLogger(CardModel.class);

    private static final String CARD_PATH = "gamedata/card.xml";
    private static final String CARD_NAME = "cardModel";
    /**
     * 卡牌模板数据
     **/
    private static Map<Short, CardModelData> cardMap;

    private static final String CARD_SUIT_LV_PATH = "gamedata/cardSuitlv.xml";
    private static final String CARD_SUIT_LV_NAME = "cardSuitLvModel";
    /**
     * 卡牌套装-等级模板数据
     **/
    private static Map<Short, Map<Byte, CardSuitLvModelData>> suitLvMap;

    private static final String CARD_SUIT_PATH = "gamedata/cardlist.xml";
    private static final String CARD_SUIT_NAME = "cardlistModel";
    /**
     * 卡牌套装-等级模板数据
     **/
    private static Map<Short, CardSuitModelData> suitMap;

    /**
     * 卡牌类型-套装 mapping
     **/
    private static Map<Short, Short> card2suitMap;

    private static int[] hp;
    private static int[] attack;
    private static int[] phyDef;
    private static int[] magicDef;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadCardData(path);
        loadCardSuit(path);
        loadCardSuitLv(path);
        loadCardAttr();
    }

    private static void loadCardSuit(String path) {
        final File file = new File(path, CARD_SUIT_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, CardSuitModelData> tmpMap = new HashMap<>();
                    Map<Short, Short> card2suitTmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "cardlist");

                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        Set<Short> formula = StringUtil.getShortSet(XmlUtils.getAttribute(element, "formula"), ",");
                        CardSuitModelData data = new CardSuitModelData(id, formula);
                        tmpMap.put(id, data);
                        for (Short cardId : formula) {
                            card2suitTmpMap.put(cardId, id);
                        }
                    }
                    suitMap = tmpMap;
                    card2suitMap = card2suitTmpMap;
                } catch (Exception e) {
                    logger.error("加载卡牌套装数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return CARD_SUIT_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCardSuitLv(String path) {
        final File file = new File(path, CARD_SUIT_LV_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, Map<Byte, CardSuitLvModelData>> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");

                    for (Element element : elements) {
                        short id = Short.parseShort(XmlUtils.getAttribute(element, "id"));
                        byte lv = Byte.parseByte(XmlUtils.getAttribute(element, "lv"));
                        short cardLv = Short.parseShort(XmlUtils.getAttribute(element, "cardlv"));
                        Double cardAddition = Double.parseDouble(XmlUtils.getAttribute(element, "carAddition")) / 10000;
                        int[] attr = EAttrType.getIntAttr(element);
                        CardSuitLvModelData data = new CardSuitLvModelData(id, lv, cardLv, cardAddition, attr);
                        if (!tmpMap.containsKey(id)) {
                            tmpMap.put(id, new HashMap<>());
                        }
                        tmpMap.get(id).put(lv, data);
                    }
                    suitLvMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载卡牌套装等级数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return CARD_SUIT_LV_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCardData(String path) {
        final File file = new File(path, CARD_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, CardModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "card");

                    for (Element element : elements) {
                        short id = Byte.parseByte(XmlUtils.getAttribute(element, "id"));
                        byte pos = Byte.parseByte(XmlUtils.getAttribute(element, "pos"));
                        List<DropData> cost = GameCommon.parseDropDataList(XmlUtils.getAttribute(element, "cost"));
                        CardModelData data = new CardModelData(id, pos, cost);
                        tmpMap.put(data.getId(), data);
                    }
                    cardMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载卡牌数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return CARD_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadCardAttr() {
        hp = new int[ConstantModel.CARD_MAX_LEVEL];
        attack = new int[ConstantModel.CARD_MAX_LEVEL];
        phyDef = new int[ConstantModel.CARD_MAX_LEVEL];
        magicDef = new int[ConstantModel.CARD_MAX_LEVEL];
        for (int lv = 0; lv < ConstantModel.CARD_MAX_LEVEL; ++lv) {
            hp[lv] = 10000 + ((int) ((lv - 1) / 5.0D + 1)) * 3400 + (lv - ((int) ((lv - 1) / 5.0D + 1))) * 2975;
            attack[lv] = (int) (hp[lv] / 5.0D);
            phyDef[lv] = (int) (hp[lv] / 10.0D);
            magicDef[lv] = (int) (hp[lv] / 10.0D);
        }
    }

    public static CardModelData getCard(short id) {
        return cardMap.get(id);
    }

    public static Set<Short> getSuitsId() {
        return suitMap.keySet();
    }

    public static CardSuitLvModelData getSuitLvData(short id, byte lv) {
        Map<Byte, CardSuitLvModelData> lvMap = getSuitLvMap(id);
        if (lvMap == null) {
            return null;
        }
        return lvMap.get(lv);
    }

    public static Map<Byte, CardSuitLvModelData> getSuitLvMap(short id) {
        return suitLvMap.get(id);
    }

    /**
     * 获取卡牌所在套装
     *
     * @param cardId
     * @return
     */
    public static short getSuitFromCard(short cardId) {
        return card2suitMap.get(cardId);
    }

    public static CardSuitModelData getSuit(short id) {
        return suitMap.get(id);
    }

    public static int[] getHp() {
        return hp;
    }

    public static int[] getAttack() {
        return attack;
    }

    public static int[] getPhyDef() {
        return phyDef;
    }

    public static int[] getMagicDef() {
        return magicDef;
    }
}
