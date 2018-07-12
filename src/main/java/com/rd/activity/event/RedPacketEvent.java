package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.PlayerActivity;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.ActivityDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

import java.util.List;

public class RedPacketEvent implements IActivityEvent {

    //7天消费
    public static final int LAST_DAY = 7;

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        //邮件发放奖励
        List<PlayerActivity> list = new ActivityDao().getAllRedpackets();
        for (PlayerActivity pa : list) {
            if (pa.getRedpacket() == null)
                continue;
            int total = 0;
            for (int i = 1; i < pa.getRedpacket().size(); i++) {
                total += pa.getRedpacket().get(i);
            }
            if (total <= 0)
                continue;
            DropData reward = new DropData(EGoodsType.DIAMOND, 0, total);
            Mail mail = MailService.createMail("返利红包", "您在7天消费返利中累计消费" + (total * 5) +
                            "元宝，此处为您奉上20%比例的消费返利红包！请笑纳",
                    EGoodsChangeType.REDPACKET_REWARD_ADD, reward);
            MailService.sendSystemMail(pa.getPlayerId(), mail);
        }
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.RED_PACKET);
        int pass = DateUtil.getDistanceDay(config.getStartTime(role.getPlayerId()), curr) + 1;
        if (pass > 100)
            pass = 100;
        msg.setByte(pass);
        PlayerActivity data = role.getActivityManager().getActivityData();
        int total = data.getRedpacketTotal();
        msg.setInt(total);
        if (pass > LAST_DAY && data.getRedpacket().size() > 0 && data.getRedpacketTotal() > 0) {
            //可领取
            if (data.getRedpacket().get(0) == 0)
                msg.setByte(2);
                //已领取
            else
                msg.setByte(1);
        } else {
            msg.setByte(0);
        }
        msg.setByte(data.getRedpacket().size() - 1);
        for (int i = 1; i < data.getRedpacket().size(); i++) {
            int yuanbao = data.getRedpacket().get(i);
            msg.setByte(i);
            msg.setInt(yuanbao);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
