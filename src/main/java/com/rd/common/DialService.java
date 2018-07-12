package com.rd.common;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.model.data.DialModelData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by XingYun on 2017/1/6.
 */
public class DialService {
    /**
     * 转盘扣钱roll奖
     *
     * @param role
     * @param data
     * @param oneKey
     * @return
     */
    public static List<DropData> roll(GameRole role, DialModelData data, boolean oneKey, EGoodsChangeType dialConsume, EGoodsChangeType dialAdd) {
        return roll(role, data, oneKey, dialConsume, dialAdd, false);
    }

    /**
     * 转盘roll奖
     *
     * @param role
     * @param data
     * @param oneKey
     * @param free   是否免费
     * @return
     */
    public static List<DropData> roll(GameRole role, DialModelData data, boolean oneKey, EGoodsChangeType consumeType, EGoodsChangeType gainType, boolean free) {
        List<DropData> cost = new ArrayList<>();
        if (!free) {
            //十连抽
            if (oneKey) {
                cost.addAll(data.getCostOneKey());
                cost.addAll(data.getMoneyOneKey());
            } else {
                cost.addAll(data.getCost());
                cost.addAll(data.getMoney());
            }
        }
        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(cost, consumeType, saves)) {
            return null;
        }
        //增加
        List<DropData> reward = data.getDrop().getRandomDrop();
        if (oneKey) {
            for (int i = 1; i < data.getOneKeyCount(); i++) {
                reward.addAll(data.getDrop().getRandomDrop());
            }
            if (data.getPointsOneKey() != null) {
                role.getPackManager().addGoods(data.getPointsOneKey(), gainType, saves);
            }
        } else {
            if (data.getPoints() != null) {
                role.getPackManager().addGoods(data.getPoints(), gainType, saves);
            }
        }
        role.getPackManager().addGoods(reward, gainType, saves);
        role.savePlayer(saves);
        return reward;
    }

}
