package com.rd.game.event;

import com.google.common.base.Preconditions;
import com.rd.dao.EPlayerSaveType;
import com.rd.game.GameRole;
import com.rd.game.event.provider.GameEventProvider;
import com.rd.game.event.provider.IGameRoleEventProvider;
import com.rd.game.event.type.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩家事件类型
 * Created by XingYun on 2016/5/24.
 */
public enum EGameEventType {


    FACTION_SHAGNXIANG(0),
    FACTION_COPY(1),
    FACTION_XIAOYAO(2),
    FACTION_CAIJI(3),
    FACTION_YAOGUAI(4),
    FACTION_ZISE_SHOUGOU(5),
    FACTION_LANSE_SHOUGOU(6),
    FACTION_LVSE_SHOUGOU(7),

    EVER_DAY_LOGIN(10),
    EQUIP_RONGLIANG(25),
    COPY_ZHOMGKUI(26),
    COPY_GEREN(28),
    COPY_QUANMIN(29),
    COPY_CAILIAO(31),
    /***************************分割线 以下是旧版本**********************************************************************************/
    NONE(10000),//(0),
    /**
     * 挂机
     **/
    X(10001),//(1),目前没用,单纯为了和文档对应上
    /**
     * 野外地图关卡
     **/
    MAP_PASS(10002),//(2),
    /**
     * 角色达到N级
     **/
    PLAYER_REACH_LEVEL(10003),//(3),
    /**
     * 装备强化
     **/
    EQUIP_STRENGTHEN(10004, GameStrengthEvent.provider),//(4),
    /**
     * 技能升级
     **/
    SKILL_LEVEL_UP(10005),//(5),
    /**
     * 装备熔炼
     **/
    EQUIP_MELTING(10006),//(6) ,
    /**
     * 升级翅膀(点一次就算)
     **/
    WING_UP(10007, GameMountUpEvent.provider),//(7),
    /**
     * 参加一次全民boss
     **/
    ALL_PEOPLE_BOSS(8),//(8) ,
    /**
     * 通关宝石副本
     **/
    DUNGEON_GEM_CLEARANCE(9),//(9) ,
    /**
     * 宝石达到N级
     **/
    GEM_REACH_LEVEL(100, GameGemUpEvent.provider),//(10),
    /**
     * 通关经脉副本
     **/
    DUNGEON_MERIDIAN_CLEARANCE(11),//(11),
    /**
     * 经脉培养
     **/
    MERIDIAN_UP(12, GameMerdianUpEvent.provider),//(12),
    /**
     * 神铸培养
     **/
    CASTING_SOUL_UP(13),//(13),
    /**
     * 进入成就副本
     **/
    DUNGEON_CUILIAN_CLEARANCE(14),//(14),
    /**
     * 勋章达到N级
     **/
    MEDAL_REACH_LEVEL(15),//(15),
    /**
     * 通关龙鳞副本
     **/
    DUNGEON_SQUAMA_CLEARANCE(16),//(16),
    /**
     * 通关龙纹副本
     **/
    DUNGEON_VEINS_CLEARANCE(17),//(17),
    /**
     * 通关诛仙台
     **/
    DUNGEON_DEKARON_PASS(18),//(18),
    /**
     * 累计登陆
     **/
    LOGON_COUNT(19),//(19),暂未实现
    /**
     * 穿装备
     **/
    WEAR_EQUIPMENT(20),//(20),
    /**
     * 击败个人BOSS
     **/
    DUNGEON_BOSS_SUCC(21),//(21),
    /**
     * 参加一次野战
     **/
    FIELD_PVP(22),//(22),
    /**
     * 天梯达到N段位
     **/
    LADDER_MATCH(23),//(23),
    /**
     * 激活传世碎片①
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_1(24),//(24),
    /**
     * 激活传世碎片②
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_2(250),//(25),
    /**
     * 激活传世龙魂
     **/
    ACTIVE_ARTIFACT_1(260),//(26),
    /**
     * 挑战材料副本
     **/
    DUNGEON_METERIAL_ENTER(27),//(27),
    /**
     * 击杀关卡小怪
     **/
    KILL_FIELD_MONSTER(280),//(28),
    /**
     * 寻宝
     **/
    SEEK_TREASURE(290),//(29),
    /**
     * 激活霸业碎片①
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_3(30),//(30),
    /**
     * 激活霸业碎片②
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_4(310),//(31),
    /**
     * 激活霸业碎片③
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_5(32),//(32),
    /**
     * 激活霸业碎片④
     **/
    ACTIVE_ARTIFACT_FRAGMENTS_6(33),//(33),
    /**
     * 激活霸业宝印
     **/
    ACTIVE_ARTIFACT_2(34),//(34),
    /**
     * 使用自动战斗
     **/
    XXXX(35),//(35),目前没用,单纯为了和文档对应上
    /**
     * 龙鳞升级
     **/
    FLUTE_UP(36, GameYudiUpEvent.provider),//(36),
    /**
     * 龙纹升级
     **/
    MIRROR_UP(37, GameMirrorUpEvent.provider),//(37),
    /**
     * 通关羽翼副本
     **/
    DUNGEON_WING_CLEARANCE(38),//(38),
    /**
     * 转生
     **/
    REIN_REACH_LEVEL(39),//(39),
    /**
     * 没用
     **/
    XX(40),//(40),
    /**
     * VIP达到N级
     **/
    VIP_REACH_LEVEL(41),//(41),
    /**
     * 参与天梯次数
     **/
    LADDER_MATCH_COUNT(42),//(42),
    /**
     * 官阶达到N阶
     **/
    GUANJIE_REACH_LEVEL(43),//(43),
    /**
     * 龙珠达到N级
     **/
    DRAGON_BALL_REACH_LEVEL(44),//(44),
    /**
     * 装备注灵
     **/
    EQUIP_FILL_SOUL(45, GameFillSoulEvent.provider),//(45),
    /**
     * 激活月卡
     **/
    ACTIVE_MONTH_CARD(46),//(46),
    /**
     * 参与转生BOSS
     **/
    REIN_BOSS(47),//(47),
    /**
     * 充值
     **/
    PAY(48, GamePayEvent.provider),//(40),
    /**
     * 登录游戏
     **/
    ENTER_GAME(49, GameEnterEvent.provider),//(49),
    /**
     * 翅膀达到N阶段
     **/
    WING_REACH_STAGE(50),//(50),
    /**
     * 升级一次龙珠
     **/
    DRAGON_BALL_LEVEL_UP(51),//(51),
    /**
     * 占位置
     **/
    XXX(52),//(52),
    /**
     * 获得n件合击符文
     **/
    COMBINE_RUNE_NUM(53),//(53),
    /**
     * 累计击杀N次一转个人BOSS
     **/
    KILL_A_REIN_BOSS_TOTAL(54),//(54),
    /**
     * 神铸总次数
     **/
    CASTING_SOUL_TOTAL_UP(55),//(55),
    /**
     * 参与秘境BOSS
     **/
    ENTER_MYSTERY_BOSS(56),//(56),
    /**
     * 完成首冲
     **/
    COMPLETE_FIRST_PAY(57),//(57),
    /**
     * 累计击杀个人BOSS次数
     **/
    KILL_PERSONAL_BOSS_TOTAL(58),//(58),
    /**
     * 参与BOSS之家
     **/
    ENTER_VIP_BOSS(59),//(59),
    /**
     * 进行一次挖矿
     **/
    CARRY_ON_MINING(60),//(60),
    /**
     * 进行一次矿源掠夺
     **/
    CARRY_ON_MINING_ROB(61),//(61),
    /**
     * 进行一次分享
     **/
    CARRY_ON_SHARE(62),//(62),
    /**
     * 通关镇魔塔
     **/
    DUNGEON_FENGMOTA_PASS(63),//(63),
    /**
     * 消耗元宝
     **/
    COST_DIAMOND(64),//(64),
    /**
     * 充值元宝
     **/
    PAY_DIAMOND(65),//(65),
    /**
     * 怪物攻城
     **/
    MONSTER_SIEGE(66),//(66),
    /**
     * 公会捐献
     **/
    GANG_DONATE(67),//(67),
    /**
     * 五行
     **/
    FIVE_ELEMENT_UP(68),//(68)
    /**
     * 通关五行副本
     **/
    DUNGEON_FIVE_ELEMENT_CLEARANCE(69),//(69)
    /**
     * 通关主宰试炼
     **/
    DUNGEON_ZHUZAISHILIAN_PASS(70),//(70)

