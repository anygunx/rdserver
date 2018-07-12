package com.rd.bean.player;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.reflect.TypeToken;
import com.rd.bean.artifact.ArtifactBoss;
import com.rd.bean.card.PlayerCardBook;
import com.rd.bean.data.LevelData;
import com.rd.bean.dragonball.DragonBall;
import com.rd.bean.drop.DropData;
import com.rd.bean.faction.NFaction;
import com.rd.bean.fanbao.NDanYao;
import com.rd.bean.fight.monstersiege.IGameMonsterAttacker;
import com.rd.bean.five.FiveElements;
import com.rd.bean.function.FunctionData;
import com.rd.bean.gang.Gang;
import com.rd.bean.goods.*;
import com.rd.bean.grow.Grow;
import com.rd.bean.grow.GrowSeed;
import com.rd.bean.grow.GrowSuit;
import com.rd.bean.lianti.Ambit;
import com.rd.bean.mission.CardMission;
import com.rd.bean.mission.RoleChainMission;
import com.rd.bean.mission.TLMissionData;
import com.rd.bean.relationship.IRelatedPlayer;
import com.rd.bean.skill.NSkillSystem;
import com.rd.bean.skin.NSkin;
import com.rd.bean.skin.NTaoZhuang;
import com.rd.common.GameCommon;
import com.rd.define.*;
import com.rd.enumeration.EAttr;
import com.rd.enumeration.EEquip;
import com.rd.enumeration.EGrow;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.lg.bean.IPlayer;
import com.rd.model.*;
import com.rd.model.data.*;
import com.rd.model.data.skin.NTaoZhuangData;
import com.rd.model.data.task.NLiLianData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;

import java.util.*;
import java.util.Map.Entry;

/**
 * 角色信息--数据库对象
 *
 * @author Created by U-Demon on 2016年11月5日 下午4:57:46
 * @version 1.0.0
 */
public class Player extends SimplePlayer implements IGameRole, IPlayer, IRelatedPlayer, IGameMonsterAttacker {

    /**
     * 装备槽
     */
    private List<NEquipSlot> nEquipSlotList = new ArrayList<>();
    /**
     * 皮肤
     */
    private Map<Byte, NSkin> nPiFuMap = new HashMap<>();
    private Map<Byte, NTaoZhuang> nTaoZhuangMap = new HashMap<>();
    private List<NDanYao> danYaoList = new ArrayList<>();
    /**
     * 穿上的装备列表
     */
    private short[] wearEquip;
    /**
     * 经脉
     */
    private short pulse = 0;
    private short liLianLevel = 1;
    private int liLianExp = 0;


    /**
     * 友情值
     */
    private int receviteFC = 0;
    private short receviteFCCount = 0;
    private short sendFCCount = 0;
    /**
     * 水晶宫关卡
     */
    private int sjgCopyId = 0;
    /**
     * 天门天战最大关卡
     */
    private short tmMaxCopyId = 0;
    /**
     * 密藏累计总星级
     */
    private short miSartTotal = 0;

    //全民BOSS剩余次数
    private short citBossLeft = 0;
    //全民BOSS次数上次回复时间
    private long citRecover = 0;
    //全民BOSS提醒
    private List<Short> citCue;

    /**
     * 全民boss购买次数*
     */
    private byte qmBossBuyCount = 0;
    /**
     *
     */
    private byte qmbossFightcount = 0;

    /***
     * 竞技场 历史名次
     */
    private short jingJirank = 0;
    private short currJJRank = 5000;

    public short getCurrJJRank() {
        return currJJRank;
    }

    public void setCurrJJRank(short currJJRank) {
        this.currJJRank = currJJRank;
    }

    public short getJingJirank() {
        return jingJirank;
    }

    public void setJingJirank(short jingJirank) {
        this.jingJirank = jingJirank;
    }

    private NRCData nrcData = new NRCData();

    public NRCData getNrcData() {
        return nrcData;
    }

    public void setNrcData(NRCData nrcData) {
        this.nrcData = nrcData;
    }

    public void fromNrcDataJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.nrcData = StringUtil.gson2Obj(json, NRCData.class);
        }
    }

    public String toNrcDataJson() {
        return StringUtil.obj2Gson(nrcData);
    }

    private List<NTAData> ntdaDataList = new ArrayList<>();
    private List<JingJiRecord> jingJiRecords = new ArrayList<>();

    public List<JingJiRecord> getJingJiRecords() {
        return jingJiRecords;
    }

    public void setJingJiRecords(List<JingJiRecord> jingJiRecords) {
        this.jingJiRecords = jingJiRecords;
    }

    public void setJingJiRecordsListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.jingJiRecords = JSON.parseArray(json, JingJiRecord.class);
        }
    }

    public String getJingJiRecordsJson() {
        return JSON.toJSONString(jingJiRecords);
    }

    public List<NTAData> getNtaData() {
        return ntdaDataList;
    }

    public void setNtaData(List<NTAData> ntaData) {
        this.ntdaDataList = ntaData;
    }


    public String getNTADataListJson() {
        return JSON.toJSONString(ntdaDataList);
    }

    public void setNTADataListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.ntdaDataList = JSON.parseArray(json, NTAData.class);
        } else {
            this.ntdaDataList = new ArrayList<>();
            for (NTaskAdvanceType type : NTaskAdvanceType.values()) {
                NTAData dat = new NTAData();
                dat.setType(type.getType());
                this.ntdaDataList.add(dat);
            }
        }
    }


    /**
     * 日常任务
     **/
    private short[] dailyProgress = null;

    private short[] yesterdateProgress = null;

    //公会技能
    private int[] factionSkill;


    public int[] getFactionSkill() {
        return factionSkill;
    }

    public void setFactionSkill(int[] factionSkill) {
        this.factionSkill = factionSkill;
    }

    public String getFactionSkillJson() {
        return JSON.toJSONString(factionSkill);
    }

    public void setFactionSkillJson(String json) {
        if (StringUtil.isEmpty(json)) {
            this.factionSkill = new int[NFactionDefine.FACTION_SKILL_COUNT];
        } else {
            this.factionSkill = JSON.parseObject(json, new TypeReference<int[]>() {
            });
        }

    }

    public byte getQmcount() {
        return qmbossFightcount;
    }

    public void setQmcount(byte qmcount) {
        this.qmbossFightcount = qmcount;
    }

    public void addQmcount(byte qmcount) {
        this.qmbossFightcount += qmcount;
    }

    public byte getQmBossBuyCount() {
        return qmBossBuyCount;
    }

    public void setQmBossBuyCount(byte qmBossBuyCount) {
        this.qmBossBuyCount = qmBossBuyCount;
    }

    public void addQmBossBuyCount(byte qmBossBuyCount) {
        this.qmBossBuyCount += qmBossBuyCount;
    }

    public short getCitBossLeft() {
        return citBossLeft;
    }

    public void setCitBossLeft(short num) {
        this.citBossLeft = num;
    }

    public void changeCitBossLeft(int num) {
        this.citBossLeft += num;
    }

    public long getCitRecover() {
        return citRecover;
    }

    public void setCitRecover(long citRecover) {
        this.citRecover = citRecover;
    }

    public List<Short> getCitCue() {
        return this.citCue;
    }

    public void setCitCue(List<Short> citCue) {
        this.citCue = citCue;
    }


    public short[] getWearEquip() {
        return wearEquip;
    }

    public String getWearEquipJson() {
        return JSON.toJSONString(wearEquip);
    }

    public void setWearEquipJson(String json) {
        if (StringUtil.isEmpty(json)) {
            this.wearEquip = new short[EquipDefine.EQUIP_POS_NUM];
        } else {
            this.wearEquip = JSON.parseObject(json, new TypeReference<short[]>() {
            });
        }

    }


    public String getDanYaoListJson() {
        return JSON.toJSONString(danYaoList);
    }

    public void setDanYaoList(String json) {
        if (StringUtil.isEmpty(json))
            for (int i = 0; i < NFaBaoModel.getNDanYaoSizeData(); i++) {
                this.danYaoList.add(new NDanYao());
            }
        else
            this.danYaoList = JSON.parseArray(json, NDanYao.class);
    }

    public String getEquipSlotListJson() {
        return JSON.toJSONString(nEquipSlotList);
    }

    public void setEquipSlotList(String json) {
        if (StringUtil.isEmpty(json))
            for (int i = 0; i < EquipDefine.EQUIP_POS_NUM; i++) {
                this.nEquipSlotList.add(new NEquipSlot());
            }
        else
            this.nEquipSlotList = JSON.parseArray(json, NEquipSlot.class);
    }

    public List<NEquipSlot> getEquipSlotList() {
        return nEquipSlotList;
    }


    public Map<Byte, NSkin> getPiFuMap() {
        return nPiFuMap;
    }

    public List<NDanYao> getDanYaoList() {
        return danYaoList;
    }

    public String getPiFuJsonData() {
        return JSON.toJSONString(nPiFuMap);
    }

    public void setPiFuJsonData(String jsonData) {
        if (!StringUtil.isEmpty(jsonData)) {
            this.nPiFuMap = JSON.parseObject(jsonData, new TypeReference<Map<Byte, NSkin>>() {
            });
        }
        setTZData();
    }

    /**
     * 数据库里没有保存套装的信息目的是减少了跟数据交互
     * 这个是动态从激活的皮肤来推算出来的
     */
    private void setTZData() {
        if (nPiFuMap == null || nPiFuMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Byte, NSkin> map : nPiFuMap.entrySet()) {
            NSkin skin = map.getValue();
            if (skin == null) {
                continue;
            }
            List<Integer> skinIds = skin.getHuanHuaList();
            if (skinIds == null || skinIds.isEmpty()) {
                continue;
            }
            initTzData(skinIds);
        }

    }

    /**
     * 动态初始化套装数据
     *
     * @param skinIds
     */
    private void initTzData(List<Integer> skinIds) {
        for (Integer skin : skinIds) {
            if (skin == null) {
                continue;
            }
            Set<Byte> tzTypeList = NSkinModel.getTZTypeBySkinId(skin);

            if (tzTypeList == null || tzTypeList.size() == 0) {
                return;
            }
            for (Byte type : tzTypeList) {
                NTaoZhuang taozhuang = nTaoZhuangMap.get(type);
                if (taozhuang == null) {
                    taozhuang = new NTaoZhuang();
                    nTaoZhuangMap.put(type, taozhuang);
                }
                List<Integer> tzlist = taozhuang.getSkinList();
                if (tzlist.contains(skin)) {
                    continue;
                }
                int count = tzlist.size();
                NTaoZhuangData tzData = NSkinModel.getTaoZhuangData(type, count);
                int level = 0;
                if (tzData != null) {
                    level = tzData.getLevel();
                }

                taozhuang.setLevel(level);
                taozhuang.setType(type);
                tzlist.add(skin);
            }

        }

    }

    public Map<Byte, NTaoZhuang> getnTaoZhuangMap() {
        return nTaoZhuangMap;
    }

    public void setnTaoZhuangMap(Map<Byte, NTaoZhuang> nTaoZhuangMap) {
        this.nTaoZhuangMap = nTaoZhuangMap;
    }


    public short getPulse() {
        return pulse;
    }

    public void setPulse(short meridian) {
        this.pulse = meridian;
    }

    public int getReFriendCoin() {
        return receviteFC;
    }

    public short getReFriendCoinCount() {
        return receviteFCCount;
    }


    public void addPulse(int add) {
        this.pulse += add;
    }

    public void addReFriendCoin(int add) {
        this.receviteFC += add;
    }

    public void setReFriendCoin(int add) {
        this.receviteFC = add;
    }

    public void addReFriendCoinCount(int add) {
        this.receviteFCCount += add;
    }

    public void setReFriendCoinCount(short add) {
        this.receviteFCCount = add;
    }


    public void addSendFriendCoinCount(int add) {
        this.sendFCCount += add;
    }

    public void setSendFriendCoinCount(short add) {
        this.sendFCCount = add;
    }

    public int getSendFCC() {
        return sendFCCount;
    }

    public int getSjgCopyId() {
        return sjgCopyId;
    }

    public void setSjgCopyId(Integer sjgCopyId) {
        this.sjgCopyId = sjgCopyId;
    }

    public short getTmMaxCopyId() {
        return tmMaxCopyId;
    }

    public void setTmMaxCopyId(short tmMaxCopyId) {
        this.tmMaxCopyId = tmMaxCopyId;
    }

    public short getMiSartTotal() {
        return miSartTotal;
    }

    public void setMiSartTotal(short miSartTotal) {
        this.miSartTotal = miSartTotal;
    }

    /**
     * 技能
     */
    private NSkillSystem skillSystem = new NSkillSystem();

    public String getSkillListJson() {
        return JSON.toJSONString(skillSystem.getSkillList());
    }

    public void setSkillListJson(String json) {
        if (json != null && !json.trim().isEmpty()) {
            List<Short> skilllist = JSON.parseObject(json, new TypeReference<List<Short>>() {
            });
            skillSystem.setSkillList(skilllist);
        }
    }

    public NSkillSystem getNSkillSystem() {
        return skillSystem;
    }


    public short[] getDailyProgress() {
        return dailyProgress;
    }


    public void setDailyProgress(short[] dailyProgress) {
        this.dailyProgress = dailyProgress;
    }

    public String toDailyProgressString() {
        if (this.dailyProgress == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.dailyProgress.length; ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(this.dailyProgress[i]);
        }
        return builder.toString();
    }

    public void setDailyProgressString(String str) {
        if (!StringUtil.isEmpty(str)) {
            String[] array = str.split(",");
            short[] s = new short[array.length];
            for (int i = 0; i < array.length; ++i) {
                s[i] = Short.parseShort(array[i]);
            }
            this.dailyProgress = s;
        }
    }


    public short[] getYesterdateProgress() {
        return yesterdateProgress;
    }

    public void setYesterdateProgress(short[] yesterdateProgress) {
        this.yesterdateProgress = yesterdateProgress;
    }

    public String toYesterdateProgressString() {
        if (this.yesterdateProgress == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.yesterdateProgress.length; ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(this.yesterdateProgress[i]);
        }
        return builder.toString();
    }

    public void setYesterdateProgressString(String str) {
        if (!StringUtil.isEmpty(str)) {
            String[] array = str.split(",");
            short[] s = new short[array.length];
            for (int i = 0; i < array.length; ++i) {
                s[i] = Short.parseShort(array[i]);
            }
            this.yesterdateProgress = s;
        }
    }


    public short getLiLianLevel() {

        return liLianLevel;
    }

    public void setLiLianLevel(short liLianLevel) {
        if (liLianLevel > 0) {
            this.liLianLevel = liLianLevel;
        }
    }

    public void addLiLianLevel(short liLianLevel) {
        this.liLianLevel += liLianLevel;
    }

    public int getLiLianExp() {
        return liLianExp;
    }

    public void setLiLianExp(int liLianExp) {
        this.liLianExp = liLianExp;
    }

    public void addLiLianExp(int liLianExp) {
        this.liLianExp += liLianExp;
    }

    public void setFaction(NFaction faction) {
        this.faction = faction;
    }

    public NFaction getFaction() {
        return faction;
    }

    /***
     * 野外关卡地图是否通关
     * */
    public boolean isTongGuan() {
        MapStageData data = MapModel.getMapStageData(getMapId(), getMapStageId());
        if (!data.isEnd()) {
            return false;
        }
        if (getMapWave() < FightDefine.MAP_WAVE_MAX_NUM) {
            return false;
        }
        return true;
    }

    /*************************************版本分割线      以上是新版的开发  以下是旧版本*****************************************************************************************/


    //渠道
    private short channel;

    //子渠道
    private short subChannel;

    //账号
    private String account;

    /**
     * 平台
     **/
    private byte platform;

    //经验
    private long exp = 0;

    //金币
    private long gold = 0;

    //钻石
    private int diamond = 0;

    //绑定元宝
    private int bindDiamond;

    //功勋
    private int honor;

    //竞技场点数
    private int arena;

    //积分
    private int points;

    //限时积分
    private int tlPoints = 0;

    //商城积分
    private int rsPoints = 0;

    //捐献
    private int donate;

    //元气
    private int yuanqi;

    //合击符文碎片
    private int combineRunePiece;

    /**
     * 激活码兑换列表
     */
    private Set<Integer> cdKeyList = new HashSet<>();
    //-=-=-=-=-=-=-=-=-=-=-=背包数据=-=-=-=-=-=-=-=-=-=-=//
    //灵器
