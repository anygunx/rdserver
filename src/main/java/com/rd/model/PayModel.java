package com.rd.model;

import com.rd.bean.drop.DropData;
import com.rd.model.data.PayAdditionModelData;
import com.rd.model.data.PayModelData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PayModel {

    private static Logger logger = Logger.getLogger(PayModel.class);

    //充值数据
    private static final String PAY_PATH = "gamedata/pay.xml";
    private static final String PAY_NAME = "payModel";
    private static Map<Integer, PayModelData> payMap;

    //充值附加数据
    private static final String PAY_ADDITION_PATH = "gamedata/payaddition.xml";
    private static final String PAY_ADDITION_NAME = "payadditionModel";
    private static Map<Integer, PayAdditionModelData> payAdditions;

    // 计数枚举
    public enum ECounterType {
        FirstPay,
        OtherPay
    }

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadPay(path);
        loadPayAddition(path);
    }

    private static void loadPayAddition(String path) {
        final File file = new File(path, PAY_ADDITION_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, PayAdditionModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        int id = Integer.valueOf(XmlUtils.getAttribute(elements[i], "id"));
                        int times = Integer.valueOf(XmlUtils.getAttribute(elements[i], "times"));
                        ECounterType counterType = ECounterType.values()[Integer.valueOf(XmlUtils.getAttribute(elements[i], "countType"))];
                        String title = XmlUtils.getAttribute(elements[i], "title");
                        String content = XmlUtils.getAttribute(elements[i], "content");
                        List<DropData> rewardList = StringUtil.getRewardDropList(XmlUtils.getAttribute(elements[i], "reward"));
                        PayAdditionModelData data = new PayAdditionModelData(id, times, counterType, title, content, rewardList);
                        tmpMap.put(id, data);
                    }
                    payAdditions = tmpMap;
                } catch (Exception e) {
                    logger.error("加载充值附加奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return PAY_ADDITION_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void loadPay(String path) {
        final File file = new File(path, PAY_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, PayModelData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "pay");
                    for (int i = 0; i < elements.length; i++) {
                        int rmb = Integer.valueOf(XmlUtils.getAttribute(elements[i], "id"));
                        int diamond = Integer.valueOf(XmlUtils.getAttribute(elements[i], "diamond"));
                        byte type = Byte.valueOf(XmlUtils.getAttribute(elements[i], "type"));
                        Set<Integer> additionList = StringUtil.getIntSet(XmlUtils.getAttribute(elements[i], "additions"), StringUtil.COMMA);
                        PayModelData data = new PayModelData(rmb, diamond, type, additionList);
                        tmpMap.put(data.getRmb(), data);
                    }
                    payMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载充值数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return PAY_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static PayModelData getModel(int rmb) {
        return payMap.get(rmb);
    }

    public static PayAdditionModelData getAdditionModel(int id) {
        return payAdditions.get(id);
    }
}
