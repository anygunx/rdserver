package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.NRiChangType;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NRiChangModel;
import com.rd.model.data.richang.NRiChang300Data;
import com.rd.model.data.richang.NZhongKuiData;
import com.rd.model.data.richang.NZuDuiData;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * 日常管理器
 */
public class NRiChangManager {

    private static final Logger logger = Logger.getLogger(NRiChangManager.class.getName());

    private GameRole gameRole;
    private Player player;

    public NRiChangManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }


    /**
     *
     * 日常
     * */
//    public void processRichang(Message request) {
//    	byte type=request.readByte();
//    	if(NRiChangType.NRiChangEnum.getType(type)==null) {
//    		return;
//    	}
//    	if(!isCon(type)) {
//    		return;
//    	}
//    	
//    	Message msg = new Message(EMessage.BOSS_QUANMIN_TIXING.CMD(), request.getChannel());
//    	EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
//		NRiChangData data= NRiChangModel.getNRiChangDataById(id);
//		if(data==null) {
//			return;
//		}
//		List<DropData> rewardList=data.getReward();
//		gameRole.getPackManager().addGoods(rewardList,EGoodsChangeType.FIGHT_DUNGEON_ADD,enumSet); 
//		gameRole.sendMessage(msg);
//    }

    /**
     * 元宝购买次数
     */
    public void processBuyReward(Message request) {
        byte type = request.readByte();
        if (NRiChangType.NRiChangEnum.getType(type) == null) {
            return;
        }
        if (player.getLevel() < NRiChangType.LEVELVIP_LIMITE) {
            return;
        }

        if (!isCon(type)) {
            return;
        }

        Message msg = new Message(EMessage.BOSS_QUANMIN_TIXING.CMD(), request.getChannel());
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        short count = (short) (NRiChangType.YEWAI_LIMITE_MAX - player.getNrcData().getYewaiCount());
        int ybCount = NRiChangType.YUANBAO_PRICE * count;

        DropData cost = new DropData(EGoodsType.DIAMOND, 0, ybCount);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        if (type == NRiChangType.NRiChangEnum.RI_300.getId()) {
            player.getNrcData().setYewaiCount(NRiChangType.YEWAI_LIMITE_MAX);
        } else if (type == NRiChangType.NRiChangEnum.TEAMCOPY.getId()) {
            player.getNrcData().setTeamCopyCount(NRiChangType.TEAM_COPY_LIMITE_MAX);
        }
        player.getNrcData().setYewaiCount(NRiChangType.YEWAI_LIMITE_MAX);
        gameRole.sendMessage(msg);
    }


    /**
     * 是否满足不同类型的条件
     */
    public boolean isCon(byte type) {
        if (type == NRiChangType.NRiChangEnum.RI_300.getId()) {
            if (player.getNrcData().getYewaiCount() >= NRiChangType.YEWAI_LIMITE_MAX) {
                return false;
            }
        } else if (type == NRiChangType.NRiChangEnum.TEAMCOPY.getId()) {

        }
        return true;
    }

    /***
     * 日常300面板
     * */
    public void processRichang300Panel(Message request) {
        Message msg = new Message(EMessage.RICHANG_300_PANEL.CMD(), request.getChannel());
        msg.setShort(player.getNrcData().getYewaiCount());
        short[] lingqu = player.getNrcData().getIsYeWailingqu();
        if (lingqu == null) {
            msg.setByte(0);
        } else {
            msg.setByte(lingqu.length);
            for (short s : lingqu) {
                msg.setByte(s);
            }
        }
        gameRole.sendMessage(msg);
    }

    /**
     * 日常300领取
     */
    public void processRichang300LingQu(Message request) {
        byte id = request.readByte();
        if (id < 0 || id > NRiChangType.RICHANG_300_COUNT_REWARD_INDEX_MAX) {
            return;
        }
        NRiChang300Data data = NRiChangModel.getNRiChang300Data(player.getLevel());
        if (data == null) {
            return;
        }
        int count = data.getTarget()[id];
        if (player.getNrcData().getYewaiCount() < count) {
            return;
        }
        int exp = data.getExps()[id];
        if (player.getNrcData().getIsYeWailingqu() == null) {
            player.getNrcData().setIsYeWailingqu(new short[NRiChangType.RICHANG_300_COUNT_REWARD_INDEX_MAX + 1]);
        }
        player.getNrcData().getIsYeWailingqu()[id] = id;
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.addExp(exp, enumSet);
        List<DropData> reward = data.getRewardList().get(id);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        Message msg = new Message(EMessage.RICHANG_300_LINGQU.CMD(), request.getChannel());
        msg.setByte(id);
        gameRole.sendMessage(msg);
        enumSet.add(EPlayerSaveType.RICHANG);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 重置
     */
    public void reset() {

        player.getNrcData().setYewaiCount((short) 0);
        player.getNrcData().setTeamCopyCount((short) 0);
        player.getNrcData().setZhongdaoCopyCnt(NRiChangType.ZHONGKUI_COPY_COUNT);
        long curr = DateUtil.getDayStartTime(System.currentTimeMillis());
        player.getNrcData().setZhognkuitime(curr + DateUtil.DAY);
    }

    /**
     * 钟馗达标界面
     */
    public void processZhongKuiDaBiaoPanel(Message request) {
        refreshZhongkuiCount();
        Message msg = new Message(EMessage.RICHANG_ZHONGKUI_PANEL.CMD(), request.getChannel());
        msg.setByte(player.getNrcData().getZhongdaoCopyCnt());
        long now = System.currentTimeMillis();
        msg.setLong(now - player.getNrcData().getZhognkuitime());
        msg.setByte(player.getNrcData().getTotalZKCopyCnt());
        msg.setByte(player.getNrcData().getIsLingqu());
        gameRole.sendMessage(msg);
    }


    /**
     * 更新钟馗次数 每隔半个小时
     */
    public void refreshZhongkuiCount() {
        if (player.getNrcData().getZhongdaoCopyCnt() >= NRiChangType.ZHONGKUI_COPY_COUNT) {
            return;
        }
        long curr = System.currentTimeMillis();
        if (player.getNrcData().getZhongdaoCopyCnt() <= 0 && player.getNrcData().getZhognkuitime() <= 0) {
            player.getNrcData().setZhongdaoCopyCnt(NRiChangType.ZHONGKUI_COPY_COUNT);
            player.getNrcData().setZhognkuitime(curr);
        } else {
            long pass = curr - player.getNrcData().getZhognkuitime();
            if (pass > 0) {
                byte count = (byte) (pass / DateUtil.HOUR / 2);
                player.getNrcData().addZhongdaoCopyCnt(count);
                if (player.getNrcData().getZhongdaoCopyCnt() >= NRiChangType.ZHONGKUI_COPY_COUNT) {
                    player.getNrcData().setZhongdaoCopyCnt(NRiChangType.ZHONGKUI_COPY_COUNT);
                    player.getNrcData().setZhognkuitime(curr);
                } else {
                    byte cnt = (byte) (player.getNrcData().getZhongdaoCopyCnt() + count);
                    if (cnt > NRiChangType.ZHONGKUI_COPY_COUNT) {
                        cnt = 5;
                    }
                    player.getNrcData().setZhongdaoCopyCnt(cnt);
                }
            }
        }
    }

    /****
     *
     * 组队面板
     *
     * **/
    public void processZuDuiPanel(Message request) {
        Message msg = new Message(EMessage.RICHANG_TEAM_PANEL.CMD(), request.getChannel());
        //player.setTeamCopyCount((byte)10);
        msg.setByte(player.getNrcData().getTeamCopyCount());
        msg.setByte(player.getNrcData().getIsTeamCopyLingqu());
        msg.setByte(player.getDayData().getCrossDunNum());

        //msg.setByte(player.getIsTeamCopyLingqu());
        gameRole.sendMessage(msg);

    }


    /**
     * 领取组队奖励
     */
    public void processLiangQuZuDui(Message request) {
//	   if(player.getTotalZKCopyCnt()<NRiChangType.ZHONGKUI_TOTAL_COPY_COUNT) {
//		   return;
//	   }
        player.getNrcData().setTeamCopyCount(NRiChangType.TEAM_COPY_LIMITE_MAX);
        if (player.getNrcData().getIsTeamCopyLingqu() > 0) {
            return;
        }

        NZuDuiData data = NRiChangModel.getNZuDuiData(player.getLevel());
        if (data == null) {
            return;
        }

        Message msg = new Message(EMessage.RICHANG_TEAM_LINGQU.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.addExp(data.getExp(), enumSet);
        gameRole.getPackManager().addGoods(data.getReward(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        player.getNrcData().setIsTeamCopyLingqu((byte) 1);
        enumSet.add(EPlayerSaveType.RICHANG);
        gameRole.savePlayer(enumSet);
    }


    /**
     * 领取钟馗奖励
     */
    public void processLiangQUZhongKui(Message request) {
//	   if(player.getTotalZKCopyCnt()<NRiChangType.ZHONGKUI_TOTAL_COPY_COUNT) {
//		   return;
//	   }
//	   if(player.getIsLingqu()>0) {
//		   return;
//	   }
        Message msg = new Message(EMessage.RICHANG_ZHONGKUI_LINGQU.CMD(), request.getChannel());
        msg.setByte(1);
        NZhongKuiData data = NRiChangModel.getNZhongKuiData(player.getLevel());
        if (data == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(data.getReward(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        player.getNrcData().setIsLingqu((byte) 1);
        gameRole.sendMessage(msg);
        enumSet.add(EPlayerSaveType.RICHANG);
        gameRole.savePlayer(enumSet);
    }


    /**
     * 一键完成
     */
    public void processZHongKuiOneKey(Message request) {
//	   if(player.getLevel()<NRiChangType.LEVELVIP_LIMITE) {
//		   return;
//	   }
//	   if(player.getLevel()<NRiChangType.LEVEL_LIMITE_MAX) {
//		   return;
//	   }

        byte type = request.readByte();
        if (NRiChangType.NRiChangEnum.getType(type) == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, getPrice(type));
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        Message msg = new Message(EMessage.RICHANG_ZHONGKUI_ONEKEY.CMD(), request.getChannel());

        updateCount(NRiChangType.NRiChangEnum.getType(type), enumSet);
        msg.setByte(type);
        if (NRiChangType.NRiChangEnum.getType(type) == NRiChangType.NRiChangEnum.ZHONGKUI) {
            msg.setByte(NRiChangType.ZHONGKUI_TOTAL_COPY_COUNT);
        }
        gameRole.sendMessage(msg);
//		if(rewards.size()<1) {
//			return;
//		}
        enumSet.add(EPlayerSaveType.RICHANG);
        gameRole.savePlayer(enumSet);

    }

    /***
     *
     *
     * 获取价格
     */
    private int getPrice(byte type) {
        int price = 0;
        int totalCount = 0;
        int count = 0;
        switch (NRiChangType.NRiChangEnum.getType(type)) {
            case ZHONGKUI:
                price = NRiChangType.ZHONGKUI_COPY_PRICE;
                totalCount = NRiChangType.ZHONGKUI_TOTAL_COPY_COUNT;
                count = player.getNrcData().getTotalZKCopyCnt();
                break;
            case TEAMCOPY:
                price = NRiChangType.TEAM_COPY_PRICE;
                totalCount = NRiChangType.TEAM_COPY_LIMITE_MAX;
                count = player.getNrcData().getTeamCopyCount();
                break;
            case RI_300:
                price = NRiChangType.YEWAI_PRICE;
                totalCount = NRiChangType.YEWAI_LIMITE_MAX;
                count = player.getNrcData().getYewaiCount();
                break;

            default:
                break;
        }

        return (totalCount - count) * price;
    }

    private List<DropData> updateCount(NRiChangType.NRiChangEnum type, EnumSet<EPlayerSaveType> enumSet) {
        List<DropData> temp = new ArrayList<>();
        int exp = 0;
        switch (type) {
            case ZHONGKUI:
                player.getNrcData().setZhongdaoCopyCnt(NRiChangType.ZHONGKUI_COPY_COUNT);
                player.getNrcData().setTotalZKCopyCnt(NRiChangType.ZHONGKUI_TOTAL_COPY_COUNT);
                NZhongKuiData data = NRiChangModel.getNZhongKuiData(player.getLevel());
                if (data != null) {
                    temp.addAll(data.getReward());
                }
                exp = data.getExp();
                player.getNrcData().setIsLingqu((byte) 1);
                break;
            case TEAMCOPY:
                player.getNrcData().setTeamCopyCount(NRiChangType.TEAM_COPY_LIMITE_MAX);
                NZuDuiData teamData = NRiChangModel.getNZuDuiData(player.getLevel());
                if (teamData != null) {
                    temp.addAll(teamData.getReward());
                }
                player.getNrcData().setIsTeamCopyLingqu((byte) 1);
                exp = teamData.getExp();
                break;
            case RI_300:
                player.getNrcData().setYewaiCount(NRiChangType.YEWAI_LIMITE_MAX);
                NRiChang300Data richangData = NRiChangModel.getNRiChang300Data(player.getLevel());
                if (player.getNrcData().getIsYeWailingqu() == null) {
                    player.getNrcData().setIsYeWailingqu(new short[NRiChangType.RICHANG_300_COUNT_REWARD_INDEX_MAX + 1]);
                }
                if (richangData != null) {
                    short i = 0;
                    for (List<DropData> dropDataList : richangData.getRewardList()) {
                        temp.addAll(dropDataList);
                        player.getNrcData().getIsYeWailingqu()[i] = i;
                        i++;
                    }
                }
                //exp=richangData.getExp();
                break;
            default:
                break;
        }

        gameRole.getPackManager().addGoods(temp, EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        if (exp > 0) {
            gameRole.addExp(exp, enumSet);
        }
        return temp;
    }


    /**
     * 组队副本双倍时间点
     */
    private boolean isDoubleTime() {


        // DateUtil.getTimeSecondFormatter().parse("15:00");

        // DateUtil.between1(start, end, middle)
        return true;
    }


}
