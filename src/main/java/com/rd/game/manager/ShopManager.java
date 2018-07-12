package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.game.local.ArenaGameService;
import com.rd.model.ShopModel;
import com.rd.model.data.ShopItemData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * 商城管理器
 *
 * @author Created by U-Demon on 2016年11月5日 上午11:43:20
 * @version 1.0.0
 */
public class ShopManager {

    private static Logger logger = Logger.getLogger(ShopManager.class);

    private GameRole gameRole;
    private Player player;

    public ShopManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void init() {

    }

    /**
     * 商城详情
     *
     * @param request
     */
    public void processShopInfoMsg(Message request) {
        Message msg = new Message(MessageCommand.SHOP_INFO_MESSAGE, request.getChannel());
        Map<Byte, Byte> discs = new HashMap<>();
        //竞技场商城折扣
        if (ArenaGameService.arenaShopDisc > 0 && ArenaGameService.arenaShopDisc < 100)
            discs.put((byte) 3, ArenaGameService.arenaShopDisc);
        msg.setByte(discs.size());
        for (Entry<Byte, Byte> entry : discs.entrySet()) {
            msg.setByte(entry.getKey());
            msg.setByte(entry.getValue());
        }
        gameRole.sendMessage(msg);
    }

    /**
     * 商城购买
     *
     * @param request
     */
    public void processShopBuyMsg(Message request) {
        byte shopType = request.readByte();
        int id = request.readInt();
        int num = request.readInt();
        ShopItemData data = ShopModel.getShopItem(id);
        if (data == null || num > 1000 || num < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        byte disc = 100;
        //竞技场商城折扣
        if (shopType == 3) {
            if (ArenaGameService.arenaShopDisc > 0 && ArenaGameService.arenaShopDisc < 100)
                disc = ArenaGameService.arenaShopDisc;
        }
        //扣除物品
        List<DropData> price = data.getPrice();
        if (num >= 1) {
            price = new ArrayList<>();
            for (DropData cost : data.getPrice()) {
                int costPrice = (int) (cost.getN() / 100.f * disc);
                price.add(new DropData(cost.getT(), cost.getG(), num * costPrice));
            }
        }
        if (!gameRole.getPackManager().useGoods(price, EGoodsChangeType.SHOP_BUY_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        //增加物品
        DropData reward = new DropData(data.getItem().getT(), data.getItem().getG(), num * data.getItem().getN());
        if (!gameRole.getPackManager().addGoods(reward, EGoodsChangeType.SHOP_BUY_ADD, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return;
        }
        gameRole.savePlayer(enumSet);
        //发送消息
        Message msg = new Message(MessageCommand.SHOP_BUY_MESSAGE, request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
    }

    /**
     * 商城刷新
     *
     * @param request
     */
    public void processShopRefreshMsg(Message request) {

    }

}
