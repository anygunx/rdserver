package com.rd.action.web.gm;

import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.GMDao;
import com.rd.dao.PlayerDao;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.EnumSet;
import java.util.Map;

@WebFilter(filter = "GMDayResetPlayer")
public class GMDayResetPlayerAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));

        Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        IGameRole igr = GameWorld.getPtr().getGameRole(player.getId());
        if (igr == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        GameRole role = igr.getGameRole();
        if (role != null) {
            //重置每日任务状态
            role.getMissionManager().reset();
            //重置副本状态
            role.getDungeonManager().reset();
            //初始化重置后的任务
            role.getMissionManager().init();
            //重置每日数据
            role.getPlayer().resetDayData();
            //重置月卡领奖时间
            role.getActivityManager().resetMonthlyCardReward();
            //重置渡劫数据
            role.getEscortManager().dayReset();
            //重置公会BOSS次数
            role.getPlayer().setGangBossCount((byte) 0);
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.allOf(EPlayerSaveType.class);
        new PlayerDao().savePlayer(role.getPlayer(), enumSet);
        HttpUtil.sendResponse(channel, "succ");
    }

}
