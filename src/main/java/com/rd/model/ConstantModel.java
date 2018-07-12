package com.rd.model;

import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

/**
 * 游戏常量
 *
 * @author Created by U-Demon on 2016年11月7日 下午4:08:43
 * @version 1.0.0
 */
public class ConstantModel {

    private static Logger logger = Logger.getLogger(ConstantModel.class);

    //匹配星级的偏移
    public static int LADDER_MATCH_MIN_STAR = 0;
    public static int LADDER_MATCH_MAX_STAR = 12;
    //匹配战斗力的偏移
    public static int LADDER_MATCH_MIN_FIGHT = 90;
    public static int LADDER_MATCH_MAX_FIGHT = 150;

    //到该星级后连胜终止
    public static int LADDER_UNCONWIN;
    //到该星级后失败开始降星
    public static int LADDER_SUBSTAR;

    //战斗次数上限
    public static int LADDER_FIGHT_MAX;
    //战斗恢复时间（秒）
    public static long LADDER_FIGHT_RECOVE_TIME;
    //购买天梯
    public static List<Integer> LADDER_FIGHT_BUY_PRICE;

    //天梯排行榜人数
    public static int LADDER_TOP_NUM;
    //天梯历史战绩人数
    public static int LADDER_HISTORY_NUM;
    //开放等级
    public static int LADDER_OPEN_LV;

    //邮件无附件删除时间（天）
    public static int EXPIRE_NO_ATTA;
    //邮件失效天数
    public static int EXPIRE_TOTAL;

    //每日运镖上限
    public static int ESCORT_DISPATCH_MAX;
    //每天免费刷新次数
    public static int ESCORT_REFRESH_FREE;
    //劫镖次数
    public static int ESCORT_ROB;
    //劫镖成功收获百分比
    public static int ESCORT_REWARD;
    //一趟镖被劫多少次
    public static int ESCORT_HURTED;
    //被劫一次损失百分比
    public static int ESCORT_CONSUME;
    //最大星级
    public static int ESCORT_STAR_MAX;
    public static final int[] ESCORT_EXP_RATE = {0, 600, 800, 1000, 1200, 1500};
    public static final float[][] ESCORT_TIME_COST_RATE = {{0.8f, 40}, {0.5f, 32}, {0, 24}};

    //排行榜捞取最大数量
    public static final int RANK_MAX = 200;
    //排行榜容量
    public static final int RANK_CAPACITY = 200;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=竞技场相关-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=//
    /**
     * 竞技场 排行总数
     */
    public static int ARENA_RANK_CAPACITY;

    /**
     * 竞技场 挑战cd
     */
    public static int ARENA_CHALLENGE_FREE_TIMES;
    public static int ARENA_CHALLENGE_REFRESH_CD;
    /**
     * 竞技场刷新挑战者排名范围 范围-该范围数量限制
     **/
    public static List<Map.Entry<Integer, Integer>> ARENA_CHALLENGE_RANGE;
    /**
     * 挑战记录总数
     */
    public static int ARENA_RECORD_CAPACITY;
    public static int ARENA_BATTLE_REWARD_SUCC;
    public static int ARENA_BATTLE_REWARD_FAIL;
    //购买竞技场
    public static List<Integer> ARENA_FIGHT_BUY_PRICE;

    /**
     * 机器人
     */
    public static int ARENA_ROBOT_VIP;
    public static short ARENA_ROBOT_LEVEL;
    /**
     * 竞技场机器人数
     **/
    public static final int ARENA_ROBOT_COUNT = 5000;
    /**
     * 竞技场战斗清理时间间隔 这段时间后战斗没有结束将被强制清理
     */
    public static final long BATTLE_CLEAR_INTERVAL = 120 * DateUtil.SECOND;

    /**
     * 卡牌最高等级
     */
    public static short CARD_MAX_LEVEL;
    /**
     * 五行最高等级
     **/
    public static short FIVE_ELEMENT_MAX_LEVEL = 400;
    /**
     * 灵境最高等级
     **/
    public static short LINGZHEN_MAX_LEVEL = 500;
    /**
     * 五行融合消耗
     **/
    public static short FIVE_ELEMENT_FUSE_COST = 1888;

