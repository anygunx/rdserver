package com.rd.enumeration;

import com.rd.define.NEquipUpGradeType;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月13日下午3:00:35
 */
public enum EMessage {

    COMBAT_STAGE_MONSTER_BEGIN(1701, EMessage::combatStageMonsterBegin),
    COMBAT_STAGE_MONSTER_END(1702, EMessage::combatStageMonsterEnd),
    COMBAT_STAGE_BOSS_BEGIN(1703, EMessage::combatStageBossBegin),
    COMBAT_STAGE_BOSS_END(1704, EMessage::combatStageBossEnd),

    ENTER_GAME(1705, EMessage::combatStageBossEnd),
    COMBAT_CHANGE_MAP(1706, EMessage::combatChangeMap),


    EQUIP_STRENGTH_ONEKEY_MESSAGE(558, EMessage::equipQh),
    OPEN_PANEL_MESSAGE(559, EMessage::openPanel),
    JIHUOPIFU(1803, EMessage::jiHuoPifu),
    GENGHUANPIFU(1804, EMessage::gengHuanPifu),
    PIFU_PANEL(1805, EMessage::piFuPanel),
    OPEN_TAOZHUANG_PANEL(1806, EMessage::taoZhuangPanel),
    PULSE_UPGRADE(1807, EMessage::pulseUpGrade),
    DANYAO_UPGRADE(1808, EMessage::danYaoUpGrade),
    OPEN_DANYAO_PANEL(1809, EMessage::openDanYaoPanel),
    GOODS_WEAR_EQUIP_MESSAGE(1810, EMessage::goodsWearEquip),
    OPEN_EQUIP_PANEL_MESSAGE(1811, EMessage::openEquipPanel),

    FRIEND_TUIJIAN_LIST(1812, EMessage::friendTuiJianList),
    FRIEND_GUANZHU(1813, EMessage::friendGuanZhu),
    FRIEND_DELTE_GUANZHU(1814, EMessage::friendDeleteGuanZhu),
    FRIEND_ADD_BLACK(1815, EMessage::friendAddBlack),
    FRIEND_GUANZHU_LIST(1816, EMessage::friendGuanZhuList),

    FRIEND_SEND_FRIEND_COIN(1817, EMessage::friendSendFriendCoin),
    FRIEND_ONEKEY_SEND_FRIEND_COIN(1818, EMessage::friendOneKeySendFriendCoin),
    FRIEND_FENSI_LIST(1819, EMessage::friendFenSiList),
    FRIEND_OONEKEY_RECEIVE_FRIENDCOIN(1820, EMessage::friendOneKeyReceiveFriendCoin),
    FRIEND_ROLE_INFO(1821, EMessage::friendRoleInfo),
    FRIEND_RECEIVE_FRIENDCOIN(1822, EMessage::friendReceiveFriendCoin),
    FRIEND_DELTE_BLACK(1823, EMessage::friendDeleteBlack),
    FRIEND_BLACK_LIST(1824, EMessage::friendBlackList),


    SKILL_SKILLPANEL(1825, EMessage::skillPanel),
    SKILL_UPGRADE(1826, EMessage::skillUpGrade),

    COPY_PANEL(1828, EMessage::copyPanel),
    COPY_REQUITE_FIGHT(1829, EMessage::copyRquestFight),
    COPY_FIGHT_FINISH(1830, EMessage::copyFinish),
    COPY_SWEEP(1831, EMessage::copySweep),
    COPY_SJG_PANEL(1832, EMessage::copySJGPanel),
    COPY_SJG_AUTO_SET(1833, EMessage::copySJGSet),
    COPY_TM_PANEL(1834, EMessage::copyTMPanel),
    COPY_TM_RANK(1835, EMessage::copyTMRank),
    COPY_TM_GET_REWARD(1836, EMessage::copyTMCopyGetReward),
    COPY_TMDB_DATA(1837, EMessage::copyTMBDData),
    COPY_TM_ONEKEY_SWEEP(1838, EMessage::copyTMOneKeySweep),
    COPY_MIZANG_PANEL(1840, EMessage::copyMiZangPanel),
    COPY_MIZANG_GET_REWARD(1841, EMessage::copygetMiZangReward),
    COPY_MIZANG_RANK(1842, EMessage::copygetMiZangRank),
    COPY_MIZANG_SWEEP(1843, EMessage::copygetMiZangOneKeySeep),
    COPY_ZHONGKUI_PANEL(1855, EMessage::copyZhongKuiPanel),
    COPY_ZHONGKUI_BUY_STAR(1857, EMessage::copyZhongKuiBuyStar),


