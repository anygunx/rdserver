package com.rd.game;

import com.rd.bean.player.Player;
import com.rd.bean.team.Team;
import com.rd.define.ErrorDef;
import com.rd.define.TeamDef;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月9日下午2:09:01
 */
public class GameTeamManager {

    private AtomicInteger TID = new AtomicInteger(0);

    private Map<Integer, Integer> teamMem = new ConcurrentHashMap<>();

    private Map<Byte, Map<Integer, Team>> crossTeam = new ConcurrentHashMap<>();

    private Map<Integer, Map<Integer, Team>> laddTeam = new ConcurrentHashMap<>();

    public static GameTeamManager getPtr() {
        return Nested.teamManger;
    }

    static class Nested {
        static GameTeamManager teamManger = new GameTeamManager();
    }

    public GameTeamManager() {

    }

    private int getOnlyTeamId(int type, int stage) {
        stage = (stage * TeamDef.SIGN_TYPE + type) * TeamDef.SIGN_KEY;
        int id;
        //do{
        id = stage + Math.abs(TID.incrementAndGet()) % Short.MAX_VALUE;
        //}while(teamMem.containsKey(id));
        return id;
    }

    /**
     * 得到队伍ID
     *
     * @param type
     * @param playerId
     * @param dungeonId
     * @return
     */
    public Integer getTeamID(byte type, int playerId, int dungeonId) {
        Integer teamId = teamMem.get(playerId);
        if (teamId != null) {
            Team team = null;
            switch (type) {
                case TeamDef.CROSS:
                    team = this.getCrossTeam((byte) dungeonId, teamId);
                    break;
                case TeamDef.LADD:
                    team = this.getLaddTeam(dungeonId, teamId);
                    break;
            }
            if (team == null) {
                teamMem.remove(playerId);
                return null;
            }
        }
        return teamId;
    }

//-------------------------- 跨服组队 -------------------------- 

    public Map<Integer, Team> getCrossTeam(byte id) {
        Map<Integer, Team> teamMap = crossTeam.get(id);
        if (teamMap == null) {
            teamMap = new HashMap<>();
            crossTeam.put(id, teamMap);
        }
        return crossTeam.get(id);
    }

    public Team getCrossTeam(byte id, int tid) {
        Map<Integer, Team> map = crossTeam.get(id);
        if (map == null) {
            return null;
        }
        return map.get(tid);
    }

    public Team crossTeamStart(byte id, int tid) {
        Map<Integer, Team> map = crossTeam.get(id);
        if (map == null) {
            return null;
        }
        Team team = map.remove(tid);
        if (team != null) {
            for (Player player : team.getMember()) {
                player.getDayData().subCrossDunNum();
                this.teamMem.remove(player.getId());
            }
        }
        return team;
    }

    public short teamCrossKick(byte id, int tid, int pid) {
        Map<Integer, Team> teamMap = crossTeam.get(id);
        if (teamMap == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        if (team.kick(pid)) {
            this.teamMem.remove(pid);
            return ErrorDef.NONE;
        }
        return ErrorDef.PARAMETER;
    }

    public short teamCrossExit(byte id, int tid, int pid) {
        Map<Integer, Team> teamMap = crossTeam.get(id);
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        if (team.exit(pid)) {
            if (team.getMember().isEmpty()) {
                teamMap.remove(tid);
            }
            this.teamMem.remove(pid);
            return ErrorDef.NONE;
        }
        return ErrorDef.PARAMETER;
    }

    private Lock lock = new ReentrantLock();

    public int createCrossTeam(byte id, GameRole role) {
        if (teamMem.containsKey(role.getPlayerId())) {
            return 0;
        }
        Map<Integer, Team> teamMap = crossTeam.get(id);
        if (teamMap == null) {
            teamMap = new HashMap<>();
            crossTeam.put(id, teamMap);
        }
        int tid = this.getOnlyTeamId(TeamDef.CROSS, id);

        teamMap.put(tid, new Team(role.getPlayer()));
        teamMem.put(role.getPlayerId(), tid);
        return tid;
    }

    public short joinCrossTeam(byte id, int tid, GameRole role) {
        if (teamMem.containsKey(role.getPlayerId())) {
            return ErrorDef.TEAM_EXIST;
        }
        Map<Integer, Team> teamMap = crossTeam.get(id);
        if (teamMap == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        short state = team.join(role.getPlayer());
        if (state == ErrorDef.NONE) {
            teamMem.put(role.getPlayerId(), tid);
        }
        return state;
    }

//-------------------------- 生死劫 -------------------------- 	

    private Lock lock1 = new ReentrantLock();

    public Map<Integer, Team> getLaddTeam(int id) {
        Map<Integer, Team> teamMap = laddTeam.get(id);
        if (teamMap == null) {
            lock1.lock();
            teamMap = new HashMap<>();
            laddTeam.put(id, teamMap);
            lock1.unlock();
        }
        return laddTeam.get(id);
    }

    public Team getLaddTeam(int id, int tid) {
        Map<Integer, Team> map = laddTeam.get(id);
        if (map == null) {
            return null;
        }
        return map.get(tid);
    }

    public int createLaddTeam(byte gid, byte sid, GameRole role) {
        if (teamMem.containsKey(role.getPlayerId())) {
            return 0;
        }
        int id = TeamDef.getLaddId(gid, sid);
        Map<Integer, Team> teamMap = laddTeam.get(id);
        if (teamMap == null) {
            teamMap = new HashMap<>();
            laddTeam.put(id, teamMap);
        }
        int tid = this.getOnlyTeamId(TeamDef.LADD, id);

        teamMap.put(tid, new Team(role.getPlayer()));
        teamMem.put(role.getPlayerId(), tid);
        return tid;
    }

    public short joinLaddTeam(byte gid, byte sid, int tid, GameRole role) {
        if (teamMem.containsKey(role.getPlayerId())) {
            return ErrorDef.TEAM_EXIST;
        }
        int id = TeamDef.getLaddId(gid, sid);
        Map<Integer, Team> teamMap = laddTeam.get(id);
        if (teamMap == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        short state = team.join(role.getPlayer());
        if (state == ErrorDef.NONE) {
            teamMem.put(role.getPlayerId(), tid);
        }
        return state;
    }

    public short teamLaddKick(byte gid, byte sid, int tid, int pid) {
        int id = TeamDef.getLaddId(gid, sid);
        Map<Integer, Team> teamMap = laddTeam.get(id);
        if (teamMap == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        if (team.kick(pid)) {
            this.teamMem.remove(pid);
            return ErrorDef.NONE;
        }
        return ErrorDef.PARAMETER;
    }

    public short teamLaddExit(byte gid, byte sid, int tid, int pid) {
        int id = TeamDef.getLaddId(gid, sid);
        Map<Integer, Team> teamMap = laddTeam.get(id);
        Team team = teamMap.get(tid);
        if (team == null) {
            return ErrorDef.TEAM_NON_EXIST;
        }
        if (team.exit(pid)) {
            if (team.getMember().isEmpty()) {
                teamMap.remove(tid);
            }
            this.teamMem.remove(pid);
            return ErrorDef.NONE;
        }
        return ErrorDef.PARAMETER;
    }

    public Team laddTeamStart(int id, int tid) {
        Map<Integer, Team> map = laddTeam.get(id);
        if (map == null) {
            return null;
        }
        Team team = map.remove(tid);
        if (team != null) {
            for (Player player : team.getMember()) {
                this.teamMem.remove(player.getId());
            }
        }
        return team;
    }
}

