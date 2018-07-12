package com.rd.game.local;

import com.rd.bean.player.BattlePlayer;
import com.rd.bean.player.Player;
import com.rd.define.GameDefine;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏服向PVP服务器发送HTTP请求的管理器
 *
 * @author U-Demon Created on 2017年5月16日 下午6:24:16
 * @version 1.0.0
 */
public class GameHttpManager {

    private static final Logger logger = Logger.getLogger(GameHttpManager.class);

    public static final String SPLIT = "&@!";

    public static GameHttpManager gi() {
        return instance;
    }

    private static final GameHttpManager instance = new GameHttpManager();

    private GameHttpManager() {
    }

    /**
     * PvP玩家信息
     *
     * @param player
     * @return
     */
    public String sendBattlePlayerInfo(Player player) {
        BattlePlayer bp = new BattlePlayer(player);
        Map<String, String> params = new HashMap<>();
        params.put("BattlePlayer", StringUtil.obj2Gson(bp));
        return sendG2PUrl("BattlePlayerInfo", params);
    }

    public String getArenaRankInfo(Player player, boolean refresh) {
        Map<String, String> params = new HashMap<>();
        params.put("serverId", GameDefine.getServerId() + "");
        params.put("id", player.getId() + "");
        params.put("refresh", refresh + "");
        return sendG2PUrl("ArenaRankInfo", params);
    }

    public String arenaBattleFight(Player player, int rank, int selfRank) {
        Map<String, String> params = new HashMap<>();
        params.put("serverId", GameDefine.getServerId() + "");
        params.put("selfId", player.getId() + "");
        params.put("rank", rank + "");
        params.put("selfRank", selfRank + "");
        return sendG2PUrl("ArenaBattleFight", params);
    }

    public String arenaRankList() {
        Map<String, String> params = new HashMap<>();
        params.put("serverId", GameDefine.getServerId() + "");
        return sendG2PUrl("ArenaRankList", params);
    }

    public String arenaRankTop() {
        Map<String, String> params = new HashMap<>();
        params.put("serverId", GameDefine.getServerId() + "");
        return sendG2PUrl("ArenaRankTop", params);
    }

    /**
     * 游戏服向PVP发送请求
     *
     * @param action
     * @param params
     */
    private String sendG2PUrl(String action, Map<String, String> params) {
        StringBuilder url = new StringBuilder();
        url.append("http://").append(GameDefine.PVPURL).append("/").append(action);
        String result = HttpUtil.sendHttpGet(url.toString(), ".do", params);
        return result;
    }

}