    BOSS_QUANMIN_PANEL(1844, EMessage::bossQuanMinPanel),
    BOSS_QUANMIN_RANK(1845, EMessage::bossQuanMinRank),
    BOSS_QUANMIN_BUY_COUNT(1846, EMessage::bossQuanMinBuyCount),
    BOSS_QUANMIN_FUHUO(1849, EMessage::bossQuanMinFuHuo),
    BOSS_QUANMIN_TIXING(1850, EMessage::bossQuanTiXing),
    BOSS_QUANMIN_TIXING_ROLE(1851, EMessage::bossQuanTiXing),
    BOSS_QUANMIN_UPDATE_HP(1852, EMessage::bossQuanTiXing),

    SHOP_EQUIP_PANEL(1847, EMessage::shopEquipPanel),
    SHOP_BUY(1848, EMessage::shopBuy),

    RICHANG_ZHONGKUI_PANEL(1853, EMessage::richangZhongKuiPanel),
    RICHANG_ZHONGKUI_LINGQU(1854, EMessage::richangZhongKuiLingQu),
    RICHANG_ZHONGKUI_ONEKEY(1856, EMessage::richangZhongKuiOneKey),
    RICHANG_300_PANEL(1858, EMessage::richang300Panel),
    RICHANG_300_LINGQU(1859, EMessage::richang300Liangqu),
    RICHANG_TEAM_PANEL(1860, EMessage::richangTeamPanel),
    RICHANG_TEAM_LINGQU(1861, EMessage::richangTeamLingQu),


    TASK_PANEL(1862, EMessage::taskPanel),
    TASK_ZHAOHUI(1863, EMessage::taskZhaoHui),
    TASK_ZHAOHUI_ONEKEY(1864, EMessage::taskOneKeyZhaoHui),
    TASK_ZHAOHUI_PANEL(1865, EMessage::taskZhaoHuiPanel),
    TASK_UPGRADE(1866, EMessage::taskUpdatePanel),

    FACTION_CREATE(1867, EMessage::factionCreate),
    FACTION_LIST(1868, EMessage::factionList),
    FACTION_APPLY(1869, EMessage::factionApply),
    FACTION_APPLY_LIST(1874, EMessage::factionApplyList),
    FACTION_INFO(1872, EMessage::factionInfo),
    FACTION_TASK_LIST(1875, EMessage::factionTaskList),
    FACTION_MEIRI_ACTIVE_REWARD_LIST(1876, EMessage::factionTaskMeiRiActiveRewardList),
    FACTION_MEIRI_ACTIVE_REWARD(1877, EMessage::factionTaskMeiRiActiveReward),
    FACTION_MEMBER(1878, EMessage::factionMember),
    FACTION_AUTO_FACTION_APPLY(1879, EMessage::factionSetAutoAddFaction),
    FACTION_ADOPT(1880, EMessage::factionAdopt),
    FACTION_APPOINT(1881, EMessage::processFactionAppoint),
    FACTION_DISMISS(1882, EMessage::processFactionDismiss),
    FACTION_NAME_CHANGE(1883, EMessage::factionNameChange),
    FACTION_NAME_DECLARATION(1884, EMessage::processFactionDeclarationNote),
    FACTION_EXIT(1885, EMessage::processFactionExit),
    FACTION_SHAGNXIANG(1886, EMessage::processFactionShangXiang),
    FACTION_SKILL_PANEL(1887, EMessage::processFactionSkillPanel),
    FACTION_SKILL_UPGRADE(1888, EMessage::processFactionSkillUpGrade),
    FACTION_SHAGNXIANG_PANEL(1889, EMessage::processFactionSXPanel),
    FACTION_SHAGNXIANG_REARD_LINGQU(1890, EMessage::processFactionSXRewardLingqu),
    FACTION_SHAGNXIANG_JILU(1891, EMessage::processFactionSXJiLu),
    FACTION_LOG(1892, EMessage::processFactionLog),
    FACTION_ACTIVE_UP_LEVEL(1893, EMessage::processFactionActiveUpLevel),


    HUSONG_RANDOM_BIAOCHE(1899, EMessage::factionNameChange),

    HUSONG_BIAOCHE_LIST_PANEL(1900, EMessage::husongBiaoCheListPanel),
    HUSONG_BIAOCHE_COUNT_PANEL(1901, EMessage::husongBiaocheInfoPanel),
    HUSONG_RANDOM_QUALITY(1902, EMessage::processRandomBiaocheQuality),
    HUSONG_BIAOCHE(1903, EMessage::processDispatch),
    HUSONG_BIAOCHE_LANJIE(1904, EMessage::processRobStart),
    HUSONG_BIAOCHE_LANJIE_RESULT(1905, EMessage::processJieBiaoEnd),
    HUSONG_BIAOCHE_END(1906, EMessage::processBiaocheFinish),
    HUSONG_BIAOCHE_LINGQU(1907, EMessage::processBiaocheRewardLingQu),
    HUSONG_LOGS(1908, EMessage::processBiaocheLogs),
    HUSONG_FUCHONG(1909, EMessage::processFuChou),
    HUSONG_FUCHONG_END(1910, EMessage::processFuChouEnd),