    //---------------------分割线 以上为策划定义 以下为程序定义----------------------------//
    /**
     * 新品拍卖
     **/
    NEW_AUCTION_ITEM(100),//(100),
    /**
     * 激活神器碎片
     **/
    ARTIFACT_PIECE_INVOKE(101),//(101),
    /**
     * 激活神器
     **/
    ARTIFACT_BOSS_INVOKE(102),//(102),
    /**
     * 激活神兵
     **/
    SHENBING_INVOKE(103),//(103),
    /**
     * 神兵升星
     **/
    SHENBING_STAR_UP(104),//(104),
    /**
     * 神兵升阶
     **/
    SHENBING_STAGE_UP(105),//(105),
    /**
     * 战纹激活
     **/
    ZHANWEN_ACTIVE(106),//(106),
    /**
     * 战纹升级
     **/
    ZHANWEN_UP(107),//(107),
    /**
     * 创建帮派
     **/
    GANG_CREATE(108, GameGangCreateEvent.provider),//(108),
    /** 合计符文套装激活 **/
//    COMBINE_SUIT_INVOKE         (109,GameCombineSuitInvokeEvent.provider),//(108),
    /**
     * 神羽装备
     **/
    WING_GOD_EQUIP(109),//(101),
    /**
     * 卡牌升级
     **/
    CARD_LEVEL_UP(110),//(110),
    /**
     * 圣纹装备
     **/
    HOLYLINES_EQUIP(111),//(111),
    /**
     * 帮会副本通关
     **/
    DUNGEON_GANG_PASS(112),;
    /**
     * 关联的主线任务
     **/
    private Map<Short, EMissionUpdateType> chainMissions = new HashMap<>();

