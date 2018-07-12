package com.rd.game;

import com.google.common.base.Preconditions;
import com.rd.bean.drop.DropData;
import com.rd.bean.goods.data.BoxData;
import com.rd.bean.mail.Mail;
import com.rd.bean.support.ServerInfo;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.PlayerDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.lg.bean.IPlayer;
import com.rd.model.GoodsModel;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XingYun on 2016/12/13.
 */
public class GameSupportUtil {
    private static final Logger logger = Logger.getLogger(GameSupportUtil.class.getName());

    public static boolean callbackOnFocus(int playerId, int diamond) {
        if (diamond <= 0) {
            logger.error("GameSupportUtil.callbackOnFocus() failed. Unexpected diamond = " + diamond);
            return false;
        }

        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        Preconditions.checkNotNull(role, "GameSupportUtil.callbackOnFocus() failed. Unexpected playerId=" + playerId);
        Mail mail = MailService.createMail(
                "关注奖励",
                "关注成功，请领取",
                EGoodsChangeType.FOCUS_ADD,
                new DropData(EGoodsType.DIAMOND.getId(), 0, diamond)
                //new DropData(EGoodsType.BOX.getId(),GoodsDefine.BOX_ID_FOCUS_MASTER,1));
        );
        MailService.sendSystemMail(playerId, mail);
        return true;
    }

    public static void callbackOnShare(int playerId, int diamond) {
        if (diamond <= 0) {
            return;
        }
        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        Preconditions.checkNotNull(role, "GameSupportUtil.callbackOnShare() failed. Unexpected playerId=" + playerId);
        Mail mail = MailService.createMail(
                "邀请奖励",
                "邀请成功，请领取",
                EGoodsChangeType.SHARE_ADD,
                new DropData(EGoodsType.DIAMOND.getId(), 0, diamond));
        MailService.sendSystemMail(playerId, mail);
    }

    public static void callbackCDKeyReward(int playerId, int modelId, int boxId) {
        if (boxId <= 0) {
            return;
        }
        IGameRole gameRole = GameWorld.getPtr().getGameRole(playerId);
        Preconditions.checkNotNull(gameRole, "GameSupportUtil.callbackCDKeyReward() failed. Unexpected playerId = " + playerId);

        // save
        gameRole.getPlayer().addCDKey(modelId);
        new PlayerDao().updateCDKey(gameRole.getPlayer());

        // reward
        Mail mail = MailService.createMail(
                "激活码奖励",
                "激活码奖励，请领取",
                EGoodsChangeType.CDKEY_ADD,
                new DropData(EGoodsType.BOX.getId(), boxId, 1));
        MailService.sendSystemMail(playerId, mail);
    }


    public static void callbackOnGetGift(int playerId, short box) {
        BoxData boxData = GoodsModel.getBoxDataById(box);
        Preconditions.checkNotNull(boxData, "GameSupportUtil.callbackOnGetGift() failed. Unexpected box=" + box);

        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        Preconditions.checkNotNull(role, "GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId);
        Mail mail = MailService.createMail(
                "礼包奖励",
                "礼包奖励，请领取",
                EGoodsChangeType.GIFT_ADD,
                new DropData(EGoodsType.BOX.getId(), box, 1));
        MailService.sendSystemMail(playerId, mail);
    }

    /**
     * 由于客户端提交playerId便于快速查找，这里须要比对账号信息。
     *
     * @param playerId
     * @param account
     * @param platform
     * @param channel
     * @param subChannel
     * @return
     */
    public static boolean checkUserData(int playerId, String account, byte platform, short channel, short subChannel) {
        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        if (role == null) {
            logger.error("GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId);
            return false;
        }
        IPlayer player = role.getPlayer();
        if (player == null) {
            logger.error("GameSupportUtil.callbackOnGetGift() failed. player=null");
            return false;
        }
        if (!player.getAccount().equals(account)) {
            logger.error("GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId + ", account=" + account);
            return false;
        }
        if (player.getChannel() != channel) {
            logger.error("GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId + ", channel=" + channel);
            return false;
        }
        if (player.getPlatform() != platform) {
            logger.error("GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId + ", platform=" + platform);
            return false;
        }
//        if (player.getSubChannel() != subChannel){
//            logger.error("GameSupportUtil.callbackOnGetGift() failed. Unexpected playerId=" + playerId + ", subChannel=" + subChannel);
//            return false;
//        }
        return true;
    }

    public static Collection<ServerInfo> getServerADInfoList() {
        Map<String, ServerInfo> infoMap = new HashMap<>();
        for (GameRole gameRole : GameWorld.getPtr().getOnlineRoles().values()) {
            if (StringUtil.isEmpty(gameRole.getAd())) {
                continue;
            }
            String key = gameRole.getAd() + "_" + gameRole.getSpid();
            if (!infoMap.containsKey(key)) {
                ServerInfo info = new ServerInfo();
                info.setServerId(GameDefine.getServerId());
                info.setChannelId(gameRole.getPlayer().getChannel());
                info.setPlayerCount(1);
                info.setSpid(gameRole.getSpid());
                info.setAdId(gameRole.getAd());
                infoMap.put(key, info);
            } else {
                ServerInfo info = infoMap.get(key);
                info.setPlayerCount(info.getPlayerCount() + 1);
            }
        }
        return infoMap.values();
    }
}