    CHAT_START(1920, EMessage::processChatStart),
    CHAT_SHOW_INFO(1921, EMessage::processShowInfo),
    CHAT_HISTORY_RECORDS(1922, EMessage::processChatHistoryRecords),

    JIGNJI_PANEL(1930, EMessage::processJingJiChangPanel),
    JIGNJI_BUY_COUNT(1931, EMessage::processBuyCount),
    JIGNJI_RECORD(1932, EMessage::processJingJiRecords),
    JIGNJI_RANK(1933, EMessage::processJingJiRank),
    JIGNJI_START(1934, EMessage::processJingJiStart),
    JIGNJI_END(1935, EMessage::processJingJiEnd),
    JIGNJI_MIAOSHA(1936, EMessage::processMiaoSha),


    TASK_ADVANCE_PANEL(1870, EMessage::taskadvancePanel),
    TASK_ADVANCE_LINGQU(1871, EMessage::taskadvanceLingQu),
    GROW_XIANLV_TIPS(643, EMessage::processXianLvTotalLv),

    GROW_LIST(230, EMessage::growList),
    GROW_ACTIVE(231, EMessage::growActive),
    GROW_GO(232, EMessage::growGo),
    GROW_RENAME(233, EMessage::growRename),
    GROW_LEVELUP(234, EMessage::growLevelUp),
    GROW_FLYUP(235, EMessage::growFlyUp),
    GROW_APTITUDE(236, EMessage::growAptitude),
    GROW_WASHINGLOCK(237, EMessage::growWashLock),
    GROW_WASHINGUNLOCK(238, EMessage::growWashUnlock),
    GROW_WASHING(239, EMessage::growWashing),
    GROW_WASHCHANGE(240, EMessage::growWashChange),
    GROW_PSYCHICLEVELUP(241, EMessage::growPsychicLevelUp),
    GROW_PSYCHICSKILL(242, EMessage::growPsychicSkill),
    GROW_PSYCHICPILL(243, EMessage::growPsychicPill),
    GROW_PSYCHICEQUIP(244, EMessage::growPsychicEquip),
    GROW_SOULLEVELUP(245, EMessage::growSoulLevelUp),
    GROW_SOULSKILL(246, EMessage::growSoulSkill),
    GROW_SOULPILL(247, EMessage::growSoulPill),
    GROW_SOULEQUIP(248, EMessage::growSoulEquip),
    GROW_LEVELUPHN(249, EMessage::growLevelUp3),
    GROW_SKILLHN(250, EMessage::growSkill3),
    GROW_PILLHN(251, EMessage::growPill3),
    GROW_EQUIPHN(252, EMessage::growEquip3),
    GROW_LEVELUPLQ(253, EMessage::growLevelUp4),
    GROW_SKILLLQ(254, EMessage::growSkill4),
    GROW_PILLLQ(255, EMessage::growPill4),
    GROW_EQUIPLQ(256, EMessage::growEquip4),
    GROW_STARUP(257, EMessage::growStarUp),

    TEAM_CROSS_LIST(260, EMessage::teamCrossList),
    TEAM_CROSS_CREATE(261, EMessage::teamCrossCreate),
    TEAM_CROSS_JOIN(262, EMessage::teamCrossJoin),
    TEAM_CROSS_INFO(263, EMessage::teamCrossInfo),
    TEAM_CROSS_KICK(264, EMessage::teamCrossKick),
    TEAM_CROSS_EXIT(265, EMessage::teamCrossExit),
    TEAM_CROSS_START(266, EMessage::teamCrossStart),
    TEAM_CROSS_END(267, EMessage::teamCrossEnd),
    TEAM_CROSS_STAGE_STATE(268, EMessage::teamCrossStageState),

    TEAM_LADD_LIST(269, EMessage::teamLaddList),
    TEAM_LADD_CREATE(270, EMessage::teamLaddCreate),
    TEAM_LADD_JOIN(271, EMessage::teamLaddJoin),
    TEAM_LADD_INFO(272, EMessage::teamLaddInfo),
    TEAM_LADD_KICK(273, EMessage::teamLaddKick),
    TEAM_LADD_EXIT(274, EMessage::teamLaddExit),
    TEAM_LADD_START(275, EMessage::teamLaddStart),
    TEAM_LADD_END(276, EMessage::teamLaddEnd),
    TEAM_LADD_STAGE_STATE(277, EMessage::teamLaddStageState),
    TEAM_LADD_RECORD(278, EMessage::teamLaddRecord),
    TEAM_LADD_SWEEP(279, EMessage::teamLaddSweep),
    TEAM_LADD_TREASURE_BOX(280, EMessage::teamLaddTreasureBox),

