package com.rd.action.web.gm;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
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

import java.util.EnumSet;
import java.util.Map;

@WebFilter(filter = "GMAddGoods")
public class GMAddGoodsAction extends WebAction {
    static Logger log = Logger.getLogger(GMAddGoodsAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));
        byte type = Byte.valueOf(params.get("type"));
        short goodsId = Short.valueOf(params.get("goodsId"));
        byte quality = Byte.valueOf(params.get("quality"));
        int num = Integer.valueOf(params.get("num"));

        Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        IGameRole role = GameWorld.getPtr().getGameRole(player.getId());
        if (role == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        if (role.isOnline()) {
            GameRole gameRole = (GameRole) role;
            gameRole.init();
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            DropData data = new DropData(type, goodsId, quality, num);
            gameRole.getPackManager().addGoods(data, EGoodsChangeType.GM_ADD, enumSet);
            gameRole.putMessageQueue(role.getPlayer().getGoodsListMessage());
            gameRole.savePlayer(enumSet);
            HttpUtil.sendResponse(channel, "succ");
        } else {
            HttpUtil.sendResponse(channel, "fail player not online");
        }

        log.info("GMAddGoodsAction:account=" + account + " type=" + type + " goodsId=" + goodsId + " quality=" + quality + " num=" + num);
    }

}
