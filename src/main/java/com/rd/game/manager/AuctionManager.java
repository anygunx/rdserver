package com.rd.game.manager;

import com.rd.bean.auction.BaseAuction;
import com.rd.common.GameCommon;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.ErrorDefine;
import com.rd.game.AuctionService;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.AuctionModel;
import com.rd.model.data.AuctionItemModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * 玩家拍卖管理器
 * Created by XingYun on 2017/10/27.
 */
public class AuctionManager implements IEventListener {
    private Logger logger = Logger.getLogger(AuctionManager.class);

    private GameRole gameRole;
    private long lastUpdateSubscribeTime = -1;

    public AuctionManager(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    public void init() {
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.getType() != EGameEventType.NEW_AUCTION_ITEM) {
            return;
        }
        short modelId = (short) event.getData();
        if (!gameRole.getPlayer().isSubscribe(modelId)) {
            return;
        }
        Message message = new Message(MessageCommand.AUCTION_NEW_SUBSCRIPTION);
        gameRole.putMessageQueue(message);
    }

    /**
     * 获取拍品信息
     *
     * @param request
     */
    public void processGetItems(Message request) {
        byte shopId = request.readByte();
        BaseAuction auction = AuctionService.getShop(shopId);
        if (auction == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Message message = new Message(MessageCommand.AUCTION_GET_ITEMS, request.getChannel());
        auction.getItemsMessage(shopId, gameRole, message);
        gameRole.sendMessage(message);
    }

    /**
     * 竞拍
     *
     * @param request
     */
    public void processAddPrice(Message request) {
        byte shopId = request.readByte();
        long uid = Long.parseLong(request.readString());
        // 客户端显示的目标竞价 用于服务器校验，避免由于延迟造成的价格差
        int targetPrice = request.readInt();

        if (targetPrice <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        BaseAuction auction = AuctionService.getShop(shopId);
        if (auction == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        boolean changed = auction.addPrice(gameRole, uid, targetPrice);
        Message addPriceMessage = new Message(MessageCommand.AUCTION_ADD_PRICE, request.getChannel());
        addPriceMessage.setBool(changed);
        gameRole.putMessageQueue(addPriceMessage);

        Message getItemsMessage = new Message(MessageCommand.AUCTION_GET_ITEMS, request.getChannel());
        auction.getItemsMessage(shopId, gameRole, getItemsMessage);
        gameRole.sendMessage(getItemsMessage);
    }


    /**
     * 获取拍卖日志
     *
     * @param request
     */
    public void processGetLogs(Message request) {
        byte shopId = request.readByte();
        BaseAuction auction = AuctionService.getShop(shopId);
        if (auction == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Message message = new Message(MessageCommand.AUCTION_GET_LOGS, request.getChannel());
        auction.getLogsMessage(shopId, gameRole, message);
        gameRole.sendMessage(message);
    }

    /**
     * 处理订阅
     *
     * @param request
     */
    public void processUpdateSubscribe(Message request) {
        long ts = System.currentTimeMillis();
        if (lastUpdateSubscribeTime != -1 && ts - lastUpdateSubscribeTime < DateUtil.SECOND) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_OVER_QUICK);
            return;
        }
        lastUpdateSubscribeTime = ts;
        byte size = request.readByte();
        Set<Short> subscriptions = new HashSet<>();
        for (int i = 0; i < size; i++) {
            short modelId = request.readShort();
            AuctionItemModelData model = AuctionModel.getData(modelId);
            if (model.getSubscribe() != GameCommon.True) {
                continue;
            }
            subscriptions.add(modelId);
        }
        gameRole.getPlayer().setAuctionSubscriptions(subscriptions);
        gameRole.savePlayer(EPlayerSaveType.AUCTION_SUBSCRIPTIONS);
        gameRole.sendTick(request);
    }

    public Message getUpdateSubscribeMessage() {
        Message message = new Message(MessageCommand.AUCTION_UPDATE_SUBSCRIPTIONS);
        Set<Short> subscribes = gameRole.getPlayer().getAuctionSubscriptions();
        message.setByte(subscribes.size());
        for (Short id : subscribes) {
            message.setShort(id);
        }
        return message;
    }

    /**
     * 获取收益信息
     *
     * @param request
     */
    public void processGetIncome(Message request) {
        byte shopId = request.readByte();
        BaseAuction auction = AuctionService.getShop(shopId);
        if (auction == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Message message = new Message(MessageCommand.AUCTION_GET_INCOME, request.getChannel());
        auction.getIncomeMessage(gameRole, message);
        gameRole.sendMessage(message);
    }
}
