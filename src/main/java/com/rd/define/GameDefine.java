package com.rd.define;

import com.google.common.collect.ImmutableList;
import com.rd.dao.PlayerDao;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GameDefine {

    static Logger log = Logger.getLogger(GameDefine.class.getName());

    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";

    public static String PROTOCOL = PROTOCOL_HTTP;

    public final static String ROOT_PATH = ".";
    public final static String RESOURCE_PATH = "/resources/";
    /**
     * 资源文件根目录
     */
    public final static String RES_PATH = ROOT_PATH + RESOURCE_PATH;
    /**
     * 游戏服端口
     */
    public static int SERVER_PORT = 3001;
    /**
     * 游戏服创建时间
     */
    public static long SERVER_CREATE_TIME;
    /**
     * 是否是发布版本
     */
    public static Boolean ISPUBLISH = true;
    /**
     * 是否PVP服务器
     */
    public static boolean ISPVP = false;
    /**
     * PVP服务器
     */
    public static String PVPURL = null;
    /**
     * REDIS地址、端口
     */
    public static String REDIS_CLUSTER = "";
    /**
     * 服务器的ID 用于ID生成规则
     */
    public static final int ID_RANG = 100000;
    private static short SERVER_ID;
    private static Set<Short> SERVER_SET;

    public static short getServerId() {
        return SERVER_ID;
    }

    public static Set<Short> getServerSet() {
        return SERVER_SET;
    }

    public static List<String> PAY_SAFE_IP = Collections.emptyList();
    //URL安全的IP
    public static List<String> URL_SAFE_IP = Collections.emptyList();

    /**
     * 离线玩家数据最小数量
     */
    public static short OFFLINE_MIN_NUM = 50;
    /**
     * 离线玩家数据最大数量
     */
    public static short OFFLINE_MAX_NUM = 500;
    /**
     * 离线超时时间
     */
    public static long OFFLINE_TIMEOUT = 1 * DateUtil.HOUR;
    /**
     * 在线超时时间
     */
    public static long ONLINE_TIMEOUT = 1 * DateUtil.HOUR;// 10 * DateUtil.MINUTE;
    //离线经验记录最大时间
    public static int OFFLINE_EXP_MAX = 24;

    /**
     * 无效的
     */
    public static final byte INVALID = -1;
    /**
     * 无
     */
    public static final byte NONE = -1;

    public final static byte TRUE = 1;
    public final static byte FALSE = 0;

    /**
     * 正常操作时间间隔
     **/
    public static final long OPERATION_INTERVAL = 1 * DateUtil.SECOND;

    /**
     * 名称长度限制
     **/
    public static byte NAME_LENGTH_LIMIT = 6;

    /**
     * 职业：女
     */
    public final static byte OCCUPATION_GIRL = 0;
    /**
     * 职业：男
     */
    public final static byte OCCUPATION_BOY = 1;
    /**
     * 职业数量
     */
    public final static byte OCCUPATION_NUM = 2;

    //机器人、玩家
    public static final byte CHALLENGER_TYPE_ROBOT = 0;
    public static final byte CHALLENGER_TYPE_PLAYER = 1;

    //邮件状态
    public static final byte MAIL_STATE_UNREAD = 1;//未读
    public static final byte MAIL_STATE_READED = 2;//已读
    public static final byte MAIL_STATE_UNREWARD = 10;//未领取
    public static final byte MAIL_STATE_REWARDED = 20;//已领取
    //邮件类型
    public static final byte MAIL_TYPE_GM = 0;//GM邮件
    public static final byte MAIL_TYPE_SYSTEM = 1;//系统邮件

    /**
     * 角色状态：正常
     **/
    public static final byte PLAYER_STATE_NORMAL = 0;
    /**
     * 角色状态：封号
     **/
    public static final byte PLAYER_STATE_FREEZE = 1;
    /**
     * 角色状态：禁言
     **/
    public static final byte PLAYER_STATE_SHUTUP = 2;

    //元魂求签的类型
    public static final byte SPIRIT_LOTTERY_NORMAL = 1;    //普通求签
    public static final byte SPIRIT_LOTTERY_GOD = 2;    //神签
    //求签索引
    public static final byte[] SPIRIT_INDEXS = {0, 1, 2, 3, 4};
    //神签索引
    public static final byte SPIRIT_GOD_INDEX = 3;
    //神签消耗
    public static final int SPIRIT_GOD_COST = 50;        //神签
    //元魂装备等级
    public static final int[] SPIRIT_UNLOCK = {0, 35, 35, 40, 45, 50, 60, 70, 80, 90, 100};

    //转生最低要求等级
    public static final int REIN_LV = 80;
    //转生等级兑换每日次数
    public static final byte REIN_EX = 5;
    /**
     * 转生阶梯等级
     */
    public static final int REIN_LV_STEP = 10;

    //摇钱树奖励  第0位  次数  后面DropData格式
    public static final int[][] GOLD_TREE_REWARD = {{5, 4, 0, 300000,},
            {20, 4, 0, 3000000,},
            {50, 4, 0, 30000000,}};
    //VIP0的摇钱树次数
    public static final int GOLD_TREE_NUM = 5;

    //运镖日志数量
    public static final int ESCORT_LOG_MAX = 20;

    //竞技场每天次数
    public static final byte ARENA_COUNT = 15;

    //空集合的JSON串
    public static final String EMPTY_COLL_STR = StringUtil.toJson(Collections.emptyList());

    public static int getIdLow(int serverId) {
        return serverId * GameDefine.ID_RANG;
    }

    public static int getIdHigh(int serverId) {
        return getIdLow(serverId) + GameDefine.ID_RANG;
    }

    /**
     * 限时任务开启等级
     **/
    public static final int FUNCTION_TLMISSION_OPEN_LEVEL = 50;
    /**
     * 卡牌任务开启等级
     **/
    public static final int FUNCTION_CARD_MISSION_OPEN_LEVEL = 50;

    public static void loadConfig(String path) {
        final File file = new File(path, "config/gameConfig.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    String proto = XmlUtils.getChildText(root, "protocol");
                    if (!StringUtil.isEmpty(proto)) {
                        PROTOCOL = proto;
                    }
                    log.info(PROTOCOL);
                    SERVER_PORT = Integer.parseInt(XmlUtils.getChildText(root, "gameport"));
                    ISPUBLISH = Boolean.parseBoolean(XmlUtils.getChildText(root, "ispublish"));
                    String ispvp = XmlUtils.getChildText(root, "ispvp");
                    if (ispvp != null && ispvp.equals("true")) {
                        ISPVP = true;
                    }
                    PVPURL = XmlUtils.getChildText(root, "pvpurl");
                    REDIS_CLUSTER = XmlUtils.getChildText(root, "rediscluster");
                    SERVER_CREATE_TIME = DateUtil.parseDataTime(XmlUtils.getChildText(root, "createTime")).getTime();
                    SERVER_ID = Short.parseShort(XmlUtils.getChildText(root, "serverID"));

                    List<String> paySafeIpList = new ArrayList<>();
                    for (String ip : XmlUtils.getChildText(root, "paysafeip").split(",")) {
                        paySafeIpList.add(ip);
                    }
                    PAY_SAFE_IP = ImmutableList.copyOf(paySafeIpList);

                    List<String> urlSafeIpList = new ArrayList<>();
                    for (String ip : XmlUtils.getChildText(root, "urlsafeip").split(",")) {
                        urlSafeIpList.add(ip);
                    }
                    URL_SAFE_IP = ImmutableList.copyOf(urlSafeIpList);
                } catch (Exception e) {
                    log.error("加载游戏配置数据出错...");
                }
            }

            @Override
            public String toString() {
                return "gameConfig";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void initServerSet() {
        SERVER_SET = new PlayerDao().getRealServerIds();
    }

    public static boolean containServer(int serverId) {
        return SERVER_SET.contains((short) serverId);
    }
}