    //常量数据
    private static final String PATH = "gamedata/constant.xml";
    private static final String NAME = "constant_192.168.xx.xx";

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadConstant(path);
    }

    public static void loadConstant(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public void onResourceChange(File file) {
                try {
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //天梯参数
                    Element ladder = XmlUtils.getChildrenByName(root, "ladder")[0];
                    LADDER_UNCONWIN = Integer.valueOf(XmlUtils.getAttribute(ladder, "UNCONWIN"));
                    LADDER_SUBSTAR = Integer.valueOf(XmlUtils.getAttribute(ladder, "SUBSTAR"));
                    LADDER_FIGHT_MAX = Integer.valueOf(XmlUtils.getAttribute(ladder, "FIGHT_MAX"));
                    LADDER_FIGHT_RECOVE_TIME = Long.valueOf(XmlUtils.getAttribute(ladder, "FIGHT_RECOVE_TIME"));
                    LADDER_TOP_NUM = Integer.valueOf(XmlUtils.getAttribute(ladder, "TOPLIST"));
                    LADDER_HISTORY_NUM = Integer.valueOf(XmlUtils.getAttribute(ladder, "HISTORY"));
                    LADDER_OPEN_LV = Integer.valueOf(XmlUtils.getAttribute(ladder, "LADDER_OPEN_LV"));
                    LADDER_FIGHT_BUY_PRICE = StringUtil.getIntList(
                            XmlUtils.getAttribute(ladder, "FIGHT_BUY_PRICE"), ",");
                    LADDER_MATCH_MIN_STAR = Integer.valueOf(XmlUtils.getAttribute(ladder, "MATCH_MIN_STAR"));
                    LADDER_MATCH_MAX_STAR = Integer.valueOf(XmlUtils.getAttribute(ladder, "MATCH_MAX_STAR"));
                    LADDER_MATCH_MIN_FIGHT = Integer.valueOf(XmlUtils.getAttribute(ladder, "MATCH_MIN_FIGHT"));
                    LADDER_MATCH_MAX_FIGHT = Integer.valueOf(XmlUtils.getAttribute(ladder, "MATCH_MAX_FIGHT"));

                    //邮件
                    Element mail = XmlUtils.getChildrenByName(root, "mail")[0];
                    EXPIRE_NO_ATTA = Integer.valueOf(XmlUtils.getAttribute(mail, "EXPIRE_NO_ATTA"));
                    EXPIRE_TOTAL = Integer.valueOf(XmlUtils.getAttribute(mail, "EXPIRE_TOTAL"));

                    //运镖
                    Element escort = XmlUtils.getChildrenByName(root, "escort")[0];
                    ESCORT_DISPATCH_MAX = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_DISPATCH_MAX"));
                    ESCORT_REFRESH_FREE = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_REFRESH_FREE"));
                    ESCORT_ROB = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_ROB"));
                    ESCORT_REWARD = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_REWARD"));
                    ESCORT_HURTED = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_HURTED"));
                    ESCORT_CONSUME = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_CONSUME"));
                    ESCORT_STAR_MAX = Integer.valueOf(XmlUtils.getAttribute(escort, "ESCORT_STAR_MAX"));

                    //竞技场
                    Element arena = XmlUtils.getChildrenByName(root, "arena")[0];
                    ARENA_RANK_CAPACITY = Integer.valueOf(XmlUtils.getAttribute(arena, "RANK_CAPACITY"));
                    ARENA_CHALLENGE_FREE_TIMES = Integer.valueOf(XmlUtils.getAttribute(arena, "FREE_TIMES"));
                    ARENA_CHALLENGE_REFRESH_CD = Integer.valueOf(XmlUtils.getAttribute(arena, "REFRESH"));
                    String[] challengeRangeStr = XmlUtils.getAttribute(arena, "RANGE").split(";");
                    List<Map.Entry<Integer, Integer>> challengeRangeList = new ArrayList<>(challengeRangeStr.length);
                    for (String rangeParamStr : challengeRangeStr) {
                        String[] params = rangeParamStr.split(",");
                        challengeRangeList.add(new AbstractMap.SimpleEntry<>(
                                Integer.valueOf(params[0]), Integer.valueOf(params[1])));
                    }
                    ARENA_CHALLENGE_RANGE = Collections.unmodifiableList(challengeRangeList);
                    ARENA_BATTLE_REWARD_SUCC = Integer.valueOf(XmlUtils.getAttribute(arena, "REWARD_POINT_SUCC"));
                    ARENA_BATTLE_REWARD_FAIL = Integer.valueOf(XmlUtils.getAttribute(arena, "REWARD_POINT_FAIL"));
                    ARENA_RECORD_CAPACITY = Integer.valueOf(XmlUtils.getAttribute(arena, "RECORD_CAPACITY"));
                    ARENA_ROBOT_VIP = Integer.valueOf(XmlUtils.getAttribute(arena, "VIP"));
                    ARENA_ROBOT_LEVEL = Short.valueOf(XmlUtils.getAttribute(arena, "LEVEL"));
                    ARENA_FIGHT_BUY_PRICE = StringUtil.getIntList(
                            XmlUtils.getAttribute(ladder, "FIGHT_BUY_PRICE"), ",");

                    //卡牌
                    Element card = XmlUtils.getChildrenByName(root, "card")[0];
                    CARD_MAX_LEVEL = Short.valueOf(XmlUtils.getAttribute(card, "CARD_MAX_LEVEL"));
                } catch (Exception e) {
                    logger.error("加载常量数据出错...", e);
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

}
