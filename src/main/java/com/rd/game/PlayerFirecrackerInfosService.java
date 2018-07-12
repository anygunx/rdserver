package com.rd.game;

import com.rd.bean.drop.DropData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerFirecrackerInfosService {
    private static final Logger logger = Logger.getLogger(PlayerTurntableInfosService.class);

    /**
     * 幸运鞭炮玩家信息
     */
    private static volatile List<PlayerFirecrackerInfo> playerFirecrackerInfos = new ArrayList<>(20);

    private static volatile AtomicInteger ai = new AtomicInteger(0);

    public static synchronized void addPlayerInfo(PlayerFirecrackerInfo playerFirecrackerInfo) {
        if (playerFirecrackerInfo == null) return;
        int size = playerFirecrackerInfos.size();
        if (size < 20) {
            playerFirecrackerInfos.add(playerFirecrackerInfo);
        }
        if (size == 20) {
            int i = ai.incrementAndGet();
            if (i <= 20) {
                playerFirecrackerInfos.set(i - 1, playerFirecrackerInfo);
            } else {
                ai = new AtomicInteger(0);
            }
        }

    }

    public static List<PlayerFirecrackerInfo> getPlayerTurntableInfos() {
        return playerFirecrackerInfos;
    }

    public static class PlayerFirecrackerInfo {
        int id;
        String playerName;
        DropData reward;

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public DropData getReward() {
            return reward;
        }

        public void setReward(DropData reward) {
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
