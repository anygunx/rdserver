package com.rd.bean.auction;

import com.google.common.reflect.TypeToken;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.TextDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.AuctionModel;
import com.rd.model.data.AuctionItemModelData;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 拍卖行基类
 * Created by XingYun onFailed 2017/10/24.
 */
public abstract class BaseAuction {
    protected static final Logger logger = Logger.getLogger(BaseAuction.class);
    /**
     * 固定消耗类型
     **/
    protected static final EGoodsType COST_TYPE = EGoodsType.DIAMOND;

    /**
     * 拍卖行ID
     **/
    protected byte id;
    /**
     * 拍品数据
     **/
    protected ConcurrentHashMap<Long, AuctionItemData> items;
    /**
     * 拍卖记录 容量见AuctionModel.getLogCapacity()
     **/
    protected ConcurrentLinkedQueue<AuctionLog> logs;
    /**
     * 脏数据标识
     **/
    protected volatile boolean dirty;

    /**
     * 创建拍卖行
     *
     * @param id
     */
    public BaseAuction(byte id) {
        this.id = id;
        this.items = new ConcurrentHashMap<>();
        this.logs = new ConcurrentLinkedQueue<>();
    }

    /**
     * 初始化拍卖行数据
     *
     * @param items
     * @param logs
     */
    public void init(String items, String logs) {
        Map<Long, AuctionItemData> tmpMap = StringUtil.gson2Map(items, new TypeToken<Map<Long, AuctionItemData>>() {
        });
        this.items = new ConcurrentHashMap<>(tmpMap);
        List<AuctionLog> logMap = StringUtil.gson2List(logs, new TypeToken<List<AuctionLog>>() {
        });
        this.logs = new ConcurrentLinkedQueue<>();
        this.logs.addAll(logMap);
        this.dirty = false;
    }

    public byte getId() {
        return id;
    }

    public String getItemsStr() {
        return StringUtil.obj2Gson(items);
    }

    public String getLogsStr() {
        return StringUtil.obj2Gson(logs);
    }

    /**
     * 新增拍品
     *
     * @param item
     */
    public void addItem(AuctionItemData item) {
        this.items.put(item.getId(), item);
        this.setDirty();
    }

    /**
     * 移除拍品
     * 注：须要外部同步
     *
     * @param item
     * @return 获取并成功移除
     */
    private boolean removeItem(AuctionItemData item) {
        boolean result = this.items.remove(item.getId()) != null;
        if (result) {
            this.setDirty();
            AuctionLog log = new AuctionLog(item, System.currentTimeMillis());
            addLog(log);
            logger.info("拍卖行" + id + " 移除拍品" + item);
        }
        return result;
    }

    /**
     * 设置脏标识
     **/
    private void setDirty() {
        this.dirty = true;
    }

    /**
     * 是否脏数据
     *
     * @return
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * 清理脏数据标识
     */
    public void clearDirty() {
        this.dirty = false;
    }

    public void getItemsMessage(byte shopId, GameRole gameRole, Message message) {
        message.setByte(shopId);
        message.setShort(items.size());
        for (AuctionItemData item : items.values()) {
            item.getMessage(gameRole.getPlayerId(), message);
        }
    }

    public void getLogsMessage(byte shopId, GameRole gameRole, Message message) {
        message.setByte(shopId);
        message.setByte(logs.size());
        for (AuctionLog log : logs) {
            log.getMessage(gameRole.getPlayerId(), message);
        }
    }

    public void getIncomeMessage(GameRole gameRole, Message message) {
        List<AuctionItemData> itemList = getItemsWithBidder(gameRole.getPlayerId());
        message.setByte(id);
        message.setByte(itemList.size());
        for (AuctionItemData item : itemList) {
            item.getMessage(gameRole.getPlayerId(), message);
            message.setInt(item.getReward(getTax()));
        }
    }

    /**
     * 获取指定玩家被出价的物品
     *
     * @param playerId
     * @return
     */
    public List<AuctionItemData> getItemsWithBidder(int playerId) {
        List<AuctionItemData> itemList = new ArrayList<>();
        for (AuctionItemData item : items.values()) {
            if (!item.containOwner(playerId)) {
                continue;
            }
            if (item.getBidder().getPlayerId() <= 0) {
                continue;
            }
            itemList.add(item);
        }
        return itemList;
    }

    protected AuctionItemData getItem(long uid) {
        return items.get(uid);
    }

    /**
     * 竞拍
     *
     * @param role
     * @param uid
     * @param targetPrice 客户端显示价格，用于校对
     * @return
     */
    public boolean addPrice(GameRole role, long uid, int targetPrice) {
        AuctionItemData item = getItem(uid);
        if (item == null) {
            return false;
        }
        AuctionItemModelData modelData = AuctionModel.getData(item.getModelId());
        if (targetPrice >= modelData.getFixedPrice()) {
            targetPrice = modelData.getFixedPrice();
        }
        // 保护中
        if (item.getProtectTime() > System.currentTimeMillis()) {
            return false;
        }
        Bidder lastBidder = item.getBidder();
        // 加价
        if (!doAddPrice(role, item, modelData, targetPrice)) {
            return false;
        }
        // 退还失败的金额
        onFailed(lastBidder);

        Bidder currentBidder = item.getBidder();
        // 处理当前用户一口价拍下
        if (currentBidder.getPlayerId() == role.getPlayerId()
                && currentBidder.getBid() == modelData.getFixedPrice()) {
            onWin(role, item);
        }
        return true;
    }

