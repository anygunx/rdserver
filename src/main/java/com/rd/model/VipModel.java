package com.rd.model;

import com.rd.define.EVipType;
import com.rd.model.data.VipModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * VIP
 *
 * @author Created by U-Demon on 2016年12月21日 下午1:16:45
 * @version 1.0.0
 */
public class VipModel {

    private static Logger logger = Logger.getLogger(VipModel.class);

    //VIP数据
    private static final String PATH = "gamedata/vip.xml";
    private static final String NAME = "vipModel";
    private static List<VipModelData> vipList;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadVip(path);
    }

    public static void loadVip(String path) {
        final File file = new File(path, PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    List<VipModelData> tmpList = new ArrayList<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "levelData");
                    for (int i = 0; i < elements.length; i++) {
                        VipModelData data = new VipModelData();
                        data.setLevel(Integer.valueOf(XmlUtils.getAttribute(elements[i], "level")));
                        data.setCost(Integer.valueOf(XmlUtils.getAttribute(elements[i], "cost")));
                        data.setRewards(StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "gainId")));
                        data.setIsNew(Integer.valueOf(XmlUtils.getAttribute(elements[i], "isnew")));
                        data.setTitle(XmlUtils.getAttribute(elements[i], "title"));
                        data.setContent(XmlUtils.getAttribute(elements[i], "context"));
                        data.setWeals(new HashMap<EVipType, Integer>());
                        String wealStr = XmlUtils.getAttribute(elements[i], "weals");
                        String[] weals = wealStr.split("#");
                        for (String weal : weals) {
                            String[] wealDatas = weal.split(",");
                            if (wealDatas.length == 2) {
                                EVipType type = EVipType.getType(Integer.valueOf(wealDatas[0]));
                                if (type != null) {
                                    data.getWeals().put(type, Integer.valueOf(wealDatas[1]));
                                }
                            }
                        }
                        data.setPvpDamage(Integer.valueOf(XmlUtils.getAttribute(elements[i], "vipindamage")));
                        tmpList.add(data);
                    }
                    vipList = tmpList;
                } catch (Exception e) {
                    logger.error("加载VIP模型数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    /**
     * 通过VIP经验获取VIP等级
     *
     * @param vipExp
     * @return
     */
    public static byte getVipLv(int vipExp) {
        for (byte i = 0; i < vipList.size(); i++) {
            if (vipExp < vipList.get(i).getCost())
                return i;
        }
        return (byte) vipList.size();
    }

    /**
     * 获取VIP模型数据
     *
     * @param lv
     * @return
     */
    public static VipModelData getModelByLv(int lv) {
        if (lv < 1)
            return null;
        if (lv > vipList.size())
            return null;
        return vipList.get(lv - 1);
    }

    /**
     * 获取VIP对应的特权值
     *
     * @param vipLv
     * @return    -1表示不包含该特权
     */
    public static int getVipWeal(int vipLv, EVipType type) {
        VipModelData vipModel = VipModel.getModelByLv(vipLv);
        if (vipModel == null)
            return -1;
        if (!vipModel.getWeals().containsKey(type))
            return -1;
        return vipModel.getWeals().get(type);
    }

}
