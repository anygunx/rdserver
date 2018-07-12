package com.rd.game.manager;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 元魂管理器
 *
 * @author Created by U-Demon on 2016年11月9日 下午7:26:26
 * @version 1.0.0
 */
public class SpiritManager {

    private GameRole role;

    public SpiritManager(GameRole role) {
        this.role = role;
    }

    /**
     * 元魂升级
     *
     * @param request
     */
    public void processSpiritUpgrade(Message request) {
//		//角色索引
//		byte idx = request.readByte();
//		//升级的位置
//		byte pos = request.readByte();
//		Character cha = role.getPlayer().getCharacter(idx);
//		if (cha == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//升级的元魂
//		Spirit sp = cha.getSpirit().get(pos);
//		if (sp == null)
//		{
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//原型数据
//		SpiritModelData model = SpiritModel.getSpirit(sp.getG());
//		//元魂下一级数据
//		SpiritModelData nextModel = SpiritModel.getSpiritTQL(model.getType(), model.getPinzhi(), model.getLv()+1);
//		if (nextModel == null)
//		{
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_SPIRIT_LV_MAX);
//			return;
//		}
//		//元魂等级数据
//		SpiritUp upLog = new SpiritUp();
//		upLog.setSid(sp.getG());
//		upLog.setOldLv(idx);
//		upLog.setOldExp(pos);
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		//消耗元神碎片
//		if (!role.getPackManager().useGoods(nextModel.getCost(), EGoodsChangeType.SPIRIT_UPGRADE_CONSUME, saves))
//		{
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		upLog.setTotalExp(nextModel.getCost().getN());
//		sp.setG(nextModel.getId());
//		
//		//role.getEventManager().notifyEvent(new GameEvent(EGameEventType.SPIRIT_UP, 1,saves));
//		
//		Message msg = new Message(MessageCommand.SPIRIT_UPGRADE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(pos);
//		msg.setShort(sp.getG());
//		role.sendMessage(msg);
//		//保存数据
//		saves.add(EPlayerSaveType.CHA_SPIRIT);
//		role.saveData(idx, saves);
//		upLog.setNewLv(sp.getG());
//		upLog.setNewExp(sp.getD());
//		LogUtil.log(role.getPlayer(), upLog);
    }

    /**
     * 装备元神
     * @param request
     */
/*	public void processSpiritActive(Message request)
    {
		//角色索引
		byte idx = request.readByte();
		//升级的位置
		byte pos = request.readByte();
		//ID
		int id = request.readInt();
		//解锁
		if (role.getPlayer().getRein() < pos+1 && role.getPlayer().getVipLevel() < pos+1){
			role.sendErrorTipMessage(request, ErrorDefine.ERROR_SPIRIT_LV_LIMIT);
			return;
		}
		Character cha = role.getPlayer().getCharacter(idx);
		if (cha == null) {
			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
			return;
		}
		//元神
		Spirit spirit = role.getPlayer().getSpirits().get(id);
		if (spirit == null) {
			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
			return;
		}
		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_SPIRIT);
		//卸下原来元神位置上的
		Spirit active = cha.getSpirit().get(pos);
		if (active != null) {
			cha.getSpirit().put(pos, null);
			DropData add = new DropData(EGoodsType.SPIRIT, active.getG(), 1);
			role.getPackManager().addGoods(add, EGoodsChangeType.SPIRIT_EQUIP_ADD, saves);
		}
		//扣除背包中的元神
		DropData cost = new DropData(EGoodsType.SPIRIT, id, 1);
		if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SPIRIT_EQUIP_CONSUME, saves)) {
			role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
			return;
		}
		//装上
		cha.getSpirit().put(pos, spirit);
		
		//role.getEventManager().notifyEvent(new GameEvent(EGameEventType.SPIRIT_ACTIVE, 1,saves));
		
		//发送消息
		Message msg = new Message(MessageCommand.SPIRIT_ACTIVE_MESSAGE, request.getChannel());
		msg.setByte(idx);
		msg.setByte(pos);
		msg.setShort(spirit.getG());
		role.sendMessage(msg);
		//保存数据
		role.saveData(idx, saves);
	}
*/
    /**
     * 元神分解
     * @param request
     */
/*	public void processSpiritRes(Message request) {
		short size = request.readShort();
		if (size == 0)
			return;
		//获得碎片数
		int num = 0;
		SpiritModelData model = null;
		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
		for (int i = 0; i < size; i++) {
			int id = request.readInt();
			Spirit sp = role.getPlayer().getSpirits().get(id);
			if (sp == null)
				continue;
			model = SpiritModel.getSpirit(sp.getG());
			if (model == null)
				continue;
			DropData cost = new DropData(EGoodsType.SPIRIT, id, 1);
			if (role.getPackManager().useGoods(cost, EGoodsChangeType.SPIRIT_RES_CONSUME, saves)) {
				num += model.getFenjie().getN();
			}
		}
		//获得碎片
		DropData add = new DropData(model.getFenjie().getT(), model.getFenjie().getG(), num);
		role.getPackManager().addGoods(add, EGoodsChangeType.SPIRIT_RES_ADD, saves);
		role.sendTick(request);
		role.savePlayer(saves);
	}
*/

    /**
     * 获取背包中元魂的数量
     *
     * @return
     */
    public int getSpiritBagNum() {
        return role.getPlayer().getSpirits().size();
    }

    /**
     * 元魂一键装备
     * @param request
     */
