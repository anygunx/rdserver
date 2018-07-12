package com.rd.common;

import com.rd.bean.player.Player;
import com.rd.model.EquipModel;
import com.rd.model.data.CombineRuneData;
import com.rd.model.data.CombineRuneSuitsData;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CombineRuneSkill {

    private boolean isHave;
    private Player player;
    private int damage;
    private int launchTime = 60000;
    private int currTime = 53000;

    public CombineRuneSkill(Player player) {
        this.player = player;
    }

    public void init() {
        this.isHave = this.player.isArtifactBossInvoked((byte) 1);
        if (this.isHave) {
            int temp = 0;
//			for(Character character:player.getCharacterList()){
//				temp+=character.getAttribute()[EAttrType.ATTACK.getId()];
//			}
            Map<Short, Integer> map = new HashMap<>();
            for (int i = 0; i < player.getCombineRune().length; ++i) {
                byte id = player.getCombineRune()[i];
                CombineRuneData data = EquipModel.getCombineRuneData(id);
                if (data == null) {
                    continue;
                }
                Integer num = map.get(data.getLevel());
                if (num == null) {
                    map.put(data.getLevel(), 1);
                } else {
                    ++num;
                    map.put(data.getLevel(), num);
                }
            }
            short level = 0;
            for (Entry<Short, Integer> entry : map.entrySet()) {
                if (entry.getValue() >= 3 && entry.getKey() > level) {
                    level = entry.getKey();
                }
            }
            float f = 4.8f;
            if (level > 0) {
                CombineRuneSuitsData data = EquipModel.getCombineRuneSuitsData(level);
                if (data != null) {
                    f = data.getPvpindamage() / 100;
                }
            }
            this.damage = (int) (temp * f);
        }
    }

    public boolean isHave() {
        return isHave;
    }

    public boolean updateDownTime(int time) {
        this.currTime += time;
        if (this.currTime >= this.launchTime) {
            this.currTime = 0;
            return true;
        }
        return false;
    }

    public int launch() {
        return damage;
    }
}