    /**
     * 竞价失败返还
     *
     * @param bidder
     */
    private void onFailed(Bidder bidder) {
        if (bidder.getPlayerId() <= 0) {
            return;
        }
        TaskManager.getInstance().scheduleTask(ETaskType.LOGIC, new Task() {
            @Override
            public void run() {
                try {
                    // 竞价人获得物品
                    DropData dropData = new DropData(COST_TYPE, 0, bidder.getBid());
                    Mail mail = MailService.createMail(TextDefine.AUCTION_RETURN_TITLE, TextDefine.AUCTION_RETURN_CONTENT, EGoodsChangeType.AUCTION_BUY_ADD, dropData);
                    MailService.sendSystemMail(bidder.getPlayerId(), mail);
                } catch (Exception e) {
                    logger.error("竞拍失败返款错误：" + bidder);
                    logger.error(e.getMessage());
                }
            }

            @Override
            public String name() {
                return "auctionReturnTask";
            }
        });
    }

    /**
     * 竞得物品
     *
     * @param winner 竞标胜出玩家
     * @param item   拍品
     */
    private void onWin(GameRole winner, AuctionItemData item) {
        AuctionItemModelData modelData = AuctionModel.getData(item.getModelId());

        // TODO  重要log打点
        TaskManager.getInstance().scheduleTask(ETaskType.LOGIC, new Task() {
            @Override
            public void run() {
                try {
                    // 竞价人获得物品
                    Mail mail = MailService.createMail(TextDefine.AUCTION_WIN_TITLE, TextDefine.AUCTION_WIN_CONTENT, EGoodsChangeType.AUCTION_BUY_ADD, modelData.getGoods());
                    MailService.sendSystemMail(winner.getPlayerId(), mail);
                } catch (Exception e) {
                    logger.error("竞价胜出物品发送失败：winner=" + winner.getPlayerId() + ", item=" + item);
                    logger.error(e.getMessage());
                }

                int reward = item.getReward(getTax());
                // 拍卖人获得收益
                for (int playerId : item.getOwners()) {
                    try {
                        DropData rewardData = new DropData(COST_TYPE, 0, reward);
                        Mail mail = MailService.createMail(TextDefine.AUCTION_SALE_TITLE, TextDefine.AUCTION_SALE_CONTENT, EGoodsChangeType.AUCTION_SALE_ADD, rewardData);
                        MailService.sendSystemMail(playerId, mail);
                    } catch (Exception e) {
                        logger.error("拍卖物品收益发送失败：item=" + item);
                        logger.error(e.getMessage());
                    }
                }
            }

            @Override
            public String name() {
                return "auctionRewardTask";
            }
        });
    }

    /**
     * 拍品过期后操作
     *
     * @param item
     */
    private void onEnd(AuctionItemData item) {
        Bidder bidder = item.getBidder();
        if (bidder.getPlayerId() <= 0) {
            // 流拍 doRemove()
            removeItem(item);
            onLoss(item);
        } else {
            // 最后竞价胜出
            IGameRole role = GameWorld.getPtr().getGameRole(bidder.getPlayerId());
            onWin(role.getGameRole(), item);
        }
    }

    private void addLog(AuctionLog log) {
        // 可能不同步 要求不严格
        if (logs.size() >= AuctionModel.getLogCapacity()) {
            logs.poll();
        }
        logs.offer(log);
        setDirty();
    }

    /**
     * 获取税率
     *
     * @return
     */
    protected abstract float getTax();

    /**
     * 物品流拍后续操作
     *
     * @param item
     */
    protected abstract void onLoss(AuctionItemData item);


    /**
     * 刷新拍品列表
     *
     * @param currentTime
     */
    public void update(long currentTime) {
        for (AuctionItemData item : items.values()) {
            if (!item.isDead(currentTime)) {
                continue;
            }
            if (!doEnd(item)) {
                // 已经售出或其他方式移除
                continue;
            }
            onEnd(item);
        }
    }


    /****************************************************** 拍品信息变更及移除 同步方法 *********************************************************/
    /**
     * 加价操作
     *
     * @param role
     * @param item
     * @param modelData
     * @param targetPrice
     * @return 钱不够、竞品信息变动等将导致操作失败
     */
    private boolean doAddPrice(GameRole role, AuctionItemData item, AuctionItemModelData modelData, int targetPrice) {
        synchronized (item) {
            // 同步的二次判断
            item = getItem(item.getId());
            if (item == null) {
                return false;
            }
            if (targetPrice < modelData.getFixedPrice() && targetPrice != item.getBidder().getBid() + modelData.getAddPrice()) {
                // 价格变动拒绝本次请求
                return false;
            }
            // 竞价合法
            // 先扣钱
            DropData cost = new DropData(COST_TYPE, 0, targetPrice);
            EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
            if (!role.getPackManager().useGoods(cost, EGoodsChangeType.AUCTION_CONSUME, saves)) {
                role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
                return false;
            }
            role.savePlayer(saves);
            // 修改当前竞价
            item.setBidder(new Bidder(role.getPlayerId(), role.getPlayer().getName(), targetPrice));
            if (targetPrice >= modelData.getFixedPrice()) {
                // 一口价,下架
                removeItem(item);
            }
            setDirty();
            return true;
        }
    }

    /**
     * 物品到期移除
     *
     * @param item
     * @return 竞品流拍校验结果
     */
    protected boolean doEnd(AuctionItemData item) {
        synchronized (item) {
            // 下架
            // 2次验证 id唯一
            return removeItem(item);
        }
    }


}
