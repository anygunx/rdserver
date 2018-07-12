package com.rd.game.event;

import com.rd.bean.comm.BanConstructor;
import com.rd.dao.EPlayerSaveType;

import java.util.EnumSet;
import java.util.Set;

public class GameEvent {

    /**
     * 事件类型
     **/
    private EGameEventType type;

    /**
     * 事件数据
     **/
    private int data;

    /**
     * 事件发起者存储的数据集
     **/
    private EnumSet<EPlayerSaveType> eventPlayerSave;

    /**
     * 需要接收者存储的数据集
     **/
    private EnumSet<EPlayerSaveType> selfPlaySave = null;

    /**
     * 游戏事件
     * FIXME 兼容老版本 以后使用Provider构造
     *
     * @param type    事件类型
     * @param data    事件数据
     * @param enumSet 事件发起者存储的数据(发起者不存储，填null)
     */
    @BanConstructor
    public GameEvent(EGameEventType type, int data, EnumSet<EPlayerSaveType> enumSet) {
        this.type = type;
        this.data = data;
        this.eventPlayerSave = enumSet;
    }

    public EGameEventType getType() {
        return type;
    }

    public int getData() {
        return data;
    }

    /**
     * 需要单独实现以支持此方法
     *
     * @return
     */
    public int getTotalData() {
        throw new UnsupportedOperationException();
    }

    public EnumSet<EPlayerSaveType> getSelfPlaySave() {
        return selfPlaySave;
    }

    public EnumSet<EPlayerSaveType> getPlayerSave() {
        if (this.eventPlayerSave != null) {
            return this.eventPlayerSave;
        } else {
            if (this.selfPlaySave == null) {
                this.selfPlaySave = EnumSet.noneOf(EPlayerSaveType.class);
            }
            return this.selfPlaySave;
        }
    }

    public void addPlayerSaveType(EPlayerSaveType type) {
        this.getPlayerSave().add(type);
    }

    public void addPlayerSaveType(Set<EPlayerSaveType> type) {
        this.getPlayerSave().addAll(type);
    }
}