    STORAGE_EXP(113, EMessage::storageExp),;

    private final static Map<Short, EMessage> commandMap = new HashMap<Short, EMessage>() {
        private static final long serialVersionUID = 1L;

        {
            for (EMessage command : EMessage.values()) {
                put(command.command, command);
            }
        }
    };

    private short command;
    private final BiConsumer<GameRole, Message> handler;

    EMessage(int command, BiConsumer<GameRole, Message> handler) {
        this.command = (short) command;
        this.handler = handler;
    }

    public final static EMessage getCommand(short key) {
        return commandMap.get(key);
    }

    public short CMD() {
        return command;
    }

    public BiConsumer<GameRole, Message> getHandler() {
        return handler;
    }

    private static void combatStageMonsterBegin(GameRole role, Message request) {
        role.getCombatManager().processCombatStageMonsterBegin(request);
    }

    private static void combatStageMonsterEnd(GameRole role, Message request) {
        role.getCombatManager().processCombatStageMonsterEnd(request);
    }

    private static void combatStageBossBegin(GameRole role, Message request) {
        role.getCombatManager().processCombatStageBossBegin(request);
    }

    private static void combatStageBossEnd(GameRole role, Message request) {
        role.getCombatManager().processCombatStageBossEnd(request);
    }

    private static void combatChangeMap(GameRole role, Message request) {
        role.getCombatManager().processCombatChangeMap(request);
    }


    private static void equipQh(GameRole role, Message request) {
        //role.getNEquipManager().processEquipStrengthOneKey(request);
        role.getNEquipManager().processEquipStrengthOneKey1(request);
    }

    /***
     * 打开面板的公共协议 执行这个方法
     * @param role
     * @param request
     */
    private static void openPanel(GameRole role, Message request) {
        byte type = request.readByte();
        if (type >= NEquipUpGradeType.EQUIP_QH.getType()
                && type <= NEquipUpGradeType.EQUIQ__BS.getType()) {
            role.getNEquipManager().openPanlMessage1(request, type);
        }

    }

    /**
     * 激活不同种类的皮肤
     *
     * @param role
     * @param request
     */
    private static void jiHuoPifu(GameRole role, Message request) {
        role.getNPiFuManager().processJiHuoPiFu(request);
    }


    private static void gengHuanPifu(GameRole role, Message request) {
        role.getNPiFuManager().processGengHuanPiFu(request);
    }

    private static void piFuPanel(GameRole role, Message request) {
        role.getNPiFuManager().processPiFuPanel(request);
    }

    private static void taoZhuangPanel(GameRole role, Message request) {
        role.getNPiFuManager().openTZPanel(request);
    }

    private static void pulseUpGrade(GameRole role, Message request) {
        role.NPulseManager().processPulseUpGrade(request);
    }

    private static void danYaoUpGrade(GameRole role, Message request) {
        role.NPulseManager().processDanYao(request);
    }

    private static void openDanYaoPanel(GameRole role, Message request) {
        role.NPulseManager().openDanYaoPanel(request);
    }

    private static void goodsWearEquip(GameRole role, Message request) {
        role.getNEquipManager().processWearEquip(request);
    }

    private static void openEquipPanel(GameRole role, Message request) {
        role.getNEquipManager().openEquipPanel(request);
    }

    private static void friendTuiJianList(GameRole role, Message request) {
        role.getNRelationManager().processFriendTuiJianList(request);
    }

    private static void friendGuanZhu(GameRole role, Message request) {
        role.getNRelationManager().prossGuanzhu(request);
    }

    private static void friendDeleteGuanZhu(GameRole role, Message request) {
        role.getNRelationManager().prossQuXiaoGuanzhu(request);
    }

    private static void friendAddBlack(GameRole role, Message request) {
        role.getNRelationManager().processBlackMessage(request);
    }

    private static void friendGuanZhuList(GameRole role, Message request) {
        role.getNRelationManager().processGuanZhuList(request);
    }

    private static void friendSendFriendCoin(GameRole role, Message request) {
        role.getNRelationManager().prossSendFriendCoin(request);
    }

    private static void friendOneKeySendFriendCoin(GameRole role, Message request) {
        role.getNRelationManager().prossSendFriendCoinByOneKey(request);
    }

    private static void friendFenSiList(GameRole role, Message request) {
        role.getNRelationManager().processFenSiList(request);
    }

    private static void friendOneKeyReceiveFriendCoin(GameRole role, Message request) {
        role.getNRelationManager().processOneKeyReceiveFriendCoin(request);
    }

    private static void friendReceiveFriendCoin(GameRole role, Message request) {
        role.getNRelationManager().processReceiveFriendCoin(request);
    }

    private static void friendRoleInfo(GameRole role, Message request) {
        role.getNRelationManager().processRoleInfo(request);
    }

