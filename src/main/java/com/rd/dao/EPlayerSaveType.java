package com.rd.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Player表中全字段映射
 *
 * @author Created by U-Demon on 2016年11月9日 下午3:55:11
 * @version 1.0.0
 */
public enum EPlayerSaveType {
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=player表-=-=-=-=-=-=-=-=-=-=-=-=-=-=//
    EQUIPSLOT("equipslot"),        //装备槽
    PIFULIST("pifulist"),
    PULSE("pulse"),
    DANYAO("danyao"),
    WEAREQUIP("wearequip"),            //已穿上的装备
    EQUIP("equip"),
    RECEIVEFC("receivefc"),//接收友情币数量
    RECEIVEFCC("receivefcc"),//接收友情币次数
    SENDFCC("sendfcc"),//发送友情币次数
    SKILL("skills"),
    SJGCOPY("sjg_copy_id"),
    TMMAXCOPYID("tm_copy_max_id"),
    MZSTAR("mz_star"),
    RICHANG("richang"),
    TASKADVANCED("taskadvanced"),
    FACTION_SKILL("faction_skill"),
    LILIAN_EXP("lilianexp"),
    LILIAN_LEVEL("lilianlevel"),
    /***************************************版本分割线****以上是新版本开发 以下 是老版本*******************************************************/
    NULL(""),
    ID("id"),
    CHANNEL("channel"),
    CHANNELCHILD("channelchild"),
    ACCOUNT("account"),
    SERVERID("serverid"),
    NAME("name"),
    HEAD("head"),            //头像
    REIN("rein"),            //转生
    EXP("exp"),                //经验
    LEVEL("level"),            //等级
    VIP("vip"),    //VIP
    GOLD("gold"),            //绑元
    DIAMOND("diamond"),        //元宝
    HONOR("honor"),                //荣誉
    ARENA("arena"),                //竞技场点数
    YUANQI("yuanqi"),                //元气
    POINTS("points"),                //积分
    DONATE("donate"),                //捐献
    //	ARTIFACT("artifact"),			//灵器
    ITEM("item"),            //道具
    BOX("box"),            //宝箱
    EQUIPBAG("equipBag"),            //红装背包
    SPIRIT("spirit"),                //元神
    MAPID("mapid"),                    //野外地图id
    MAPSTAGEID("mapstageid"),        //野外地图关卡id
    MELTEXP("meltExp"),            //熔炼
    MELTLV("meltLv"),
    GODARTIFACT("godArtifact"),            //神器
    BOSSCOUNT("bossCount"),            //BOSS战次数
    GANGBOSSCOUNT("gangBossCount"),        //公会BOSS次数
    BOSSRECOVER("bossRecover"),            //上次BOSS战次数恢复
    CITBOSSLEFT("citBossLeft"),        //全民BOSS剩余次数
    CITRECOVE("citRecover"),            //全民BOSS上次恢复时间
    CITCUE("citCue"),                //全民BOSS提醒
    FIGHTING("fighting"),            //战斗力
    STATE("state"),                    //角色状态
    LOGINTIME("loginTime"),            //上次登陆时间
    LOGOUTTIME("logoutTime"),        //上次登出时间
    CREATETIME("createTime"),        //创建时间
    CHAINMISSION("chainmission"),    //支线任务
    DAILYMISSION("dailymission"),    //每日任务
    SMALLDATA("smalldata"),    //小数据存储
    MAPREWARD("mapReward"),    //通关奖励
    DAYDATA("daydata"),    //每日数据
    GANGSKILL("gangskill"),        //公会技能
    FOREVER("forever"),            //终生卡标识
    REDLOTTERY("redLottery"),    //寻宝首次十连抽
    PETLOTTERY("petLottery"),    //宠物首次十连抽
    HUANHUALIST("huanhualist"), //幻化列表
    HUANHUAAPPEARANCE("huanhuaAppearance"), //幻化外形
    TLPOINTS("tlpoints"),                //限时积分
    RSPOINTS("rspoints"),                //商城积分
    MAGICLEVEL("magiclevel"),    //法宝等级
    MAGICLEVELSTAR("magiclevelstar"),//法宝等级星数
    MAGICSTAGE("magicstage"),    //法宝阶数
    MAGICSTAGESTAR("magicstagestar"),    //法宝阶段星级
    MAGICSTAGEEXP("magicstageexp"),    //法宝阶段经验
    DEKARON("dekaron"), //诛仙台进度
    REQUESTFIGHTTIME("requestfighttime"), //请求战斗时间
    TITLE("title"),                //称号
    SHARE("share"),                //分享
    PET("pet"),                //宠物
    PETSHOW("petshow"),        //宠物出战
    DAILYUPDATEMARK("dailyupdatemark"),        //每日更新标示
    AUCTION_SUBSCRIPTIONS("auctionsubscriptions"), //拍卖订阅
    DRAGON_BALL("dragonball"), //龙珠
    DRAGON_BALL_PROCESS("dragonballprocess"), //龙珠每日任务进度
    ACHIEVEMENT("achievement"), //成就
    ACHIEVEMENT_MISSION("achievementmission"), //成就任务
    MEDAL("medal"), //勋章
    AUCTION_BOX("auctionbox"), //拍卖宝箱
    HEART_SKILL("heartskill"),    //心法背包
    ARTIFACT_PIECES("artifactpieces"), //关卡神器碎片
    ARTIFACT_BOSS("artifactboss"), //关卡神器
    COMBINE_RUNE("combinerune"),    //合击符文
    COMBINE_RUNE_PIECE("combinerunepiece"),    //合击符文碎片
    COMBINE_RUNE_BAG("combinerunebag"),    //合击符文背包
    WEIWANG("weiwang"), //威望
    ZHANWEN("zhanwen"), //战纹
    ZHANWEN_JINGHUA("zhanwen_jinghua"), //战纹精华
    TLMISSION("tlmission"), //限时任务
    MYSTERYBOSSLEFT("mysteryBossLeft"),        //秘境BOSS剩余次数
    MYSTERYCUE("mysteryCue"),                //秘境BOSS提醒
    VIPBOSSCUE("vipBossCue"),                //BOSS之家提醒
    WINGGODS("winggods"),                    //仙羽装备
    FENGMOTA("fengmota"),                    //封魔塔进度
    CARD_BAG("card_bag"),                    //卡牌材料包
    CARD_BOOK("card_book"),                    //卡牌图鉴
    CARD_MISSION("card_mission"),            //卡牌任务
    DAILY_CONSUM("daily_consum"),            //每日消耗元宝
    FIVEELEMENT("fiveElement"),             //五行
    FIVELEMENT_STATE("fiveElementState"),   //五行激活状态
    LOGONTIME_FEST("lastLoginTime2Fest"),    //上次登录时间2节日
    SUUL_PIECE("soul"),                     //灵髓包
    ZHUZAISHILIAN("zhuzai"),         //主宰试炼进度
    FAZHEN("fazhen"),                //法阵
    VOUCHERS("vouchers"),             //代金券
    TOWN_SOUL("townsoul"),             //镇魂宝库
    HOLYLINES("holylines"),        //圣纹
    MYSTERY_INTEGRAL("mysteryIntegral"),    //秘闻积分
    QIZHEN_INTEGRAL("qizhenIntegral"),        //奇珍积分
    AMBIT("ambit"), //境界
    TRAIN_ITEM("trainItems"), //坐宠
    TRAIN_ITEM_EQUIP("trainItemEquips"),//坐宠装备
    PET_BABY("petBabys"),//宠物
    PET_BABY_SPIRIT("petBabySpirits"),//通灵和兽魂
    FAIRY_COM("fairyComs"),//仙侣
    GROW("grow"),
    GROWEQUIP("growequip"),

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=character表-=-=-=-=-=-=-=-=-=-=-=-=-=-=//
    CHA_PID("playerid", 1),
    CHA_IDX("idx", 1),
    CHA_OCCUPATION("occupation", 1),
    CHA_MERIDIAN("meridian", 1),            //经脉
    CHA_EQUIP("equip", 1),        //角色装备
    CHA_SKILL("skill", 1),        //角色技能
    CHA_DOM("dom", 1),            //主宰套装
    CHA_EQUIPSLOT("equipslot", 1),        //装备槽
    CHA_GONG("gong", 1),                //功法
    CHA_TONGJING("tongjing", 1),//铜镜
    CHA_YUDI("yudi", 1),        //玉笛
    CHA_ZUOYAN("zuoyan", 1),    //左眼
    CHA_YOUYAN("youyan", 1),    //右眼
    CHA_MOUNTSTAR("mountlevel", 1),    //坐骑等级
    CHA_MOUNTJIEDUAN("mountjieduan", 1),//坐骑阶段
    CHA_MOUNTEXP("mountexp", 1),    //坐骑经验
    CHA_CREATE("createtime", 1),    //创建时间
    CHA_SPIRIT("spirit", 1),                //元神
    CHA_WINGS("wings", 1),                //激活的翅膀
    CHA_WINGSHOW("wingShow", 1),        //展示的翅膀
    CHA_WEAPONS("weapons", 1),                //激活的武器
    CHA_WEAPONSHOW("weaponShow", 1),        //展示的武器
    CHA_ARMORS("armors", 1),                //激活的装备
    CHA_ARMORSHOW("armorShow", 1),            //展示的装备
    CHA_MOUNTS("mounts", 1),                //激活的坐骑
    CHA_MOUNTSHOW("mountShow", 1),            //展示的坐骑
    CHA_TITLE("title", 1),                //称号
    CHA_GANGSKILL("gangskill", 1),            //帮派普通技能\
    CHA_GANGSKILL2("gangskill2", 1),        //帮派特殊技能
    CHA_MOUNTEQUIP("mountequip", 1),        //坐骑装备
    CHA_HEART_SKILL_SLOT("heartskillslot", 1),        //心法装备位
    CHA_SHENBING("shenbing", 1),    //神兵
    CHA_ZHANWEN("zhanwen", 1),       //战纹
    CHA_SOUL_GOD("soulgods", 1),         //灵髓装备
    CHA_TOWN_SOUL("townsoul", 1),    //镇魂装备
    CHA_HOLYGOODS("holygoods", 1),      //圣物


