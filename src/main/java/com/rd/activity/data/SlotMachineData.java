package com.rd.activity.data;

import com.lg.util.XmlUtils;
import org.w3c.dom.Element;

public class SlotMachineData extends BaseActivityLogicData {

    private byte id;

    private int minrewards;

    private int maxrewards;

    private int price;

    @Override
    public String getKey() {
        return Byte.toString(id);
    }

    @Override
    public void loadData(Element root) {
        id = Byte.parseByte(XmlUtils.getAttribute(root, "id"));
        minrewards = Integer.parseInt(XmlUtils.getAttribute(root, "minrewards"));
        maxrewards = Integer.parseInt(XmlUtils.getAttribute(root, "maxrewards"));
        price = Integer.parseInt(XmlUtils.getAttribute(root, "price"));
    }

    public byte getId() {
        return id;
    }

    public int getMinrewards() {
        return minrewards;
    }

    public int getMaxrewards() {
        return maxrewards;
    }

    public int getPrice() {
        return price;
    }
}