    private static void friendDeleteBlack(GameRole role, Message request) {
        role.getNRelationManager().processDeleteBlack(request);
    }

    private static void friendBlackList(GameRole role, Message request) {
        role.getNRelationManager().processBlackList(request);
    }


    private static void skillPanel(GameRole role, Message request) {
        role.getNSkillManager().processSkillPanel(request);
    }

    private static void skillUpGrade(GameRole role, Message request) {
        role.getNSkillManager().processSkillUpGrade(request);
    }

    private static void copyPanel(GameRole role, Message request) {
        role.getNCopyManager().processCopyPanel(request);
    }

    private static void copyRquestFight(GameRole role, Message request) {
        role.getNCopyManager().processCopyRquest(request);
    }

    private static void copyFinish(GameRole role, Message request) {
        role.getNCopyManager().processCopyFinish(request);
    }

    private static void copySweep(GameRole role, Message request) {
        role.getNCopyManager().processCopySweep(request);
    }

    private static void copySJGPanel(GameRole role, Message request) {
        role.getNCopyManager().processSJGCopyPanel(request);
    }

    private static void copySJGSet(GameRole role, Message request) {
        role.getNCopyManager().processSJGCopySet(request);
    }

    private static void copyTMPanel(GameRole role, Message request) {
        role.getNCopyManager().processTMCopyPanel(request);
    }

    private static void copyTMBDData(GameRole role, Message request) {
        role.getNCopyManager().processTMDBData(request);
    }

    private static void copyTMCopyGetReward(GameRole role, Message request) {
        role.getNCopyManager().processTMCopyGetReward(request);
    }

    private static void copyTMRank(GameRole role, Message request) {
        role.getNCopyManager().processTMRankPanel(request);
    }

    private static void copyTMOneKeySweep(GameRole role, Message request) {
        role.getNCopyManager().processTMCopyOneKeySweep(request);
    }

    private static void copyMiZangPanel(GameRole role, Message request) {
        role.getNCopyManager().processMiZangPanel(request);
    }

    private static void copygetMiZangReward(GameRole role, Message request) {
        role.getNCopyManager().processMiZangGetReward(request);
    }

    private static void copygetMiZangRank(GameRole role, Message request) {
        role.getNCopyManager().processMZRankPanel(request);
    }

    private static void copygetMiZangOneKeySeep(GameRole role, Message request) {
        role.getNCopyManager().processMiZangOneKeySeep(request);
    }

    private static void copyZhongKuiPanel(GameRole role, Message request) {
        role.getNCopyManager().processZhongKuiPanel(request);
    }

    private static void copyZhongKuiBuyStar(GameRole role, Message request) {
        role.getNCopyManager().processZhongKuiBuySar(request);
    }


    private static void bossQuanMinPanel(GameRole role, Message request) {
        role.getNBossManager().processCitizenInfo(request);
    }

    private static void bossQuanMinRank(GameRole role, Message request) {
        role.getNBossManager().processBossRank(request);
    }

    private static void bossQuanMinBuyCount(GameRole role, Message request) {
        role.getNBossManager().processBuyCont(request);
    }

    private static void bossQuanMinFuHuo(GameRole role, Message request) {
        role.getNBossManager().processBossFuHuo(request);
    }

    private static void bossQuanTiXing(GameRole role, Message request) {
        role.getNBossManager().processBossFuHuoTiXiangSet(request);
    }


    private static void shopEquipPanel(GameRole role, Message request) {
        role.getNshopManager().processPanel(request);
    }

    private static void shopBuy(GameRole role, Message request) {
        role.getNshopManager().processBuy(request);
    }

    private static void richangZhongKuiPanel(GameRole role, Message request) {
        role.getNRiChangManager().processZhongKuiDaBiaoPanel(request);
    }

    private static void richangZhongKuiLingQu(GameRole role, Message request) {
        role.getNRiChangManager().processLiangQUZhongKui(request);
    }

    private static void richangZhongKuiOneKey(GameRole role, Message request) {
        role.getNRiChangManager().processZHongKuiOneKey(request);
    }

    private static void richang300Panel(GameRole role, Message request) {
        role.getNRiChangManager().processRichang300Panel(request);
    }

    private static void richang300Liangqu(GameRole role, Message request) {
        role.getNRiChangManager().processRichang300LingQu(request);
    }

    private static void richangTeamPanel(GameRole role, Message request) {
        role.getNRiChangManager().processZuDuiPanel(request);
    }

    private static void richangTeamLingQu(GameRole role, Message request) {
        role.getNRiChangManager().processLiangQuZuDui(request);
    }

    private static void taskPanel(GameRole role, Message request) {
        role.getNTaskManager().processTaskPanel(request);
    }

    private static void taskZhaoHui(GameRole role, Message request) {
        role.getNTaskManager().proccessTaskZhaoHui(request);
    }