//	private List<Artifact> artifactList=new ArrayList<>();
    //物品
    private List<Goods> itemList = new ArrayList<>();
    //宝物
    private List<Box> boxList = new ArrayList<>();
    //红装包
    private List<DropData> equipBag = new ArrayList<>();
    //元魂背包
    private Map<Integer, Spirit> spirits = new HashMap<>();
    //拍卖宝箱
    private Map<Long, AuctionBox> auctionBox = new HashMap<>();
    //关卡神器碎片
    private Map<Short, Goods> artifactPieces = new HashMap<>();
    //神羽装备
    private Map<Short, Goods> wingGods = new HashMap<>();
    //圣纹装备
    private Map<Byte, Goods> holyLines = new HashMap<>();
    //关卡神器
    private ArtifactBoss artifactBoss = new ArtifactBoss(ArtifactModel.getFirst());
    //卡牌包
    private Map<Short, Goods> cardbag = new HashMap<>();
    //卡牌图鉴
    private PlayerCardBook cardBook = new PlayerCardBook();
    //灵髓碎片
    private Map<Short, Goods> lingSuiPieces = new HashMap<>();
    //地图ID
    private short mapId = FightDefine.MAP_BIRTH_ID;
    //地图关卡
    private short mapStageId = 1;
    //波数
    private byte mapWave = 0;
    //通过奖励
    private short mapReward = 0;

    //地图类型
    private EMapType mapType = EMapType.FIELD_NORMAL;

    //法宝等级
    private short magicLevel = 0;
    //法宝等级星数
    private byte magicLevelStar = 0;
    //法宝阶数
    private short magicStage = 1;
    //法宝星数
    private byte magicStageStar = 0;
    //法宝经验
    private int magicStageExp = 0;

    /**
     * 幻化数据列表
     **/
    private List<Byte> huanhuaList = new ArrayList<>();
    /**
     * 幻化外形
     **/
    private Map<Byte, Byte> huanhuaAppearance = new HashMap<>();

    //熔炼等级和经验
    private short meltLv = 0;
    private int meltExp = 0;

    //神器
    private List<FunctionData> godArtifact = new ArrayList<>();

    //BOSS次数
    private short bossCount = 0;
    //BOSS次数上次恢复时间
    private long bossRecover = 0;
    //公会BOSS次数
    private byte gangBossCount = 0;


    //秘境BOSS剩余次数
    private short mysteryBossLeft = 0;
    //秘境BOSS提醒
    private List<Short> mysteryCue;

    //BOSS之家提醒
    private List<Short> vipBossCue;

    //终身卡购买标识
    private int forever = 0;

    //终生卡上次领取时间
    private long foreverReward = 0;

    //寻宝首次十连抽
    private byte redLottery = 0;

    //状态
    private byte state = 0;
    //上次登陆时间
    private long lastLoginTime;
    //上次登出时间
    private long lastLogoutTime;
    //创建时间
    private long createTime;

    //上次登录时间节日
    private long lastLoginTime2Fest;

    //公会技能
    private List<LevelData> gangSkill = new ArrayList<>();

    //称号
    private Map<Short, Long> title = new HashMap<>();

    /**
     * 诛仙台进度
     */
    private short dekaron;

    /**
     * 封魔塔进度
     */
    private short fengmota;

    /**
     * 主宰试炼进度
     **/
    private short zhuzai;


    /**
     * 战斗请求时间
     */
    private long fightRequestTime = 0;

    /**
     * 拍卖订阅列表
     */
    private Set<Short> auctionSubscriptions = new HashSet<>();
    /**
     * 龙珠碎片
     */
    private DragonBall dragonBall = new DragonBall();

    /**
     * 成就
     **/
    private int achievement = 0;
    /**
     * 勋章
     **/
    private byte medal = 0;
    /**
     * 成就任务-任务状态
     **/
    private Map<Short, RoleChainMission> achievementMission = new HashMap<>();
    /**
     * 心法背包
     */
    private Map<Byte, Short> heartSkillMap = new HashMap<Byte, Short>();
    /**
     * 合击技符文
     */
    private byte[] combineRune = new byte[8];
    /**
     * 合击符文背包
     */
    private Map<Byte, Integer> combineRuneBag = new HashMap<>();

    /**
     * 官阶威望值
     **/
    private int weiWang;

    /**
     * 战纹精华值
     **/
    private int zhanWenJinghua;
    /**
     * 限时任务
     **/
    private TLMissionData tlMissionData = new TLMissionData();
    /**
     * 卡牌任务
     **/
    private CardMission cardMission = new CardMission();

    /**
     * 五行
     **/
    private FiveElements fiveElements = new FiveElements();

    /**五行副本**/
