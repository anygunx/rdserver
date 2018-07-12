package com.rd.define;

public enum NShopType {

    SHOP_EQUI(1, "装备商店"),
    SHOP_GUANGH(2, ""),
    SHOP_CLUB(3, "帮会"),
    SHOP_CLUB_REWARD(4, ""),
    SHOP_MONEY(5, ""),
    SHOP_GOLD(6, ""),
    SHOP_PET(7, ""),
    SHOP_XIANLV(8, ""),
    SHOP_SKIN(9, ""),
    SHOP_PIFU(10, ""),
    SHOP_FRIEND(11, ""),
    SHOP_WEIWANG(12, ""),
    SHOP_PVP(13, ""),
    SHOP_PVP_REWARD(14, ""),
    SHOP_HONGSONG(15, ""),
    SHOP_DATI(16, ""),
    SHOP_GEREN(17, ""),
    SHOP_QUANMIN(18, ""),
    SHOP_SHENGSI(19, ""),
    SHOP_CAILIAO(20, "材料副本商城");

    private int id;

    private String name;

    NShopType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static NShopType getType(int id) {
        for (NShopType type : NShopType.values()) {
            if (type.id == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
