package com.rd.bean.team;

import com.rd.bean.player.Player;
import com.rd.define.ErrorDef;
import com.rd.define.TeamDef;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月9日下午3:54:18
 */
public class Team {

    private List<Player> member;

    public Team(Player player) {
        this.member = new ArrayList<>();
        this.member.add(player);
    }

    public List<Player> getMember() {
        return member;
    }

    public short join(Player player) {
        if (this.member.size() >= TeamDef.MAXIMUM) {
            return ErrorDef.TEAM_MEM_FULL;
        }
        for (Player p : member) {
            if (p.getId() == player.getId()) {
                return ErrorDef.NONE;
            }
        }
        this.member.add(player);
        return ErrorDef.NONE;
    }

    public void getMessage(Message message) {
        message.setByte(this.member.size());
        for (Player player : this.member) {
            player.getTeamMessage(message);
        }
    }

    public boolean kick(int pid) {
        if (this.member.size() < 2) {
            return false;
        }
        for (int i = 1; i < this.member.size(); ++i) {
            if (this.member.get(i).getId() == pid) {
                this.member.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean exit(int pid) {
        for (int i = 0; i < this.member.size(); ++i) {
            if (this.member.get(i).getId() == pid) {
                this.member.remove(i);
                return true;
            }
        }
        return false;
    }
}