//	private FiveElementsDungeon fiveElementsDungeon = new FiveElementsDungeon();

    /**
     * 五行激活状态
     **/
    private List<FiveElements> fiveState = new ArrayList<>();

    /**
     * 法阵
     **/
    private List<FaZhen> fazhenList = new ArrayList<>();

    /**
     * 镇魂宝库
     **/
    private TownSoulTreasure townSoulTreasure = new TownSoulTreasure();

    /**
     * 代金券
     **/
    private int vouchers;

    /**
     * 秘闻积分
     **/
    private int mysteryIntegral;

    /**
     * 奇珍积分
     **/
    private int qizhenIntegral;

    /**
     * 境界
     **/
    private Ambit ambit = new Ambit();

    //角色装备
    private Map<Short, Integer> roleEquipMap;

    //成长项
    private List<Grow> growList;

    //装备
    private List<Map<Short, Integer>> growEquipList;

    /**
     * 生死劫
     **/
    private int ladDisaster = TeamDef.getLaddId(1, 0);

    /**
     * 职业
     */
    private byte occupation;

    private long power;

    public int getBindDiamond() {
        return bindDiamond;
    }

    public void setBindDiamond(int bindDiamond) {
        this.bindDiamond = bindDiamond;
    }

    public byte getOccupation() {
        return occupation;
    }

    public void setOccupation(byte occupation) {
        this.occupation = occupation;
    }

    public int getLadDisaster() {
        return ladDisaster;
    }

    public void setLadDisaster(int ladDisaster) {
        this.ladDisaster = ladDisaster;
    }

    private short[] skill = new short[5];

    private byte[] skillPos = new byte[5];

    public short[] getSkill() {
        return skill;
    }

    public void setSkill(short[] skill) {
        this.skill = skill;
    }

    public byte[] getSkillPos() {
        return skillPos;
    }

    public void setSkillPos(byte[] skillPos) {
        this.skillPos = skillPos;
    }

    //-=-=-=-=-=-=-=-=-=-=-=不入库的数据=-=-=-=-=-=-=-=-=-=-=//

    private int[] attr;

    public int[] getAttr() {
        int[] attr = Arrays.copyOf(CombatModel.getHeroLevelData(this.level).getAttr(), EAttr.SIZE);
        for (EGrow eg : EGrow.values()) {
            Grow grow = this.getGrowList().get(eg.ordinal());
            for (GrowSeed seed : grow.getMap().values()) {
                //升级属性
                if (eg.getGrowDataMap() != null) {
                    GrowSeedData seedData = eg.getGrowDataMap().get(seed.getId());
                    if (seedData != null) {
                        GrowAttrData attrData = eg.getUpAttrMap().get(seedData.getQuality() + "_" + seedData.getUp()).get(seedData.getId());
                        if (attrData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += attrData.getAttr()[i];
                            }
                        }
                    }
                }
                //飞升属性
                if (eg.getFlyUpDataMap() != null) {
                    GrowSeedLevelUpData upData = eg.getFlyUpDataMap().get((short) seed.getFlyUp());
                    if (upData != null) {
                        for (int i = 0; i < EAttr.SIZE; ++i) {
                            attr[i] += upData.getAttr()[i];
                        }
                    }
                }
                //资质属性
                if (eg.getAptitudeDataMap() != null) {
                    GrowSeedData seedData = eg.getGrowDataMap().get(seed.getId());
                    if (seedData != null) {
                        GrowSeedLevelUpData upData = eg.getAptitudeDataMap().get(seedData.getQuality() + "_" + seedData.getAptitude()).get(seed.getAptitude());
                        if (upData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += upData.getAttr()[i];
                            }
                        }
                    }
                }

                for (int j = 0; j < grow.getSuit().length; ++j) {
                    GrowSuit suit = grow.getSuit()[j];
                    if (j == 0) {
                        GrowSeedLevelUpData upData = eg.getPsychicLevelUpDataMap().get(suit.getLevel());
                        if (upData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += upData.getAttr()[i];
                            }
                        }
                        for (int n = 0; n < suit.getSkill().length; ++n) {
                            GrowSkillData skillData = eg.getPsychicSkillDataMap().get(suit.getSkill()[n] + "_" + (n + 1));
                            if (skillData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += skillData.getAttr()[i];
                                }
                            }
                        }
                        GrowCostData cdata = eg.getPsychicPillDataMap().get(suit.getPill());
                        if (cdata != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += cdata.getAttr()[i];
                            }
                        }
                        for (short id : suit.getEquip()) {
                            GrowEquipData equipData = eg.getPsychicEquipDataMap().get(id);
                            if (equipData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += equipData.getAttr()[i];
                                }
                            }
                        }
                    } else if (j == 1) {
                        GrowSeedLevelUpData upData = eg.getSoulLevelUpDataMap().get(suit.getLevel());
                        if (upData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += upData.getAttr()[i];
                            }
                        }
                        for (int n = 0; n < suit.getSkill().length; ++n) {
                            GrowSkillData skillData = eg.getSoulSkillDataMap().get(suit.getSkill()[n] + "_" + (n + 1));
                            if (skillData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += skillData.getAttr()[i];
                                }
                            }
                        }
                        GrowCostData cdata = eg.getSoulPillDataMap().get(suit.getPill());
                        if (cdata != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += cdata.getAttr()[i];
                            }
                        }
                        for (short id : suit.getEquip()) {
                            GrowEquipData equipData = eg.getSoulEquipDataMap().get(id);
                            if (equipData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += equipData.getAttr()[i];
                                }
                            }
                        }
                    } else if (j == 2) {
                        GrowSeedLevelUpData upData = eg.getLevelUp3DataMap().get(suit.getLevel());
                        if (upData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += upData.getAttr()[i];
                            }
                        }
                        for (int n = 0; n < suit.getSkill().length; ++n) {
                            GrowSkillData skillData = eg.getSkillData3Map().get(suit.getSkill()[n] + "_" + (n + 1));
                            if (skillData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += skillData.getAttr()[i];
                                }
                            }
                        }
                        GrowCostData cdata = eg.getPillData3Map().get(suit.getPill());
                        if (cdata != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += cdata.getAttr()[i];
                            }
                        }
                        for (short id : suit.getEquip()) {
                            GrowEquipData equipData = eg.getEquipData3Map().get(id);
                            if (equipData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += equipData.getAttr()[i];
                                }
                            }
                        }
                    } else if (j == 3) {
                        GrowSeedLevelUpData upData = eg.getLevelUp4DataMap().get(suit.getLevel());
                        if (upData != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += upData.getAttr()[i];
                            }
                        }
                        for (int n = 0; n < suit.getSkill().length; ++n) {
                            GrowSkillData skillData = eg.getSkillData4Map().get(suit.getSkill()[n] + "_" + (n + 1));
                            if (skillData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += skillData.getAttr()[i];
                                }
                            }
                        }
                        GrowCostData cdata = eg.getPillData4Map().get(suit.getPill());
                        if (cdata != null) {
                            for (int i = 0; i < EAttr.SIZE; ++i) {
                                attr[i] += cdata.getAttr()[i];
                            }
                        }
                        for (short id : suit.getEquip()) {
                            GrowEquipData equipData = eg.getEquipData4Map().get(id);
                            if (equipData != null) {
                                for (int i = 0; i < EAttr.SIZE; ++i) {
                                    attr[i] += equipData.getAttr()[i];
                                }
                            }
                        }
                    }

                }
            }
        }

        NLiLianData data = NTaskModel.getNLiLianData(getLiLianLevel());
        if (data != null) {
            for (int i = 0; i < EAttr.SIZE; ++i) {
                attr[i] += data.getAttr()[i];
            }
        }
        return attr;
    }

    //登陆码
    private short loginCode;

    /**
     * 支线任务
     **/
    private RoleChainMission chainMission = null;

    /**
     * 小数据存储
     **/
    private SmallData smallData = new SmallData();
    /**
     * 每日数据
     **/
    private volatile DayData dayData = new DayData();

    /**
     * 公会
     */
    private Gang gang = null;
    private NFaction faction = null;

    /**
     * 分享数据
     */
    private ShareData shareData = new ShareData();

    /**
     * 每日更新标示
     */
    private String dailyUpdateMark = "";

    /**
     * 死亡
     */
    private Boolean dead = false;

    private int[] attribute = new int[EAttr.SIZE];

    private int[] getAttribute() {
        return attribute;
    }

    /*
     * 每日消耗元宝数
	 * key = 当天凌晨
	 * value = 当天消耗元宝数
	 *
	 */
    private String consumDaily;


    /**
     * 创建角色数据
     */
    public Player() {

    }

    /**
     * 初始化角色
     *
     * @param account
     * @param channel
     * @param subChannel
     * @param serverId
     * @param platform
     * @param name
     * @param occupation
     */
    public void initialPlayer(String account, short channel, short subChannel, short serverId, byte platform, String name, byte occupation) {
        this.channel = channel;
        this.subChannel = subChannel;
        this.account = account;
        this.serverId = serverId;
        this.platform = platform;
        this.name = name;
        this.head = occupation;
        this.occupation = occupation;
        this.cardBook.init(null);
        //2件装备 1,3
//		Equip equip1=new Equip();
//		equip1.setD((short)1);
//		equip1.setG((short)221);
//		equip1.setQ((byte)1);
//
//		this.equipList.add(equip1);
//		Equip equip2=new Equip();
//		equip2.setD((short)2);
//		equip2.setG((short)229);
//		equip2.setQ((byte)0);
//
//		this.equipList.add(equip2);
////		//127和129
//		Equip equip127=new Equip();
//		equip127.setD((short)3);
//		equip127.setG((short)241);
//		equip127.setQ((byte)0);
//		equip127.setF((byte)0);
//		this.equipList.add(equip127);
//		Equip equip129=new Equip();
//		equip129.setD((short)4);
//		equip129.setG((short)245);
//		equip129.setQ((byte)0);
//
//		this.equipList.add(equip129);
////		//253和255
//		Equip equip253=new Equip();
//		equip253.setD((short)5);
//		equip253.setG((short)225);
//		equip253.setQ((byte)0);
//
//		this.equipList.add(equip253);
//		Equip equip255=new Equip();
//		equip255.setD((short)6);
//		equip255.setG((short)226);
//		equip255.setQ((byte)0);
//
//		this.equipList.add(equip255);

        this.diamond = 888;
        this.setFazhenList("");
        setLiLianLevel((short) 1);

        /***********************************版本分割线以上是老版本  以下是新版本的开发******************************************************/
        for (int i = 0; i < EquipDefine.EQUIP_POS_NUM; i++) {
            this.nEquipSlotList.add(new NEquipSlot());
        }

        this.wearEquip = new short[EquipDefine.EQUIP_POS_NUM];

        this.growList = new ArrayList<>();
        for (EGrow type : EGrow.values()) {
            this.growList.add(new Grow(type));
        }

        this.danYaoList = new ArrayList<>();
        for (int i = 0; i < NFaBaoModel.getNDanYaoSizeData(); ++i) {
            this.danYaoList.add(new NDanYao());
        }

        this.growEquipList = new ArrayList<>();
        for (EEquip type : EEquip.values()) {
            this.growEquipList.add(new HashMap<>());
        }

        this.roleEquipMap = new HashMap();
        this.ntdaDataList = new ArrayList<>();
        for (NTaskAdvanceType type : NTaskAdvanceType.values()) {
            NTAData data = new NTAData();
            data.setType(type.getType());
            this.ntdaDataList.add(data);
        }

        factionSkill = new int[NFactionDefine.FACTION_SKILL_COUNT];

//this.itemList.add(new Goods((short)1,100));
//this.itemList.add(new Goods((short)3,100));
//this.itemList.add(new Goods((short)5,100));
//this.itemList.add(new Goods((short)6,100));
//this.itemList.add(new Goods((short)8,100));
//this.itemList.add(new Goods((short)9,100));
//this.itemList.add(new Goods((short)10,100));
//this.itemList.add(new Goods((short)11,100));
//this.itemList.add(new Goods((short)12,100));
//this.itemList.add(new Goods((short)14,100));
//this.itemList.add(new Goods((short)15,100));
//this.itemList.add(new Goods((short)17,100));
//this.itemList.add(new Goods((short)18,100));
//this.itemList.add(new Goods((short)20,100));
//this.itemList.add(new Goods((short)22,100));
//this.itemList.add(new Goods((short)24,100));
//this.itemList.add(new Goods((short)26,100));
//this.itemList.add(new Goods((short)28,100));
//this.itemList.add(new Goods((short)30,100));
//this.itemList.add(new Goods((short)31,100));
//this.itemList.add(new Goods((short)32,100));
//this.itemList.add(new Goods((short)33,100));
//this.itemList.add(new Goods((short)34,100));
//this.itemList.add(new Goods((short)35,100));
//this.itemList.add(new Goods((short)36,100));
//this.itemList.add(new Goods((short)37,100));
//this.itemList.add(new Goods((short)38,100));
//this.itemList.add(new Goods((short)39,100));
//this.itemList.add(new Goods((short)41,100));
//this.itemList.add(new Goods((short)42,100));
//this.itemList.add(new Goods((short)43,100));
//this.itemList.add(new Goods((short)44,100));
//this.itemList.add(new Goods((short)45,100));
//this.itemList.add(new Goods((short)46,100));
//this.itemList.add(new Goods((short)48,100));
//this.itemList.add(new Goods((short)50,100));
//this.itemList.add(new Goods((short)52,100));
//this.itemList.add(new Goods((short)55,100));
//this.itemList.add(new Goods((short)56,100));
//this.itemList.add(new Goods((short)57,100));
//this.itemList.add(new Goods((short)58,100));
//this.itemList.add(new Goods((short)59,100));
//this.itemList.add(new Goods((short)60,100));
//this.itemList.add(new Goods((short)61,100));
//this.itemList.add(new Goods((short)62,100));
//this.itemList.add(new Goods((short)63,100));
//this.itemList.add(new Goods((short)64,100));
//this.itemList.add(new Goods((short)66,100));
//this.itemList.add(new Goods((short)68,100));
//this.itemList.add(new Goods((short)70,100));
//this.itemList.add(new Goods((short)72,100));
//this.itemList.add(new Goods((short)74,100));
//this.itemList.add(new Goods((short)76,100));
//this.itemList.add(new Goods((short)78,100));
//this.itemList.add(new Goods((short)80,100));
//this.itemList.add(new Goods((short)82,100));
//this.itemList.add(new Goods((short)84,100));
//this.itemList.add(new Goods((short)86,100));
//this.itemList.add(new Goods((short)88,100));
//this.itemList.add(new Goods((short)1001,100));
//this.itemList.add(new Goods((short)1002,100));
//this.itemList.add(new Goods((short)1003,100));
//this.itemList.add(new Goods((short)1004,100));
//this.itemList.add(new Goods((short)1005,100));
//this.itemList.add(new Goods((short)1006,100));
//this.itemList.add(new Goods((short)1007,100));
//this.itemList.add(new Goods((short)1008,100));
//this.itemList.add(new Goods((short)1009,100));
//this.itemList.add(new Goods((short)1010,100));
//this.itemList.add(new Goods((short)1151,100));
//this.itemList.add(new Goods((short)1152,100));
//this.itemList.add(new Goods((short)1153,100));
//this.itemList.add(new Goods((short)1201,100));
//this.itemList.add(new Goods((short)1202,100));
//this.itemList.add(new Goods((short)1203,100));

    }

    /**
     * 计算战斗力
     */
    public boolean updateFighting() {
        int[] tempAttribute = new int[EAttr.SIZE];

        for (Grow grow : growList) {
            for (GrowSeed seed : grow.getMap().values()) {
                GrowSeedData data = EGrow.PET.getGrowDataMap().get(seed.getId());
                GrowSeedLevelUpData upData = EGrow.PET.getLevelUpDataMap().get(data.getQuality() + "_" + data.getLevelUp()).get(seed.getLevel());
                upData.getAttr();
                for (int i = 0; i < EAttr.SIZE; ++i) {
                    tempAttribute[i] += upData.getAttr()[i];
                }
            }
        }

        long totalFighting = GameCommon.calculationFighting(tempAttribute);
        if (totalFighting != this.fighting) {
            this.fighting = totalFighting;
            return true;
        }
        return false;
    }

