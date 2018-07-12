package com.rd.task.global;

import com.rd.common.ChatService;
import com.rd.game.GameGangManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.local.ArenaGameService;
import com.rd.game.local.GameHttpManager;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * <p>Title: 分钟任务</p>
 * <p>Description: 每分钟需要执行的任务</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年11月25日 下午2:33:56
 */
public class MinuteTask implements Task {

    private static final Logger logger = Logger.getLogger(MinuteTask.class);

    public void run() {
        long currentTime = System.currentTimeMillis();
        //更新会话
        onUpdateSession(currentTime);
        //更新聊天缓存
        ChatService.update(currentTime);
        //更新公会战
        GameGangManager.getInstance().updateState();
        //onUpdateArenaState(currentTime);
        onUpdateArenaBattlePlayer(currentTime);

        //TODO:暂时先一分钟更新一次在线玩家战力
        for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
            role.getPlayer().updateFighting();
        }
    }

    /**
     * 更新会话
     *
     * @param currentTime
     */
    private void onUpdateSession(long currentTime) {
        try {
            GameWorld.getPtr().updateSession(currentTime);
        } catch (Exception e) {
            logger.error("更新会话失败...", e);
        }
    }

    private void onUpdateArenaState(long currentTime) {
        try {
            ArenaGameService.onUpdateState();
        } catch (Exception e) {
            logger.error("更新竞技场状态失败...", e);
        }
    }

    private void onUpdateArenaBattlePlayer(long currentTime) {
        try {
            if (!ArenaGameService.open)
                return;
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                if (!ArenaGameService.inPicks(role.getPlayerId()))
                    continue;
                role.getPlayer().updateFighting();
                GameHttpManager.gi().sendBattlePlayerInfo(role.getPlayer());
            }
        } catch (Exception e) {
            logger.error("更新竞技场状态失败...", e);
        }
    }

    public String name() {
        return "MinuteTask";
    }
}
