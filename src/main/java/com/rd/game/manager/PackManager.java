package com.rd.game.manager;

import com.google.common.base.Preconditions;
import com.rd.bean.auction.AuctionItemData;
import com.rd.bean.comm.BaseRandomData;
import com.rd.bean.drop.DropData;
import com.rd.bean.drop.DropGroupData;
import com.rd.bean.goods.*;
import com.rd.bean.goods.data.AuctionBoxData;
import com.rd.bean.goods.data.BoxData;
import com.rd.bean.player.Player;
import com.rd.common.GameCommon;
import com.rd.common.goods.DiamondCmd;
import com.rd.common.goods.EGoodsType;
import com.rd.common.goods.GoldCmd;
import com.rd.common.goods.IGoodsCmd;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.AuctionService;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.model.AuctionModel;
import com.rd.model.DropModel;
import com.rd.model.GoodsModel;
import com.rd.model.TitleModel;
import com.rd.model.data.AuctionItemModelData;
import com.rd.model.data.TitleModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.LogUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 背包管理器
 *
 * @author Created by U-Demon on 2016年11月1日 上午10:28:26
 * @version 1.0.0
 */
public class PackManager {
    static Logger log = Logger.getLogger(PackManager.class.getName());

    private GameRole gameRole;
    private Player player;

    public PackManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public boolean costCurrency(EGoodsType type, int num, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        DropData data = new DropData(type.getId(), 0, num);
        return useGoods(data, changeType, enumSet);
    }

    public boolean capacityEnough(DropData data) {
        return capacityEnough(Arrays.asList(data));
    }

    public void onEnterGame() {
        updateAuctionBox();
    }

    /**
     * 判断背包容量是否充足
     *
     * @param datas
     * @return
     */
    public boolean capacityEnough(List<DropData> datas) {
        if (datas == null)
            return true;
        //判断容量
        int equipNum = 0;//, spiritNum = 0;
        for (DropData data : datas) {
            if (EGoodsType.EQUIP.getId() == data.getT())
                equipNum++;
//			else if (EGoodsType.SPIRIT.getId() == data.getT())
//				spiritNum++;
        }
        if (equipNum > 0 && player.getRoleEquipMap().size() +
                equipNum > player.getEquipBagMax())
            return false;
//		if (spiritNum > 0 && gameRole.getSpiritManager().getSpiritBagNum() + spiritNum > player.getSpiritBagMax())
//			return false;
        return true;
    }

    /**
     * 得到背包空闲容量
     *
     * @return
     */
    public int getBagFreeCapacity() {
        return player.getEquipBagMax() - player.getRoleEquipMap().size();
    }

    /**
     * 增加物品到背包
     *
     * @param data       物品
     * @param changeType 资源变更途径
     * @param enumSet    保存数据枚举
     */
    public boolean addGoods(DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
        if (cmd == null)
            return false;
        return cmd.reward(gameRole, data, changeType, enumSet);
    }

    /**
     * 增加物品到背包
     *
     * @param data           物品
     * @param changeType     资源变更途径
     * @param enumSet        保存数据枚举
     * @param isNotifyClient 是否通知客户端
     * @return
     */
    public boolean addGoods(DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient) {
        IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
        if (cmd == null)
            return false;
        return cmd.reward(gameRole, data, changeType, enumSet, isNotifyClient);
    }

    /**
     * 增加物品到背包
     *
     * @param datas      物品列表
     * @param changeType 资源变更途径
     * @param enumSet    保存数据枚举
     */
    public boolean addGoods(List<DropData> datas, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        for (DropData data : datas) {
            EGoodsType type = EGoodsType.getGoodsType(data.getT());
            if (type != null) {
                IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
                cmd.reward(gameRole, data, changeType, enumSet);
            }
        }
        return true;
    }

    /**
     * 扣除物品
     *
     * @param data       物品
     * @param changeType 资源变更途径
     * @param enumSet    保存数据枚举
     * @return
     */
    public boolean useGoods(DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return useGoods(data, changeType, enumSet, true);
    }

    /**
     * 扣除物品
     *
     * @param data           物品
     * @param changeType     资源变更途径
     * @param enumSet        保存数据枚举
     * @param isNotifyClient 是否通知客户端
     * @return
     */
    public boolean useGoods(DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient) {
        IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
        if (cmd == null)
            return false;
        return cmd.consume(gameRole, data, changeType, enumSet);
    }

