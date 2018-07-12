package com.rd.action.web.gm;

import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.game.GameMonsterSiegeService;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 怪物攻城重置
 */
@WebFilter(filter = "GMMonsterSiegeOperation")
public class GMMonsterSiegeOperation extends WebAction {
    private static final Logger logger = Logger.getLogger(GMMonsterSiegeOperation.class);
    private static final byte OPERATION_GET = 1;
    private static final byte OPERATION_RESET = 2;

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        short monsterId = Short.valueOf(params.get("id"));
        byte operation = Byte.valueOf(params.get("op"));
        try {
            GameMonsterData monsterData = GameMonsterSiegeService.getMonster(monsterId);
            if (monsterData == null) {
                HttpUtil.sendResponse(channel, "fail");
                return;
            }

            switch (operation) {
                case OPERATION_GET:
                    HttpUtil.sendResponse(channel, monsterData.toString());
                    return;
                case OPERATION_RESET:
                    GameMonsterSiegeService.resetMonster(monsterId);
                    HttpUtil.sendResponse(channel, "succ");
                    return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        HttpUtil.sendResponse(channel, "fail");
    }
}
