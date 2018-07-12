package com.rd.model.data.mission;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Created by XingYun on 2017/12/6.
 */
public class TLMissionGroupModelData {
    private final byte id;
    private final Set<Short> missions;
    private final short suit;
    private final long time;

    public TLMissionGroupModelData(byte id, Set<Short> missions, short suit, long time) {
        this.id = id;
        this.missions = ImmutableSet.copyOf(missions);
        this.suit = suit;
        this.time = time;
    }

    public byte getId() {
        return id;
    }

    public Set<Short> getMissions() {
        return missions;
    }

    public short getSuit() {
        return suit;
    }

    public long getTime() {
        return time;
    }
}
