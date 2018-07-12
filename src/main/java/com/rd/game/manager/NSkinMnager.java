package com.rd.game.manager;

import com.rd.bean.player.Player;
import com.rd.bean.skin.NSkin;
import com.rd.bean.skin.NTaoZhuang;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.NSkinType;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NSkinModel;
import com.rd.model.data.skin.NSkinData;
import com.rd.model.data.skin.NTaoZhuangData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 皮肤
 *
 * @author MyPC
 */
public class NSkinMnager {
    private static final Logger logger = Logger.getLogger(NSkinMnager.class);

    private GameRole gameRole;
    private Player player;

    public NSkinMnager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    /****
     * 激活皮肤
     * @param request
     */
    public void processJiHuoPiFu(Message request) {

        int id = request.readShort();//
        NSkinData nPiFuData = NSkinModel.getPiFuByType(id);
        if (nPiFuData == null) {
            return;
        }
        if (!isCondition(nPiFuData)) {
            return;
        }
        byte type = (byte) nPiFuData.getClas();
        NSkin pifu = player.getPiFuMap().get(type);
        if (pifu != null && pifu.isHaveHuanHuaId(id)) {//已经激活了
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.PIFULIST);
        //物品不足
        if (!gameRole.getPackManager().useGoods(nPiFuData.getCost(), EGoodsChangeType.JIHUOSKIN_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        if (pifu == null) {
            pifu = new NSkin();
            player.getPiFuMap().put((byte) type, pifu);
        }
        pifu.setType(type);
        pifu.setCurrHZId(id);
        pifu.getHuanHuaList().add(id);
        Message message = new Message(EMessage.JIHUOPIFU.CMD(), request.getChannel());
        message.setShort(id);
        gameRole.sendMessage(message);
        jiHuoTaoZhuang(id);
        gameRole.savePlayer(enumSet);


    }

    /**
     * 激活套装
     */
    public void jiHuoTaoZhuang(int skinId) {
        Set<Byte> tzTypeList = NSkinModel.getTZTypeBySkinId(skinId);

        if (tzTypeList == null || tzTypeList.size() == 0) {
            return;
        }
        for (Byte tzType : tzTypeList) {
            NTaoZhuang tz = player.getnTaoZhuangMap().get(tzType);
            if (tz == null) {
                tz = new NTaoZhuang();
                player.getnTaoZhuangMap().put(tzType, tz);
            }
            List<Integer> list = tz.getSkinList();
            if (list.contains(skinId)) {
                return;
            }
            int count = list.size();
            NTaoZhuangData data = NSkinModel.getTaoZhuangData(tzType.intValue(), count);
            int level = 0;
            if (data != null) {
                level = data.getLevel();
            }
            tz.setLevel(level);
            tz.setType(tzType);
            list.add(skinId);
        }
    }

    /**
     * 打开套装面板
     */
    public void openTZPanel(Message request) {

        Map<Byte, NTaoZhuang> tzs = player.getnTaoZhuangMap();
        Message message = new Message(EMessage.OPEN_TAOZHUANG_PANEL.CMD(), request.getChannel());
        if (tzs == null) {
            message.setByte(0);
            gameRole.sendMessage(message);
            return;
        }
        message.setShort(tzs.size());
        for (Map.Entry<Byte, NTaoZhuang> map : tzs.entrySet()) {
            NTaoZhuang tz = map.getValue();
            message.setByte(tz.getType());
            List<Integer> list = tz.getSkinList();
            message.setByte(list.size());
            for (Integer id : list) {
                message.setShort(id);
            }

        }


        gameRole.sendMessage(message);
    }


    /****
     * 更换皮肤
     * @param request
     */
    public void processGengHuanPiFu(Message request) {
        int modelId = request.readShort();
        NSkinData nPiFuData = NSkinModel.getPiFuByType(modelId);
        if (nPiFuData == null) {
            return;
        }
        byte clas = nPiFuData.getClas();
        NSkin pifu = player.getPiFuMap().get(clas);
        if (pifu == null || !pifu.isHaveHuanHuaId(modelId)) {//没有激活
            return;
        }

        if (pifu.getCurrHZId() == modelId) {
            return;
        }
        pifu.setCurrHZId(modelId);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.PIFULIST);
        Message message = new Message(EMessage.GENGHUANPIFU.CMD(), request.getChannel());
        message.setShort(modelId);
        gameRole.sendMessage(message);
        gameRole.savePlayer(enumSet);

    }

    /**
     * 是否满足条件
     *
     * @return
     */
    private boolean isCondition(NSkinData nPiFuData) {

        return true;
    }

    /**
     * 打开不同皮肤的面板
     *
     * @param request
     */
    public void processPiFuPanel(Message request) {
        byte type = request.readByte();//皮肤类型

        NSkinType pifuType = NSkinType.getNPiFuType(type);
        if (pifuType == null) {
            return;
        }
        NSkin pifu = player.getPiFuMap().get(type);
        Message message = new Message(EMessage.PIFU_PANEL.CMD(), request.getChannel());
        if (pifu == null
                || pifu.getHuanHuaList() == null
                || pifu.getHuanHuaList().isEmpty()) {
            message.setByte(0);
            message.setShort(0);
            gameRole.sendMessage(message);
            return;
        }

        List<Integer> piFuList = pifu.getHuanHuaList();
        message.setByte(piFuList.size());
        for (Integer pifuQui : piFuList) {
            message.setShort(pifuQui);
        }
        message.setShort(pifu.getCurrHZId());
        gameRole.sendMessage(message);
    }
}
