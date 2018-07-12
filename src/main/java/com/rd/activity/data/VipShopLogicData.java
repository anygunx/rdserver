package com.rd.activity.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 限时商城
 *
 * @author Created by U-Demon on 2016年12月27日 下午2:32:32
 * @version 1.0.0
 */
public class VipShopLogicData extends BaseActivityLogicData {

    private int id;

    private byte shopType;

    private List<DropData> goods;

    private DropData price;

    private Map<Integer, Integer> vip = new HashMap<>();

    private int bagGrid;

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        shopType = Byte.valueOf(XmlUtils.getAttribute(root, "shopType"));
        price = StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "price")).get(0);
        goods = ImmutableList.copyOf(StringUtil.getRewardDropList(XmlUtils.getAttribute(root, "goods")));
        String vipStr = XmlUtils.getAttribute(root, "vip");
        if (vipStr != null) {
            String[] vipStrs = vipStr.split(";");
            for (String str : vipStrs) {
                String[] ss = str.split(",");
                if (ss.length == 2) {
                    vip.put(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]));
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DropData> getGoods() {
        return goods;
    }

    public byte getShopType() {
        return shopType;
    }

    public void setShopType(byte shopType) {
        this.shopType = shopType;
    }

    public DropData getPrice() {
        return price;
    }

    public void setPrice(DropData price) {
        this.price = price;
    }

    public Map<Integer, Integer> getVip() {
        return vip;
    }

    public void setVip(Map<Integer, Integer> vip) {
        this.vip = vip;
    }

    public int getBagGrid() {
        return bagGrid;
    }

    public void setBagGrid(int bagGrid) {
        this.bagGrid = bagGrid;
    }
}