//--------------------------------------------------------------------------
//--------------- 消息区 ------------------------------------------------------
//--------------------------------------------------------------------------

    public void getHeroData(Message message) {
        message.setByte(occupation);
        message.setShort(level);
        //法宝
        message.setShort(growList.get(EGrow.GODDESS.I()).getSuit()[0].getLevel());
        //法宝皮肤
        NSkin skin = nPiFuMap.get(NSkinType.TIANNV);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
        //神兵
        message.setShort(growList.get(EGrow.GODDESS.I()).getSuit()[1].getLevel());
        //神兵皮肤
        skin = nPiFuMap.get(NSkinType.TIANXIAN);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
        //翅膀
        message.setShort(growList.get(EGrow.ROLE.I()).getSuit()[0].getLevel());
        //翅膀皮肤
        skin = nPiFuMap.get(NSkinType.CHIBANG);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
        //坐骑
        message.setShort(growList.get(EGrow.ROLE.I()).getSuit()[1].getLevel());
        //坐骑皮肤
        skin = nPiFuMap.get(NSkinType.ZUOQI);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
        //时装
        skin = nPiFuMap.get(NSkinType.SHIZHUANG);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
        //称号
        skin = nPiFuMap.get(NSkinType.CHENGHAO);
        if (skin != null) {
            message.setShort(skin.getCurrHZId());
        } else {
            message.setShort(0);
        }
    }

    public Message getPlayerMessage() {
        Message message = new Message(MessageCommand.PLAYER_MESSAGE);
        message.setInt(id);
        message.setShort(loginCode);
        message.setString(name);
        message.setByte(head);
        message.setLong(exp);
        message.setLong(gold);
        message.setInt(diamond);
        message.setInt(bindDiamond);
        message.setShort(mapId);
        message.setShort(mapStageId);
        message.setBool(isTongGuan());
        message.setInt(this.vip);
        message.setLong(this.createTime);
        message.setLong(System.currentTimeMillis());
        message.setLong(GameDefine.SERVER_CREATE_TIME);
        message.setLong(this.power);

        //宠物
        message.setShort(growList.get(EGrow.PET.I()).getGo()[0]);
        //通灵
        message.setShort(growList.get(EGrow.PET.I()).getSuit()[0].getLevel());
        //兽魂
        message.setShort(growList.get(EGrow.PET.I()).getSuit()[1].getLevel());
        //仙侣
        message.setShort(growList.get(EGrow.MATE.I()).getGo()[0]);
        //仙位
        message.setShort(growList.get(EGrow.MATE.I()).getSuit()[0].getLevel());
        //法阵
        message.setShort(growList.get(EGrow.MATE.I()).getSuit()[1].getLevel());
        getHeroData(message);


/*		Message message=new Message(MessageCommand.PLAYER_MESSAGE);
        message.setByte(0);
		message.setInt(id);
		message.setShort(loginCode);
		message.setString(name);
		message.setByte(head);
//		message.setShort(rein);
		message.setInt(exp);
		message.setShort(level);
		message.setLong(gold);
		message.setInt(diamond);
		message.setInt(bindDiamond);
		message.setShort(mapId);
		message.setShort(mapStageId);
//		message.setByte(mapWave);

//		this.getMeltMessage(message);
//		this.getGodArtifactMsg(message);

		message.setInt(this.honor);
		message.setInt(this.arena);
		message.setInt(this.vip);
		message.setLong(this.createTime);
		message.setLong(System.currentTimeMillis());
		message.setInt(this.points);
//		message.setShort(this.spirits.size());
//		for(Spirit sp:spirits.values()){
//			sp.getMessage(message);
//		}
		/** 将元神改为战纹  消息格式和元神保持一致 **/
//		message.setShort(this.zhanWens.size());
//		for(ZhanWen zw:zhanWens.values()){
//			zw.getMessage(message);
//		}
//		message.setShort(this.mapReward);
//		message.setByte(this.smallData.getNoviceGuide().size());
//		for(Byte guide:this.smallData.getNoviceGuide()){
//			message.setByte(guide);
//		}
//		message.setInt(donate);
//		message.setInt(yuanqi);

//		message.setByte(huanhuaList.size());
//		for (Byte id: huanhuaList){
//			message.setByte(id);
//		}
//		message.setByte(huanhuaAppearance.size());
//		for (Map.Entry<Byte, Byte> entry: huanhuaAppearance.entrySet()){
//			message.setByte(entry.getKey());
//			message.setByte(entry.getValue());
//		}
//		message.setInt(tlPoints);
//		message.setInt(rsPoints);

//		//法宝等级
//		message.setShort(this.magicLevel);
//		//法宝等级星数
//		message.setByte(this.magicLevelStar);
//		//法宝阶数
//		message.setShort(this.magicStage);
//		//法宝星数
//		message.setByte(this.magicStageStar);
//
//		//诛仙台进度
//		message.setShort(this.dekaron);
//		//寻宝十连抽首次
//		message.setByte(this.redLottery);

        //宠物信息
//		message.setByte(0);
//		for(Entry<Byte,Grow> entry:pet1.entrySet()){
//			//Pet pet = entry.getValue();
//			message.setByte(1);
//			message.setByte(1);
//			message.setShort(1);
//			message.setByte(1);
//		}
//		message.setByte(0);
//		//龙珠信息
//		this.getDragonBallMessage(message);
//		//勋章
//		message.setByte(medal);
//		//成就
//		message.setInt(achievement);
//		//神器
//		artifactBoss.getMessage(message);
//		//合击符文
//		for(int i=0;i<combineRune.length;++i){
//			message.setByte(combineRune[i]);
//		}
        //官阶
//		message.setInt(weiWang);
        //代金券
//		message.setInt(vouchers);
        //战纹精华
//		message.setInt(zhanWenJinghua);
        //秘闻积分
//		message.setInt(mysteryIntegral);
        //奇珍积分
//		message.setInt(qizhenIntegral);
        //三生新消息
//		message.setByte(this.characterList.size());
//		for(Character character:this.characterList){
//			character.getMessage(message);
//		}

        //服务器开服时间
//		message.setLong(GameDefine.SERVER_CREATE_TIME);*/
        return message;
    }

    public Message getEasyPlayerMessage() {
        Message message = new Message(MessageCommand.PLAYER_MESSAGE);
        message.setByte(1);
        message.setInt(id);
        message.setShort(loginCode);
        message.setString(name);
        message.setByte(head);
        message.setInt(diamond);
        message.setLong(this.createTime);
        message.setLong(System.currentTimeMillis());
        //三生新消息
//		message.setByte(this.characterList.size());
//		for(Character character:this.characterList){
//			character.getMessage(message);
//		}

        //服务器开服时间
        message.setLong(GameDefine.SERVER_CREATE_TIME);
        return message;
    }

    private void getDragonBallMessage(Message message) {
        message.setShort(dragonBall.getLevel());
        message.setInt(dragonBall.getPieces());
        message.setInt(dragonBall.getMothCardAddition());
    }

    /**
     * 好友、战斗数据
     *
     * @param message
     */
    public void getBattleMsg(Message message) {
        //简易信息
        SimplePlayer sp = new SimplePlayer();
        sp.init(this);
        sp.getSimpleMessage(message);
        //外形数据
        AppearPlayer ap = new AppearPlayer();
        ap.init(this, -1);
        ap.getMessage(message);
        //属性数据
//		message.setByte(this.attr.length);
//		for (byte i = 0; i < this.attr.length; i++)
//		{
//			message.setInt(this.attr[i]);
//		}
    }

    public void getGodArtifactMsg(Message msg) {
        msg.setByte(this.godArtifact.size());
        for (FunctionData data : this.godArtifact) {
            msg.setByte(data.getId());
            msg.setShort(data.getLevel());
        }
    }

    public Message getBagCapacityMsg() {
        Message msg = new Message(MessageCommand.UPDATE_BAG_CAPACITY);
        msg.setByte(1);
        msg.setShort(getEquipBagMax());
//		msg.setByte(2);
//		msg.setShort(getSpiritBagMax());
        return msg;
    }

    public Message getGoodsListMessage() {
        Message message = new Message(MessageCommand.GOODS_LIST_MESSAGE);
        //装备包容量上限
        message.setShort(this.getEquipBagMax());
        int size = roleEquipMap.size();
        message.setShort(size);
        for (Entry<Short, Integer> entry : roleEquipMap.entrySet()) {
            message.setShort(entry.getKey());
            message.setInt(entry.getValue());
        }
        size = this.itemList.size();
        message.setShort(size);
        for (int i = 0; i < size; ++i) {
            this.itemList.get(i).getMessage(message);
        }
        size = this.boxList.size();
        message.setShort(size);
        for (int i = 0; i < size; ++i) {
            this.boxList.get(i).getMessage(message);
        }
        message.setByte(growEquipList.size());
        for (Map<Short, Integer> map : this.growEquipList) {
            message.setShort(map.size());
            for (Entry<Short, Integer> entry : map.entrySet()) {
                message.setShort(entry.getKey());
                message.setInt(entry.getValue());
            }
        }
        return message;
    }

    public void getMeltMessage(Message message) {
        message.setShort(this.meltLv);
        message.setInt(this.meltExp);
    }

    /**
     * 得到外形消息
     *
     * @param message
     */
    public void getAppearMessage(Message message) {
        //合击符文
//		for(int i=0;i<combineRune.length;++i){
//			message.setByte(combineRune[i]);
//		}
        AppearPlayer appear = new AppearPlayer();
        appear.init(this, -1);
        appear.getMessage(message);
//		message.setByte(this.magicStage); //法宝外形
//		message.setByte(this.characterList.size()); //角色数量
//		for(Character character:this.characterList){
//			character.getAppearMessage(message);
//		}
    }

    /**
     * 得到战斗外形消息
     *
     * @param message
     */
    public void getFightAppearMessage(Message message) {
        message.setInt(this.id);
        message.setString(this.name);
        message.setByte(this.head);
//		for(Character character:this.characterList){
//			character.getAppearMsg(message);
//		}
        message.setLong(this.fighting);
//		Character character = this.characterList.get(0);
//		character.getAppearMsg(message);
    }

    /**
     * 获取龙珠战斗力
     *
     * @return
     */
    public int getDragonBallFighting() {
        int tempAttribute[] = new int[EAttrType.ATTR_SIZE];
        if (this.dragonBall.getLevel() > 0) {
            DragonBallModelData dragonball = DragonBallModel.getData(dragonBall.getLevel());
            for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                tempAttribute[i] += dragonball.getAttr()[i];
            }
        }
        return this.getAttrFighting(tempAttribute);
    }

    /**
     * 得到属性战力消息
     *
     * @param message
     */
    public void getAttrFighting(Message message) {
//		message.setByte(this.characterList.size()); //角色数量
//		for(Character character:this.characterList){
//			character.getAttrFighting(message);
//			message.setByte(character.getHeartSkillSlotList().size());
//			//心法装备
//			for(HeartSkillSlot slot:character.getHeartSkillSlotList()){
//				slot.getMessage(message);
//			}
//		}
    }

    /**
     * 获取属性的的战斗力
     *
     * @param attr 属性
     * @return
     */
    public int getAttrFighting(int[] attr) {
        int fighting = 0;
        fighting += attr[EAttrType.HP.getId()] * EAttrType.HP.getFactor();
        fighting += attr[EAttrType.ATTACK.getId()] * EAttrType.ATTACK.getFactor();
        fighting += attr[EAttrType.PHYDEF.getId()] * EAttrType.PHYDEF.getFactor();
        fighting += attr[EAttrType.MAGICDEF.getId()] * EAttrType.MAGICDEF.getFactor();
        fighting += attr[EAttrType.PHPUNCTURE.getId()] * EAttrType.PHPUNCTURE.getFactor();
        fighting += attr[EAttrType.MAGPUNCTURE.getId()] * EAttrType.MAGPUNCTURE.getFactor();
        fighting += attr[EAttrType.HIT.getId()] * EAttrType.HIT.getFactor();
        fighting += attr[EAttrType.DODGE.getId()] * EAttrType.DODGE.getFactor();
        fighting += attr[EAttrType.CRIT.getId()] * EAttrType.CRIT.getFactor();
        fighting += attr[EAttrType.DUCT.getId()] * EAttrType.DUCT.getFactor();
        fighting += attr[EAttrType.CRITDAM.getId()] * EAttrType.CRITDAM.getFactor();
        fighting += attr[EAttrType.CRITRES.getId()] * EAttrType.CRITRES.getFactor();
        fighting += attr[EAttrType.AMP.getId()] * EAttrType.AMP.getFactor();
        fighting += attr[EAttrType.DR.getId()] * EAttrType.DR.getFactor();
        fighting += attr[EAttrType.RESTORE.getId()] * EAttrType.RESTORE.getFactor();
        fighting += attr[EAttrType.ATTACKRADIO.getId()] * EAttrType.ATTACKRADIO.getFactor();
        fighting += attr[EAttrType.HPRADIO.getId()] * EAttrType.HPRADIO.getFactor();
        return fighting;
    }

    public void getSkillMessage(Message message) {
        //关卡神器
        message.setByte(artifactBoss.getId());
        //合击符文
        for (int i = 0; i < combineRune.length; ++i) {
            message.setByte(combineRune[i]);
        }
//		message.setByte(this.characterList.size()); //角色数量
//		for(Character character:this.characterList){
//			character.getSkillMessage(message);
//		}
    }

    /**
     * 每日推送刷新消息
     *
     * @return
     */
    public Message getDayRefreshMsg() {
        if (this.dayData == null)
            this.dayData = new DayData();
        Message msg = new Message(MessageCommand.DAY_REFRESH_MESSAGE);
        msg.setByte(GameDefine.REIN_EX - this.dayData.getReinEx());
        msg.setShort(this.dayData.getBoxUsed().size());
        for (Entry<Short, Integer> entry : this.dayData.getBoxUsed().entrySet()) {
            msg.setShort(entry.getKey());
            msg.setInt(entry.getValue());
        }
        return msg;
    }

