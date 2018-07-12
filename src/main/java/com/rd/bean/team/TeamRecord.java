package com.rd.bean.team;

import com.rd.bean.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月21日上午11:19:51
 */
public class TeamRecord {

    private List<String> name = new ArrayList<>();

    private byte round;

    private int time;

    public TeamRecord(Team team, byte round) {
        this.update(team, round);
    }

    public void update(Team team, byte round) {
        this.name = new ArrayList<>();
        for (Player player : team.getMember()) {
            this.name.add(player.getName());
        }
        this.round = round;
        this.time = (int) (System.currentTimeMillis() / 1000);
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public byte getRound() {
        return round;
    }

    public void setRound(byte round) {
        this.round = round;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
