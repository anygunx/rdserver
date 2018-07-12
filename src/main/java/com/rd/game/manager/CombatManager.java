package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.combat.CombatSystem;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.CombatDef;
import com.rd.define.EGoodsChangeType;
import com.rd.define.EMapType;
import com.rd.define.FightDefine;
import com.rd.game.GameRole;
import com.rd.model.MapModel;
import com.rd.model.data.MapStageData;
import com.rd.net.message.Message;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月17日下午1:08:55
 */
public class CombatManager {

    private GameRole role;
    private Player player;

    private long stamp;

    private byte monsterResult = CombatDef.LOSE;
    private List<DropData> monsterReward = new ArrayList<>();

    private byte bossResult = CombatDef.LOSE;
    private List<DropData> bossReward = new ArrayList<>();

    private EnumSet<EPlayerSaveType> save = EnumSet.noneOf(EPlayerSaveType.class);
    private int monsterCount = 0;

    public CombatManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    public void onReset() {
        this.stamp = 0;
    }

    public void processCombatStageMonsterBegin(Message request) {

        if (this.stamp == 0) {
            this.stamp = System.currentTimeMillis() + CombatDef.INTERVAL;
            role.sendTick(request);
            return;
        } else if (this.stamp > System.currentTimeMillis()) {

            role.sendTick(request);
            return;
        }
        this.stamp = System.currentTimeMillis() + CombatDef.INTERVAL;

        if (this.inDungeon()) {
            role.sendTick(request);
            return;
        }

        player.setMapType(EMapType.FIELD_NORMAL);

        MapStageData data = MapModel.getMapStageData(player.getMapId(), player.getMapStageId());

        Message message = new Message(request.getCmdId(), request.getChannel());

        //short[] monster = new short[RandomUtils.nextInt(4, data.getMonster().length)];
        int[] monster = new int[RandomUtils.nextInt(1, 2)];
        for (int i = 0; i < monster.length; ++i) {
            monster[i] = data.getMonster()[RandomUtils.nextInt(0, data.getMonster().length)];
        }

        monsterResult = CombatSystem.pveStageMonster(message, player, monster, CombatDef.ROUND_FIVE) ? CombatDef.WIN : CombatDef.LOSE; //[min,max)

        role.sendMessage(message);

        if (monsterResult == CombatDef.WIN) {
            monsterReward.clear();
            monsterReward.add(data.getExp());
            monsterReward.add(data.getGold());
        }
    }

    public void processCombatStageMonsterEnd(Message request) {

        if (monsterResult == CombatDef.WIN && player.getMapWave() < FightDefine.MAP_WAVE_NUM) {
            player.addMapWave();
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(monsterResult);
        if (monsterResult == CombatDef.WIN) {
            message.setByte(player.getMapWave());
            message.setByte(monsterReward.size());
            for (DropData data : monsterReward) {
                data.getMessage(message);
            }

            role.getPackManager().addGoods(monsterReward, EGoodsChangeType.FIGHT_BRUSH_MONSTER_ADD, save);
            if (monsterCount % 5 == 0) {
                save.add(EPlayerSaveType.RICHANG);
                role.savePlayer(save);

                save.clear();
            } else {
                ++monsterCount;
            }
        }
        role.getPlayer().getNrcData().addYewaiCount((short) 1);
        role.sendMessage(message);

        clearMonster();
    }

    private void clearMonster() {
        stamp = System.currentTimeMillis() + CombatDef.INTERVAL;
        monsterResult = CombatDef.LOSE;
        monsterReward.clear();
    }

    public void processCombatStageBossBegin(Message request) {
        if (this.inDungeon()) {
            role.sendTick(request);
            return;
        }

        //未达到打boss条件，随机打小怪
        if (player.getMapWave() >= 3 || player.getMapWave() < FightDefine.MAP_WAVE_NUM) {
            processCombatStageMonsterBegin(request);
            return;
        }

        player.setMapType(EMapType.FIELD_BOSS);

        MapStageData data = MapModel.getMapStageData(player.getMapId(), player.getMapStageId());

        Message message = new Message(request.getCmdId(), request.getChannel());

        bossResult = CombatSystem.pveStageBoss(message, player, data.getBoss(), data.getMonster(), CombatDef.ROUND_FIVE) ? CombatDef.WIN : CombatDef.LOSE; //[min,max)

        role.sendMessage(message);

        if (bossResult == CombatDef.WIN) {
            bossReward.clear();
            bossReward.add(data.getExp());
            bossReward.add(data.getGold());
        }
    }

    public void processCombatStageBossEnd(Message request) {
        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(bossResult);
        if (bossResult == CombatDef.WIN) {
            MapStageData data = MapModel.getMapStageData(player.getMapId(), player.getMapStageId());

            player.setMapWave((byte) 3);
            //player.setMapWave((byte)0);
            //player.addMapStageId();

            //save.add(EPlayerSaveType.MAPSTAGEID);
//			if(data.isEnd()){
//				player.addMapId();
//				save.add(EPlayerSaveType.MAPID);
//			}
            role.getPackManager().addGoods(bossReward, EGoodsChangeType.FIGHT_BRUSH_BOSS_ADD, save);
            role.savePlayer(save);
            save.clear();

            message.setShort(player.getMapId());
            message.setShort(player.getMapStageId());
            message.setByte(bossReward.size());
            for (DropData reward : bossReward) {
                reward.getMessage(message);
            }
        }
        role.sendMessage(message);

        clearBoss();
    }

    private void clearBoss() {
        stamp = System.currentTimeMillis() + CombatDef.INTERVAL;
        bossResult = CombatDef.LOSE;
        bossReward.clear();
    }

    /**
     * 是否在副本中
     *
     * @return
     */
    public boolean inDungeon() {
        if (EMapType.FIELD_NORMAL == player.getMapType() || EMapType.FIELD_BOSS == player.getMapType()) {
            return false;
        }
        return true;
    }

    /***
     *
     * 切换地图
     */
    public void processCombatChangeMap(Message request) {
        if (player.isTongGuan()) {
            return;
        }
        player.addMapId();
        player.addMapStageId();
        player.setMapWave((byte) 0);
        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setShort(player.getMapId());
        message.setShort(player.getMapStageId());
        //message.setByte(player.getMapWave());
        role.sendMessage(message);
        save.add(EPlayerSaveType.MAPID);
        save.add(EPlayerSaveType.MAPSTAGEID);
        role.savePlayer(save);

    }
}
