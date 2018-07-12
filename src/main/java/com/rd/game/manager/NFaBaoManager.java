package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.fanbao.NDanYao;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NFaBaoModel;
import com.rd.model.data.fabao.NDanYaoData;
import com.rd.model.data.fabao.NPulseData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.List;

/**
 * 法宝系统
 *
 * @author MyPC
 */
public class NFaBaoManager {
    private static final Logger logger = Logger.getLogger(NFaBaoManager.class);

    private GameRole gameRole;
    private Player player;

    public NFaBaoManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();

    }

    /****
     *经脉升级
     * @param request
     */
    public void processPulseUpGrade(Message request) {
        byte type = request.readByte();
        //发送消息
        Message msg = new Message(EMessage.PULSE_UPGRADE.CMD(), request.getChannel());
        if (type == 0) {//只是代表的是打开面板
            msg.setShort(player.getPulse() + 1);
            gameRole.sendMessage(msg);
            return;
        }
        short pulse = player.getPulse();
        NPulseData pulseData = NFaBaoModel.getNPulseData((short) (pulse + 1));
        if (pulseData == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.PULSE);
        //物品不足
        if (!gameRole.getPackManager().useGoods(pulseData.getCost(), EGoodsChangeType.PULSE_UPGRADE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        //升级
        player.addPulse(1);
        msg.setShort(player.getPulse());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(enumSet);

    }


    /****
     *丹药加成
     * @param request
     */
    public void processDanYao(Message request) {
        byte pos = request.readByte();
        boolean isHavePos = NFaBaoModel.isNDanYaoData(pos);
        if (!isHavePos) {
            return;
        }
        NDanYao danyao = player.getDanYaoList().get(pos);
        if (danyao == null) {
            danyao = new NDanYao();
            danyao.setP(pos);
            danyao.setLv((byte) 0);
            player.getDanYaoList().set(pos, danyao);

        }
        NDanYaoData danyaoData = NFaBaoModel.getNDanYaoData(pos, (byte) (danyao.getLv() + 1));
        if (danyaoData == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.DANYAO);
        DropData dropData = danyaoData.getCost();
        //物品不足
        if (!gameRole.getPackManager().useGoods(dropData, EGoodsChangeType.DANYAO_UPGRADE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        //升级
        danyao.setLv((danyaoData.getLevel()));
        danyao.addGn((short) dropData.getN());
        //发送消息
        Message msg = new Message(EMessage.DANYAO_UPGRADE.CMD(), request.getChannel());
        msg.setByte(pos);
        msg.setByte(danyao.getLv());
        msg.setShort(danyao.getGn());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(enumSet);
    }

    /**
     * 打开丹药面板
     *
     * @param request
     */
    public void openDanYaoPanel(Message request) {
        List<NDanYao> danyao = player.getDanYaoList();
        Message msg = new Message(EMessage.OPEN_DANYAO_PANEL.CMD(), request.getChannel());
        msg.setByte(danyao.size());
        for (int i = 0; i < danyao.size(); i++) {
            NDanYao dy = danyao.get(i);
            msg.setByte(i);
            msg.setByte(dy.getLv());
            msg.setShort(dy.getGn());
        }

        gameRole.sendMessage(msg);
    }


}
