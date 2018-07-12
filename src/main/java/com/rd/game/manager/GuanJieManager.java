package com.rd.game.manager;

import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.PlayerDao;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.GuanJieModel;
import com.rd.model.data.GuanJieData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.EnumSet;
import java.util.Map;

/**
 * @author lwq
 */
public class GuanJieManager {

    private GameRole gameRole;
    private Player player;

    public GuanJieManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }


    /**
     * 根据变化的威望值判断官阶的变化
     *
     * @param value   变化的威望值
     * @param enumSet
     * @return
     */
    public boolean calculateWeiWang(int value, EnumSet<EPlayerSaveType> enumSet) {

        if (value == 0) {
            return false;
        }

        int originalWeiWang = player.getWeiWang();
        int nowWeiWang = player.getWeiWang() + value;

        //原始官阶等级
        byte originalLevel = GuanJieModel.getData(originalWeiWang).getLevel();
        //威望变化后的官阶等级
        byte nowLevel = GuanJieModel.getData(nowWeiWang).getLevel();

        enumSet.add(EPlayerSaveType.WEIWANG);
        player.setWeiWang(nowWeiWang);

        if (nowLevel > originalLevel) {
            //官阶等级提升
            changeGuanJie(nowLevel, enumSet);

        } else if (nowLevel < originalLevel) {
            //官阶等级下降
            changeGuanJie(nowLevel, enumSet);
        }

        return true;
    }


    private void changeGuanJie(byte level, EnumSet<EPlayerSaveType> enumSet) {
        //通知角色官阶变化消息
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GUANJIE_REACH_LEVEL, level, enumSet));

        Message message = this.getGuanJieUpMessage();
        gameRole.putMessageQueue(message);
    }

    private Message getGuanJieUpMessage() {
        Message message = new Message(MessageCommand.GUANJIE_RANK_MESSAGE);
        message.setInt(player.getWeiWang());
        return message;
    }


    /**
     * 回收所有玩家的指定官阶的威望
     */
    public static void updateWeiWang() {
        //官阶数据
        Map<Byte, GuanJieData> guanJieDataMap = GuanJieModel.getGuanJieDataMap();

        new PlayerDao().updateWeiWang(guanJieDataMap);
    }

}
