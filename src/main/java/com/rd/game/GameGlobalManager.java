package com.rd.game;

import com.rd.bean.team.Team;
import com.rd.bean.team.TeamRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月21日上午11:40:31
 */
public class GameGlobalManager {

    private Map<Integer, TeamRecord[]> teamRecordMap = new HashMap<>();

    public static GameGlobalManager getPtr() {
        return Nested.gameGlobalManager;
    }

    private GameGlobalManager() {

    }

    static class Nested {
        static GameGlobalManager gameGlobalManager = new GameGlobalManager();
    }

    public Map<Integer, TeamRecord[]> getTeamRecordMap() {
        return teamRecordMap;
    }

    public TeamRecord[] getLaddRecord(int dungeonId) {
        return teamRecordMap.get(dungeonId);
    }

    public void addLaddRecord(int dungeonId, Team team, byte round) {
        TeamRecord[] record = teamRecordMap.get(dungeonId);
        if (record == null) {
            record = new TeamRecord[2];
            record[0] = new TeamRecord(team, round);
            record[1] = new TeamRecord(team, round);
            teamRecordMap.put(dungeonId, record);
        } else {
            if (record[1].getRound() > round) {
                record[1].update(team, round);
            }
        }
    }
}
