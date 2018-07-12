package com.rd.game.manager;


import com.rd.bean.drop.DropData;
import com.rd.bean.player.DayData;
import com.rd.bean.player.JingJiRecord;
import com.rd.bean.player.Player;
import com.rd.bean.rank.NJingJiRank;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.NJingJiChangType;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.NGameRankManager;
import com.rd.model.NJingJiModel;
import com.rd.model.data.jingji.NJJMGroupData;
import com.rd.model.data.jingji.NJJMRandomData;
import com.rd.model.data.jingji.NJingJiAoundData;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/****
 * 竞技场 如果挑战玩家不在线 的话 存在并发的问题
 * @author MyPC
 *
 */
public class NJingJiChangManager {

    private GameRole gameRole;
    private Player player;

    public NJingJiChangManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();

    }


    /**
     * 每隔两个小时
     */
    public void refreshJingJiChangCount() {
        DayData dayData = player.getDayData();
        if (dayData.getjJCCnt() >= NJingJiChangType.Limite_COUNT) {
            return;
        }
        long curr = System.currentTimeMillis();
        if (dayData.getjJCCnt() <= 0 && dayData.getjJTime() <= 0) {
            dayData.setjJCCnt(NJingJiChangType.Limite_COUNT);
            dayData.setjJTime(curr);
        } else {
            long pass = curr - dayData.getjJTime();
            if (pass > 0) {
                byte count = (byte) (pass / DateUtil.HOUR * 2);
                //dayData.setjJCCnt((byte)(dayData.getjJCCnt()+1));
                if (dayData.getjJCCnt() >= NJingJiChangType.Limite_COUNT) {
                    dayData.setjJCCnt(NJingJiChangType.Limite_COUNT);
                    dayData.setjJTime(curr);
                } else {
                    byte cnt = (byte) (dayData.getjJCCnt() + count);
                    if (cnt > NJingJiChangType.Limite_COUNT) {
                        cnt = NJingJiChangType.Limite_COUNT;
                    }
                    dayData.setjJCCnt(cnt);
                }
            }
        }
    }


    public void processJingJiChangPanel(Message request) {
        refreshJingJiChangCount();
        DayData dayData = player.getDayData();
        randomnRank();
        long now = System.currentTimeMillis();
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setShort(player.getCurrJJRank());
        msg.setByte(getBuyCount() - dayData.getjJbuyCnt());
        msg.setByte(dayData.getjJCCnt());
        msg.setLong(DateUtil.HOUR * 2 - (now - dayData.getjJTime()));
        msg.setByte(jingjiPlayer.size());
        for (NJingJiRank pjj : jingjiPlayer) {
            msg.setByte(pjj.getType());
            if (pjj.getType() == NJingJiChangType.TYPE_PLAYER) {
                IGameRole player = GameWorld.getPtr().getGameRole(pjj.getId());
                msg.setInt(1);
                msg.setLong(0);
                msg.setShort(pjj.getRank());
                msg.setString(player.getPlayer().getName());
            } else {
                msg.setInt(pjj.getId());
                msg.setLong(0);
                msg.setShort(pjj.getRank());
            }
        }
        gameRole.sendMessage(msg);

    }


    /***
     * 随机怪物机器人通过名次范围
     */
    public int randomMonster(int min, int max) {
        if (min < 0) {
            min = Math.abs(min);
            max = Math.abs(max);
        }
        if (min == max) {
            return min;
        }
        return RandomUtils.nextInt(min, max);
    }

    public void processJingJiStart(Message request) {
        short rank = request.readShort();
//    	  	if(player.getDayData().getjJCCnt()<1) {
//    	  		gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NONE);
//    	  		return;
//    	  	}
        NJingJiRank data = getNJingJiRank(rank);
        if (data == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NONE);
            return;
        }

        Message msg = new Message(request.getCmdId(), request.getChannel());
        fight = true;
        int otherId = 0;
        if (data.getType() == 1) {
            otherId = data.getId();
        }
        if (fight) {
            NGameRankManager.getInstance().upgradeJingJiRank(player.getId(), player.getCurrJJRank(), otherId, rank);
        }
        player.getDayData().costJJCCnt();
        player.setCurrJJRank(rank);
        List<JingJiRecord> records = player.getJingJiRecords();
        JingJiRecord re = new JingJiRecord();
        byte reult = (byte) (fight ? 1 : 0);
        re.setRe(reult);
        re.setTi(System.currentTimeMillis());
        re.setTy((byte) 0);
        re.setTzt((byte) 1);
        int ra = Math.abs(rank - player.getCurrJJRank());
        re.setRa((byte) ra);
        re.setId(data.getId());
        records.add(re);
        id = 1;
        type = 1;
        this.rank = rank;
        gameRole.sendMessage(msg);
    }

    private boolean fight = false;
    private int id = 0;
    private byte type = 0;
    private short rank = 0;

    public void processJingJiEnd(Message request) {
//    	 if(id<=0||rank<=0) {
//    		 return;
//    	 } 

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, 500), EGoodsChangeType.GANG_DONATE_ADD, enumSet);
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setShort(player.getJingJirank());
        msg.setShort(player.getCurrJJRank());
        msg.setByte(fight ? 1 : 0);
        msg.setShort(500);
        gameRole.sendMessage(msg);
        id = 0;
        type = 0;
        rank = 0;
        gameRole.savePlayer(enumSet);
    }

    public void processMiaoSha(Message request) {
        if (player.getCurrJJRank() > 4999) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NONE);
            return;
        }
        short rank = request.readShort();
        NJingJiRank data = getNJingJiRank(rank);
        if (data == null) {

        }
        player.getDayData().costJJCCnt();
        //player.setCurrJJRank(rank);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, 500), EGoodsChangeType.GANG_DONATE_ADD, enumSet);
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setShort(player.getJingJirank());
        msg.setShort(player.getCurrJJRank());
        msg.setByte(1);
        msg.setShort(500);
        gameRole.sendMessage(msg);
        gameRole.savePlayer(enumSet);

    }

    public NJingJiRank getRank(short rank) {
        NJingJiRank rankData = NGameRankManager.getInstance().getNJingJiRank(rank);
        return rankData;
    }

    public NJingJiRank getNJingJiRank(short rank) {
        NJingJiRank rankData = getRank(rank);
        if (rankData != null) {
            return rankData;
        }
        for (NJingJiRank nJingJiRank : jingjiPlayer) {
            if (nJingJiRank.getRank() == rank) {
                return nJingJiRank;
            }
        }
        return null;
    }

    List<NJingJiRank> jingjiPlayer = new ArrayList<>();

    private void randomnRank() {
        jingjiPlayer.clear();
        int rank = player.getCurrJJRank();
        Map<String, List<NJingJiAoundData>> jingjiAount = NJingJiModel.getJingJiAoundMap();

        List<NJingJiAoundData> currDataList = new ArrayList<>();
        for (List<NJingJiAoundData> data : jingjiAount.values()) {
            NJingJiAoundData indexFirst = data.get(0);
            boolean isHave = rangeInDefined(rank, indexFirst.getFrom_min(), indexFirst.getTo_max());
            if (isHave) {
                currDataList = data;
                break;
            }
        }
        List<Integer> idList = new ArrayList<>();
        for (NJingJiAoundData data : currDataList) {
            int id = randomMonster(data.getRandom_min(), data.getRandom_max());
            int rak = 0;
            if (data.getPos() >= 5) {
                //NJingJiRank playerRank=NGameRankManager.getInstance().getMinRank();
//    			 if(playerRank!=null) {
//    				  rak=playerRank.getRank();
//    			 }else {
                if (rank >= 5000) {
                    rak = 5000 - id;
                } else {
                    rak = rank + id >= 5000 ? 4999 : rank + id;
                }
                // }

            } else {
                if (rank < 5000) {
                    rak = rank - id;
                } else {
                    rak = data.getTo_max() - id;
                }

            }

            idList.add(rak);
        }


        for (int r : idList) {
            NJingJiRank playerRank = NGameRankManager.getInstance().getNJingJiRank(r);
            NJingJiRank jingji = new NJingJiRank();
            if (playerRank == null) {
                int jjrobId = randomJjBotId(r);
                NJJMGroupData data = NJingJiModel.getNJJMGroupDataMap().get(jjrobId);
                jingji.setId(data.getSen_monster_id());
                jingji.setRank(r);
                jingji.setType(NJingJiChangType.TYPE_JIQI);
                jingjiPlayer.add(jingji);
            } else {

                jingjiPlayer.add(playerRank);
            }

        }

    }

    /****
     * 通过排名获取排名所在的范围 然后通过相同范围的排行 随机一个jingjibot 表中的id
     * @param id
     * @return
     */
    private int randomJjBotId(int id) {
        Map<String, NJJMRandomData> rds = NJingJiModel.getNJJMRandomList();
        NJJMRandomData rd = null;
        for (NJJMRandomData data : rds.values()) {
            boolean ishave = rangeInDefined(id, data.getStartnum(), data.getEndnum());
            if (ishave) {
                rd = data;
                break;
            }
        }
        if (rd == null) {
            return 0;
        }
        int nums = rd.getRandom_num_start();
        int nume = rd.getRandom_num_end();
        return randomMonster(nums, nume);

    }


    public boolean rangeInDefined(int rank, int min, int max) {
        return Math.max(min, rank) == Math.min(rank, max);
    }

    public int getBuyCount() {
        List<Integer> list = NJingJiModel.getjingJiNumList();
        int index = 0;
        if (player.getVipLevel() > list.size()) {
            index = list.size() - 1;
        } else if (player.getVipLevel() > 0) {
            index = player.getVipLevel() - 1;
        }
        int count = list.get(index);
        return count;
    }

    /***
     * 购买次数
     * @param request
     */
    public void processBuyCount(Message request) {
        DayData dayData = player.getDayData();
        if (dayData.getjJbuyCnt() >= getBuyCount()) {
            return;
        }
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 500);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
// 		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME,enumSet,false)){
// 			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT); 
// 			return;
// 		}
        dayData.addjJCCnt(NJingJiChangType.Limite_COUNT);
        dayData.addjJbuyCnt();
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setByte(getBuyCount() - dayData.getjJbuyCnt());
        msg.setByte(dayData.getjJCCnt());
        gameRole.sendMessage(msg);
        gameRole.savePlayer(enumSet);
    }

    /***
     * 挑战记录
     * @param request
     */
    public void processJingJiRecords(Message request) {
        List<JingJiRecord> jignjirecord = player.getJingJiRecords();
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setByte(jignjirecord.size());
        for (JingJiRecord re : jignjirecord) {
            msg.setByte(re.getRe());
            msg.setShort(re.getRa());
            msg.setByte(re.getTzt());
            msg.setLong(re.getTi());
            msg.setInt(re.getId());
            IGameRole role = GameWorld.getPtr().getGameRole(re.getId());
            msg.setByte(re.getTy());
            if (re.getTy() == NJingJiChangType.TYPE_PLAYER) {
                if (role != null) {
                    String name = role.getPlayer().getName();
                    msg.setString(name);
                }
            }

        }
        gameRole.sendMessage(msg);
    }


    /***
     * 排行榜
     *
     * 	Size	Byte	排行数量
     Rank	Short	名次
     Id	Int	角色或者机器人id
     Long	Fight	战力
     Type	Byte	 是否是角色 0机器人 1角色
     Name	String	角色名称
     * @param request
     */
    public void processJingJiRank(Message request) {
        List<NJingJiRank> rankList = NJingJiModel.getjingJiRankTop20();
        Message msg = new Message(request.getCmdId(), request.getChannel());
        msg.setByte(rankList.size());
        for (NJingJiRank re : rankList) {
            msg.setShort(re.getRank());
            NJingJiRank play = NGameRankManager.getInstance().getNJingJiRank(re.getRank());
            if (play != null) {
                IGameRole role = GameWorld.getPtr().getGameRole(re.getId());
                msg.setInt(play.getId());
                msg.setLong(role.getPlayer().getFighting());
                msg.setByte(play.getType());
                msg.setString(role.getPlayer().getName());
            } else {
                msg.setInt(re.getId());
                msg.setLong(0);
                msg.setByte(re.getType());
            }
        }
        gameRole.sendMessage(msg);
    }


}
