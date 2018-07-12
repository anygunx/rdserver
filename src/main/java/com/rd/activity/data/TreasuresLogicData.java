package com.rd.activity.data;

import com.rd.bean.drop.DropData;
import com.rd.define.GameDefine;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TreasuresLogicData extends BaseActivityLogicData {

    public static final long REFRESH_SPACE = 4 * DateUtil.HOUR;


    private byte id;

    private int type;

    private int chance;

    private DropData rewards;

    private DropData price;

    private Map<Integer, Vouchers> vos = new HashMap<>();

    @Override
    public String getKey() {
        return id + "";
    }

    @Override
    public void loadData(Element root) {
        id = Byte.valueOf(XmlUtils.getAttribute(root, "id"));
        type = Integer.valueOf(XmlUtils.getAttribute(root, "type"));
        chance = Integer.valueOf(XmlUtils.getAttribute(root, "diaoluo"));
        rewards = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "reward"));
        price = StringUtil.getRewardDropData(XmlUtils.getAttribute(root, "price"));
        this.loadVouchersData(GameDefine.RES_PATH);
    }

    private void loadVouchersData(String path) {
        final File file = new File(path, "gamedata/mibaoedu.xml");
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
                    Element[] elements = XmlUtils.getChildrenByName(root, "xinfahc");
                    for (Element element : elements) {
                        int id = Integer.valueOf(XmlUtils.getAttribute(element, "id"));
                        int cost = Integer.valueOf(XmlUtils.getAttribute(element, "cost"));
                        DropData rewards = StringUtil.getRewardDropData(XmlUtils.getAttribute(element, "reward"));
                        Vouchers vo = new Vouchers(id, cost, rewards);
                        vos.put(id, vo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "mibaoedu";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public DropData getRewards() {
        return rewards;
    }

    public void setRewards(DropData rewards) {
        this.rewards = rewards;
    }

    public DropData getPrice() {
        return price;
    }

    public void setPrice(DropData price) {
        this.price = price;
    }

    public Map<Integer, Vouchers> getVos() {
        return vos;
    }

    public void setVos(Map<Integer, Vouchers> vos) {
        this.vos = vos;
    }

    public static class Vouchers {
        private int id;
        private int cost;
        private DropData reward;

        public Vouchers(int id, int cost, DropData reward) {
            super();
            this.id = id;
            this.cost = cost;
            this.reward = reward;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public DropData getReward() {
            return reward;
        }

        public void setReward(DropData reward) {
            this.reward = reward;
        }
    }

    public static class BoughtRecord {
        private int id;//商品id(xml里的id)
        private int vouchers;//消耗的代金券
        private int diamond;//消耗的元宝数

        public BoughtRecord(int id, int vouchers, int diamond) {
            super();
            this.id = id;
            this.vouchers = vouchers;
            this.diamond = diamond;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVouchers() {
            return vouchers;
        }

        public void setVouchers(int vouchers) {
            this.vouchers = vouchers;
        }

        public int getDiamond() {
            return diamond;
        }

        public void setDiamond(int diamond) {
            this.diamond = diamond;
        }

    }

}
