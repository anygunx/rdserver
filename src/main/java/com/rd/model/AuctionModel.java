package com.rd.model;

import com.google.common.base.Preconditions;
import com.rd.bean.drop.DropData;
import com.rd.define.EAuction;
import com.rd.model.data.AuctionItemModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 拍卖模板
 * Created by XingYun on 2017/10/24.
 */
public class AuctionModel {

    private static Logger logger = Logger.getLogger(AuctionModel.class);

    /**
     * 战盟税率
     **/
    private static float TAX_GANG = 0.08f;
    /**
     * 全服税率
     **/
    private static float TAX_WORLD = 0.15f;
    /**
     * 日志容量
     **/
    private static int LOG_CAPACITY = 20;
    /**
     * 保护时间
     **/
    private static long PROTECT_TIME = 2 * DateUtil.MINUTE;


    //商城数据
    private static final String AUCTION_PATH = "gamedata/auctionItem.xml";
    private static final String AUCTION_NAME = "auctionModel";
    private static Map<Short, AuctionItemModelData> items;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadItems(path);
    }

    public static void loadItems(String path) {
        final File file = new File(path, AUCTION_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Short, AuctionItemModelData> tmpAuctionMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "item");
                    for (int i = 0; i < elements.length; i++) {
                        short id = Short.parseShort(XmlUtils.getAttribute(elements[i], "id"));
                        DropData drop = StringUtil.getRewardDropData(XmlUtils.getAttribute(elements[i], "reward"));
                        int basePrice = Integer.parseInt(XmlUtils.getAttribute(elements[i], "basePrice"));
                        int addPrice = Integer.parseInt(XmlUtils.getAttribute(elements[i], "addPrice"));
                        int fixedPrice = Integer.parseInt(XmlUtils.getAttribute(elements[i], "fixedPrice"));
                        String timeStr = XmlUtils.getAttribute(elements[i], "keepTime");
                        Map<EAuction, Long> keepTime = new HashMap<>();
                        for (String pair : timeStr.split(StringUtil.SEMIC)) {
                            String[] param = pair.split(StringUtil.COMMA);
                            EAuction auction = EAuction.get(Byte.parseByte(param[0]));
                            Long time = Integer.valueOf(param[1]) * DateUtil.SECOND;
                            keepTime.put(auction, time);
                        }
                        byte subscribe = Byte.parseByte(XmlUtils.getAttribute(elements[i], "subscribe"));
                        AuctionItemModelData data = new AuctionItemModelData(id, drop, basePrice, addPrice, fixedPrice, keepTime, subscribe);
                        tmpAuctionMap.put(id, data);
                    }
                    items = tmpAuctionMap;
                } catch (Exception e) {
                    logger.error("加载拍品数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return AUCTION_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static AuctionItemModelData getData(short id) {
        AuctionItemModelData modelData = items.get(id);
        Preconditions.checkNotNull(modelData, "拍品模板获取失败, 错误的id=" + id);
        return modelData;
    }

    public static float getTaxGang() {
        return TAX_GANG;
    }

    public static float getTaxWorld() {
        return TAX_WORLD;
    }

    public static int getLogCapacity() {
        return LOG_CAPACITY;
    }

    public static long getProtectTime() {
        return PROTECT_TIME;
    }
}