    /**
     * 扣除物品
     *
     * @param datas      物品列表
     * @param changeType 资源变更途径
     * @param enumSet    保存数据枚举
     * @return
     */
    public boolean useGoods(List<DropData> datas, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        //检测物品数量
        for (DropData data : datas) {
            IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
            if (cmd == null)
                return false;
            if (!cmd.validate(gameRole, data))
                return false;
        }
        for (DropData data : datas) {
            IGoodsCmd cmd = EGoodsType.getGoodsType(data.getT()).getCmd();
            cmd.consume(gameRole, data, changeType, enumSet);
        }
        return true;
    }

    /**
     * 优先消耗绑元，再消耗元宝
     *
     * @param cost
     * @param changeType
     * @return
     */
    public boolean useGoldAndDiamond(int cost, EGoodsChangeType changeType) {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        long gold = GoldCmd.gi().getValue(gameRole, null);
        long diamond = DiamondCmd.gi().getValue(gameRole, null);
        //检查货币是否充足
        if (cost > gold && cost > diamond) {
            return false;
        }
        //先扣绑元
        if (gold >= cost) {
            gameRole.getPackManager().useGoods(new DropData(EGoodsType.GOLD, 0, (int) cost), changeType, enumSet);
        }
        //再扣元宝
        else {
            gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, (int) cost), changeType, enumSet);
        }
        gameRole.savePlayer(enumSet);
        return true;
    }

    /**
     * 优先消耗绑元，再消耗元宝，
     *
     * @param cost
     * @param changeType
     * @return
     */
    public boolean useGoldAndDiamondInt(int cost, EGoodsChangeType changeType) {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        long gold = GoldCmd.gi().getValue(gameRole, null);
        long diamond = DiamondCmd.gi().getValue(gameRole, null);
        //检查货币是否充足
        if (cost > gold + diamond) {
            return false;
        }
        //先扣绑元
        long goldCost = gold > cost ? cost : gold;
        if (goldCost > 0) {
            gameRole.getPackManager().useGoods(new DropData(EGoodsType.GOLD, 0, (int) goldCost), changeType, enumSet);
        }
        //再扣元宝
        if (goldCost < cost) {
            int diamondCost = (int) (cost - goldCost);
            gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, diamondCost), changeType, enumSet);
        }
        gameRole.savePlayer(enumSet);
        return true;
    }

    /**
     * 获取掉落品中装备的数量
     *
     * @param drops
     * @return
     */
    public int getItemNumByType(List<DropData> drops, EGoodsType type) {
        int num = 0;
        for (DropData data : drops) {
            if (data.getT() == type.getId())
                num++;
        }
        return num;
    }

    /**
     * 通过ID获取道具
     *
     * @param id
     * @return
     */
    public com.rd.bean.goods.Goods getItemById(short id) {
        for (com.rd.bean.goods.Goods goods : player.getItemList()) {
            if (goods.getD() == id) {
                return goods;
            }
        }
        return null;
    }

    /**
     * 通过ID获取宝物
     *
     * @param id
     * @return
     */
    public Box getBoxById(short id) {
        for (Box goods : player.getBoxList()) {
            if (goods.getD() == id) {
                return goods;
            }
        }
        return null;
    }

    public void removeBoxById(short id) {
        Iterator<Box> it = player.getBoxList().iterator();
        while (it.hasNext()) {
            if (it.next().getD() == id) {
                it.remove();
                break;
            }
        }
    }

    public void removeItemById(short id) {
        Iterator<com.rd.bean.goods.Goods> it = player.getItemList().iterator();
        while (it.hasNext()) {
            if (it.next().getD() == id) {
                it.remove();
                break;
            }
        }
    }

    /**
     * 添加成品装备
     *
     * @param equip
     * @param changeType
     * @return
     */
    public boolean addEquip(short id, EGoodsChangeType changeType) {
        return addEquip(id, changeType, true);
    }

    /**
     * 添加成品装备
     *
     * @param equip
     * @param changeType
     * @return
     */
    public boolean addEquip(short id, EGoodsChangeType changeType, boolean isNotifyClient) {
        Integer num = player.getRoleEquipMap().get(id);
        if (num == null) {
            player.getRoleEquipMap().put(id, 1);
        } else {
            ++num;
            player.getRoleEquipMap().put(id, num);
        }
        if (isNotifyClient) {
            sendAddEquipMsg(id, changeType);
        }
        return true;
    }

    public int costEquip(short id) {
        Integer num = player.getRoleEquipMap().get(id);
        if (num != null) {
            --num;
            if (num <= 0) {
                player.getRoleEquipMap().remove(id);
            } else {
                player.getRoleEquipMap().put(id, num);
            }
        }
        return num;
    }


    /**
     * 使用消耗品
     *
     * @param request
     */
    public void processUseGoods(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        int num = request.readInt();

        if (num < 1) {
            log.error("Cheating players :" + player.getId());
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE, request.getChannel());
        msg.setByte(type);
        msg.setShort(id);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //宝物
        if (type == EGoodsType.BOX.getId()) {
            com.rd.bean.goods.Goods goods = getBoxById(id);
            if (goods == null || goods.getN() < num) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            BoxData box = GoodsModel.getBoxDataById(id);
            if (box.getLevelLimit() > player.getLevel()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
                return;
            }
            if (box.getVipLimit() > player.getVipLevel()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
                return;
            }
            if (box.getLastTime() > 0) {
                updateTimeGoods(curr);
                for (TimeGoods timeGoods : player.getSmallData().getTimeGoodsList()) {
                    if (timeGoods.getType() == box.getType() && timeGoods.getId() == box.getGoodsId()) {
                        if (timeGoods.getTime() < curr) {
                            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                            return;
                        }
                    }
                }
            }
            //次数限制
            if (box.getDayCount() > -1) {
                int count = 0;
                if (player.getDayData().getBoxUsed().containsKey(id))
                    count = player.getDayData().getBoxUsed().get(id);
                if (count + num > box.getDayCount()) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
                    return;
                }
                player.getDayData().getBoxUsed().put(id, count + num);
                saves.add(EPlayerSaveType.DAYDATA);
                //物品使用次数消息
                gameRole.putMessageQueue(player.getDayRefreshMsg());
            }
            //增加
            List<DropData> rewards = new ArrayList<>();
            if (GoodsDefine.BOX_SUB_NORMAL == box.getType()) {
                rewards.addAll(getRewardList(box, num));
            }
            //转生丹
            else if (GoodsDefine.BOX_SUB_REIN == box.getType()) {
                for (DropData data : box.getRewards()) {
                    DropData reward = new DropData(data.getT(), data.getG(), num * data.getN());
                    rewards.add(reward);
                }
            }
            //称号
            else if (GoodsDefine.BOX_SUB_TITLE == box.getType()) {
                TitleModelData model = TitleModel.getCost(id);
                if (model == null) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                    return;
                }
                Long endTime = player.getTitle().get(model.getId());
                //永久
                if (model.getTime() == -1)
                    endTime = -1L;
                    //时效
                else {
                    if (endTime == null || endTime < curr)
                        endTime = curr;
                    endTime += num * model.getTime() * DateUtil.SECOND;
                }
                player.getTitle().put(model.getId(), endTime);
                saves.add(EPlayerSaveType.TITLE);
                gameRole.putMessageQueue(gameRole.getTitleManager().getTitleInfoMsg(model.getId()));
            }
            //坐骑装备
            else if (GoodsDefine.BOX_SUB_MOUNTEQUIP == box.getType()) {
                //判断背包剩余容量
//				if(this.getBagFreeCapacity()<num){
//					num=this.getBagFreeCapacity();
//				}
//				if (num <= 0) {
//					gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
//					return;
//				}
//				for(int i=0;i<num;++i){
//					ArtifactData artifactData=GoodsModel.getArtifactData(box.getHaveNum(), GameCommon.getRandomIndex(GoodsModel.getMountPosSize()), GameCommon.getRandomIndex(0,0,6600,3400));
//					rewards.add(new DropData(EGoodsType.ARTIFACT,artifactData.getGoodsId(),1));
//				}
            } else if (GoodsDefine.BOX_SUB_GIFT == box.getType()) {
                boolean dead = false;
                // 过期验证
                if (!StringUtil.isEmpty(box.getStartTime()) && !box.getStartTime().equals("0")) {
                    Date startDate = DateUtil.parseDataTime(box.getStartTime());
                    if (curr < startDate.getTime()) {
                        dead = true;
                    }
                }
                if (!dead &&
                        !StringUtil.isEmpty(box.getEndTime()) && !box.getEndTime().equals("0")) {
                    Date endDate = DateUtil.parseDataTime(box.getEndTime());
                    if (curr > endDate.getTime()) {
                        dead = true;
                    }
                }
                if (!dead) {
                    rewards.addAll(getRewardList(box, num));
                }
            } else if (GoodsDefine.BOX_SUB_LEVEL_UP_DAN == box.getType()) {
                boolean isUp = false;
                for (int i = 0; i < num; ++i) {
                    if (player.getLevel() < 80) {
                        player.setLevel((short) (player.getLevel() + 1));
                        gameRole.levelUp(saves);
                        isUp = true;
                    } else {
                        if (gameRole.addExp(box.getHaveNum(), saves)) {
                            isUp = true;
                        }
                    }
                }
                gameRole.sendUpdateExpMsg(isUp);
            } else if (GoodsDefine.BOX_SUB_ALL_BOSS_ORDER == box.getType()) {
                gameRole.getBossManager().refreshCizBossCount();
                if (player.getCitBossLeft() >= BossManager.CIT_BOSS_FIGHT_MAX) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ALL_BOSS_COUNT_MAX);
                    return;
                } else {
                    int citBossNum = BossManager.CIT_BOSS_FIGHT_MAX - player.getCitBossLeft();
                    if (citBossNum < num) {
                        num = citBossNum;
                    }
                    player.changeCitBossLeft(box.getHaveNum() * num);
                    saves.add(EPlayerSaveType.CITBOSSLEFT);
                }
            } else if (GoodsDefine.BOX_SUB_MINING_HOE == box.getType()) {
                gameRole.getEscortManager().addCountTimes(box.getHaveNum() * num);
                gameRole.putMessageQueue(gameRole.getEscortManager().getEscortDetail());
            } else if (GoodsDefine.BOX_SUB_MINING_ROBBERY == box.getType()) {
                gameRole.getEscortManager().addRobTimes(box.getHaveNum() * num);
                gameRole.putMessageQueue(gameRole.getEscortManager().getEscortDetail());
            } else if (GoodsDefine.BOX_SUB_COST_DIAMOND == box.getType()) {
                if (EGoodsType.DIAMOND.getCmd().consume(gameRole, new DropData(EGoodsType.DIAMOND.getId(), 0, box.getHaveNum() * num), EGoodsChangeType.GOODS_USE_CONSUME, saves)) {
                    rewards.addAll(getRewardList(box, num));
                } else {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
            } else if (GoodsDefine.BOX_SUB_SELECT == box.getType()) {
                DropData selectReward = box.getRewards().get(request.readByte()).createCopy();
                selectReward.setN(selectReward.getN() * num);
                rewards.add(selectReward);
            } else if (GoodsDefine.BOX_SUB_VIP == box.getType()) {
                player.addVip(box.getHaveNum() * num);
                saves.add(EPlayerSaveType.VIP);
                gameRole.sendUpdateCurrencyMsg(EGoodsType.VIP, EGoodsChangeType.GOODS_USE_ADD);
            }
            //判断背包空间
            int equipNum = 0;
            for (DropData dropData : rewards) {
                if (dropData.getT() == EGoodsType.EQUIP.getId()) {
                    ++equipNum;
                }
            }
            if (equipNum > player.getEquipBagFreeGrid()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
                return;
            }

            //消耗
            gameRole.getPackManager().useGoods(new DropData(type, id, num), EGoodsChangeType.GOODS_USE_CONSUME, saves);
            gameRole.getPackManager().addGoods(rewards, EGoodsChangeType.GOODS_USE_ADD, saves);
            msg.setInt(goods.getN());
        }
        gameRole.sendMessage(msg);
        gameRole.savePlayer(saves);
    }

    private List<DropData> getRewardList(BoxData box, int num) {
        List<DropData> rewardList = new ArrayList<>();
        for (int i = 0; i < num; ++i) {
            DropGroupData dropGroupData = DropModel.getDropGroupData(box.getGainId());
            if (dropGroupData != null) {
                rewardList.addAll(dropGroupData.getRandomDrop());
            }
        }
        for (DropData r : box.getRewards()) {
            DropData ar = new DropData(r.getT(), r.getG(), r.getQ(), num * r.getN());
            rewardList.add(ar);
        }
        return rewardList;
    }

    private void sendAddEquipMsg(short id, EGoodsChangeType changeType) {
        //发送消息
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.EQUIP.getId());
        msg.setShort(id);
        msg.setShort(changeType.getId());
        gameRole.putMessageQueue(msg);
    }

    public void sendUseEquipMsg(short id) {
        Message message = new Message(MessageCommand.GOODS_USE_MESSAGE);
        message.setByte(EGoodsType.EQUIP.getId());
        message.setShort(id);
        message.setInt(1);
        gameRole.putMessageQueue(message);
    }