//--------------------------------------------------------------------------
//--------------- 方法区 ------------------------------------------------------
//--------------------------------------------------------------------------

//	public Character createCharacter(byte idx,byte occupation){
//		Character character=new Character(this.id,idx,occupation);
//		for(int i=0;i<SkillDefine.SKILL_NUM;++i){
//			if(SkillDefine.SKILL_OPEN_LEVEL[i]<=this.level){
//
//			}
//		}
//		return character;
//	}

    /**
     * 得到目前最高技能等级
     *
     * @return
     */
    public int getSkillMaxLevel() {

        return 100;
    }

    /**
     * 得到目前主角色装备件数
     *
     * @return
     */
    @JSONField(serialize = false)
    public int getMasterEquipedNumber() {
        int number = 0;
//		for(Equip equip:this.characterList.get(0).getEquipList()){
//			if(equip!=null){
//				++number;
//			}
//		}
        return number;
    }

    public int getJewelTotal() {
        int total = 0;
//		for (Character cha : this.characterList) {
//			total += cha.getJewelTotal();
//		}
        return total;
    }

    public int getYudiTotal() {
        int total = 0;
//		for (Character cha : this.characterList) {
//			total += cha.getYudi();
//		}
        return total;
    }

    public boolean isEquipBagFull() {
        if (this.wingGods.size() + this.roleEquipMap.size() >= this.getEquipBagMax())
            return true;
        return false;
    }

    public int getEquipBagFreeGrid() {
        if (this.wingGods.size() + this.roleEquipMap.size() < this.getEquipBagMax()) {
            return this.getEquipBagMax() - this.wingGods.size() - this.roleEquipMap.size();
        } else {
            return 0;
        }
    }


    //获取装备包容量上限
    public short getEquipBagMax() {
        short max = EquipDefine.EQUIP_BAG_MAX;
        //VIP
        int add = VipModel.getVipWeal(getVipLevel(), EVipType.BAG_CAPACITY);
        if (add > 0)
            max += add;
        //月卡
        GameRole role = getGameRole();
        if (role != null && role.getActivityManager() != null) {
            if (role.getActivityManager().isMonthlyCard()) {
                Map<EVipType, Integer> tq = MonthlyCardModel.getModel(1).getTequan();
                if (tq.containsKey(EVipType.BAG_CAPACITY))
                    max += tq.get(EVipType.BAG_CAPACITY);
            }
        }
        //终生卡
//		if (forever > 0)
//		{
//			Map<EVipType, Integer> tq = MonthlyCardModel.getModel(2).getTequan();
//			if (tq.containsKey(EVipType.BAG_CAPACITY))
//				max += tq.get(EVipType.BAG_CAPACITY);
//		}
        return max;
    }

    //获取元魂包容量上限
//	public short getSpiritBagMax()
//	{
//		return EquipDefine.SPIRIT_BAG_MAX;
//	}
    //获取主宰包容量上限
    public short getDomBagMax() {
        return EquipDefine.DOM_BAG_MAX;
    }

