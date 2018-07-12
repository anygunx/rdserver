package com.rd.model;

import com.rd.common.goods.EGoodsType;
import com.rd.define.EGoodsQuality;
import com.rd.model.data.ShopItemData;
import com.rd.model.data.ShopShenMi;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城数据
 *
 * @author Created by U-Demon on 2016年11月5日 上午11:55:51
 * @version 1.0.0
 */
public class ShopModel {

    private static Logger logger = Logger.getLogger(ShopModel.class);

    //刷新商城最大可购买数量
    public static final int BUY_MAX = 1;
    //刷新商城间隔
    public static final long REFRESH_SPACE = 4 * DateUtil.HOUR;
    //刷新商城物品总数
    public static final int SELL_MAX = 6;
    //出现装备数量的分布
    public static final int[] SELL_EQUIP_NUM = {0, 0, 5, 80, 10, 5};
    public static final byte SELL_EQUIP_QUALITY = EGoodsQuality.PURPLE.getValue();
    //打折几率
    public static final Map<Integer, Integer> DISCOUNT_RATES = new HashMap<Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(50, 10);
            put(80, 20);
            put(100, 70);
        }
    };


    //商城数据
    private static final String SHOP_PATH = "gamedata/shop.xml";
    private static final String SHOP_NAME = "shopModel";
    private static Map<Byte, List<ShopItemData>> shopTypeMap;
    private static Map<Integer, ShopItemData> shopMap;

    //神秘商城数据
    private static final String SHOP_SHENMI_PATH = "gamedata/shenmishop.xml";
    private static final String SHOP_SHENMI_NAME = "shenmishopModel";
    private static Map<Short, ShopShenMi> shenMiMap;
    private static Map<Integer, Integer> goodsRates;
    private static Map<Short, ShopShenMi> equipMap;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadShop(path);
        loadShenMi(path);
    }

    public static void loadShop(String path) {
        final File file = new File(path, SHOP_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, List<ShopItemData>> tmpMap = new HashMap<>();
                    Map<Integer, ShopItemData> tmpShopMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shop");
                    for (int i = 0; i < elements.length; i++) {
                        ShopItemData data = new ShopItemData();
                        data.setShopType(Byte.valueOf(XmlUtils.getAttribute(elements[i], "shopType")));
                        data.setId(Integer.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setItem(StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "item")));
                        data.setPrice(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "price")));
                        if (tmpMap.get(data.getShopType()) == null)
                            tmpMap.put(data.getShopType(), new ArrayList<ShopItemData>());
                        tmpMap.get(data.getShopType()).add(data);
                        tmpShopMap.put(data.getId(), data);
                    }
                    shopTypeMap = tmpMap;
                    shopMap = tmpShopMap;
                } catch (Exception e) {
                    logger.error("加载普通商城数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return SHOP_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadShenMi(String path) {
        final File file = new File(path, SHOP_SHENMI_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, ShopShenMi> tmpMap = new HashMap<>();
                    Map<Integer, Integer> tmpRates = new HashMap<>();
                    Map<Short, ShopShenMi> tmpEquip = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "shop");
                    for (int i = 0; i < elements.length; i++) {
                        ShopShenMi data = new ShopShenMi();
                        data.setId(Short.valueOf(XmlUtils.getAttribute(elements[i], "id")));
                        data.setItemType(Byte.valueOf(XmlUtils.getAttribute(elements[i], "itemType")));
                        data.setItemId(Short.valueOf(XmlUtils.getAttribute(elements[i], "modelId")));
                        data.setItemNum(Integer.valueOf(XmlUtils.getAttribute(elements[i], "num")));
                        data.setGold(Integer.valueOf(XmlUtils.getAttribute(elements[i], "goldprice")));
                        data.setDiamond(Integer.valueOf(XmlUtils.getAttribute(elements[i], "yuanbaoprice")));
                        data.setRate(Integer.valueOf(XmlUtils.getAttribute(elements[i], "gailv")));
                        String discount = XmlUtils.getAttribute(elements[i], "discount");
                        if (!StringUtil.isEmpty(discount)) {
                            data.setDiscount(Byte.parseByte(discount));
                        }
                        tmpMap.put(data.getId(), data);
                        //装备
                        if (data.getItemType() == EGoodsType.EQUIP.getId())
                            tmpEquip.put(data.getItemId(), data);
                        else
                            tmpRates.put((int) data.getId(), data.getRate());
                    }
                    shenMiMap = tmpMap;
                    goodsRates = tmpRates;
                    equipMap = tmpEquip;
                } catch (Exception e) {
                    logger.error("加载神秘商城数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return SHOP_SHENMI_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static ShopItemData getShopItem(int id) {
        return shopMap.get(id);
    }

    public static List<ShopItemData> getShopList(byte shopType) {
        return shopTypeMap.get(shopType);
    }

    public static ShopShenMi getShenMiData(int id) {
        return shenMiMap.get((short) id);
    }

    public static ShopShenMi getShopEquip(short itemId) {
        return equipMap.get(itemId);
    }

    public static Map<Integer, Integer> getGoodsRates() {
        return goodsRates;
    }

}
