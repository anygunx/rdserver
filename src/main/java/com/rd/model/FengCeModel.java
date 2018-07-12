package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.FengCeDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.rd.model.data.FengCeModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FengCeModel {

    private static Logger logger = Logger.getLogger(FengCeModel.class);

    //封测数据
    private static final String FENGCE_PATH = "gamedata/fengce.xml";
    private static final String FENGCE_NAME = "fengceModel";
    //战力榜奖励
    private static List<FengCeModelData> fightRewards = new ArrayList<>();
    //等级榜奖励
    private static List<FengCeModelData> lvRewards = new ArrayList<>();
    //登录奖励
    private static FengCeModelData loginReward;
    //充值奖励
    private static FengCeModelData payReward;

    //AVU战力榜
    private static Map<String, Integer> avu_fight = null;
    //AVU等级榜
    private static Map<String, Integer> avu_level = null;
    //AVU登录榜
    private static List<String> avu_login = null;

    //白鹭战力榜
    private static Map<String, Integer> egret_fight = null;
    //白鹭等级榜
    private static Map<String, Integer> egret_level = null;
    //白鹭登录榜
    private static List<String> egret_login = null;
    //白鹭充值榜
    private static Map<String, Integer> egret_pay = null;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        if (GameDefine.getServerId() != 1)
            return;
        //登录奖励
        loginReward = new FengCeModelData();
        loginReward.setTitle("封测登陆大回馈");
        loginReward.setContent("感谢您在封测期间积极参与，连续登陆达到了5天，公测如约为您奉上300元宝和VIP1经验卡！");
        loginReward.getRewards().add(new DropData(EGoodsType.DIAMOND, 0, 300));
        loginReward.getRewards().add(new DropData(EGoodsType.BOX, 68, 1));
        //充值奖励
        payReward = new FengCeModelData();
        payReward.setTitle("封测充值双倍返还");
        payReward.setContent("感谢您在封测期间积极参与充值，公测如约为您奉上双倍元宝返还，同时继承封测的VIP等级！");
        loadFengCe(path);
    }

    public static void loadDatabase() {
        if (!new FengCeDao().hasTable("aa_avu_fight_rank"))
            return;
        //AVU
        avu_fight = new FengCeDao().getAvuFight();
        avu_level = new FengCeDao().getAvuLevel();
        avu_login = new FengCeDao().getAvuLogin();
        //EGRET
        egret_fight = new FengCeDao().getEgretFight();
        egret_level = new FengCeDao().getEgretLevel();
        egret_login = new FengCeDao().getEgretLogin();
        egret_pay = new FengCeDao().getEgretPay();
    }

    /**
     * 发放封测奖励奖励
     *
     * @param account
     * @param playerId
     */
    public static void sendFengCeReward(String account, int playerId) {
        if (GameDefine.getServerId() != 1)
            return;
        //AVU登录奖励
        if (avu_login != null) {
            if (avu_login.contains(account)) {
                Mail mail = MailService.createMail(loginReward.getTitle(), loginReward.getContent(),
                        EGoodsChangeType.FENGCE_AVU_LOGIN_ADD, loginReward.getRewards());
                MailService.sendSystemMail(playerId, mail);
            }
        }
        //AVU战力奖励
        if (avu_fight != null) {
            if (avu_fight.containsKey(account)) {
                FengCeModelData reward = getFightReward(avu_fight.get(account));
                if (reward != null) {
                    Mail mail = MailService.createMail(reward.getTitle(), reward.getContent(),
                            EGoodsChangeType.FENGCE_AVU_FIGHT_ADD, reward.getRewards());
                    MailService.sendSystemMail(playerId, mail);
                }
            }
        }
        //AVU等级奖励
        if (avu_level != null) {
            if (avu_level.containsKey(account)) {
                FengCeModelData reward = getLvReward(avu_level.get(account));
                if (reward != null) {
                    Mail mail = MailService.createMail(reward.getTitle(), reward.getContent(),
                            EGoodsChangeType.FENGCE_AVU_LEVEL_ADD, reward.getRewards());
                    MailService.sendSystemMail(playerId, mail);
                }
            }
        }

        //白鹭登录奖励
        if (egret_login != null) {
            if (egret_login.contains(account)) {
                Mail mail = MailService.createMail(loginReward.getTitle(), loginReward.getContent(),
                        EGoodsChangeType.FENGCE_EGRET_LOGIN_ADD, loginReward.getRewards());
                MailService.sendSystemMail(playerId, mail);
            }
        }
        //白鹭战力奖励
        if (egret_fight != null) {
            if (egret_fight.containsKey(account)) {
                FengCeModelData reward = getFightReward(egret_fight.get(account));
                if (reward != null) {
                    Mail mail = MailService.createMail(reward.getTitle(), reward.getContent(),
                            EGoodsChangeType.FENGCE_EGRET_FIGHT_ADD, reward.getRewards());
                    MailService.sendSystemMail(playerId, mail);
                }
            }
        }
        //白鹭等级奖励
        if (egret_level != null) {
            if (egret_level.containsKey(account)) {
                FengCeModelData reward = getLvReward(egret_level.get(account));
                if (reward != null) {
                    Mail mail = MailService.createMail(reward.getTitle(), reward.getContent(),
                            EGoodsChangeType.FENGCE_EGRET_LEVEL_ADD, reward.getRewards());
                    MailService.sendSystemMail(playerId, mail);
                }
            }
        }
        //白鹭充值奖励
        if (egret_pay != null) {
            if (egret_pay.containsKey(account)) {
                int money = egret_pay.get(account);
                if (money > 0) {
                    List<DropData> rewards = new ArrayList<>();
                    rewards.add(new DropData(EGoodsType.DIAMOND, 0, money * 20));
                    rewards.add(new DropData(EGoodsType.VIP, 0, money * 10));
                    Mail mail = MailService.createMail(payReward.getTitle(), payReward.getContent(),
                            EGoodsChangeType.FENGCE_EGRET_PAY_ADD, rewards);
                    MailService.sendSystemMail(playerId, mail);
                }
            }
        }
    }

    private static void loadFengCe(String path) {
        final File file = new File(path, FENGCE_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    fightRewards.clear();
                    lvRewards.clear();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");
                    for (int i = 0; i < elements.length; i++) {
                        FengCeModelData data = new FengCeModelData();
                        byte type = Byte.valueOf(XmlUtils.getAttribute(elements[i], "days"));
                        data.setMin(Byte.valueOf(XmlUtils.getAttribute(elements[i], "min")));
                        data.setMax(Byte.valueOf(XmlUtils.getAttribute(elements[i], "max")));
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "content"));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward")));
                        if (type == 1)
                            fightRewards.add(data);
                        else if (type == 2)
                            lvRewards.add(data);
                    }
                } catch (Exception e) {
                    logger.error("加载渡劫数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return FENGCE_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static FengCeModelData getFightReward(int rank) {
        for (FengCeModelData data : fightRewards) {
            if (rank >= data.getMin() && rank <= data.getMax())
                return data;
        }
        return null;
    }

    private static FengCeModelData getLvReward(int rank) {
        for (FengCeModelData data : lvRewards) {
            if (rank >= data.getMin() && rank <= data.getMax())
                return data;
        }
        return null;
    }

}