//	/**
//	 * 添加成品灵器
//	 * @param artifact
//	 * @param changeType
//	 * @return
//	 */
//	public boolean addArtifact(Artifact artifact, EGoodsChangeType changeType)
//	{
//		player.getArtifactList().add(artifact);
//		sendAddArtifactMsg(artifact,changeType);
//		return true;
//	}
//
//	public Artifact costArtifact(int id)
//	{
//		List<Artifact> artifacts = player.getArtifactList();
//		for(int i = 0; i < artifacts.size(); ++i)
//		{
//			Artifact artifact = artifacts.get(i);
//			if(artifact.getD() == id)
//			{
//				artifacts.remove(i);
//				return artifact;
//			}
//		}
//		return null;
//	}
//
//	private void sendAddArtifactMsg(Artifact artifact, EGoodsChangeType changeType)
//	{
//		//发送消息
//		Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
//		msg.setByte(EGoodsType.ARTIFACT.getId());
//		artifact.getMessage(msg);
//		msg.setShort(changeType.getId());
//		gameRole.putMessageQueue(msg);
//	}
//
//	public void sendUseArtifactMsg(Artifact artifact)
//	{
//		Message message = new Message(MessageCommand.GOODS_USE_MESSAGE);
//		message.setByte(EGoodsType.ARTIFACT.getId());
//		message.setShort(artifact.getD());
//		message.setInt(0);
//		gameRole.putMessageQueue(message);
//	}
//

    /**
     * 获取元魂的唯一ID
     *
     * @return
     */
    public int getSpiritNewId() {
        int id = 1;
        for (Spirit sp : player.getSpirits().values()) {
            if (sp.getD() >= id) {
                id = sp.getD();
                id++;
            }
        }
        return id;
    }

    /**
     * 获取战纹的唯一ID
     *
     * @return
     */
    public int getZhanWenNewId() {
        int id = 1;
        for (ZhanWen zw : player.getZhanWens().values()) {
            if (zw.getD() >= id) {
                id = zw.getD();
                id++;
            }
        }
        return id;
    }

