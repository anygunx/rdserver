package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.*;

/**
 * 邮件管理器
 *
 * @author Created by U-Demon on 2016年11月7日 下午3:32:47
 * @version 1.0.0
 */
public class MailManager {

    private GameRole role;

    //邮件列表
    private Map<Integer, Mail> _mails;

    public MailManager(GameRole gameRole) {
        this.role = gameRole;
    }

    /**
     * 初始化邮件列表
     */
    private void init() {
        //每个玩家登陆时自己清楚无附件过期的邮件。服务器定时清楚长期邮件
        this.role.getDbManager().mailDao.deleteMails(role.getPlayer().getId());
        //获取邮件列表
        List<Mail> mailList = this.role.getDbManager().mailDao.getMailList(role.getPlayer().getId());
        _mails = new HashMap<>();
        for (Mail mail : mailList) {
            _mails.put(mail.getId(), mail);
        }
    }

    private Map<Integer, Mail> getMails() {
        if (null == _mails) {
            init();
        }
        return _mails;
    }

    public Mail getMailById(int id) {
        return this.getMails().get(id);
    }

    public void addMail(Mail mail) {
        this.getMails().put(mail.getId(), mail);
    }

    /**
     * 邮件列表信息
     *
     * @param request
     */
    public void processMailList(Message request) {
        Message msg = getMailList();
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    public Message getMailList() {
        Message msg = new Message(MessageCommand.MAIL_LIST_MESSAGE);
        msg.setShort(this.getMails().size());
        for (Mail mail : getMails().values()) {
            mail.getMessage(msg);
        }
        return msg;
    }

    /**
     * 读取邮件
     *
     * @param request
     */
    public void processMailRead(Message request) {
        int id = request.readInt();
        Message msg = new Message(MessageCommand.MAIL_READ_MESSAGE, request.getChannel());
        msg.setInt(id);
        role.sendMessage(msg);
        //设置邮件状态
        Mail mail = getMailById(id);
        if (mail == null)
            return;
        mail.read();
        role.getDbManager().mailDao.updateStateById(id, mail.getState());
    }

    /**
     * 领取单封邮件
     *
     * @param request
     */
    public void processMailRewardSingle(Message request) {
        int id = request.readInt();
        Mail mail = getMailById(id);
        if (mail == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAIL_EXPIRED);
            return;
        }
        if (mail.getState() >= GameDefine.MAIL_STATE_REWARDED) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAIL_REWARDED);
            return;
        }
        //判断背包容量
        if (!role.getPackManager().capacityEnough(mail.getAtta())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
            return;
        }
        //设置邮件状态
        mail.reward();
        role.getDbManager().mailDao.updateStateById(id, mail.getState());
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //增加物品
        if (!role.getPackManager().addGoods(mail.getAtta(), EGoodsChangeType.getChangeType(mail.getType()), saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return;
        }
        Message msg = new Message(MessageCommand.MAIL_REWARD_SINGLE_MESSAGE, request.getChannel());
        msg.setInt(id);
        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * 领取所有邮件
     *
     * @param request
     */
    public void processMailRewardAll(Message request) {
        //判断背包容量
        List<DropData> rewards = new ArrayList<>();
        for (Mail mail : this.getMails().values()) {
            if (mail.getAtta() == null || mail.getAtta().size() <= 0)
                continue;
            if (mail.getState() >= GameDefine.MAIL_STATE_REWARDED)
                continue;
            rewards.addAll(mail.getAtta());
        }
        if (!role.getPackManager().capacityEnough(rewards)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL_MELT);
            return;
        }
        EnumSet<EPlayerSaveType> saveTypes = EnumSet.noneOf(EPlayerSaveType.class);
        //领取所有邮件
        List<Integer> ids = new ArrayList<>();
        for (Mail mail : this.getMails().values()) {
            if (mail.getAtta() == null || mail.getAtta().size() <= 0)
                continue;
            if (mail.getState() >= GameDefine.MAIL_STATE_REWARDED)
                continue;
            //设置邮件状态
            mail.reward();
            ids.add(mail.getId());
            //增加物品
            role.getPackManager().addGoods(mail.getAtta(), EGoodsChangeType.getChangeType(mail.getType()), saveTypes);
        }
        if (ids.size() < 1) {
            role.sendTick(request);
            return;
        }
        //修改邮件状态
        role.getDbManager().mailDao.updateStateBatch(ids,
                (byte) (GameDefine.MAIL_STATE_REWARDED + GameDefine.MAIL_STATE_READED));
        Message msg = new Message(MessageCommand.MAIL_REWARD_ALL_MESSAGE, request.getChannel());
        msg.setShort(ids.size());
        for (int id : ids) {
            msg.setInt(id);
        }
        role.sendMessage(msg);
        role.savePlayer(saveTypes);
    }

    public void addMailAndNotify(Mail mail) {
        addMail(mail);
        notifyClient(mail);
    }

    public void notifyClient(Mail mail) {
        Message msg = new Message(MessageCommand.MAIL_ADD_MESSAGE);
        mail.getMessage(msg);
        this.role.putMessageQueue(msg);
    }

    public boolean contains(String title) {
        for (Mail mail : getMails().values()) {
            if (mail.getTitle().equals(title))
                return true;
        }
        return false;
    }

}
