package com.rd.game;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 元宝王者玩家信息
 *
 * @author wh
 */
public class PlayerTurntableInfosService {
    private static final Logger logger = Logger.getLogger(PlayerTurntableInfosService.class);
    private static final String message = "大吉大利，恭喜至尊 {0} {1} 通过元宝王者获得{2}元宝奖励，美滋滋~";
    /**
     * 元宝王者玩家信息
     */
    private static volatile List<PlayerTurntableInfo> playerTurntableInfos = new ArrayList<>(5);

//    private static volatile AtomicInteger ai = new AtomicInteger(0);

    private static volatile int i = 0;

    public static synchronized void addPlayerInfo(PlayerTurntableInfo playerTurntableInfo) {
        if (playerTurntableInfo == null) return;
        int size = playerTurntableInfos.size();
        if (size < 5) {
            playerTurntableInfos.add(playerTurntableInfo);
        }
        if (size == 5) {
            if (i < 5) {
                playerTurntableInfos.set(i, playerTurntableInfo);
                i++;
            } else {
                i = 0;
                playerTurntableInfos.set(i, playerTurntableInfo);
            }
        }

    }

    public static List<PlayerTurntableInfo> getPlayerTurntableInfos() {
        return playerTurntableInfos;
    }

    public static String getMessage(String argus1, String argus2, String argus3) {
        return MessageFormat.format(message, argus1, argus2, argus3);
    }

    public static class PlayerTurntableInfo {
        int id;
        String playerName;
        int reward;

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public int getReward() {
            return reward;
        }

        public void setReward(int reward) {
            this.reward = reward;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