    private static void taskOneKeyZhaoHui(GameRole role, Message request) {
        role.getNTaskManager().proccessZhaoHuiOneKey(request);
    }

    private static void taskZhaoHuiPanel(GameRole role, Message request) {
        role.getNTaskManager().processZHaoHuiPanel(request);
    }

    private static void taskUpdatePanel(GameRole role, Message request) {
        role.getNTaskManager().processUpgrade(request);
    }


    private static void factionCreate(GameRole role, Message request) {
        role.getNFactionManager().processFactionCreate(request);
    }

    private static void factionList(GameRole role, Message request) {
        role.getNFactionManager().processFactionList(request);
    }

    private static void factionApply(GameRole role, Message request) {
        role.getNFactionManager().processFactionApply(request);
    }

    private static void factionApplyList(GameRole role, Message request) {
        role.getNFactionManager().processFactionApplyList(request);
    }

    private static void factionSetAutoAddFaction(GameRole role, Message request) {
        role.getNFactionManager().processSetAutoAddFaction(request);
    }

    private static void factionAdopt(GameRole role, Message request) {
        role.getNFactionManager().processFactionAdopt(request);
    }

    private static void processFactionAppoint(GameRole role, Message request) {
        role.getNFactionManager().processFactionAppoint(request);
    }

    private static void processFactionDismiss(GameRole role, Message request) {
        role.getNFactionManager().processFactionDismiss(request);
    }


    private static void factionInfo(GameRole role, Message request) {
        role.getNFactionManager().processFactionInfo(request);
    }


    private static void factionNameChange(GameRole role, Message request) {
        role.getNFactionManager().processChangeFactionName(request);
    }

    private static void processFactionDeclarationNote(GameRole role, Message request) {
        role.getNFactionManager().processFactionDeclarationNote(request);
    }

    private static void processFactionExit(GameRole role, Message request) {
        role.getNFactionManager().processFactionExit(request);
    }


    private static void factionTaskList(GameRole role, Message request) {
        role.getNFactionManager().processFactionTaskList(request);
    }

    private static void factionTaskMeiRiActiveRewardList(GameRole role, Message request) {
        role.getNFactionManager().processFactionActiveMeiRiRewardPanel(request);
    }

    private static void factionTaskMeiRiActiveReward(GameRole role, Message request) {
        role.getNFactionManager().processFactionActiveMeiRiReward(request);
    }

    private static void factionMember(GameRole role, Message request) {
        role.getNFactionManager().processFactionMember(request);
    }

    private static void processFactionShangXiang(GameRole role, Message request) {
        role.getNFactionManager().processFactionShangXiang(request);
    }

    private static void processFactionSXPanel(GameRole role, Message request) {
        role.getNFactionManager().processSXPanel(request);
    }

    private static void processFactionSXRewardLingqu(GameRole role, Message request) {
        role.getNFactionManager().processSXRewardLingqul(request);
    }

    private static void processFactionSXJiLu(GameRole role, Message request) {
        role.getNFactionManager().processSXJiLu(request);
    }

    private static void processFactionLog(GameRole role, Message request) {
        role.getNFactionManager().processFactionLog(request);
    }

    private static void processFactionActiveUpLevel(GameRole role, Message request) {
        role.getNFactionManager().processFactionActiveUpLevel(request);
    }


    private static void processFactionSkillPanel(GameRole role, Message request) {
        role.getNFactionManager().processFactionSkillPanel(request);
    }

    private static void processFactionSkillUpGrade(GameRole role, Message request) {
        role.getNFactionManager().processFactionSkill(request);
    }


    private static void taskadvancePanel(GameRole role, Message request) {
        role.getNTaskAdvancedManager().proccessPanel(request);
    }

    private static void taskadvanceLingQu(GameRole role, Message request) {
        role.getNTaskAdvancedManager().proccessLingQu(request);
    }

    private static void husongBiaoCheListPanel(GameRole role, Message request) {
        role.getNHuSongManger().processBiaoCheListPanel(request);
    }

    private static void husongBiaocheInfoPanel(GameRole role, Message request) {
        role.getNHuSongManger().processBiaoCheInfoPanel(request);
    }

    private static void processRandomBiaocheQuality(GameRole role, Message request) {
        role.getNHuSongManger().processRandomBiaocheQuality(request);
    }

    private static void processDispatch(GameRole role, Message request) {
        role.getNHuSongManger().processDispatch(request);
    }

    private static void processRobStart(GameRole role, Message request) {
        role.getNHuSongManger().processJieBiaoStart(request);
    }

    private static void processJieBiaoEnd(GameRole role, Message request) {
        role.getNHuSongManger().processJieBiaoEnd(request);
    }

    private static void processBiaocheFinish(GameRole role, Message request) {
        role.getNHuSongManger().processComplete(request);
    }

