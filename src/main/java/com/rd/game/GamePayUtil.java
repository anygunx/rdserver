package com.rd.game;

import com.google.common.base.Preconditions;
import com.rd.bean.drop.DropData;
import com.rd.bean.pay.OrderData;
import com.rd.common.MonthlyCardService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.PlayerDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.type.GamePayEvent;
import com.rd.game.manager.ActivityManager;
import com.rd.model.PayModel;
import com.rd.model.data.PayModelData;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;

/**
 * Created by XingYun on 2016/11/23.
 */
public class GamePayUtil {

    private static Logger logger = Logger.getLogger(GamePayUtil.class);

    /**
     * 充值回调
     * playerid是我们自己的metadata，不在渠道检测
     * 所以可能存在改端account和playerid不对应的情况，没什么重要影响现不管
     *
     * @param orderData 订单数据
     */
    public static OrderData callbackOnPay(OrderData orderData) {
        IGameRole role = GameWorld.getPtr().getGameRole(orderData.getPlayerId());
        orderData.setSubChannel(role.getPlayer().getSubChannel());
        orderData.setLevel(role.getPlayer().getLevel());
        orderData.setCreateTime(System.currentTimeMillis());
        Preconditions.checkNotNull(role, "GamePayUtil.callbackOnPay() failed. Cannot find role id=" + orderData.getPlayerId());

        orderData = rewardOnPay(role, orderData);
        return orderData;
    }

    /**
     * 充值处理
     *
     * @param role
     * @param orderData
     * @return
     */
    private static OrderData rewardOnPay(IGameRole role, OrderData orderData) {
        GameRole gameRole = null;
        if (role.isOnline()) {
            gameRole = (GameRole) role;
        } else {
            gameRole = new GameRole(role.getPlayer());
            //gameRole.getPayManager().init();
            //gameRole.getActivityManager().init();
            logger.error("离线玩家 playerId=" + role.getPlayer().getId() + " 充值.");
        }

        if (gameRole.getPayManager().isOrderExisted(orderData)) {
            logger.error("Order already existed. " + StringUtil.obj2Gson(orderData));
            return null;
        }

        //订单对应的充值数据
        int rmb = orderData.getAmount();
        PayModelData model = PayModel.getModel(rmb);
        if (model == null) {
            logger.error("作弊订单：" + orderData.toString());
            return orderData;
        }
        Preconditions.checkNotNull(model, "严重错误：充值数据找不到对应的档位, " + orderData.toString());
        orderData.setDiamond(model.getDiamond());

        gameRole.getPayManager().createOrder(orderData);

        // FIXME 操作不在线玩家 可能造成数据不同步
        ActivityManager activityManager = gameRole.getActivityManager();
        EnumSet<EPlayerSaveType> saveTypes = EnumSet.noneOf(EPlayerSaveType.class);
        //充值元宝
        DropData diamond = new DropData(EGoodsType.DIAMOND, 0, model.getDiamond());
        gameRole.getPackManager().addGoods(diamond, EGoodsChangeType.PAY_ADD, saveTypes);

        gameRole.getPayManager().handleAdditions(model);

        //月卡处理
        if (model.getType() > 0) {
            MonthlyCardService.buyMonthlyCard(gameRole.getPlayerId(), model.getType());

            if (model.getType() == 2) {
                DropData investFund = new DropData(EGoodsType.DIAMOND, 0, 8800);
                gameRole.getPackManager().addGoods(investFund, EGoodsChangeType.PAY_ADD, saveTypes);
            }
        }
        //充值记录
        gameRole.putMessageQueue(role.getPlayer().getBagCapacityMsg());
        gameRole.putMessageQueue(gameRole.getPayManager().getPayRecordMsg());
        //增加VIP经验
        DropData vipReward = new DropData(EGoodsType.VIP, 0, model.getDiamond());
        gameRole.getPackManager().addGoods(vipReward, EGoodsChangeType.PAY_VIP_ADD, saveTypes);
        //充值活动相关
        if (orderData.isCountable()) {
            //TODO 修改这里 主动领取
            activityManager.handlePay(rmb, orderData.getDiamond());
        }
        // 首充处理
        boolean isFirst = gameRole.getPayManager().isFirstPay();
        //充值事件
        GamePayEvent event = EGameEventType.PAY.create(gameRole, 1, saveTypes);
        event.setFirst(isFirst);
        gameRole.getEventManager().notifyEvent(event);
        if (isFirst) {
            //首冲事件
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COMPLETE_FIRST_PAY, 1, saveTypes));
        }

        //保存数据
        new PlayerDao().savePlayer(gameRole.getPlayer(), saveTypes);
        if (role.isOnline()) {
            activityManager.sendActivityPayUpdateMessage();
            //activityManager.sendActivityNewPayUpdateMessage();
        }
        return orderData;
    }

}