//--------------------------------------------------------------------------
//--------------- 属性区 ------------------------------------------------------
//--------------------------------------------------------------------------

    public void addVip(int add) {
        this.vip += add;
    }

    public byte getVipLevel() {
        return VipModel.getVipLv(vip);
    }

    public short getMagicLevel() {
        return magicLevel;
    }

    public void setMagicLevel(short magicLevel) {
        this.magicLevel = magicLevel;
    }

    public byte getMagicLevelStar() {
        return magicLevelStar;
    }

    public void setMagicLevelStar(byte magicLevelStar) {
        this.magicLevelStar = magicLevelStar;
    }

    public boolean addMagicLevelStar(int addStar) {
        this.magicLevelStar += addStar;
        if (this.magicLevelStar >= SectionDefine.STAR_FULL) {
            this.magicLevelStar -= SectionDefine.STAR_FULL;
            ++this.magicLevel;
            return true;
        }
        return false;
    }

    public short getMagicStage() {
        return magicStage;
    }

    public void setMagicStage(short magicStage) {
        this.magicStage = magicStage;
    }

    public void addMagicStage() {
        this.magicStageStar = 0;
        ++this.magicStage;
    }

    public byte getMagicStageStar() {
        return magicStageStar;
    }

    public void setMagicStageStar(byte magicStageStar) {
        this.magicStageStar = magicStageStar;
    }

    public int getMagicStageExp() {
        return magicStageExp;
    }

    public void setMagicStageExp(int magicStageExp) {
        this.magicStageExp = magicStageExp;
    }

    public boolean addMagicStageExp(int addExp, int maxExp) {
        this.magicStageExp += addExp;
        if (this.magicStageExp >= maxExp) {
            this.magicStageExp -= maxExp;
            ++this.magicStageStar;
            return true;
        }
        return false;
    }

    public List<DropData> getEquipBag() {
        return equipBag;
    }

    public void setEquipBag(List<DropData> equipBag) {
        this.equipBag = equipBag;
    }

    public void setEquipBagJson(String json) {
        if (json != null && json.length() > 0)
            this.equipBag = JSON.parseArray(json, DropData.class);
    }

    public String getEquipBagJson() {
        return JSON.toJSONString(equipBag);
    }

    public long getFightRequestTime() {
        return fightRequestTime;
    }

    public void setFightRequestTime(long fightRequestTime) {
        this.fightRequestTime = fightRequestTime;
    }

    public byte getMapWave() {
        return mapWave;
    }

    public void setMapWave(byte mapWave) {
        this.mapWave = mapWave;
    }

    public void addMapWave() {
        if (this.mapWave >= Byte.MAX_VALUE) {
            return;
        }
        ++this.mapWave;
    }

    public void addMapId() {
        ++this.mapId;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
    }

    public short getMapStageId() {
        return mapStageId;
    }

    public void setMapStageId(short mapNodeId) {
        this.mapStageId = mapNodeId;
    }

    public void addMapStageId() {
        ++this.mapStageId;
    }

    public short getMapReward() {
        return mapReward;
    }

    public void setMapReward(short mapReward) {
        this.mapReward = mapReward;
    }

    public short getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(short loginCode) {
        this.loginCode = loginCode;
    }

    public void setGodArtifactList(String json) {
        if (StringUtil.isEmpty(json))
            return;
        this.godArtifact = JSON.parseArray(json, FunctionData.class);
    }

    public List<FunctionData> getGodArtifactList() {
        return this.godArtifact;
    }

    public String getGodArtifactListJson() {
        return JSON.toJSONString(this.godArtifact);
    }

    public List<Goods> getItemList() {
        return itemList;
    }

    public String getItemListJson() {
        return JSON.toJSONString(itemList);
    }

    public void setItemList(String json) {
        if (StringUtil.isEmpty(json)) {
            this.itemList = new ArrayList<>();
        } else {
            this.itemList = JSON.parseArray(json, Goods.class);
        }
    }

    public List<Box> getBoxList() {
        return boxList;
    }

    public String getBoxListJson() {
        return JSON.toJSONString(boxList);
    }

    public void setBoxList(String json) {
        if (StringUtil.isEmpty(json)) {
            this.boxList = new ArrayList<>();
        } else {
            this.boxList = JSON.parseArray(json, Box.class);
        }
    }

    public List<FaZhen> getFazhenList() {
        return fazhenList;
    }

    public String getFazhenListJson() {
        return JSON.toJSONString(fazhenList);
    }

    public void setFazhenList(String json) {
        if (StringUtil.isEmpty(json)) {
            List<FaZhen> list = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                FaZhen fazhen = new FaZhen();
                fazhen.setT((byte) (i + 1));
                fazhen.setLev((short) 0);
                list.add(fazhen);
            }
            this.fazhenList = list;
        } else {
            this.fazhenList = JSON.parseArray(json, FaZhen.class);
        }
    }

    public Map<Long, AuctionBox> getAuctionBox() {
        return auctionBox;
    }

    public String getAuctionBoxJson() {
        return StringUtil.obj2Gson(auctionBox.values());
    }

    public void setAuctionBox(Map<Long, AuctionBox> auctionBox) {
        this.auctionBox = auctionBox;
    }

    public void setAuctionBoxStr(String json) {
        List<AuctionBox> list = StringUtil.gson2List(json, new TypeToken<List<AuctionBox>>() {
        });
        Map<Long, AuctionBox> map = new HashMap<>();
        for (AuctionBox box : list) {
            map.put(box.getUid(), box);
        }
        auctionBox = map;
    }

    public Map<Integer, Spirit> getSpirits() {
        return spirits;
    }

    public String getSpiritsJson() {
        return JSON.toJSONString(this.spirits.values());
    }

    public void setSpirits(String json) {
        if (StringUtil.isEmpty(json))
            return;
        List<Spirit> spList = JSON.parseArray(json, Spirit.class);
        if (spList != null) {
            for (Spirit sp : spList) {
                this.spirits.put(sp.getD(), sp);
            }
        }
    }

//	public Map<Integer, Byte> getDoms() {
//		return this.doms;
//	}
//
//	public String getDomsJson() {
//		return JSON.toJSONString(this.doms);
//	}
//
//	public void setDomsJson(String json) {
//		if (!StringUtil.isEmpty(json)) {
//			this.doms = JSON.parseObject(json, new TypeReference<Map<Integer, Byte>>(){});
//		}
//	}

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public short getChannel() {
        return channel;
    }

    public void setChannel(short channel) {
        this.channel = channel;
    }

    public short getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(short subChannel) {
        this.subChannel = subChannel;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public EMapType getMapType() {
        return mapType;
    }

    public void setMapType(EMapType mapType) {
        this.mapType = mapType;
    }

    public void changeGold(int value) {
        this.gold += value;
    }

    public void changeDiamond(int value) {
        this.diamond += value;
    }

    public void changeBindDiamond(int value) {
        this.bindDiamond += value;
    }

    public void changeDonate(int value) {
        this.donate += value;
    }

    public void changeExp(int value) {
        this.exp += value;
    }

    public short getMeltLv() {
        return meltLv;
    }

    public void setMeltLv(short meltLv) {
        this.meltLv = meltLv;
    }

    public int getMeltExp() {
        return meltExp;
    }

    public void setMeltExp(int meltExp) {
        this.meltExp = meltExp;
    }

    public void addMeltLv(int lv) {
        this.meltLv += lv;
    }

    public void addMeltExp(int exp) {
        this.meltExp += exp;
    }

    public int getArena() {
        return arena;
    }

    public void setArena(int arena) {
        this.arena = arena;
    }

    public void addArena(int add) {
        this.arena += add;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int add) {
        this.points += add;
    }

    public int getDonate() {
        return donate;
    }

    public void setDonate(int donate) {
        this.donate = donate;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public RoleChainMission getChainMission() {
        return chainMission;
    }

    public void setChainMission(RoleChainMission chainMission) {
        this.chainMission = chainMission;
    }

    public String getChainMissionJson() {
        return StringUtil.obj2Gson(this.chainMission);
    }

    public void setChainMissionJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.chainMission = StringUtil.gson2Obj(json, RoleChainMission.class);
        }
    }


    public SmallData getSmallData() {
        return smallData;
    }

    public void setSmallData(SmallData smallData) {
        this.smallData = smallData;
    }

    public String toSmallDataJson() {
        return JSON.toJSONString(smallData);
    }

    public int getHonor() {
        return honor;
    }

    public void setHonor(int honor) {
        this.honor = honor;
    }

    public void addHonor(int change) {
        this.honor += change;
    }

    public int getYuanqi() {
        return yuanqi;
    }

    public void setYuanqi(int yuanqi) {
        this.yuanqi = yuanqi;
    }

    public void addYuanqi(int add) {
        this.yuanqi += add;
    }

    public void fromSmallDataJson(String json) {
        if (StringUtil.isEmpty(json)) {
            this.smallData = new SmallData();
        } else {
            this.smallData = JSON.parseObject(json, SmallData.class);
        }
    }

    public void resetDayData() {
        this.dayData = new DayData();
    }

    public FunctionData getGodArtifactById(int id) {
        for (FunctionData data : this.godArtifact) {
            if (data.getId() == id)
                return data;
        }
        return null;
    }

    public boolean isGodArtifactActive(int id) {
        FunctionData data = this.getGodArtifactById(id);
        if (data != null && data.getLevel() > 0) {
            return true;
        }
        return false;
    }

    public boolean isOnline() {
        return false;
    }

    public Player getPlayer() {
        return this;
    }

    public short getBossCount() {
        return bossCount;
    }

    public void changeBossCount(int num) {
        this.bossCount += num;
    }

    public void setBossCount(int bossCount) {
        this.bossCount = (short) bossCount;
    }

    public byte getGangBossCount() {
        return gangBossCount;
    }

    public void setGangBossCount(byte gangBossCount) {
        this.gangBossCount = gangBossCount;
    }

    public void addGangBossCount() {
        this.gangBossCount++;
    }


    public long getBossRecover() {
        return bossRecover;
    }

    public void setBossRecover(long bossRecover) {
        this.bossRecover = bossRecover;
    }

    public int getForever() {
        return forever;
    }

    public void setForever(int forever) {
        this.forever = forever;
    }

    public void addForever() {
        this.forever++;
    }

    public byte getRedLottery() {
        return redLottery;
    }

    public void setRedLottery(byte redLottery) {
        this.redLottery = redLottery;
    }

    public long getForeverReward() {
        return foreverReward;
    }

    public void setForeverReward(long foreverReward) {
        this.foreverReward = foreverReward;
    }

    public DayData getDayData() {
        return dayData;
    }

    public void setDayData(DayData dayData) {
        this.dayData = dayData;
    }

    public void fromDayDataJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.dayData = StringUtil.gson2Obj(json, DayData.class);
        }
    }

    public String toDayDataJson() {
        return StringUtil.obj2Gson(dayData);
    }

    public void setGang(Gang gang) {
        this.gang = gang;
    }

    public Gang getGang() {
        return gang;
    }

    public String getGangName() {
        return gang == null ? "" : gang.getName();
    }

    public List<LevelData> getGangSkill() {
        return gangSkill;
    }

    public void setGangSkill(List<LevelData> gangSkill) {
        this.gangSkill = gangSkill;
    }

    public void fromGangSkillJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.gangSkill = JSON.parseArray(json, LevelData.class);
        }
    }

    public String toGangSkillJson() {
        return JSON.toJSONString(gangSkill);
    }

    public List<FiveElements> getFiveState() {
        return fiveState;
    }

    public void setFiveState(List<FiveElements> fiveState) {
        this.fiveState = fiveState;
    }

    public void fromFiveStateJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.fiveState = JSON.parseArray(json, FiveElements.class);
        }
    }

    public void setTitleJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.title = JSON.parseObject(json, new TypeReference<Map<Short, Long>>() {
            });
        }
    }

    public String getTitleJson() {
        return JSON.toJSONString(this.title);
    }

    public Map<Short, Long> getTitle() {
        return title;
    }

    @Override
    public GameRole getGameRole() {
        GameRole role = GameWorld.getPtr().getOnlineRole(this.id);
        return role;
    }

    public Message getLimitGoodsListMessage() {
        long currentTime = System.currentTimeMillis();
        Message message = new Message(MessageCommand.GOODS_LIMIT_LIST_MESSAGE);
        message.setShort(this.smallData.getTimeGoodsList().size());
        for (TimeGoods timeGoods : this.smallData.getTimeGoodsList()) {
            timeGoods.getMessage(message, currentTime);
        }
        return message;
    }

    public Message getGangSkillList() {
        Message message = new Message(MessageCommand.GANG_SKILL_LIST_MESSAGE);
        message.setByte(gangSkill.size());
        for (LevelData data : gangSkill) {
            message.setByte(data.getId());
            message.setShort(data.getLevel());
        }
        return message;
    }

    public short getGangSkillLevel(byte id) {
        for (LevelData data : gangSkill) {
            if (data.getId() == id) {
                return data.getLevel();
            }
        }
        return 0;
    }

    public void addGangSkillLevel(byte id, short level) {
        boolean isNew = true;
        for (LevelData data : gangSkill) {
            if (data.getId() == id) {
                data.setLevel(level);
                isNew = false;
            }
        }
        if (isNew) {
            gangSkill.add(new LevelData(id, level));
        }
    }

    public void addTimeGoods(TimeGoods timeGoods) {
        for (TimeGoods goods : this.smallData.getTimeGoodsList()) {
            if (goods.getType() == timeGoods.getType() && goods.getId() == timeGoods.getId()) {
                goods.setTime(timeGoods.getTime());
                return;
            }
        }
        this.smallData.getTimeGoodsList().add(timeGoods);
    }

    public List<Byte> getHuanhuaList() {
        return huanhuaList;
    }

    public String getHuanhuaListJsonData() {
        return JSON.toJSONString(huanhuaList);
    }

    public void setHuanhuaListJsonData(String jsonData) {
        if (!StringUtil.isEmpty(jsonData)) {
            this.huanhuaList = JSON.parseArray(jsonData, Byte.class);
        }
    }

    public Map<Byte, Byte> getHuanhuaAppearance() {
        return huanhuaAppearance;
    }

    public String getHuanhuaAppearanceJsonData() {
        return JSON.toJSONString(huanhuaAppearance);
    }

    public void setHuanhuaAppearanceJsonData(String jsonData) {
        if (!StringUtil.isEmpty(jsonData)) {
            this.huanhuaAppearance = JSON.parseObject(jsonData, new TypeReference<Map<Byte, Byte>>() {
            });
        }
    }


    public String getCitCueJson() {
        return JSON.toJSONString(this.citCue);
    }

    public void setCitCueJson(String json) {
        if (StringUtil.isEmpty(json)) {
            return;
        }
        this.citCue = JSON.parseObject(json, new TypeReference<List<Short>>() {
        });
    }

    public int getTlPoints() {
        return tlPoints;
    }

    public void setTlPoints(int tlPoints) {
        this.tlPoints = tlPoints;
    }

    public void addTLPoints(int add) {
        this.tlPoints += add;
    }

    public int getRsPoints() {
        return rsPoints;
    }

    public void setRsPoints(int rsPoints) {
        this.rsPoints = rsPoints;
    }

    public void addRsPoints(int add) {
        this.rsPoints += add;
    }

    public void setCdKeyListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.cdKeyList = JSONArray.parseObject(json, new TypeReference<HashSet<Integer>>() {
            });
        }
    }

    public String getCDKeyListJson() {
        return JSONArray.toJSONString(this.cdKeyList);
    }

    public void addCDKey(int modelId) {
        cdKeyList.add(modelId);
    }

    public Character getCharacter(int idx) {
//		if (idx >= characterList.size())
//			return null;
//		return characterList.get(idx);
        return null;
    }

