package com.rd.action.web.gm;

import com.alibaba.fastjson.JSON;
import com.rd.bean.rank.PlayerRank;
import com.rd.define.ERankType;
import com.rd.game.GameRankManager;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebFilter(filter = "GMGetFightingRank")
public class GMGetFightingRankAction extends WebAction {
    static Logger log = Logger.getLogger(GMGetFightingRankAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        List<PlayerRank> rankList = GameRankManager.getInstance().getGameRanks(ERankType.FIGHTING);
        List<RankData> rank = new ArrayList<>();
        int i = 0;
        for (PlayerRank playerRank : rankList) {
            ++i;
            if (i > 100) {
                break;
            }
            RankData data = new RankData();
            data.serverid = playerRank.getServerId();
            data.uid = playerRank.getId();
            data.nickName = playerRank.getName();
            data.fighting = playerRank.getValue();
            data.rank = i;
            rank.add(data);
        }
        String json = JSON.toJSONString(rank);
        HttpUtil.sendResponse(channel, json);
        log.info("GMGetFightingRank");
    }

    class RankData {
        int serverid;
        int uid;
        String nickName;
        long fighting;
        int rank;

        public int getServerid() {
            return serverid;
        }

        public void setServerid(int serverid) {
            this.serverid = serverid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public long getFighting() {
            return fighting;
        }

        public void setFighting(long fighting) {
            this.fighting = fighting;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }
}
