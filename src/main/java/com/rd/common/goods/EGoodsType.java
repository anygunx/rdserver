package com.rd.common.goods;

import com.rd.enumeration.EEquip;

import java.util.HashMap;
import java.util.Map;

/**
 * 物品类型
 *
 * @author Created by U-Demon on 2016年11月1日 上午10:35:46
 * @version 1.0.0
 */
public enum EGoodsType {
    EQUIP(0, "装备", "equip", EquipCmd.gi()),
    BINDDIAMOND(1, "绑定元宝", "bindGold", DiamondBindCmd.gi()),
    ITEM(2, "物品", "item", ItemCmd.gi()),
    BOX(3, "宝物", "box", BoxCmd.gi()),
    GOLD(4, "绑元", "currency", GoldCmd.gi()),
    DIAMOND(5, "元宝", "diamond", DiamondCmd.gi()),
    EXP(6, "经验", "exp", ExpCmd.gi()),
    //SPIRIT(9, "元神", "yuanshen", SpiritCmd.gi()),
    HONOR(11, "战绩", "honor", HonorCmd.gi()),
    VIP(12, "VIP经验", "vip", VipCmd.gi()),
    ARENA(13, "竞技场点数", "arena", ArenaCmd.gi()),
    POINTS(14, "积分", "points", PointsCmd.gi()),
    SHOW(15, "显示", "show", null),
    DONATE(16, "帮派贡献", "donate", DonateCmd.gi()),
    YUANQI(18, "元气", "yuanqi", YuanQiCmd.gi()),
    TLPOINTS(20, "限时积分", "tlpoints", TLPointsCmd.gi()),
    RSPOINTS(21, "商城积分", "rspoints", RSPointsCmd.gi()),
    DRAGONBALL_PIECE(22, "龙珠碎片", "dragonball", DragonBallCmd.gi()),
    ACHIEVEMENT(23, "成就", "achievement", AchievementCmd.gi()),
    AUCTION_BOX(24, "拍卖宝箱", "auctionbox", AuctionBoxCmd.gi()),
    HEART_SKILL(25, "心法", "xinfa", HeartSkillCmd.gi()),
    ARTIFACT_PIECES(26, "关卡神器碎片", "artifact_pieces", ArtifactPiecesCmd.gi()),
    WEIWANG(27, "威望", "weiwang", WeiWangCmd.gi()),
    COMBINE_RUNE_PIECE(28, "合击符文碎片", "combinerunepiece", CombineRunePieceCmd.gi()),
    COMBINE_RUNE(29, "合击符文", "combinerune", CombineRuneCmd.gi()),
    ZHANWEN(30, "战纹", "zhanwen", ZhanWenCmd.gi()),
    ZHANWEN_JINGHUA(31, "战纹精华", "zhanwen_jinghua", ZhanWenJingHuaCmd.gi()),
    WING_GODS(32, "仙羽装备", "wing_god", WingGodCmd.gi()),
    CARD_ITEM(33, "图鉴材料", "card", CardItemCmd.gi()),
    SUUL_PIECE(34, "灵髓碎片", "lingsui", LingSuiCmd.gi()),
    VOUCHERS(35, "代金券", "daijinquan", VouchersCmd.gi()),
    HOLYLINES(36, "圣纹", "holylines", HolyLinesCmd.gi()),
    MYSTERY_INTEGRAL(37, "秘闻积分", "miwenjifen", MysteryIntegralCmd.gi()),
    QIZHEN_INTEGRAL(38, "奇珍积分", "qizhenjifen", QizhenIntegralCmd.gi()),
    SPIRIT_EQUIP(39, "通灵装备", "spiritEquip", SpiritEquipCmd.gi()),

    EQUIP_PSYCHIC(40, "通灵装备", "growEquip0", new GrowEquipCmd(EEquip.PET_PSYCHIC)),
    EQUIP_SOUL(41, "兽魂装备", "growEquip1", new GrowEquipCmd(EEquip.PET_SOUL)),
    EQUIP_XW(42, "装备2", "growEquip2", new GrowEquipCmd(EEquip.MATE_XW)),
    EQUIP_FZ(43, "装备3", "growEquip3", new GrowEquipCmd(EEquip.MATE_FZ)),
    EQUIP_TN(44, "装备4", "growEquip4", new GrowEquipCmd(EEquip.FAIRY_TN)),
    EQUIP_XQ(45, "装备5", "growEquip5", new GrowEquipCmd(EEquip.FAIRY_XQ)),
    EQUIP_HN(46, "装备6", "growEquip6", new GrowEquipCmd(EEquip.FAIRY_HN)),
    EQUIP_LQ(47, "装备7", "growEquip7", new GrowEquipCmd(EEquip.FAIRY_LQ)),
    EQUIP_MAGIC(48, "装备8", "growEquip8", new GrowEquipCmd(EEquip.MAGIC)),
    EQUIP_WEAPON(49, "装备9", "growEquip9", new GrowEquipCmd(EEquip.WEAPON)),
    EQUIP_WING(50, "装备10", "growEquip10", new GrowEquipCmd(EEquip.WING)),
    EQUIP_MOUNTS(51, "装备11", "growEquip11", new GrowEquipCmd(EEquip.MOUNTS)),;

    public static final Map<Integer, EGoodsType> valueMap = new HashMap<Integer, EGoodsType>() {
        private static final long serialVersionUID = 1L;

        {
            for (EGoodsType type : EGoodsType.values()) {
                put(type.id, type);
            }
        }
    };

    private final int id;
    private final String desc;
    private final String dropName;
    private final IGoodsCmd cmd;

    EGoodsType(int id, String desc, String dropName, IGoodsCmd cmd) {
        this.id = id;
        this.desc = desc;
        this.dropName = dropName;
        this.cmd = cmd;
    }

    public static final EGoodsType getGoodsType(int id) {
        return valueMap.get(id);
    }

    public byte getId() {
        return (byte) id;
    }

    public String getDesc() {
        return desc;
    }

    public String getDropName() {
        return dropName;
    }

    public IGoodsCmd getCmd() {
        return cmd;
    }

}