    private static void processBiaocheRewardLingQu(GameRole role, Message request) {
        role.getNHuSongManger().processReward(request);
    }

    private static void processBiaocheLogs(GameRole role, Message request) {
        role.getNHuSongManger().processLogs(request);
    }

    private static void processFuChou(GameRole role, Message request) {
        role.getNHuSongManger().processFuChou(request);
    }

    private static void processFuChouEnd(GameRole role, Message request) {
        role.getNHuSongManger().processFuChouEnd(request);
    }

    private static void processChatStart(GameRole role, Message request) {
        role.getNChatManager().processChat(request);
    }

    private static void processShowInfo(GameRole role, Message request) {
        role.getNChatManager().processShowInfo(request);
    }

    private static void processChatHistoryRecords(GameRole role, Message request) {
        role.getNChatManager().ProcessHistoryRecords(request);
    }

    private static void processJingJiChangPanel(GameRole role, Message request) {
        role.getNJingJiChangManager().processJingJiChangPanel(request);
    }


    private static void processJingJiRecords(GameRole role, Message request) {
        role.getNJingJiChangManager().processJingJiRecords(request);
    }

    private static void processBuyCount(GameRole role, Message request) {
        role.getNJingJiChangManager().processBuyCount(request);
    }

    private static void processJingJiRank(GameRole role, Message request) {
        role.getNJingJiChangManager().processJingJiRank(request);
    }

    private static void processJingJiStart(GameRole role, Message request) {
        role.getNJingJiChangManager().processJingJiStart(request);
    }

    private static void processJingJiEnd(GameRole role, Message request) {
        role.getNJingJiChangManager().processJingJiEnd(request);
    }

    private static void processMiaoSha(GameRole role, Message request) {
        role.getNJingJiChangManager().processMiaoSha(request);
    }


    private static void processXianLvTotalLv(GameRole role, Message request) {
        role.getNTaskAdvancedManager().processXianLvTotalLv(request);
    }

    //113 存储经验
    private static void storageExp(GameRole role, Message request) {
        role.getSectionManager().processStorageExp(request);
    }

    //230 成长项列表
    private static void growList(GameRole role, Message request) {
        role.getGrowManager().processGrowList(request);
    }

    //231 成长项激活
    private static void growActive(GameRole role, Message request) {
        role.getGrowManager().processGrowActive(request);
    }

    //232 成长项上阵
    private static void growGo(GameRole role, Message request) {
        role.getGrowManager().processGrowGo(request);
    }

    //233 成长项改名
    private static void growRename(GameRole role, Message request) {
        role.getGrowManager().processGrowRename(request);
    }

    //234 成长项升级
    private static void growLevelUp(GameRole role, Message request) {
        role.getGrowManager().processGrowLevelUp(request);
    }

    //235 成长项飞升
    private static void growFlyUp(GameRole role, Message request) {
        role.getGrowManager().processFlyUp(request);
    }

    //236 成长项资质
    private static void growAptitude(GameRole role, Message request) {
        role.getGrowManager().processAptitude(request);
    }

    //237 成长项洗炼技能锁定
    private static void growWashLock(GameRole role, Message request) {
        role.getGrowManager().processWashLock(request);
    }

    //238 成长项洗炼技能解锁
    private static void growWashUnlock(GameRole role, Message request) {
        role.getGrowManager().processWashUnlock(request);
    }

    //239 成长项洗炼技能
    private static void growWashing(GameRole role, Message request) {
        role.getGrowManager().processWashing(request);
    }

    //240 成长项洗炼技能更换
    private static void growWashChange(GameRole role, Message request) {
        role.getGrowManager().processWashChange(request);
    }

    //241 成长项通灵升阶
    private static void growPsychicLevelUp(GameRole role, Message request) {
        role.getGrowManager().processGrowPsychicLevelUp(request);
    }

    //242 成长项通灵技能
    private static void growPsychicSkill(GameRole role, Message request) {
        role.getGrowManager().processGrowPsychicSkill(request);
    }

    //243 成长项通灵丹
    private static void growPsychicPill(GameRole role, Message request) {
        role.getGrowManager().processGrowPsychicPill(request);
    }

    //244 成长项通灵装备
    private static void growPsychicEquip(GameRole role, Message request) {
        role.getGrowManager().processGrowPsychicEquip(request);
    }

    //245 成长项兽魂升阶
    private static void growSoulLevelUp(GameRole role, Message request) {
        role.getGrowManager().processGrowSoulLevelUp(request);
    }

    //246 成长项兽魂技能
    private static void growSoulSkill(GameRole role, Message request) {
        role.getGrowManager().processGrowSoulSkill(request);
    }

    //247 成长项兽魂丹
    private static void growSoulPill(GameRole role, Message request) {
        role.getGrowManager().processGrowSoulPill(request);
    }