//	/**
//	 * 通过唯一ID获取灵器
//	 * @param id
//	 * @return
//	 */
//	public Artifact getArtifactById(short id)
//	{
//		for(Artifact equip : player.getArtifactList())
//		{
//			if(equip.getD() == id)
//			{
//				return equip;
//			}
//		}
//		return null;
//	}
//

    /**
     * 更新限时物品
     */
    public void updateTimeGoods(long currentTime) {
        //遍历限时物品，到时则删除
        Iterator<TimeGoods> it = player.getSmallData().getTimeGoodsList().iterator();
        while (it.hasNext()) {
            TimeGoods timeGoods = it.next();
            if (timeGoods.getTime() < currentTime) {
                if (EGoodsType.ITEM.getId() == timeGoods.getType()) {
                    com.rd.bean.goods.Goods goods = this.getItemById(timeGoods.getId());
                    if (goods != null) {
                        this.removeItemById(timeGoods.getId());
                    }
                    it.remove();
                } else if (EGoodsType.BOX.getId() == timeGoods.getType()) {
                    com.rd.bean.goods.Goods goods = this.getBoxById(timeGoods.getId());
                    if (goods != null) {
                        this.removeBoxById(timeGoods.getId());
                    }
                    it.remove();
                }
            }
        }
    }

    /**************************************************** 拍卖宝箱相关 *********************************************************/
    /**
     * 刷新拍卖宝箱
     */
    private void updateAuctionBox() {
        Set<Long> deadSet = new HashSet<>();
        for (AuctionBox box : player.getAuctionBox().values()) {
            if (box.isDead()) {
                deadSet.add(box.getUid());
            }
        }
        for (Long id : deadSet) {
            removeAuctionBox(id, EGoodsChangeType.GOODS_TIME_LIMIT, false);
        }
    }

    private void removeAuctionBox(long id, EGoodsChangeType changeType, boolean notifyClient) {
        AuctionBox box = player.getAuctionBox().remove(id);
        if (box == null) {
            return;
        }
        LogUtil.log(player, new Goods(EGoodsType.AUCTION_BOX.getId(), box.getItemId(), 1, changeType.getId()));
        if (notifyClient) {
            putRemoveAuctionBoxMsg(id, changeType);
        }
    }

    public void addAuctionBox(AuctionBox box, EGoodsChangeType changeType) {
        player.getAuctionBox().put(box.getUid(), box);
        //记录玩家装备变化日志
        LogUtil.log(player, new Goods(EGoodsType.AUCTION_BOX.getId(), box.getItemId(), 1, changeType.getId()));
        putAddAuctionBoxMsg(box, changeType);
    }

    private void putRemoveAuctionBoxMsg(long uid, EGoodsChangeType changeType) {
        Message message = new Message(MessageCommand.GOODS_REMOVE_AUCTION_BOX_MESSAGE);
        message.setString(String.valueOf(uid));
        gameRole.putMessageQueue(message);
    }

    private void putAddAuctionBoxMsg(AuctionBox box, EGoodsChangeType changeType) {
        //发送消息
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.AUCTION_BOX.getId());
        box.getMessage(msg);
        msg.setShort(changeType.getId());
        gameRole.putMessageQueue(msg);
    }

    /**
     * 使用拍品宝箱
     *
     * @param request
     */
    public void processUseAuctionBox(Message request) {
        long id = Long.parseLong(request.readString());
        boolean sale = request.readBoolean();
        AuctionBox box = player.getAuctionBox().get(id);
        if (box == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        if (box.isDead()) {
            removeAuctionBox(id, EGoodsChangeType.GOODS_TIME_LIMIT, true);
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        Preconditions.checkArgument(box.getItemId() > 0, "Illegal itemId=" + box.getItemId());
        removeAuctionBox(id, EGoodsChangeType.GOODS_USE_CONSUME, true);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.AUCTION_BOX);
        if (sale) {
            // 生成拍品
            AuctionItemData item = AuctionService.createAuctionItem(EAuctionItemType.Personal, box.getItemId(), gameRole.getPlayerId());
            // 上架
            AuctionService.addItem(EAuction.WorldAuction, item);
        } else {
            AuctionItemModelData modelData = AuctionModel.getData(box.getItemId());
            addGoods(modelData.getGoods(), EGoodsChangeType.GOODS_USE_CONSUME, enumSet);
        }
        gameRole.savePlayer(enumSet);
        gameRole.sendTick(request);
    }

    /**
     * 处理拍卖宝箱预开启
     *
     * @param request
     */
    public void processPreopenAuctionBox(Message request) {
        long id = Long.parseLong(request.readString());
        AuctionBox box = getAuctionBox(id);
        if (box == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        if (box.isDead()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        if (box.getItemId() > 0) {
            // 开过了
            return;
        }
        AuctionBoxData modelData = GoodsModel.getAuctionBox(box.getD());
        BaseRandomData<Short> randomData = GameCommon.getRandomData(modelData.getDataList());
        if (randomData != null) {
            box.setItemId(randomData.getData());
        }
        gameRole.savePlayer(EPlayerSaveType.AUCTION_BOX);
        Message message = new Message(MessageCommand.GOODS_PREOPEN_AUCTION_BOX_MESSAGE, request.getChannel());
        box.getMessage(message);
        gameRole.sendMessage(message);
    }

    private AuctionBox getAuctionBox(Long id) {
        return player.getAuctionBox().get(id);
    }

    /**************************************************** 拍卖宝箱结束 *********************************************************/

    /**************************************************** 神器碎片相关 **********************************************************/
    public com.rd.bean.goods.Goods getArtifactPieces(short id) {
        return player.getArtifactPieces().get(id);
    }

    public void removeArtifactPiecesById(short id) {
        player.getArtifactPieces().remove(id);
    }

    /**************************************************** 神羽装备相关 **********************************************************/
    public com.rd.bean.goods.Goods getWingGod(short id) {
        return player.getWingGods().get(id);
    }

    public void removeWingGod(short id) {
        player.getArtifactPieces().remove(id);
    }

    /****************************************************灵髓碎片相关*********************************************************/
    public com.rd.bean.goods.Goods getLingSuiPieces(short id) {
        return player.getLingSuiPieces().get(id);
    }

    public void removeLingSuiPiecesById(short id) {
        player.getArtifactPieces().remove(id);
    }

    /*****************************************************圣纹装备相关*******************************************************/
    public com.rd.bean.goods.Goods getHolyLines(byte id) {
        return player.getHolyLines().get(id);
    }

    public void removeHolyLines(byte id) {
        player.getHolyLines().remove(id);
    }

    /**************************************************** 卡牌相关 **********************************************************/
    public com.rd.bean.goods.Goods getCardItem(short id) {
        return player.getCardbag().get(id);
    }

    public void removCardItem(short id) {
        player.getCardbag().remove(id);
    }

    public void addCardItem(com.rd.bean.goods.Goods item) {
        player.getCardbag().put(item.getD(), item);
    }
}
