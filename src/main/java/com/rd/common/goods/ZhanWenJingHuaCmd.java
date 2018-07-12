package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;

import java.util.EnumSet;

/**
 * @author lwq
 */
public class ZhanWenJingHuaCmd implements IGoodsCmd {

    private static final ZhanWenJingHuaCmd _instance = new ZhanWenJingHuaCmd();

    public static ZhanWenJingHuaCmd gi() {
        return _instance;
    }

    @Override
    public long getValue(GameRole role, DropData data) {

        return role.getPlayer().getZhanWenJinghua();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        long value = getValue(role, data);
        if (value < data.getN()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {

        if (data.getN() <= 0) {
            return false;
        }

        if (!validate(role, data)) {
            return false;
        }

        //消耗的战纹精华
        int consumeData = -data.getN();

        role.getPlayer().addZhanWenJinghua(consumeData);

        role.sendUpdateCurrencyMsg(EGoodsType.ZHANWEN_JINGHUA, changeType);


        return saveData(enumSet);
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {

        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {

        if (data.getN() <= 0) {
            return false;
        }
        //增加的战纹精华
        int addData = data.getN();

        role.getPlayer().addZhanWenJinghua(addData);

        role.sendUpdateCurrencyMsg(EGoodsType.ZHANWEN_JINGHUA, changeType);


        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        return false;
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.ZHANWEN_JINGHUA);
        return true;
    }

}