//	public Character getCharacterByOcc(int occ) {
//		for (Character cha : characterList) {
//			if (cha.getOccupation() == occ)
//				return cha;
//		}
//		return null;
//	}

    public short getDekaron() {
        return dekaron;
    }

    public void setDekaron(short dekaron) {
        this.dekaron = dekaron;
    }

    public short getFengmota() {
        return fengmota;
    }

    public void setFengmota(short fengmota) {
        this.fengmota = fengmota;
    }

    public short getZhuzai() {
        return zhuzai;
    }

    public void setZhuzai(short zhuzai) {
        this.zhuzai = zhuzai;
    }

    @Override
    public byte getPlatform() {
        return platform;
    }

    public void setPlatform(byte platform) {
        this.platform = platform;
    }

    public ShareData getShareData() {
        return shareData;
    }

    public void setShareData(ShareData shareData) {
        this.shareData = shareData;
    }

    public String getShareDataJson() {
        return JSON.toJSONString(shareData);
    }

    public void setShareDataJson(String json) {
        if (StringUtil.isEmpty(json)) {
            this.shareData = new ShareData();
        } else {
            this.shareData = JSON.parseObject(json, ShareData.class);
        }
    }

    public String getDailyUpdateMark() {
        return dailyUpdateMark;
    }

    public void setDailyUpdateMark(String dailyUpdateMark) {
        this.dailyUpdateMark = dailyUpdateMark;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public boolean isDailyUpdate() {
        if (DateUtil.formatDate(System.currentTimeMillis()).equals(this.dailyUpdateMark)) {
            return false;
        }
        return true;
    }

    public void resetFight() {
        this.dead = false;
//		for(Character character:this.getCharacterList()){
//
//			character.setHp(character.getAttribute()[EAttrType.HP.getId()]);
//		}
    }

    public Character getActiveCharacter() {
        Character temp = null;
//		for(Character character:this.getCharacterList()){
//			if(character.getHp()>0){
//				temp=character;
//			}
//		}
        return temp;
    }

    public void HurtAll(int damage, Character characterA) {
//		for(Character character:this.getCharacterList()){
//			if(character.getHp()>0){
//				int currDamage = damage;
//				HeartSkillData hsDataHurt=characterA.getHeartSkillDataByType(SkillDefine.HEART_SKILL_TYPE_HURT);
//				if(hsDataHurt!=null){
//					int triggerHurtHp = (int)(character.currentAttr()[EAttrType.HP.getId()]*hsDataHurt.getPram());
//					if(character.getHp()<triggerHurtHp){
//						currDamage += (int)(hsDataHurt.getHp_hurt()*currDamage);
//					}
//					if(hsDataHurt.getPram2()>0){
//						triggerHurtHp = (int)(character.currentAttr()[EAttrType.HP.getId()]*hsDataHurt.getPram2());
//						if(character.getHp()<triggerHurtHp && hsDataHurt.getRate()>Math.random() * 10000){
//							character.setHp(0);
//						}
//					}
//				}
//				hsDataHurt=characterA.getHeartSkillDataByType(SkillDefine.HEART_SKILL_TYPE_ATTCK);
//				if(hsDataHurt!=null && characterA.getDecattAttackDownTime()<=0 && hsDataHurt.getRate()>Math.random() * 10000){
//					character.setDecattAttack(hsDataHurt.getDecatt());
//					character.setDecattAttackTime((int)(hsDataHurt.getPram()*100));
//					characterA.setDecattAttackDownTime((int)(hsDataHurt.getPram1()*100));
//				}
//				character.setHurt(currDamage);
//				HeartSkillData hsData=character.getHeartSkillDataByType(SkillDefine.HEART_SKILL_TYPE_BACK);
//				if(hsData!=null){
//					if(hsData.getRate()>Math.random() * 10000){
//						characterA.setHurt((int)(currDamage*hsData.getHitback()));
//					}
//				}
//				hsData=character.getHeartSkillDataByType(SkillDefine.HEART_SKILL_TYPE_DEF);
//				if(hsData!=null){
//					if(hsData.getRate()>Math.random() * 10000){
//						characterA.setDecattDef(hsDataHurt.getDecatt());
//						characterA.setDecattDefTime((int)(hsDataHurt.getPram()*100));
//						character.setDecattDefDownTime((int)(hsDataHurt.getPram1()*100));
//					}
//				}
//			}
//		}
    }

    public Set<Short> getAuctionSubscriptions() {
        return auctionSubscriptions;
    }

    public void setAuctionSubscriptions(String auctionSubscriptions) {
        this.auctionSubscriptions = StringUtil.gson2set(auctionSubscriptions, new TypeToken<Set<Short>>() {
        });
    }

    public void setAuctionSubscriptions(Set<Short> subscriptions) {
        this.auctionSubscriptions = subscriptions;
    }

    public String getAuctionSubscriptionsStr() {
        return StringUtil.obj2Gson(auctionSubscriptions);
    }

    public boolean isSubscribe(short id) {
        return auctionSubscriptions.contains(id);
    }

    public DragonBall getDragonBall() {
        return dragonBall;
    }

    public void setDragonBall(DragonBall dragonBall) {
        this.dragonBall = dragonBall;
    }

    public void setDragonBall(String string) {
        if (!StringUtil.isEmpty(string)) {
            this.dragonBall = StringUtil.gson2Obj(string, DragonBall.class);
        }
    }

    public String getDragonBallStr() {
        return StringUtil.obj2Gson(dragonBall);
    }

    public int getAchievement() {
        return achievement;
    }

    public void setAchievement(int achievement) {
        this.achievement = achievement;
    }

    public void addAchievement(int value) {
        this.achievement += value;
    }

    public byte getMedal() {
        return medal;
    }

    public void setMedal(byte medal) {
        this.medal = medal;
    }

    public String getAchievementMissionStr() {
        return StringUtil.obj2Gson(achievementMission.values());
    }

    public void setAchievementMissionString(String string) {
        if (!StringUtil.isEmpty(string)) {
            List<RoleChainMission> list = StringUtil.gson2List(string, new TypeToken<List<RoleChainMission>>() {
            });
            Map<Short, RoleChainMission> map = new HashMap<>();
            for (RoleChainMission mission : list) {
                map.put(mission.getId(), mission);
            }
            this.achievementMission = map;
        }
    }

    public void updateAchievemntMission(short id, RoleChainMission mission) {
        this.achievementMission.put(id, mission);
    }

    public RoleChainMission getAchievement(short id) {
        return this.achievementMission.get(id);
    }

    public Map<Short, RoleChainMission> getAchievementMission() {
        return this.achievementMission;
    }

    public void getNightFightAppearMessage(Message message) {
        message.setInt(this.id);
        message.setString(this.name);
        message.setByte(this.head);
        message.setByte(this.magicStage);

        //Character character=this.characterList.get(0);
        //character.getNightFightAppearMessage(message);
        message.setLong(this.fighting);
    }

    public Map<Byte, Short> getHeartSkillMap() {
        return heartSkillMap;
    }

    public String getHeartSkillJson() {
        return JSON.toJSONString(this.heartSkillMap);
    }

    public void setHeartSkillJson(String json) {
        if (json == null || json.isEmpty())
            return;
        this.heartSkillMap = JSON.parseObject(json, new TypeReference<Map<Byte, Short>>() {
        });
    }

    public Map<Short, Goods> getArtifactPieces() {
        return artifactPieces;
    }

    public String getArtifactPiecesJson() {
        return StringUtil.obj2Gson(artifactPieces.values());
    }

    public void setArtifactPiecesStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            List<Goods> list = StringUtil.gson2List(json, new TypeToken<List<Goods>>() {
            });
            Map<Short, Goods> map = new HashMap<>();
            for (Goods data : list) {
                map.put(data.getD(), data);
            }
            artifactPieces = map;
        }
    }

    public void addArtifactPieces(Goods goods) {
        artifactPieces.put(goods.getD(), goods);
    }

    public String getArtifactBossJson() {
        return StringUtil.obj2Gson(artifactBoss);
    }

    public void setArtifactBoss(String artifactBoss) {
        if (!StringUtil.isEmpty(artifactBoss)) {
            this.artifactBoss = StringUtil.gson2Obj(artifactBoss, ArtifactBoss.class);
        }
    }

    public void setArtifactBoss(ArtifactBoss artifactBoss) {
        this.artifactBoss = artifactBoss;
    }

    public ArtifactBoss getArtifactBoss() {
        return artifactBoss;
    }

    public byte[] getCombineRune() {
        return combineRune;
    }

    public String getCombineRuneString() {
        return GameCommon.toString(combineRune);
    }

    public void setCombineRune(String str) {
        if (!StringUtil.isEmpty(str))
            this.combineRune = GameCommon.parseByteArray(str);
    }

    public int getCombineRunePiece() {
        return combineRunePiece;
    }

    public void setCombineRunePiece(int combineRunePiece) {
        this.combineRunePiece = combineRunePiece;
    }

    public void addCombineRunePiece(int add) {
        this.combineRunePiece += add;
    }

    public Map<Byte, Integer> getCombineRuneBag() {
        return combineRuneBag;
    }

    public void setCombineRuneBag(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.combineRuneBag = JSON.parseObject(json, new TypeReference<Map<Byte, Integer>>() {
            });
        }
    }

    public String getCombineRuneBagJson() {
        return JSON.toJSONString(this.combineRuneBag);
    }

    /**
     * 关卡神器是否激活
     *
     * @param id
     * @return
     */
    public boolean isArtifactBossInvoked(byte id) {
        return id < artifactBoss.getId();
    }


    public int getZhanWenJinghua() {
        return zhanWenJinghua;
    }

    public void setZhanWenJinghua(int zhanWenJinghua) {
        this.zhanWenJinghua = zhanWenJinghua;
    }

    public void addZhanWenJinghua(int value) {
        this.zhanWenJinghua += value;
    }

    /**
     * 战纹背包
     **/
    private Map<Integer, ZhanWen> zhanWens = new HashMap<>();

    public String getZhanWensJson() {

        return JSON.toJSONString(this.zhanWens.values());
    }

    public void setZhanWensJson(String json) {
        if (json == null || json.isEmpty()) {
            return;
        }
        List<ZhanWen> zhanWenList = JSON.parseArray(json, ZhanWen.class);

        if (zhanWenList != null && zhanWenList.size() > 0) {

            for (ZhanWen zhanWen : zhanWenList) {
                this.zhanWens.put(zhanWen.getD(), zhanWen);
            }
        }
    }

    public Map<Integer, ZhanWen> getZhanWens() {
        return zhanWens;
    }

    public int getWeiWang() {
        return weiWang;
    }

    public void setWeiWang(int weiWang) {
        this.weiWang = weiWang;
    }

    /**
     * 使用此方法前须使用 MissionManager.updateTLMissionData()更新
     *
     * @return
     */
    public TLMissionData getTLMissionData() {
        return tlMissionData;
    }

    public void setTLMissionData(TLMissionData tlMissionData) {
        this.tlMissionData = tlMissionData;
    }

    public void setTimeLimitMissionDataStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.tlMissionData = StringUtil.gson2Obj(json, TLMissionData.class);
        }
    }

    public String getTimeLimitMissionJson() {
        return tlMissionData == null ? "" : StringUtil.obj2Gson(tlMissionData);
    }

    public short getMysteryBossLeft() {
        return mysteryBossLeft;
    }

    public void setMysteryBossLeft(short mysteryBossLeft) {
        this.mysteryBossLeft = mysteryBossLeft;
    }

    public List<Short> getMysteryCue() {
        return mysteryCue;
    }

    public void setMysteryCue(List<Short> mysteryCue) {
        this.mysteryCue = mysteryCue;
    }

    public String getMysteryCueJson() {
        return JSON.toJSONString(this.mysteryCue);
    }

    public void setMysteryCueJson(String json) {
        if (StringUtil.isEmpty(json)) {
            return;
        }
        this.mysteryCue = JSON.parseObject(json, new TypeReference<List<Short>>() {
        });
    }

    public void changeMysteryBossLeft(int num) {
        this.mysteryBossLeft += num;
    }

    public List<Short> getVipBossCue() {
        return vipBossCue;
    }

    public void setVipBossCue(List<Short> vipBossCue) {
        this.vipBossCue = vipBossCue;
    }

    public String getVipBossCueJson() {
        return JSON.toJSONString(this.vipBossCue);
    }

    public void setVipBossCueJson(String json) {
        if (StringUtil.isEmpty(json)) {
            return;
        }
        this.vipBossCue = JSON.parseObject(json, new TypeReference<List<Short>>() {
        });
    }

    public int getCombineRuneTotalNum() {
        int total = 0;
        for (int i = 0; i < combineRune.length; ++i) {
            if (combineRune[i] == 0)
                continue;
            ++total;
        }
        for (int num : combineRuneBag.values()) {
            total += num;
        }
        return total;
    }

    public int getCastingSoulTotalNum() {
        int total = 0;
//		for(Character cha : this.getCharacterList()){
//			for(EquipSlot slot : cha.getEquipSlotList()){
//				total+=slot.getZh();
//			}
//		}
        return total;
    }

    public int getJingMaiFighting() {
        int total = 0;
//		for(Character cha : getCharacterList()){
//			total += cha.getJingMaiFighting();
//		}
        return total;
    }

    public Map<Short, Goods> getWingGods() {
        return wingGods;
    }

    public String getWingGodsJson() {
        return StringUtil.obj2Gson(wingGods.values());
    }

    public void setWingGods(Map<Short, Goods> wingGods) {
        this.wingGods = wingGods;
    }

    public void setWingGodsStr(String json) {
        List<Goods> list = StringUtil.gson2List(json, new TypeToken<List<Goods>>() {
        });
        Map<Short, Goods> map = new HashMap<>();
        for (Goods wingGod : list) {
            map.put(wingGod.getD(), wingGod);
        }
        wingGods = map;
    }

    public Map<Byte, Goods> getHolyLines() {
        return holyLines;
    }

    public String getHolyLinesJson() {
        return JSON.toJSONString(holyLines);
    }

    public void setHolyLinesJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.holyLines = JSON.parseObject(json, new TypeReference<Map<Byte, Goods>>() {
            });
        }
    }

    public Map<Short, Goods> getLingSuiPieces() {
        return lingSuiPieces;
    }

    public String getLingSuiPiecesJson() {
        return StringUtil.obj2Gson(lingSuiPieces.values());
    }

    public void setlingSuiPieces(Map<Short, Goods> lingSuiGods) {
        this.lingSuiPieces = lingSuiGods;
    }

    public void setLingSuiPiecesStr(String json) {
        List<Goods> list = StringUtil.gson2List(json, new TypeToken<List<Goods>>() {
        });
        Map<Short, Goods> map = new HashMap<>();
        for (Goods lingSuiGod : list) {
            map.put(lingSuiGod.getD(), lingSuiGod);
        }
        lingSuiPieces = map;
    }

    public Map<Short, Goods> getCardbag() {
        return cardbag;
    }

    public void setCardbag(Map<Short, Goods> cardbag) {
        this.cardbag = cardbag;
    }

    public String getCardBagJson() {
        return StringUtil.obj2Gson(cardbag.values());
    }

    public void setCardBagStr(String json) {
        List<Goods> list = StringUtil.gson2List(json, new TypeToken<List<Goods>>() {
        });
        Map<Short, Goods> map = new HashMap<>();
        for (Goods wingGod : list) {
            map.put(wingGod.getD(), wingGod);
        }
        cardbag = map;
    }

    public PlayerCardBook getCardBook() {
        return cardBook;
    }

    public void setCardBook(PlayerCardBook cardBook) {
        this.cardBook = cardBook;
    }

    public String getCardBookJson() {
        return cardBook.getJson();
    }

    public void setCardBookStr(String json) {
        cardBook.init(json);
    }


    /**
     * 使用此方法前须使用 MissionManager.updateCardMissionData()更新
     *
     * @return
     */
    public CardMission getCardMission() {
        return cardMission;
    }

    public void setCardMission(CardMission mission) {
        this.cardMission = mission;
    }

    public void setCardMissionStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.cardMission = StringUtil.gson2Obj(json, CardMission.class);
        }
    }

    public String getCardMissionJson() {
        return cardMission == null ? "" : StringUtil.obj2Gson(cardMission);
    }

    public FiveElements getFiveElements() {
        return fiveElements;
    }

    public void setFiveElements(FiveElements fiveElements) {
        this.fiveElements = fiveElements;
    }

    public void setFiveElementsStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.fiveElements = StringUtil.gson2Obj(json, FiveElements.class);
        }
    }

    public String getFiveElementsJson() {
        return fiveElements == null ? "" : StringUtil.obj2Gson(fiveElements);
    }

    public Ambit getAmbit() {
        return ambit;
    }

    public void setAmbitJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            ambit = JSON.parseObject(json, Ambit.class);
        }
    }

    public String getAmbitJson() {
        return JSON.toJSONString(ambit);
    }

    public String getConsumeDaily() {
        return this.consumDaily;
    }

    public void setConsumeDailyStr(String str) {
        this.consumDaily = str;
    }

    public long getLastLoginTime2Fest() {
        return lastLoginTime2Fest;
    }

    public void setLastLoginTime2Fest(long lastLoginTime2Fest) {
        this.lastLoginTime2Fest = lastLoginTime2Fest;
    }

    public TownSoulTreasure getTownSoulTreasure() {
        return townSoulTreasure;
    }

    public String getTownSoulTreasureJson() {
        return JSON.toJSONString(this.townSoulTreasure);
    }

    public void setTownSoulTreasureJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.townSoulTreasure = JSON.parseObject(json, TownSoulTreasure.class);
        }
    }

    public int getVouchers() {
        return vouchers;
    }

    public void setVouchers(int vouchers) {
        this.vouchers = vouchers;
    }

    public void addVouchers(int vouchers) {
        this.vouchers += vouchers;
    }

    public int getMysteryIntegral() {
        return mysteryIntegral;
    }

    public void setMysteryIntegral(int mysteryIntegral) {
        this.mysteryIntegral = mysteryIntegral;
    }

    public void addMysteryIntegral(int mysteryIntegral) {
        this.mysteryIntegral += mysteryIntegral;
    }

    public int getQizhenIntegral() {
        return qizhenIntegral;
    }

    public void setQizhenIntegral(int qizhenIntegral) {
        this.qizhenIntegral = qizhenIntegral;
    }

    public void addQizhenIntegral(int qizhenIntegral) {
        this.qizhenIntegral += qizhenIntegral;
    }

