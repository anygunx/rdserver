package com.rd.bean.fight.monstersiege;

import com.rd.common.GameCommon;
import com.rd.model.MonsterSiegeModel;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家守城数据
 */
public class PlayerMonsterData implements IGameMonsterAttacker {
    private final int playerId;
    /**
     * 方便排行获取的attacker数据
     **/
    private String name;
    /**
     * 方便排行获取的attacker数据
     **/
    private byte head;
    /**
     * 次数更新时间戳
     **/
    private long ts;
    /**
     * 挑战次数
     **/
    private byte times;
    /**
     * 积分
     **/
    private int score;
    /**
     * 宝箱掩码
     **/
    private byte boxMask;
    /**
     * 历史记录
     **/
    private List<PlayerMonsterRecord> records;


    public PlayerMonsterData(IGameMonsterAttacker attacker) {
        this(attacker.getId(), attacker.getName(), attacker.getHead(),
                0, MonsterSiegeModel.MAX_TIMES, 0, (byte) 0, new ArrayList<>());
    }

    public PlayerMonsterData(int playerId, String name, byte head,
                             long ts, byte times, int score, byte boxMask, List<PlayerMonsterRecord> records) {
        this.playerId = playerId;
        this.name = name;
        this.head = head;
        this.ts = ts;
        this.times = times;
        this.score = score;
        this.boxMask = boxMask;
        this.records = records;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public byte getTimes() {
        return times;
    }

    public void setTimes(byte times) {
        this.times = times;
    }

    /**
     * 减少挑战次数
     * 注：需要提前检测配合
     *
     * @param ts
     */
    public void decTimes(long ts) {
        updateTimes(ts);
        if (times == MonsterSiegeModel.MAX_TIMES) {
            this.ts = ts;
        }
        times--;
    }

    /**
     * 更新挑战次数
     *
     * @param ts
     * @return 是否变化
     */
    public boolean updateTimes(long ts) {
        AbstractMap.SimpleEntry<Byte, Long> timeInfo = calculateAddTimes(ts);
        if (times == timeInfo.getKey()) {
            return false;
        }
        this.times = timeInfo.getKey();
        this.ts = timeInfo.getValue();
        return true;
    }

    /**
     * 返回本次应该回复的次数和更新时间
     * 注：只计算 不会真实更新
     *
     * @return
     */
    public AbstractMap.SimpleEntry<Byte, Long> calculateAddTimes(long ts) {
        byte addTimes = 0;
        long lastTime = this.ts;
        while (addTimes + times < MonsterSiegeModel.MAX_TIMES) {
            // 本次回复时间已到
            long thisTime = lastTime + (addTimes + 1) * MonsterSiegeModel.TIMES_INCREASE_INTERVAL;
            if (thisTime > ts) {
                break;
            }
            addTimes++;
            lastTime = thisTime;
        }
        return new AbstractMap.SimpleEntry<>((byte) (times + addTimes), lastTime);
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int value) {
        this.score += value;
    }

    public byte getBoxMask() {
        return boxMask;
    }

    public void setBoxMask(byte boxMask) {
        this.boxMask = boxMask;
    }

    public boolean isReceived(byte boxId) {
        return GameCommon.getBit2BooleanValue(boxMask, boxId);
    }

    public void receive(byte boxId) {
        boxMask = (byte) GameCommon.setSubValue(boxMask, GameCommon.True, boxId, boxId);
    }

    public List<PlayerMonsterRecord> getRecords() {
        return records;
    }

    public void setRecords(List<PlayerMonsterRecord> records) {
        this.records = records;
    }

    public void getRecordMessage(Message message) {
        message.setByte(records.size());
        for (PlayerMonsterRecord record : records) {
            record.getMessage(message);
        }
    }

    /**
     * 获取玩家攻城信息
     *
     * @param message
     * @param ts
     */
    public void getMessage(Message message, long ts) {
        AbstractMap.SimpleEntry<Byte, Long> timeInfo = calculateAddTimes(ts);
        byte times = timeInfo.getKey();
        int restTime = (times >= MonsterSiegeModel.MAX_TIMES) ?
                0 : (int) ((timeInfo.getValue() + MonsterSiegeModel.TIMES_INCREASE_INTERVAL - ts) / DateUtil.SECOND);

        // 只计算 并不真实恢复 避免此处的db操作
        message.setInt(score);
        message.setByte(times);
        message.setInt(restTime < 0 ? 0 : restTime);
        message.setByte(boxMask);
    }

    public void addRecord(PlayerMonsterRecord record) {
        if (records.size() > MonsterSiegeModel.RECORD_CAPACITY) {
            this.records.remove(0);
        }
        this.records.add(record);
    }

    public String getRecordsJson() {
        return StringUtil.obj2Gson(records);
    }

    @Override
    public int getId() {
        return playerId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte getHead() {
        return head;
    }
}
