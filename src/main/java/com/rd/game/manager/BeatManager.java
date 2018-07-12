package com.rd.game.manager;

import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * 心跳管理
 *
 * @author U-Demon Created on 2017年4月26日 上午11:40:43
 * @version 1.0.0
 */
public class BeatManager {

    private static final Logger logger = Logger.getLogger(BeatManager.class);

    //误差20%
    private static final float FIGHTING_OFFSET = 0.2f;
    private static final int VALUE_OFFSET = 300000;
    //误差次数
    public static final int CHEAT_MAX = 2;
    public static final long BEAT_TIME_OUT = 20000;

    private GameRole role;

    private int cheat;

    public BeatManager(GameRole role) {
        this.role = role;
    }

    /**
     * 心跳消息
     *
     * @param request
     */
    public void gameBeat(Message request) {
        long curr = System.currentTimeMillis();
        this.role.setLastBeatTime(curr);
        //客户端战力
        long fightingClient = Long.valueOf(request.readString());
        //战力校验
        if (this.isFightingCheat(fightingClient)) {
            //重新计算服务器战力
            this.role.getPlayer().updateFighting();
            //再次校验
            if (this.isFightingCheat(fightingClient)) {
                //计数
                this.cheat++;
            }
        }
        Message msg = new Message(MessageCommand.GAME_BEAT_MESSAGE, request.getChannel());
        //战力校验失败一定次数，踢下线，玩家数据还在线
        if (this.cheat >= CHEAT_MAX) {
            logger.error("作弊警告==============玩家：" + role.getPlayerId() + ",客户端战力：" +
                    fightingClient + ",服务器战力：" + this.role.getPlayer().getFighting());
            msg.setBool(false);

            //role.getPlayer().setState(GameDefine.PLAYER_STATE_FREEZE);
            //role.getDbManager().playerDao.freeze(role.getPlayerId());
        } else {
            msg.setBool(true);
        }
        this.role.sendMessage(msg);
    }

    public void setCheat(int cheat) {
        this.cheat = cheat;
    }

    public void addCheat() {
        this.cheat++;
    }

    public boolean isCheat() {
        return this.cheat >= CHEAT_MAX;
    }

    /**
     * 战力是否非法
     *
     * @param fightingClient
     * @return
     */
    private boolean isFightingCheat(long fightingClient) {
        long fightingServer = this.role.getPlayer().getFighting();
        if (fightingClient - fightingServer < VALUE_OFFSET)
            return false;
        float offset = (fightingClient - fightingServer) * 1.0f / fightingServer;
        if (offset <= FIGHTING_OFFSET)
            return false;
        return true;
    }

}
