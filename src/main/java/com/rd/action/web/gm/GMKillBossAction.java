package com.rd.action.web.gm;

import com.rd.bean.boss.Boss;
import com.rd.common.BossService;
import com.rd.game.GameRankManager;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 杀死BOSS
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "GMKillBoss")
public class GMKillBossAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        short bossId = Short.valueOf(params.get("bossId"));
        //重刷红装
        if (bossId == -9) {
            GameRankManager.getInstance().initXunbaoTop();
            HttpUtil.sendResponse(channel, "succ");
            return;
        }
        Boss boss = BossService.getCitizenBoss().get(bossId);
        if (boss == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        boss.changeCitHp(null, -999999999);
        HttpUtil.sendResponse(channel, "succ");
    }

}
