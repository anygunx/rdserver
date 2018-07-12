package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.NEquipSlot;
import com.rd.bean.goods.data.NEquipData;
import com.rd.bean.player.Player;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.lg.bean.game.Strength;
import com.rd.model.NGoodModel;
import com.rd.model.data.equip.NEquipUpGradeItemData;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/*******
 * 装备强化
 * @author MyPC
 *
 */
public class NEquipManager {

    private static final Logger logger = Logger.getLogger(NEquipManager.class);

    private GameRole gameRole;
    private Player player;

    public NEquipManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    public void processEquipStrengthOneKey1(Message request) {
        byte type = request.readByte();
        NEquipUpGradeType equipUpGradeType = NEquipUpGradeType.geEquipUpGradeType(type);
        if (equipUpGradeType == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.EQUIPSLOT);
        Map<Byte, Integer> map = new HashMap<Byte, Integer>();
        short state = ErrorDefine.ERROR_NONE;
        byte pos = getStrMinIndexByType(type);
        boolean isStrength = true;
        int strCount = 0;
        while (isStrength) {
            //装备槽
            NEquipSlot slot = player.getEquipSlotList().get(pos);

            int temp = getGradeBytype(slot, type);
            NEquipUpGradeItemData next = equipUpGradeType.getNEquipUpGradeItemData(temp + 1, EquipDefine.getEquipType(pos));

            if (next == null) {// 装备槽位等级达到上限
                isStrength = false;
                continue;
            }

            //物品不足
            if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.EQUIPQH_CONSUME, enumSet)) {
                if (strCount == 0) {
                    state = ErrorDefine.ERROR_GOODS_LESS;
                }
                isStrength = false;
                continue;
            }
            if (slot == null) {
                slot = new NEquipSlot();
                player.getEquipSlotList().set(pos, slot);
            }
            slot.addUpGradeByType(1, equipUpGradeType);
            map.put(pos, getGradeBytype(slot, type));
            if (pos >= EquipDefine.EQUIP_TYPE_SHOE) {//最后一个装备 之后就应该停止啦
                isStrength = false;
                continue;
            }
            pos++;//如果不到最后一个装备继续强化下个槽位的装备
            strCount++;
        }
        if (state != ErrorDefine.ERROR_NONE) {
            gameRole.sendErrorTipMessage(request, state);
            return;
        }
        //pos = getStrMinIndex();
        //装备槽
        NEquipSlot slot = player.getEquipSlotList().get(pos);


        NEquipUpGradeItemData curr = getEquiQHDSModelDataByMin1(slot, type);

        int qHDSLv = 0;//强化大师等级
        if (curr != null) {
            qHDSLv = curr.getLv();
        }
        //通知强化消息
        //gameRole.getEventManager().notifyEvent(EGameEventType.EQUIP_STRENGTHEN.create(gameRole, 1,enumSet));
        //发送消息
        Message message = new Message(EMessage.EQUIP_STRENGTH_ONEKEY_MESSAGE.CMD(), request.getChannel());
        //message.setByte(idx);
        message.setByte(map.size());
        for (Entry<Byte, Integer> entry : map.entrySet()) {
            message.setByte(entry.getKey());
            message.setInt(entry.getValue());

            //记录玩家强化升级
            LogUtil.log(player, new Strength(entry.getKey(), entry.getValue()));
        }
        message.setInt(qHDSLv);
        gameRole.sendMessage(message);
        gameRole.savePlayer(enumSet);
    }


    /**
     * 打开装备强化面板或者是精炼面板
     *
     * @param request
     */
    public void openPanlMessage1(Message request, byte type) {
        // TODO Auto-generated method stub
        Message message = new Message(EMessage.EQUIP_STRENGTH_ONEKEY_MESSAGE.CMD(), request.getChannel());
        byte pos = getStrMinIndexByType(type);
        NEquipSlot slot = player.getEquipSlotList().get(pos);
        message.setByte(player.getEquipSlotList().size());
        for (int i = 0; i < player.getEquipSlotList().size(); i++) {
            message.setByte(i);
            NEquipSlot tempSlot = player.getEquipSlotList().get(i);
            int grade = getGradeBytype(tempSlot, type);
            message.setInt(grade);

        }

        NEquipUpGradeItemData curr = getEquiQHDSModelDataByMin1(slot, type);
        int qHDSLv = 0;//强化大师等级0
        if (curr != null) {
            qHDSLv = curr.getLv();
        }
        message.setInt(qHDSLv);
        gameRole.sendMessage(message);
    }


    /**
     * 获取不同种大师的数据  这里的传进来的参数 type 例如 强化精炼 类型 需要在此方法里转一下不同种大师的 类型
     *
     * @param slot
     * @param type
     * @return
     */
    public NEquipUpGradeItemData getEquiQHDSModelDataByMin1(NEquipSlot slot, byte type) {
        NEquipUpGradeItemData curr = null;
        int tempLv = 0;
        tempLv = getGradeBytype(slot, type);
        byte dsType = (byte) getDSBytype(type);
        NEquipUpGradeType equipUpGradeType = NEquipUpGradeType.geEquipUpGradeType(dsType);
        for (int i = tempLv; i >= 1; i--) {//从高到底
            curr = equipUpGradeType.getNEquipUpGradeItemData(i);
            if (curr != null) {
                break;
            }
        }
        return curr;
    }


    /**
     * 获取所有装备当中最小等级的 装备索引
     *
     * @return
     */
    private byte getStrMinIndexByType(byte type) {
        List<NEquipSlot> slots = player.getEquipSlotList();
        int tempLv = 0;
        int tempLv2 = 0;
        for (int i = 0; i < slots.size() - 1; i++) {

            NEquipSlot slot = slots.get(i);
            if (slot == null) {
                return (byte) i;
            }

            tempLv = getGradeBytype(slot, type);
            if (tempLv == 0)
                return (byte) i;
            NEquipSlot slot2 = slots.get(i + 1);
            if (slot2 == null)
                return (byte) (i + 1);
            tempLv2 = getGradeBytype(slot2, type);
            if (tempLv > tempLv2)
                return (byte) (i + 1);
        }
        return 0;
    }

    /**
     * 通过不同种的类型获取不同种的等级
     *
     * @param slot
     * @param type
     * @return
     */
    private int getGradeBytype(NEquipSlot slot, byte type) {
        if (slot == null) {
            return 0;
        }
        int tempLv = 0;
        if (NEquipUpGradeType.EQUIP_QH.getType() == type) {
            tempLv = slot.getStr();
        } else if (NEquipUpGradeType.EQUIQ__JL.getType() == type) {
            tempLv = slot.getJl();
        } else if (NEquipUpGradeType.EQUIQ__DL.getType() == type) {
            tempLv = slot.getDl();
        } else if (NEquipUpGradeType.EQUIQ__BS.getType() == type) {
            tempLv = slot.getBs();
        }
        return tempLv;
    }

    /**
     * 通过不同种的升级 类型获取对应的大师 类型
     *
     * @param type
     * @return
     */
    private int getDSBytype(byte type) {
        if (NEquipUpGradeType.EQUIP_QH.getType() == type) {
            return NEquipUpGradeType.EQUIQ__QHDS.getType();
        } else if (NEquipUpGradeType.EQUIQ__JL.getType() == type) {
            return NEquipUpGradeType.EQUIQ__JLDS.getType();
        } else if (NEquipUpGradeType.EQUIQ__DL.getType() == type) {
            return NEquipUpGradeType.EQUIQ__DLDS.getType();
        } else if (NEquipUpGradeType.EQUIQ__BS.getType() == type) {
            return NEquipUpGradeType.EQUIQ__BSDS.getType();
        }
        return 0;
    }

    /**
     * 穿装备
     *
     * @param request
     */
    public void processWearEquip(Message request) {
        int[][] best = checkBagGoodsEquip();
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        for (int i = 0; i < best.length; ++i) {
            if (best[i][0] == 0) {
                continue;
            }
            boolean isChange = false;
            if (player.getWearEquip()[i] != 0) {
                NEquipData equipData = NGoodModel.getNEquipDataById(player.getWearEquip()[i]);
                int power = GameCommon.calculationFighting(equipData.getAttr());
                if (best[i][1] > power) {
                    gameRole.getPackManager().addGoods(new DropData(EGoodsType.EQUIP, player.getWearEquip()[i], 1), EGoodsChangeType.TAKEOFF_ADD, enumSet);
                    isChange = true;
                }
            } else {
                isChange = true;
            }
            if (isChange) {
                DropData data = new DropData(EGoodsType.EQUIP, best[i][0], 1);
                System.out.println(EGoodsChangeType.TAKEON_CONSUME);

                gameRole.getPackManager().useGoods(data, EGoodsChangeType.TAKEON_CONSUME, enumSet);
                player.getWearEquip()[i] = (short) best[i][0];
            }
        }

        Message message = getWearEquipMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 检测背包中最好的 可以穿的装备
     */
    private int[][] checkBagGoodsEquip() {
        int[][] temp = new int[EquipDefine.EQUIP_POS_NUM][2];
        for (short id : player.getRoleEquipMap().keySet()) {
            NEquipData equipData = NGoodModel.getNEquipDataById(id);
            if (equipData == null) {
                continue;
            }
            if (!isContion(equipData)) {
                continue;
            }
            if (temp[equipData.getPosition()][0] == 0) {
                temp[equipData.getPosition()][0] = id;
            } else {
                int power = GameCommon.calculationFighting(equipData.getAttr());
                if (power > temp[equipData.getPosition()][1]) {
                    temp[equipData.getPosition()][0] = id;
                    temp[equipData.getPosition()][1] = power;
                }
            }
        }
        return temp;
    }

    /**
     * 是否可以穿上该装备
     *
     * @param curr1
     * @return
     */
    private boolean isContion(NEquipData curr1) {
        if (curr1.getLevel() > player.getLevel()) {
            return false;
        }
        return true;
    }

    /**
     * 打开装备面板
     */
    public void openEquipPanel(Message request) {
        Message message = getWearEquipMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);

    }

    private Message getWearEquipMessage() {
        Message message = new Message(EMessage.OPEN_EQUIP_PANEL_MESSAGE.CMD());
        for (short id : player.getWearEquip()) {
            message.setShort(id);
        }
        return message;
    }

    /**
     * 557 人物装备一键熔炼
     *
     * @param request
     */
    public void processMeltingOneKeyRole(Message request) {
        if (player.getVipLevel() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }
        this.rongLianEquip(request);
    }


    private void rongLianEquip(Message request) {

        List<Short> results = new ArrayList<>();
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        for (short id : player.getRoleEquipMap().keySet()) {
            NEquipData equipData = NGoodModel.getNEquipDataById(id);
            if (equipData == null) {
                continue;
            }
            if (equipData.getQuality() > EGoodsQuality.PURPLE.getValue()) {
                continue;
            }
            player.getRoleEquipMap().remove(id);
            results.add(id);
            gameRole.getPackManager().addGoods(equipData.getBreakItem(), EGoodsChangeType.EQUIPBAG_PICKUP_ADD, enumSet);

        }
        Message message = new Message(EMessage.EQUIP_STRENGTH_ONEKEY_MESSAGE.CMD(), request.getChannel());
        message.setByte(results.size());
        for (Short result : results) {
            message.setShort(result);
        }

        gameRole.sendMessage(message);
        enumSet.add(EPlayerSaveType.EQUIP);
        gameRole.savePlayer(enumSet);
    }


}
