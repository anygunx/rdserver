package com.rd.action.web.gm;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.common.MailService;
import com.rd.dao.GMDao;
import com.rd.define.EGoodsChangeType;
import com.rd.model.VipModel;
import com.rd.model.data.VipModelData;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发邮件
 *
 * @author Created by U-Demon on 2016年12月16日 下午6:59:28
 * @version 1.0.0
 */
@WebFilter(filter = "GMMail")
public class GMMailAction extends WebAction {

    static Logger log = Logger.getLogger(GMMailAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));

        String title = "";
        String content = "";
        try {
            title = URLDecoder.decode(params.get("title"), "UTF-8");
            content = URLDecoder.decode(params.get("content"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        String rewards = params.get("rewards");

        List<DropData> datas = null;
        //解析附件物品
        if (rewards != null && rewards.length() > 0) {
            datas = StringUtil.getRewardDropList(rewards);
        } else {
            datas = new ArrayList<>();
        }
        Mail mail = MailService.createMail(title, content, EGoodsChangeType.GM_ADD, datas);
        if (account.equals("-1")) {
            String vip = params.get("vip");
            if (vip == null || Integer.parseInt(vip) == 0) {
                MailService.sendGlobalMail(serverId, mail);
            } else {
                VipModelData data = VipModel.getModelByLv(Integer.parseInt(vip));
                if (data != null) {
                    MailService.sendGlobalMailByVip(serverId, mail, data.getCost());
                } else {
                    HttpUtil.sendResponse(channel, "fail");
                    return;
                }
            }
        } else {
            Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
            if (player != null) {
                MailService.sendSystemMail(player.getId(), mail);
            } else {
                HttpUtil.sendResponse(channel, "fail");
                return;
            }
        }
        HttpUtil.sendResponse(channel, "succ");

        log.info("GMMailAction:account" + account + " title=" + title + " content=" + content + " rewards=" + rewards);
    }

}
