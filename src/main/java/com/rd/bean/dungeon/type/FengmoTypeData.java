package com.rd.bean.dungeon.type;

import com.rd.bean.dungeon.IDungeonTypeData;

public class FengmoTypeData implements IDungeonTypeData {
    private boolean received = false;

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