//	public void processAutoActive(Message request)
//	{
//		List<Spirit> actives = new ArrayList<>();
//		//发送消息
//		Message msg = new Message(MessageCommand.SPIRIT_AUTO_ACTIVE, request.getChannel());
//		msg.setByte(actives.size());
//		for (Spirit sp : actives)
//		{
//			msg.setByte(sp.getP());
//			msg.setInt(sp.getD());
//		}
//	}

    /**
     * 元魂求签
     * @param request
     */
//	public void processSpiritLottery(Message request)
//	{
//		byte type = request.readByte();
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		int spiritData = role.getPlayer().getSpiritIndex();
//		//神签
//		if (type == GameDefine.SPIRIT_LOTTERY_GOD)
//		{
//			if (spiritData % 10 >= GameDefine.SPIRIT_GOD_INDEX)
//			{
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//				return;
//			}
//			//消耗
//			if (!role.getPackManager().useGoldAndDiamond(GameDefine.SPIRIT_GOD_COST, 
//					EGoodsChangeType.SPIRIT_LOTTERY_CONSUME))
//			{
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			spiritData -= spiritData % 10;
//			spiritData += GameDefine.SPIRIT_GOD_INDEX;
//			role.getPlayer().setSpiritIndex(spiritData);
//		}
//		//普通求签
//		else if (type == GameDefine.SPIRIT_LOTTERY_NORMAL)
//		{
//			SpiritLotteryModelData lotteryData = SpiritModel.getLotteryData(spiritData % 10 + 1);
//			if (lotteryData == null)
//				lotteryData = SpiritModel.getLotteryData(1);
//			DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_SPIRIT, lotteryData.getCost());
//			//消耗
//			if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SPIRIT_LOTTERY_CONSUME, saves))
//			{
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			//首次抽取上上签
//			DropData reward = null;
//			if (firstGod())
//			{
//				//抽取橙色元魂
//				int id = SpiritModel.getRandomOrange();
//				reward = new DropData(EGoodsType.SPIRIT, id, 1);
//			}
//			//正常抽取掉落组
//			else
//			{
//				DropGroupData dropGroupData = DropModel.getDropGroupData(lotteryData.getGain());
//				reward = dropGroupData.getRandomDrop().get(0);
//			}
//			role.getPackManager().addGoods(reward, EGoodsChangeType.SPIRIT_LOTTERY_ADD, saves);
//			//求签索引变化
//			changeIndex(lotteryData);
//			//求签次数增加
//			addCount(1);
//			SpiritModelData model = SpiritModel.getSpirit(reward.getG());
//			if (model != null)
//			{
//				if (model.getQuality() == EGoodsQuality.PURPLE.getValue())
//					role.getEventManager().notifyEvent(new GameEvent(EGameEventType.SPIRIT_PURE,1));
//				else if (model.getQuality() == EGoodsQuality.ORANGE.getValue())
//					role.getEventManager().notifyEvent(new GameEvent(EGameEventType.SPIRIT_ORANGE,1));
//			}
//		}
//		else
//		{
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//通知元神聚魂消息
//		role.getEventManager().notifyEvent(new GameEvent(EGameEventType.SPIRIT_LOTTERY,1));
//		//发送消息
//		Message msg = new Message(MessageCommand.SPIRIT_LOTTERY_MESSAGE, request.getChannel());
//		msg.setByte(role.getPlayer().getSpiritIndex() % 10);
//		role.sendMessage(msg);
//		//保存数据
//		saves.add(EPlayerSaveType.SPIRITINDEX);
//		role.savePlayer(saves);
//	}

//	public void addCount(int add)
//	{
//		int spiritData = role.getPlayer().getSpiritIndex();
//		role.getPlayer().setSpiritIndex(spiritData+add*100);
//	}
//	public void changeIndex(SpiritLotteryModelData lotteryData)
//	{
//		int spiritData = role.getPlayer().getSpiritIndex();
//		int rate = GameUtil.getRangedRandom(1, 100);
//		//归零
//		if (rate > lotteryData.getRate() || spiritData % 10 >= GameDefine.SPIRIT_INDEXS.length - 1)
//		{
//			spiritData -= spiritData % 10;
//		}
//		//增加
//		else
//		{			
//			spiritData++;
//		}
//		role.getPlayer().setSpiritIndex(spiritData);
//	}
    /**
     * 进行首次抽取上上签
     * @return
     */
//	public boolean firstGod()
//	{
//		int spiritData = role.getPlayer().getSpiritIndex();
//		int god = spiritData % 100 / 10;
//		if (god == 0 && spiritData % 10 >= GameDefine.SPIRIT_INDEXS.length - 1)
//		{
//			role.getPlayer().setSpiritIndex(spiritData+10);
//			return true;
//		}
//		return false;
//	}

}
