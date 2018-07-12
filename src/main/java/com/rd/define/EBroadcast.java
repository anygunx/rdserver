package com.rd.define;

/**
 * 广播类型枚举
 *
 * @author Created by U-Demon on 2016年11月8日 下午1:41:41
 * @version 1.0.0
 */
public enum EBroadcast {
    SERVER_CLOSE(-1),       // -1 服务器即将关闭
    SYSTEM(0),        // 0 系统消息
    FirstPay(1),        // 1 首冲
    VipLevelUp(2),        // 2 VIP更新
    EnterGame(3),        // 3 NB玩家上线
    //    BlessWeaponUp   (4),        // 4 神兵培养
//    BlessClothesUp  (5),        // 5 神装培养
//    BlessWingUp     (6),        // 6 仙羽培养
//    EquipStrengthen (7),        // 7 装备强化
//    BossCall		(8),		// 8  召唤BOSS
//    BossKill		(9),		// 9  击杀BOSS
    GodArtifactUp(10),       // 10 神器升级
    //    MagicUp         (11),       // 11 法宝培养
    GangCreate(12),       // 12 帮派创建
    XUNBAO(13),        // 13 寻宝1转以上装备
    ORANGE_UP(14),        // 14 30级开始橙装升级
    LADDER_GOLD(15),        // 15 JJC到黄金段位
    LADDER_DIAMOND(16),        // 16 JJC到钻石段位
    BOSS_ORANGE(17),        // 17 击杀BOSS获得橙装
    SHENQI_JIHUO(18),        // 18 神器激活
    LIANHUA(19),        // 19 炼化
    //    DUJIE_FUCHOU	(20),		// 20 渡劫复仇成功
    ORANGE_MIX(21),        // 21 橙装合成
    //    WISHING_WELL	(22),		// 22 许愿池
    GANG_CHAMPION(23),        // 23 帮派战冠军
    GANG_PERSONAL(24),        // 24 帮派战个人
    //    FEST_ZHUANPAN	(25),		// 25 节日转盘
//    PET_LOTTERY		(26),		// 26 宠物抽奖
    REBATE(27),        // 27 特惠礼包
    PAY_CUMULATE(28),        // 28 特惠礼包
    ESCORT(29),       // 29 高级矿
    TURN_TABLE(30),   //30 元宝王者
    KAM_PO2(31),      //31  鉴宝2
    NOREPEAT_TURNTABLE1(32),    //32 至尊转盘1
    NOREPEAT_TURNTABLE2(33),    //33 至尊转盘2
    LIMIT_LIMIT_LIMIT(34),        //34 特卖公告
    SEVEN_DAY(35),                //35 七日狂欢

    TEAM_CROSS(36),        //跨服组队
    TEAM_LADD(37),        //生死劫组队
    ;

    private final byte id;

    EBroadcast(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
