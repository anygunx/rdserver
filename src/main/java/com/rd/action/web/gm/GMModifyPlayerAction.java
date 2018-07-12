package com.rd.action.web.gm;

import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.GMDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

@WebFilter(filter = "GMModifyPlayer")
public class GMModifyPlayerAction extends WebAction {
    static Logger log = Logger.getLogger(GMModifyPlayerAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        int playerId = Integer.valueOf(params.get("playerId"));

        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        if (role == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        Player player = role.getPlayer();
        player.setVip(Integer.valueOf(params.get("vipLevel")));
        player.setRein(Integer.valueOf(params.get("rein")));
        player.setLevel(Short.valueOf(params.get("level")));
        player.setExp(Integer.valueOf(params.get("exp")));
        player.setGold(Long.valueOf(params.get("gold")));
        player.setDiamond(Integer.valueOf(params.get("diamond")));

        player.setHonor(Integer.valueOf(params.get("honor")));
        player.setRsPoints(Integer.valueOf(params.get("rsPoints")));
        player.setDonate(Integer.valueOf(params.get("donate")));
        player.setYuanqi(Integer.valueOf(params.get("yuanqi")));
        player.setMagicLevel(Short.valueOf(params.get("magicLevel")));
        player.setMagicLevelStar(Byte.valueOf(params.get("magicLevelStar")));
        player.setMagicStage(Short.valueOf(params.get("magicStage")));
        player.setMagicStageStar(Byte.valueOf(params.get("magicStageStar")));
        player.setMagicStageExp(Integer.valueOf(params.get("magicStageExp")));

        player.setMeltLv(Short.valueOf(params.get("meltLv")));
        player.setMeltExp(Integer.valueOf(params.get("meltExp")));
        player.setBossCount(Integer.valueOf(params.get("bossCount")));
        player.setBossRecover(Long.valueOf(params.get("bossRecover")));
        player.setCitBossLeft(Short.valueOf(params.get("citBossLeft")));
        player.setCitRecover(Long.valueOf(params.get("citRecover")));
        player.setState(Byte.valueOf(params.get("state")));

        GMDao.getInstance().gmUpdatePlayerBaseInfo(player);
        if (role.isOnline()) {
            GameRole gameRole = (GameRole) role;
            gameRole.putMessageQueue(player.getPlayerMessage());
            gameRole.sendUpdateCurrencyMsg(EGoodsType.HONOR, EGoodsChangeType.GM_ADD);
            gameRole.sendUpdateCurrencyMsg(EGoodsType.DIAMOND, EGoodsChangeType.GM_ADD);
            gameRole.sendUpdateCurrencyMsg(EGoodsType.GOLD, EGoodsChangeType.GM_ADD);
            gameRole.sendUpdateCurrencyMsg(EGoodsType.VIP, EGoodsChangeType.GM_ADD);
        }
        HttpUtil.sendResponse(channel, "succ");

        log.info("GMModifyPlayer:id=" + playerId + " gold=" + player.getGold() + " diamond=" + player.getDiamond());
    }

}
