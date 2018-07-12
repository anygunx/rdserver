package com.rd.common;

import com.alibaba.fastjson.JSON;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.dao.MailDao;
import com.rd.dao.PlayerDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.data.MailRewardModelData;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 邮件服务类
 *
 * @author Created by U-Demon on 2016年11月7日 下午2:13:23
 * @version 1.0.0
 */
public class MailService {

    private static Logger logger = Logger.getLogger(MailService.class);

    /**
     * 创建邮件实体
     *
     * @param title
     * @param content
     * @param mailType
     * @param datas
     * @return
     */
    public static Mail createMail(String title, String content, EGoodsChangeType mailType, List<DropData> datas) {
        Mail mail = new Mail();
        mail.setTitle(title);
        mail.setContent(content);
        mail.setType((short) mailType.getId());
        mail.setAtta(datas);
        mail.initState();
        return mail;
    }

    public static Mail createMail(String title, String content, EGoodsChangeType mailType, String attaJson) {
        List<DropData> datas = new ArrayList<>();
        if (!StringUtil.isEmpty(attaJson)) {
            datas = JSON.parseArray(attaJson, DropData.class);
        }
        return createMail(title, content, mailType, datas);
    }

    public static Mail createMail(String title, String content, EGoodsChangeType mailType, DropData... datas) {
        return createMail(title, content, mailType, Arrays.asList(datas));
    }

    public static Mail createMail(MailRewardModelData data, EGoodsChangeType mailType) {

        if (data != null) {

            String title = data.getTitle();
            String content = data.getContent();
            List<DropData> datas = data.getDropDatas();
            return createMail(title, content, mailType, datas);
        }

        return null;
    }

    /**
     * 发送系统邮件
     *
     * @param receive
     * @param mail
     */
    public static int sendSystemMail(int receiver, Mail mail) {
        try {
            MailDao dao = new MailDao();
            int id = dao.insertMail(receiver, mail);
            GameRole gameRole = GameWorld.getPtr().getOnlineRole(receiver);
            if (gameRole != null) {
                gameRole.getMailManager().addMailAndNotify(mail);
            }
            return id;
        } catch (Exception e) {
            logger.error("MailService.sendSystemMail() failed. receiver=" + receiver +
                    ",mail=" + mail.getTitle(), e);
        }
        return -1;
    }

    /**
     * 发送全服邮件
     *
     * @param mail
     */
    public static void sendGlobalMail(Mail mail) {
        GameDefine.initServerSet();
        for (short id : GameDefine.getServerSet()) {
            sendGlobalMail(id, mail);
        }
    }

    /**
     * 发送全服邮件
     *
     * @param mail
     */
    public static void sendGlobalMail(int serverId, Mail mail) {
        //一批次保存多少个
        final int batch = 1024;
        //for (Integer serverId: GameDefine.SERVER_ID_LIST) 
        //{
        int idLow = GameDefine.getIdLow(serverId);
        int idHigh = GameDefine.getIdHigh(serverId);
        int maxId = new PlayerDao().getPlayerMaxId(idLow, idHigh);
        int saveId = 1;
        List<Integer> idList = new ArrayList<>();
        for (int playerId = idLow; playerId <= maxId; ++playerId) {
            //换批次
            if (playerId >= idLow + saveId * batch) {
                saveId++;
                //保存
                sendMailBatch(mail, idList);
                idList = new ArrayList<>();
            }
            idList.add(playerId);
        }
        if (idList.size() > 0) {
            //保存
            sendMailBatch(mail, idList);
            idList = new ArrayList<>();
        }
        //}
    }

    /**
     * 发送全服邮件
     *
     * @param mail
     */
    public static void sendGlobalMailByVip(int serverId, Mail mail, int vip) {
        List<Integer> idList = new PlayerDao().getPlayerByVip(serverId, vip);
        sendMailBatch(mail, idList);
    }

    /**
     * 批量发送邮件
     *
     * @param mail
     * @param receiveIds
     */
    public static void sendMailBatch(final Mail mail, final List<Integer> receiveIds) {
        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                if (receiveIds == null || receiveIds.size() <= 0)
                    return;
                //发送邮件
                Map<Integer, Integer> mailIds = null;
                try {
                    //批量入库
                    mailIds = new MailDao().addMailBatch(receiveIds, mail);
                } catch (Exception e) {
                    logger.error("MailService.sendMailBatch() add2Sql failed. Try to send one by one.", e);
                    //一个玩家一个玩家发送
                    for (Integer playerId : receiveIds) {
                        sendSystemMail(playerId, mail);
                    }
                    return;
                }

                try {
                    //向在线的玩家推送消息
                    for (Integer playerId : receiveIds) {
                        GameRole role = GameWorld.getPtr().getOnlineRole(playerId);
                        if (role == null)
                            continue;
                        Mail newMail = mail.clone();
                        newMail.setId(mailIds.get(playerId));
                        role.getMailManager().addMailAndNotify(newMail);
                    }
                } catch (Exception e) {
                    logger.error("MailService.sendMailBatch() addMailAndNotify to players failed.. ");
                    StringBuilder builder = new StringBuilder("Error playerIdList=");
                    for (Integer playerId : receiveIds) {
                        builder.append(playerId).append(",");
                    }
                    logger.error(builder.toString());
                    builder = new StringBuilder("Error mails=");
                    for (Entry<Integer, Integer> entry : mailIds.entrySet()) {
                        builder.append("result:" + entry.getKey()).append(":").append(entry.getValue()).append(",");
                    }
                    logger.error(builder.toString());
                }
            }

            @Override
            public String name() {
                return "SendMailBatch";
            }
        });
    }

    /**
     * 发送系统充值邮件
     *
     * @param playerId 玩家id
     * @param mail     邮件
     */
    public static boolean sendPaymentSystemMail(int playerId, Mail mail) {
        try {
            int id = new MailDao().insertMail(playerId, mail);
            if (GameDefine.INVALID != id) {
                GameRole gameRole = GameWorld.getPtr().getOnlineRole(playerId);
                if (gameRole != null) {
                    gameRole.getMailManager().addMailAndNotify(mail);
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("MailService.sendSystemMail() failed. receiver=" + playerId +
                    ",mail=" + mail.getTitle(), e);
        }
        return false;
    }

}
