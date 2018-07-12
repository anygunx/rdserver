package com.rd.game;

import com.rd.bean.auction.AuctionItemData;
import com.rd.bean.auction.BaseAuction;
import com.rd.common.GameCommon;
import com.rd.dao.AuctionDao;
import com.rd.define.EAuction;
import com.rd.define.EAuctionItemType;
import com.rd.model.AuctionModel;
import com.rd.model.data.AuctionItemModelData;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 拍卖服务
 * Created by XingYun on 2017/10/24.
 */
public class AuctionService {
    private static final Logger logger = Logger.getLogger(AuctionService.class);
    private static AuctionDao dao = new AuctionDao();

    private static Map<EAuction, BaseAuction> shops = new HashMap<>();

    public static void init() {
        Map<EAuction, BaseAuction> shopDatas = dao.getShops();

        for (EAuction type : EAuction.values()) {
            BaseAuction auction = shopDatas.putIfAbsent(type, type.builder.build());
            if (auction == null) {
                dao.createAuction(shopDatas.get(type));
            }
        }
        shops = shopDatas;
    }

    /**
     * 创建拍品
     *
     * @param itemType
     * @param itemList
     * @param owner
     * @return
     */
    public static List<AuctionItemData> createAuctionItem(EAuctionItemType itemType, List<Short> itemList, int owner) {
        Set<Integer> ownerList = new HashSet<>();
        ownerList.add(owner);
        return createAuctionItem(itemType, itemList, ownerList);
    }

    /**
     * 创建指定拍品
     *
     * @param itemType
     * @param itemList
     * @param ownerList
     */
    public static List<AuctionItemData> createAuctionItem(EAuctionItemType itemType, List<Short> itemList, Set<Integer> ownerList) {
        List<AuctionItemData> list = new ArrayList<>();
        for (Short id : itemList) {
            // 只处理拍品
            list.add(createAuctionItem(itemType, id, ownerList));
        }
        return list;
    }

    /**
     * 创建拍品
     *
     * @param itemType
     * @param itemId
     * @param owner
     * @return
     */
    public static AuctionItemData createAuctionItem(EAuctionItemType itemType, short itemId, int owner) {
        Set<Integer> owners = new HashSet<>();
        owners.add(owner);
        return createAuctionItem(itemType, itemId, owners);
    }

    public static AuctionItemData createAuctionItem(EAuctionItemType itemType, short itemId, Set<Integer> owners) {
        AuctionItemModelData modelData = AuctionModel.getData(itemId);
        long uid = GameCommon.generateId();
        long protectTime = System.currentTimeMillis() + AuctionModel.getProtectTime();

        return new AuctionItemData(itemType, uid, itemId, owners, protectTime, modelData.getBasePrice());
    }

    public static void addItem(EAuction auction, AuctionItemData item) {
        AuctionItemModelData modelData = AuctionModel.getData(item.getModelId());
        long deadline = item.getProtectTime() + modelData.getKeepTime(auction);
        item.setDeadline(deadline);
        BaseAuction shop = getShop(auction);
        shop.addItem(item);

        logger.info("拍卖行" + auction.id + " 上架拍品" + item);
    }

    /**
     * 创建单个拍品
     *
     * @param auction
     * @param itemList
     */
    public static void addItems(EAuction auction, List<AuctionItemData> itemList) {
        for (AuctionItemData item : itemList) {
            addItem(auction, item);
        }
    }

    public static BaseAuction getShop(byte id) {
        EAuction type = EAuction.get(id);
        return getShop(type);
    }

    private static BaseAuction getShop(EAuction type) {
        return shops.get(type);
    }

    public static void onTick(long currentTime) {
        for (BaseAuction auction : shops.values()) {
            auction.update(currentTime);
            if (auction.isDirty()) {
                auction.clearDirty();
                dao.updateAuction(auction);
            }
        }
    }
    // TODO onClose可以再save一遍

}
