package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.common.MailService;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.util.EnumSet;
import java.util.List;

public class SmallDataManager {

    private GameRole gameRole;
    private Player player;

    public SmallDataManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    public void processNoviceGuide(Message request) {
        byte index = request.readByte();
        byte value = request.readByte();
        while (player.getSmallData().getNoviceGuide().size() <= index) {
            player.getSmallData().getNoviceGuide().add((byte) 0);
        }
        player.getSmallData().getNoviceGuide().set(index, value);
        EnumSet<EPlayerSaveType> saveTypes = EnumSet.of(EPlayerSaveType.SMALLDATA);
        gameRole.savePlayer(saveTypes);
        gameRole.sendTick(request);
    }

    public Message getDeskInfoMessage() {
        Message message = new Message(MessageCommand.WANBA_DESK_INFO_MESSAGE);
        message.setByte(player.getSmallData().getSendDesk());
        return message;
    }

    public void processDeskInfo(Message request) {
        Message message = getDeskInfoMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    public void processDeskReward(Message request) {
        if (player.getSmallData().getSendDesk() == GameDefine.FALSE) {
            player.getSmallData().setSendDesk(GameDefine.TRUE);
            EnumSet<EPlayerSaveType> saveTypes = EnumSet.of(EPlayerSaveType.SMALLDATA);
            gameRole.savePlayer(saveTypes);

            List<DropData> datas = StringUtil.getRewardDropList("5,0,30;2,1,500");
            Mail mail = MailService.createMail("添加到桌面奖励", "恭喜您添加桌面成功，方便快速进入游戏，这是添加到桌面的奖励，请笑纳~", EGoodsChangeType.GM_ADD, datas);
            MailService.sendSystemMail(player.getId(), mail);
        }
        this.processDeskInfo(request);
    }
}
