package com.rd.model.data.mission;

import com.google.common.collect.ImmutableSet;
import com.rd.bean.drop.DropData;

import java.util.Set;

/**
 * 卡牌任务奖励
 */
public class CardMissionReward {
    private final byte id;
    private final Set<Byte> cardList;
    private final DropData reward;

    public CardMissionReward(byte id, Set<Byte> cardList, DropData reward) {
        this.id = id;
        this.cardList = ImmutableSet.copyOf(cardList);
        this.reward = reward;
    }

    public byte getId() {
        return id;
    }

    public Set<Byte> getCards() {
        return cardList;
    }

    public DropData getReward() {
        return reward;
    }
}