    /**
     * 关联的日常任务
     **/
    private short dailyMissionId = 0;

    /**
     * 关联的帮会任务
     **/
    private byte gangMissionId = 0;

    /**
     * 关联的龙珠任务
     **/
    private short dragonballMissionId = 0;

    /**
     * 关联的成就任务
     **/
    private Map<Short, EMissionUpdateType> achievementMissions = new HashMap<>();
    /**
     * 关联的限时任务
     **/
    private Map<Short, EMissionUpdateType> tlMissions = new HashMap<>();
    /**
     * 关联的卡牌限时任务
     **/
    private Map<Short, EMissionUpdateType> cardMissions = new HashMap<>();

    private final static Map<Short, EGameEventType> map = new HashMap<Short, EGameEventType>() {
        {
            for (EGameEventType type : EGameEventType.values()) {
                put(type.id, type);
            }
        }
    };

    private final IGameRoleEventProvider provider;

    private short id;

    EGameEventType(int id) {
        this(id, new GameEventProvider());
    }

    EGameEventType(int id, IGameRoleEventProvider provider) {
        this.id = (short) id;
        this.provider = provider;
    }

    public static EGameEventType getEventType(int id) {
        return map.get((short) id);
    }

    public void addChainMissions(Short missionId, EMissionUpdateType updateType) {
        if (missionId != null) {
            Preconditions.checkNotNull(updateType,
                    "EGameEventType create failed. Error chainMissionId=" + missionId + " chainMissionUpdateType is null.");
        }
        chainMissions.put(missionId, updateType);
    }

    public Map<Short, EMissionUpdateType> getChainMissions() {
        return chainMissions;
    }

    public void setDailyMission(short id) {
        dailyMissionId = id;
    }

    public short getDailyMissionId() {
        return dailyMissionId;
    }

    public void setGangMission(byte gangMissionId) {
        this.gangMissionId = gangMissionId;
    }

    public byte getGangMissionId() {
        return gangMissionId;
    }

    public short getDragonballMissionId() {
        return dragonballMissionId;
    }

    public void setDragonballMissionId(short dragonballMissionId) {
        this.dragonballMissionId = dragonballMissionId;
    }

    public void addAchievementMission(Short missionId, EMissionUpdateType updateType) {
        if (missionId != null) {
            Preconditions.checkNotNull(updateType,
                    "EGameEventType create failed. Error achievementMissionId=" + missionId + " achievementMissionUpdateType is null.");
        }
        achievementMissions.put(missionId, updateType);
    }

    public Map<Short, EMissionUpdateType> getAchievementMissions() {
        return achievementMissions;
    }

    public void addTLMission(Short missionId, EMissionUpdateType updateType) {
        if (missionId != null) {
            Preconditions.checkNotNull(updateType,
                    "EGameEventType create failed. Error tlMissionId=" + missionId + " tlMissionId is null.");
        }
        tlMissions.put(missionId, updateType);
    }

    public Map<Short, EMissionUpdateType> getTLMissions() {
        return tlMissions;
    }

    public void addCardMission(Short missionId, EMissionUpdateType updateType) {
        if (missionId != null) {
            Preconditions.checkNotNull(updateType,
                    "EGameEventType create failed. Error tlMissionId=" + missionId + " tlMissionId is null.");
        }
        cardMissions.put(missionId, updateType);
    }

    public Map<Short, EMissionUpdateType> getCardMissions() {
        return cardMissions;
    }

    /**
     * 构造事件
     *
     * @param gameRole
     * @param data
     * @param enumSet
     * @param <T>
     * @return
     */
    public <T extends GameEvent> T create(GameRole gameRole, int data, EnumSet<EPlayerSaveType> enumSet) {
        return (T) provider.create(gameRole, this, data, enumSet);
    }

    /**
     * 模拟玩家事件
     *
     * @param gameRole
     * @param <T>
     * @return
     */
    public <T extends GameEvent> T simulate(GameRole gameRole) {
        return (T) provider.simulate(this, gameRole);
    }
}