    //248 成长项兽魂装备
    private static void growSoulEquip(GameRole role, Message request) {
        role.getGrowManager().processGrowSoulEquip(request);
    }

    //249 成长项升阶3
    private static void growLevelUp3(GameRole role, Message request) {
        role.getGrowManager().processGrowLevelUp3(request);
    }

    //250 成长项技能3
    private static void growSkill3(GameRole role, Message request) {
        role.getGrowManager().processGrowSkill3(request);
    }

    //251 成长项丹3
    private static void growPill3(GameRole role, Message request) {
        role.getGrowManager().processGrowPill3(request);
    }

    //252 成长项装备3
    private static void growEquip3(GameRole role, Message request) {
        role.getGrowManager().processGrowEquip3(request);
    }

    //253 成长项升阶4
    private static void growLevelUp4(GameRole role, Message request) {
        role.getGrowManager().processGrowLevelUp4(request);
    }

    //254 成长项技能4
    private static void growSkill4(GameRole role, Message request) {
        role.getGrowManager().processGrowSkill4(request);
    }

    //255 成长项丹4
    private static void growPill4(GameRole role, Message request) {
        role.getGrowManager().processGrowPill4(request);
    }

    //256 成长项装备4
    private static void growEquip4(GameRole role, Message request) {
        role.getGrowManager().processGrowEquip4(request);
    }

    //257 成长项升星
    private static void growStarUp(GameRole role, Message request) {
        role.getGrowManager().processGrowStarUp(request);
    }

    //260 跨服副本队伍列表
    private static void teamCrossList(GameRole role, Message request) {
        role.getTeamManager().processCrossList(request);
    }

    //261 跨服组队创建队伍
    private static void teamCrossCreate(GameRole role, Message request) {
        role.getTeamManager().processCrossCreate(request);
    }

    //262 跨服组队加入队伍
    private static void teamCrossJoin(GameRole role, Message request) {
        role.getTeamManager().processCrossJoin(request);
    }

    //263 跨服组队信息
    private static void teamCrossInfo(GameRole role, Message request) {
        role.getTeamManager().processCrossInfo(request);
    }

    //264 跨服组队踢人
    private static void teamCrossKick(GameRole role, Message request) {
        role.getTeamManager().processCrossKick(request);
    }

    //265 跨服组队退出
    private static void teamCrossExit(GameRole role, Message request) {
        role.getTeamManager().processCrossExit(request);
    }

    //266 跨服组队开始
    private static void teamCrossStart(GameRole role, Message request) {
        role.getTeamManager().processCrossStart(request);
    }

    //267 跨服组队结束
    private static void teamCrossEnd(GameRole role, Message request) {
        role.getTeamManager().processCrossEnd(request);
    }

    //268 跨服副本通关状态
    private static void teamCrossStageState(GameRole role, Message request) {
        role.getTeamManager().processCrossStageState(request);
    }

    //269 生死劫组队列表
    private static void teamLaddList(GameRole role, Message request) {
        role.getTeamManager().processLaddList(request);
    }

    //270 生死劫创建队伍
    private static void teamLaddCreate(GameRole role, Message request) {
        role.getTeamManager().processLaddCreate(request);
    }

    //271 生死劫加入队伍
    private static void teamLaddJoin(GameRole role, Message request) {
        role.getTeamManager().processLaddJoin(request);
    }

    //272 生死劫队伍信息
    private static void teamLaddInfo(GameRole role, Message request) {
        role.getTeamManager().processLaddTeamInfo(request);
    }

    //273 生死劫队长踢人
    private static void teamLaddKick(GameRole role, Message request) {
        role.getTeamManager().processLaddTeamKick(request);
    }

    //274 生死劫退出队伍
    private static void teamLaddExit(GameRole role, Message request) {
        role.getTeamManager().processLaddTeamExit(request);
    }

    //275 生死劫开始战斗
    private static void teamLaddStart(GameRole role, Message request) {
        role.getTeamManager().processLaddTeamStart(request);
    }

    //276 生死劫战斗结束
    private static void teamLaddEnd(GameRole role, Message request) {
        role.getTeamManager().processLaddTeamEnd(request);
    }

    //277生死劫通关状态
    private static void teamLaddStageState(GameRole role, Message request) {
        role.getTeamManager().processLaddStageState(request);
    }

    //278生死劫查看记录
    private static void teamLaddRecord(GameRole role, Message request) {
        role.getTeamManager().processLaddRecord(request);
    }

    //279生死劫一键扫荡
    private static void teamLaddSweep(GameRole role, Message request) {
        role.getTeamManager().processLaddSweep(request);
    }

    //280生死劫开启宝箱
    private static void teamLaddTreasureBox(GameRole role, Message request) {
        role.getTeamManager().processLaddTreasureBox(request);
    }
}


