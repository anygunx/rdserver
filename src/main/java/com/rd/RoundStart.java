package com.rd;

import com.rd.activity.ActivityService;
import com.rd.common.GangService;
import com.rd.common.NBossService;
import com.rd.dao.GlobalDao;
import com.rd.dao.db.DBOperator;
import com.rd.dao.db.ProxoolDB;
import com.rd.define.GameDefine;
import com.rd.game.*;
import com.rd.model.*;
import com.rd.model.activity.Activity7Model;
import com.rd.net.ServerPipelineHttpFactory;
import com.rd.net.web.WebActionManager;
import com.rd.task.TaskManager;
import com.rd.task.global.GlobalTasks;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RoundStart {

    static Logger log = Logger.getLogger(RoundStart.class.getName());

    public static void main(String[] args) {
        //String logConfigPath = System.getProperty("user.dir") + GameDefine.RESOURCE_PATH + "config/log4j.properties";
        //PropertyConfigurator.configure(logConfigPath);

        log.info("初始化游戏资源");
        initResource(GameDefine.RES_PATH);

        initResources(GameDefine.RES_PATH);

        RoundStart peach = new RoundStart();
        //PVP服
        if (GameDefine.ISPVP)
            peach.startPvp();
            //游戏服
        else
            peach.startPve();
        log.info("服务器启动成功");
    }

    /**
     * 开始游戏服
     */
    private void startPve() {
        log.info("初始化DB配置");
        initDB(GameDefine.RES_PATH);

        GameDefine.initServerSet();
        log.info("初始化GameWorld");
        initGameWorld();

        log.info("初始化HTTP请求Action");
        WebActionManager.init();

        FengCeModel.loadDatabase();
        GlobalDao.getInstance().initData();
        GameRankManager.getInstance().loadRankList();
        //GameRankManager.getInstance().init();
        NGameRankManager.getInstance().init();
        NGameBiaocCheManager.getInstance().init();
        log.info("初始化任务管理器");
        TaskManager.getInstance().init();
        TaskManager.getInstance().startService();
        GlobalTasks.gi().init();
        LadderModel.loadRankList();
        LadderModel.refreshRankPlayerInfo();
        //ArenaGameService.onUpdateState();
        TipsModel.broadcastTips();
        ActivityService.init();
        ActivityService.initActivityTask();
        //BossService.init();
        NBossService.init();
        AuctionService.init();
        NGameRelationshipManager.getInstance().init();
        log.info("初始化网络");
        initNet();

        log.info("记录启动信息");
        saveStartData();
    }

    /**
     * 开始PVP服
     */
    private void startPvp() {
        log.info("初始化DB配置");
        initDB(GameDefine.RES_PATH);

        log.info("初始化Redis连接");
        //JedisManager.gi();

        log.info("初始化HTTP请求Action");
        WebActionManager.init();

        TaskManager.getInstance().init();
        TaskManager.getInstance().startService();
        GlobalTasks.gi().initPvP();

        //PvPWorld.gi().init();

        log.info("初始化网络");
        initNet();
        log.info("PVP服务器启动成功...");
    }

    private void initDB(String path) {
        ProxoolDB.init(path);
    }

    private static void initResources(String path) {
        CombatModel.loadData(path);
        GrowModel.loadData(path);
        log.info("加载一键强化信息");
        NEquipModel.loadModelData(path);
        log.info("加载皮肤信息");
        NSkinModel.loadModelData(path);
        log.info("加载法宝信息");
        NFaBaoModel.loadModelData(path);
        log.info("加载装备物品信息");
        NGoodModel.loadPulseModelData(path);
        log.info("加载技能信息");
        NskillModel.loadModelData(path);
        log.info("加载材料副本信息");
        NCopyModel.loadData(path);
        log.info("加载全民boss信息");
        NBossModel.loadData(path);
        log.info("加载组队数据");
        TeamModel.loadData(path);
        log.info("加载装备商店数据");
        NShopModel.loadModelData(path);
        log.info("加载日常钟馗数据");
        NRiChangModel.loadData(path);
        log.info("加载日常任务数据");
        NTaskModel.loadModelData(path);
        log.info("加载培养系统数据");
        NTaskAdvancedModel.loadModelData(path);
        log.info("加载帮派系统数据");
        NFactionModel.loadModelData(path);
        log.info("加载护送数据");
        NHuSongModel.loadData(path);
        log.info("加载竞技场数据");
        NJingJiModel.loadData(path);
    }

    private static void initResource(String path) {
        log.info("加载游戏配置数据");
        GameDefine.loadConfig(path);
        log.info("加载游戏常量数据");
        ConstantModel.loadData(path);
        log.info("加载战士模型数据");
        FighterModel.loadData(path);
        log.info("加载物品模型数据");
        GoodsModel.loadGoods(path);
        log.info("加载主角模型数据");
        RoleModel.loadRole(path);
        log.info("加载地图模型数据");
        MapModel.loadData(path);
        log.info("加载副本模型数据");
        DungeonModel.loadData(path);
        log.info("加载掉落模型数据");
        DropModel.loadDrop(path);
        log.info("加载装备模型数据");
        EquipModel.loadData(path);
        log.info("加载功能模型数据");
        FunctionModel.loadFunction(path);
        log.info("加载神器模型数据");
        ShenQiModel.loadData(path);
        log.info("加载天梯奖励数据");
        LadderModel.loadData(path);
        log.info("加载活动数据");
        ActivityService.loadData(path);
        log.info("加载经脉数据");
        MeridianModel.loadData(path);
        log.info("加载商城数据");
        ShopModel.loadData(path);
        log.info("加载任务数据");
        MissionModel.loadMission(path);
        log.info("加载敏感字数据");
        WordSensitiveModel.loadData(path);
        log.info("加载元魂数据");
        SpiritModel.loadData(path);
        log.info("加载橙装模型数据");
        OrangeModel.loadData(path);
        log.info("加载BOSS数据");
        BossModel.loadData(path);
        log.info("加载押镖数据");
        EscortModel.loadData(path);
        log.info("加载7日活动");
        Activity7Model.loadData(path);
        log.info("加载TIPS数据");
        TipsModel.loadData(path);
        log.info("加载月卡数据");
        MonthlyCardModel.loadData(path);
        log.info("加载VIP数据");
        VipModel.loadData(path);
        log.info("加载充值数据");
        PayModel.loadData(path);
        log.info("加载每日福利数据");
        WelfareModel.loadData(path);
        log.info("加载公会数据");
        GangModel.loadGang(path);
        log.info("加载转生数据");
        ReinModel.loadData(path);
        log.info("加载主宰数据");
        DomModel.loadData(path);
        log.info("加载玩法数据");
        SectionModel.loadSection(path);
        log.info("加载称号数据");
        TitleModel.loadData(path);
        log.info("加载功法数据");
        GongFaModel.loadData(path);
        log.info("加载封测奖励数据");
        FengCeModel.loadData(path);
        log.info("加载跨服奖励数据");
        KuaFuModel.loadData(path);
        log.info("加载技能数据");
        SkillModel.loadData(path);
        log.info("加载拍卖数据");
        AuctionModel.loadData(path);
        log.info("加载龙珠数据");
        DragonBallModel.loadData(path);
        log.info("加载勋章数据");
        MedalModel.loadData(path);
        log.info("加载夜战比奇数据");
        NightFightModel.loadData(path);
        log.info("加载官阶数据");
        GuanJieModel.loadData(path);
        log.info("加载关卡神器数据");
        ArtifactModel.loadData(path);
        log.info("加载战纹数据");
        ZhanWenModel.loadData(path);
        log.info("加载炼体数据");
        LianTiModel.loadData(path);
        log.info("加载邮件奖励数据");
        MailModel.loadData(path);
        log.info("加载传世争霸数据");
        BattleModel.loadData(path);
        log.info("加载怪物攻城数据");
        MonsterSiegeModel.loadData(path);
        log.info("加载卡牌数据");
        CardModel.loadData(path);
        log.info("加载五行数据");
        FunctionModel.loadFunction(path);
        //log.info("加载新活动数据");
        //ActivityNewService.init(path);
        log.info("加在每日累计消费达标奖励数据");
        TargetModel.loadData(path);


        log.info("加载培养项数据");
        //TrainItemModel.loadTrainItem(path);
    }

    private void initGameWorld() {
        GameWorld.getPtr().init();

        log.info("初始化帮派管理器");
        GameGangManager.getInstance().init();
        NGameFactionManager.getInstance().init();

        log.info("初始化野外pvp管理器");
        GamePvpManager.getInstance().init();

        log.info("初始化传世争霸服务");
        GangService.getPtr().init();

        log.info("初始化怪物攻城");
        GameMonsterSiegeService.init();
    }

    private void initNet() {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), 256));
        bootstrap.setPipelineFactory(new ServerPipelineHttpFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
//		bootstrap.setOption("child.keepAlive", true);
//		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
        bootstrap.bind(new InetSocketAddress(GameDefine.SERVER_PORT));
    }

    private void saveStartData() {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into startup(port,serverid,createtime,starttime) values(");
        sql.append(GameDefine.SERVER_PORT + ",");
        sql.append(GameDefine.getServerId() + ",");
        sql.append("'" + DateUtil.formatDateTime(GameDefine.SERVER_CREATE_TIME) + "',");
        sql.append("'" + DateUtil.formatDateTime(System.currentTimeMillis()) + "')");
        new DBOperator().executeSql(sql.toString());
    }
}
