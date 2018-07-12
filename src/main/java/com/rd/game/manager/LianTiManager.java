package com.rd.game.manager;

import com.rd.bean.lianti.ShenBingData;
import com.rd.bean.player.Player;
import com.rd.game.GameRole;
import com.rd.model.data.ShenBingModelData;
import com.rd.net.message.Message;
import org.jboss.netty.channel.Channel;

/**
 * 炼体
 * Created by XingYun on 2017/11/30.
 */
public class LianTiManager {
    private GameRole gameRole;
    private Player player;
    private int[] attr;

    public LianTiManager(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    /**
     * 神兵提升
     *
     * @param request
     */
    public void processPromoteShenBing(Message request) {
//        byte idx = request.readByte();
//        byte type = request.readByte();
//
//        Character character = gameRole.getPlayer().getCharacter(idx);
//        if (character == null){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//            return;
//        }
//        ShenBingData shenBingData = character.getShenBing(type);
//        short id = shenBingData == null ? LianTiModel.getFirstShenBingId(type) : shenBingData.getId();
//        ShenBingModelData modelData = LianTiModel.getShenBing(id);
//        if (modelData == null){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//            return;
//        }
//
//        if (shenBingData == null){
//            // 激活
//            processInvokeShenBing(request, character, modelData);
//        } else {
//            short next = (short) (shenBingData.getId() +1);
//            ShenBingModelData nextModel = LianTiModel.getShenBing(next);
//            if (nextModel == null){
//                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//                // 满级了
//                return;
//            }
//            if (modelData.getStage() != nextModel.getStage()){
//                // 升阶
//                processAddShenBingStage(request, character, nextModel);
//                return;
//            } else {
//                // 升星
//                processAddShenBingExp(request, character, modelData);
//                return;
//            }
//        }

    }

    /**
     * 神兵升阶
     *
     * @param request
     * @param character
     * @param nextModel
     */
    private void processAddShenBingStage(Message request, Character character, ShenBingModelData nextModel) {
//        ShenBingData shenBingData = character.getShenBing(nextModel.getType());
//        EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//        shenBingData.setId(nextModel.getId());
//        shenBingData.setExp(0);
//        enumSet.add(EPlayerSaveType.CHA_SHENBING);
//        gameRole.saveData(character.getIdx(), enumSet);
//        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SHENBING_STAGE_UP, 1, enumSet));
//
//        sendShenBingUpdateMessage(request.getChannel(), character, shenBingData);
    }

    /**
     * 神兵激活
     *
     * @param request
     * @param character
     * @param modelData
     */
    private void processInvokeShenBing(Message request, Character character, ShenBingModelData modelData) {
//        EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//        // 消耗
//        if (!gameRole.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.SHENBING_INVOKE, enumSet)){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//            return;
//        }
//        // 激活
//        ShenBingData shenBingData = new ShenBingData();
//        shenBingData.setId(modelData.getId());
//        character.addShenBing(modelData.getType(), shenBingData);
//
//        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SHENBING_INVOKE, 1, enumSet));
//
//        enumSet.add(EPlayerSaveType.CHA_SHENBING);
//        gameRole.saveData(character.getIdx(), enumSet);
//        sendShenBingUpdateMessage(request.getChannel(), character, shenBingData);
    }

    /**
     * 神兵经验提升
     *
     * @param request
     * @param character
     * @param modelData
     */
    private void processAddShenBingExp(Message request, Character character, ShenBingModelData modelData) {
//        EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//        ShenBingData shenBingData = character.getShenBing(modelData.getType());
//
//        // 消耗
//        if (modelData.getExp() <= shenBingData.getExp() //兼容经验满无法升星的状况
//                || !gameRole.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.SHENBING_STARTUP, enumSet)){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//            return;
//        }
//
//        // 提升经验
//        shenBingData.setExp(shenBingData.getExp() + modelData.getConsume().getN());
//        if (shenBingData.getExp() >= modelData.getExp()){
//            if (modelData.getStage() != modelData.getStage()){
//                // 须要升阶
//                shenBingData.setExp(modelData.getExp());
//            }else{
//                // 升星
//                shenBingData.setId((short) (modelData.getId() + 1));
//                shenBingData.setExp(0);
//            }
//        }
//        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SHENBING_STAR_UP, 1, enumSet));
//        
//        enumSet.add(EPlayerSaveType.CHA_SHENBING);
//        gameRole.saveData(character.getIdx(), enumSet);
//        sendShenBingUpdateMessage(request.getChannel(), character, shenBingData);
    }

    private void sendShenBingUpdateMessage(Channel channel, Character character, ShenBingData shenBingData) {
//        Message message = new Message(MessageCommand.SHENBING_UPSTAR_MESSAGE, channel);
//        message.setByte(character.getIdx());
//        shenBingData.getMessage(message);
//        gameRole.sendMessage(message);
    }


    public int[] getAttr() {
        return attr;
    }
}