    ;

    //数据库中字段名
    private final String sql;

    //0--player表   1--character表
    private byte tb = 0;

    EPlayerSaveType(String sql) {
        this.sql = sql;
    }

    EPlayerSaveType(String sql, int tb) {
        this.sql = sql;
        this.tb = (byte) tb;
    }


    public String getSql() {
        return sql;
    }

    public byte getTb() {
        return tb;
    }

    /**
     * 刷新clo列的数值
     *
     * @param args
     */
    public static void main(String[] args) {
        final String START_FLAG = "NULL(0,";
        final String END_FLAG = ";";
        List<String> wls = new ArrayList<>();
        try {
            String classPath = EPlayerSaveType.class.getName().replace(".", "/");
            Reader reader = new FileReader("./src/" + classPath + ".java");
            BufferedReader br = new BufferedReader(reader);
            boolean work = false;
            String line = null;
            int id = 0;
            while ((line = br.readLine()) != null) {
                if (!work && line.trim().startsWith(START_FLAG)) {
                    work = true;
                    id = Integer.valueOf(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                    wls.add(line + "\r\n");
                    continue;
                }
                if (line.trim().equals(END_FLAG)) {
                    work = false;
                }
                if (work) {
                    id++;
                    int leftIndex = line.indexOf("(");
                    int rightIndex = line.indexOf(",");
                    if (leftIndex >= 0 && rightIndex >= 0) {
                        String left = line.substring(0, leftIndex + 1);
                        String right = line.substring(rightIndex);
                        line = left + id + right;
                        System.out.println(line);
                    }
                }
                wls.add(line + "\r\n");
            }
            br.close();
            //写入
            Writer writer = new FileWriter("./src/" + classPath + ".java");
            for (String nl : wls) {
                writer.write(nl);
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
