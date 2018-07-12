package com.rd.game.manager;

import com.rd.bean.copy.cailiao.CLCopy;
import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.bean.shop.NShop;
import com.rd.bean.shop.NTypeShop;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.NDungeonDefine;
import com.rd.define.NShopType;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NShopModel;
import com.rd.model.data.shop.NShopData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.Map;

public class NshopManager {
    private static Logger logger = Logger.getLogger(NshopManager.class);

    private GameRole gameRole;
    private Player player;
    private NShop nShop;

    public NshopManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void init() {
        nShop = gameRole.getDbManager().nShopDao.selectNShop(player.getId());
    }

    /**
     * 商品购买
     */
    public void processBuy(Message request) {
        byte type = request.readByte();
        int id = request.readInt();
        byte num = request.readByte();
        if (num < 0 || num > 100) {
            return;
        }
        NShopData data = NShopModel.getShopMap(type, id);
        if (data == null) {
            return;
        }
        boolean iscon = isCondition(type, data.getCondition(), request);
        if (!iscon) {
            return;
        }

        NTypeShop typeShop = getNShop().getShopMap().get(type);
        if (typeShop != null) {
            if (data.getType() != 0) {
                byte daNum = data.getNum();
                Byte count = typeShop.getM().get(id);
                if (count != null && count + num >= daNum) {
                    return;
                }
            }
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost = data.getCost();
        cost.setN(cost.getN() * num);
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SHOP_BUY_CONSUME, enumSet))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
        DropData rewardData = data.getReward();
//		//增加物品
        DropData reward = new DropData(rewardData.getT(), rewardData.getG(), num * rewardData.getN());
        if (!gameRole.getPackManager().addGoods(reward, EGoodsChangeType.SHOP_BUY_ADD, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return;
        }
        if (typeShop == null) {
            typeShop = new NTypeShop();
        }
        Byte count = typeShop.getM().get(id);
        if (count == null) {
            count = 0;
        }
        byte n = (byte) (num + count);
        typeShop.getM().put(id, n);
        Message msg = new Message(EMessage.SHOP_BUY.CMD(), request.getChannel());
        msg.setByte(type);
        msg.setInt(id);
        msg.setByte(num);
        gameRole.sendMessage(msg);
        gameRole.savePlayer(enumSet);

    }


    public void processPanel(Message request) {
        byte type = request.readByte();
        Message msg = new Message(EMessage.SHOP_EQUIP_PANEL.CMD(), request.getChannel());
        msg.setShort(12);
        NTypeShop typeShop = getNShop().getShopMap().get(type);
        if (typeShop == null) {
            msg.setByte(type);
            msg.setShort(0);
            gameRole.sendMessage(msg);
            return;
        }
        msg.setByte(type);
        msg.setShort(typeShop.getM().size());
        for (Map.Entry<Integer, Byte> shopMap : typeShop.getM().entrySet()) {
            int shopid = shopMap.getKey();
            byte count = shopMap.getValue();
            msg.setInt(shopid);
            msg.setByte(count);
        }

        gameRole.sendMessage(msg);

    }

    public NShop getNShop() {
        if (nShop == null) {
            nShop = new NShop();
            nShop.setPlayerId(player.getId());
        }

        return nShop;
    }


    public boolean isCondition(byte type, byte conCount, Message request) {
        if (type == NShopType.SHOP_CAILIAO.getId()) {
            CLCopy clcop = gameRole.getNCopyManager().getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.CAILIAO.getType(), NDungeonDefine.SUBTYPE);
            if (clcop.getTotalCount() > conCount) {
                return true;
            }
        }


        return true;

    }

    /**
     * 重置
     */
    public void retset() {
        if (nShop == null) {
            return;
        }
        Map<Byte, NTypeShop> typeShopMap = nShop.getShopMap();
        for (Map.Entry<Byte, NTypeShop> map : typeShopMap.entrySet()) {
            map.getValue().retset(map.getKey());
        }
    }

}
