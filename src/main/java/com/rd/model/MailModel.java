package com.rd.model;

import com.google.common.collect.ImmutableMap;
import com.rd.bean.drop.DropData;
import com.rd.model.data.MailRewardModelData;
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

/**
 * 邮件奖励数据模型
 *
 * @author lwq
 */
public class MailModel {

    private static Logger logger = Logger.getLogger(MailModel.class);
    private static final String MAILREWARD_PATH = "gamedata/mailReward.xml";
    private static final String MAILREWARD_NAME = "MailModel";

    private static Map<Short, MailRewardModelData> mailRewardMap;

    public static void loadData(String path) {
        loadMailReward(path);
    }

    private static void loadMailReward(String path) {

        final File file = new File(path + MAILREWARD_PATH);

        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {

                try {
                    Map<Short, MailRewardModelData> temp = new HashMap<>();

                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "logic");

                    for (Element ele : elements) {
                        short id = Byte.valueOf(XmlUtils.getAttribute(ele, "id"));
                        String title = XmlUtils.getAttribute(ele, "title");
                        String content = XmlUtils.getAttribute(ele, "content");
                        List<DropData> dropDatas = StringUtil.getRewardDropList(XmlUtils.getAttribute(ele, "reward"));
                        MailRewardModelData data = new MailRewardModelData(id, title, content, dropDatas);
                        temp.put(data.getId(), data);
                    }

                    mailRewardMap = ImmutableMap.copyOf(temp);
                } catch (Exception e) {
                    logger.error("加载邮件奖励模型数据出错...", e);
                }

            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return MAILREWARD_NAME;
            }


        };

        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static MailRewardModelData getMailRewardModelData(short id) {
        return mailRewardMap.get(id);
    }

}
