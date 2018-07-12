package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;

import java.util.EnumSet;

/**
 * 物品、代币处理的接口
 *
 * @author Created by U-Demon on 2016年11月1日 上午10:41:57
 * @version 1.0.0
 */
public interface IGoodsCmd {

    /**
     * 获取角色当前值
     *
     * @param role
     * @param data
     * @return
     */
    public long getValue(GameRole role, DropData data);

    /**
     * 是否满足条件
     *
     * @param role
     * @param data
     * @return
     */
    public boolean validate(GameRole role, DropData data);

    /**
     * 消耗物品
     *
     * @param role
     * @param data
     * @return
     */
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet);

    /**
     * 消耗物品
     *
     * @param role
     * @param data
     * @return
     */
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient);

    /**
     * 奖励物品
     *
     * @param role
     * @param data
     * @return
     */
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet);

    /**
     * 奖励物品
     *
     * @param role
     * @param data
     * @return
     */
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient);

    /**
     * 保存数据
     *
     * @param role
     * @return
     */
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet);

}
