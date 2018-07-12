package com.rd.game.manager;

import com.rd.bean.grow.Grow;
import com.rd.bean.grow.GrowSeed;
import com.rd.bean.grow.GrowSuit;
import com.rd.bean.player.NTAData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.NTaskAdvanceType;
import com.rd.enumeration.EGrow;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NTaskAdvancedModel;
import com.rd.model.data.taskadvanced.NTaskAdvancedData;
import com.rd.net.message.Message;

import java.util.EnumSet;

/***
 *
 * 培养成就系统
 * */
public class NTaskAdvancedManager {


    private GameRole gameRole;
    private Player player;

    public NTaskAdvancedManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }


    public void proccessLingQu(Message request) {
        short id = request.readShort();
        NTaskAdvancedData data = NTaskAdvancedModel.getNTaskAdvancedDataIdMap(id);
        if (data == null) {
            return;
        }
        int jieshu = getjieShu(data.getType());
        if (data.getParam() > jieshu) {
            return;
        }
        int index = data.getType() - 1;
        NTAData ntAData = player.getNtaData().get(index);
        if (ntAData.getLingquId().contains(id)) {
            return;
        }
        ntAData.getLingquId().add(id);
        Message msg = new Message(EMessage.TASK_ADVANCE_LINGQU.CMD(), request.getChannel());

        msg.setByte(data.getType());
        msg.setShort(getjieShu(data.getType()));
        msg.setByte(ntAData.getLingquId().size());
        for (Short lqid : ntAData.getLingquId()) {
            msg.setShort(lqid);
        }
        gameRole.sendMessage(msg);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(data.getRewards(), EGoodsChangeType.TASK_ADVANCED, enumSet);
        enumSet.add(EPlayerSaveType.TASKADVANCED);
        gameRole.savePlayer(enumSet);
    }

    /***
     *
     * 打开成就面板
     *
     * */
    public void proccessPanel(Message request) {

        byte type = request.readByte();
        if (NTaskAdvanceType.getNTaskAdvanceType(type) == null) {
            return;
        }
        int index = type - 1;
        NTAData ntAData = player.getNtaData().get(index);
        Message msg = new Message(EMessage.TASK_ADVANCE_PANEL.CMD(), request.getChannel());
        msg.setByte(type);
        msg.setShort(getjieShu(type));
        msg.setByte(ntAData.getLingquId().size());
        for (Short id : ntAData.getLingquId()) {
            msg.setShort(id);
        }
        gameRole.sendMessage(msg);

    }


    /**
     * 是否领取
     */
    private short getjieShu(int type) {

        byte growId = 0;
        byte index = -1;
        switch (NTaskAdvanceType.getNTaskAdvanceType(type)) {
            case WING:
                growId = EGrow.ROLE.I();
                index = 0;
                break;
            case ZUOQI:
                index = 1;
                growId = EGrow.ROLE.I();
                break;
            case TIANXIAN:
                growId = EGrow.GODDESS.I();
                index = 0;
                break;
            case SHENGBING:
                growId = EGrow.GODDESS.I();
                index = 1;
                break;
            case XIANWEI:
                growId = EGrow.MATE.I();
                index = 0;
                break;
            case XIANZHEN:
                growId = EGrow.MATE.I();
                index = 1;
                break;
            case TONGLING:
                index = 0;
                growId = EGrow.PET.I();
                break;
            case SHOUHUN:
                index = 1;
                growId = EGrow.PET.I();
                break;
            case TIANNV:
                growId = EGrow.FAIRY.I();
                index = 0;
                break;
            case XIANQI:
                growId = EGrow.FAIRY.I();
                index = 1;
                break;
            case HUAPAN:
                growId = EGrow.FAIRY.I();
                index = 2;
                break;
            case LINGQI:
                index = 3;
                growId = EGrow.FAIRY.I();
                break;
            default:
                break;
        }


        Grow grow = player.getGrowList().get(growId);
        GrowSuit suit = grow.getSuit()[index];
        if (suit == null) {
            return 0;
        }
        return suit.getLevel();
    }


    public void processXianLvTotalLv(Message request) {
        Grow grow = player.getGrowList().get(EGrow.MATE.I());
        GrowSuit suit = grow.getSuit()[0];
        int xianLvLevel = 0;
        if (suit != null) {
            xianLvLevel = suit.getLevel();
        }
        Message msg = new Message(EMessage.TASK_ADVANCE_PANEL.CMD(), request.getChannel());
        msg.setInt(xianLvLevel);
        msg.setInt(0);
    }


    /**
     * 是否领取
     */
    private boolean isLingQu(NTaskAdvancedData data) {
        //NTaskAdvancedData data=NTaskAdvancedModel.getNTaskAdvancedDataMap(type, id);
        if (data == null) {
            return false;
        }
        byte growType = 0;
        byte growId = 0;
        switch (NTaskAdvanceType.getNTaskAdvanceType(data.getType())) {
            case ZUOQI:

                break;

            case WING:

                break;
            case SHENGBING:

                break;
            case TIANXIAN:

                break;
            case XIANZHEN:

                break;
            case XIANWEI:

                break;
            case TONGLING:

                break;
            case SHOUHUN:

                break;

            case TIANNV:

                break;
            case XIANQI:

                break;
            case HUAPAN:

                break;
            case LINGQI:

                break;
            default:
                break;
        }

        EGrow egrow = EGrow.type(growType);
        Grow grow = player.getGrowList().get(egrow.I());
        GrowSeed growSeed = grow.getMap().get(growId);
        if (growSeed.getLevel() < data.getParam()) {
            return true;
        }
        return false;

    }

}