//	public Map<Byte,Map<Integer, TrainItem>> getTrainItems(){
//		return this.trainItems;
//	}
//
//	public void setTrainItemsJson(String json) {
//		if(!StringUtil.isEmpty(json)) {
//			this.trainItems = StringUtil.gson2Map(json, new TypeToken<Map<Byte,Map<Integer,TrainItem>>>() {});
//		}
//	}
//
//	public String getTrainItemsStr() {
//		return StringUtil.obj2Gson(this.trainItems);
//	}
//
//	public Map<Byte, Map<Short, Goods>> getTrainItemEquips() {
//		return this.trainItemEquips;
//	}

    private int getSpiritEquipMaxId(List<SpiritEquipData> list, int lv) {
        for (SpiritEquipData sed : list) {
            if (sed.getLv() == lv) {
                return sed.getId();
            }
        }
        return -1;
    }

    public Message getEGMessage() {
        Message message = new Message(EMessage.ENTER_GAME.CMD());

        return message;
    }

    public List<Grow> getGrowList() {
        return growList;
    }

    public void setGrowList(List<Grow> growList) {
        this.growList = growList;
    }

    public String getGrowListJson() {
        return JSON.toJSONString(growList);
    }

    public void setGrowListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.growList = JSON.parseArray(json, Grow.class);
        } else {
            this.growList = new ArrayList<>();
            for (EGrow type : EGrow.values()) {
                this.growList.add(new Grow(type));
            }
        }
    }

    public List<Map<Short, Integer>> getGrowEquipList() {
        return growEquipList;
    }

    public void setGrowEquipList(List<Map<Short, Integer>> growEquipList) {
        this.growEquipList = growEquipList;
    }

    public String getGrowEquipListJson() {
        return JSON.toJSONString(growEquipList);
    }

    public void setGrowEquipListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.growEquipList = JSON.parseObject(json, new TypeReference<List<Map<Short, Integer>>>() {
            });
        } else {
            this.growEquipList = new ArrayList<>();
            for (EEquip type : EEquip.values()) {
                this.growEquipList.add(new HashMap<>());
            }
        }
    }

    public Map<Short, Integer> getRoleEquipMap() {
        return roleEquipMap;
    }

    public String getRoleEquipMapJson() {
        return JSON.toJSONString(this.roleEquipMap);
    }

    public void setRoleEquipMapJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.roleEquipMap = JSON.parseObject(json, new TypeReference<Map<Short, Integer>>() {
            });
        } else {
            this.roleEquipMap = new HashMap<>();
        }
    }

    public void getTeamMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setShort(level);
        message.setLong(fighting);
    }
}
