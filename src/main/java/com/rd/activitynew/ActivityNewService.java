package com.rd.activitynew;

import com.rd.activitynew.config.ActivityConfig;
import com.rd.activitynew.data.ActivityNewData;
import com.rd.activitynew.data.ActivityNewGroupData;
import com.rd.activitynew.data.ActivityNewOpenData;
import com.rd.activitynew.info.IActivityInfo;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 新活动服务
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月2日下午2:13:28
 */
public class ActivityNewService {

    private static Logger logger = Logger.getLogger(ActivityNewService.class.getName());

    private static Map<Byte, ActivityNewOpenData> activityOpenMap;
    private static Map<Byte, ActivityNewGroupData> activityGroupMap;

    public static Map<Byte, ActivityNewOpenData> getActivityOpenMap() {
        return activityOpenMap;
    }

    public static Map<Byte, ActivityNewGroupData> getActivityGroupMap() {
        return activityGroupMap;
    }

    public static void init(String path) {
        loadActivityNewOpenData(path);
        loadActivityNewGroupData(path);
        ActivityConfig.init(path);
    }

    private static void loadActivityNewOpenData(String path) {
        final File file = new File(path, "gamedata/activityNew.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ActivityNewOpenData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "group");
                    for (int i = 0; i < elements.length; ++i) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        String startTimeStr = XmlUtils.getAttribute(elements[i], "startTime");
                        String endTimeStr = XmlUtils.getAttribute(elements[i], "endTime");
                        long startTime = 0;
                        long endTime = DateUtil.parseDataTime(endTimeStr).getTime();
                        if (startTimeStr.startsWith("server_")) {
                            int day = Integer.parseInt(startTimeStr.substring(7));
                            startTime = GameDefine.SERVER_CREATE_TIME + day * DateUtil.DAY;
                        } else {
                            startTime = DateUtil.parseDataTime(startTimeStr).getTime();
                        }
                        if (startTime < endTime) {
                            ActivityNewOpenData data = new ActivityNewOpenData(id, startTime, endTime);
                            tmpMap.put(data.getId(), data);
                        }
                    }
                    activityOpenMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载新活动开启数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "activityNew";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadActivityNewGroupData(String path) {
        final File file = new File(path, "gamedata/activityNewGroup.xml");
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, ActivityNewGroupData> tmpMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "group");
                    for (int i = 0; i < elements.length; ++i) {
                        byte id = Byte.parseByte(XmlUtils.getAttribute(elements[i], "id"));
                        Map<Byte, ActivityNewData> actMap = new HashMap<>();
                        Element[] childs = XmlUtils.getChildrenByName(elements[i], "activity");
                        long startTime = activityOpenMap.get(id).getStartTime();
                        for (int j = 0; j < childs.length; ++j) {
                            byte actId = Byte.parseByte(XmlUtils.getAttribute(childs[j], "id"));
                            byte configId = Byte.parseByte(XmlUtils.getAttribute(childs[j], "config"));
                            int keepDay = Integer.parseInt(XmlUtils.getAttribute(childs[j], "keepDay"));
                            long endTime = startTime + keepDay * DateUtil.DAY;

                            ActivityNewData activityNewData = new ActivityNewData(actId, configId, endTime);
                            actMap.put(activityNewData.getId(), activityNewData);
                        }
                        ActivityNewGroupData group = new ActivityNewGroupData(id, actMap);
                        tmpMap.put(group.getId(), group);
                    }
                    activityGroupMap = tmpMap;
                } catch (Exception e) {
                    logger.error("加载新活动组数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return "activityGroup";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static Message getActivityOpenMessage(GameRole role) {
        Map<Byte, List<Byte>> activityMap = new HashMap<>();
        long currTime = System.currentTimeMillis();
        for (ActivityNewOpenData openData : activityOpenMap.values()) {
            if (openData.getStartTime() < currTime && currTime < openData.getEndTime()) {
                ActivityNewGroupData activityNewGroupData = activityGroupMap.get(openData.getId());
                for (ActivityNewData activityNewData : activityNewGroupData.getActivityMap().values()) {
                    if (currTime < activityNewData.getEndTime()) {
                        if (activityMap.containsKey(openData.getId())) {
                            activityMap.get(openData.getId()).add(activityNewData.getId());
                        } else {
                            activityMap.put(openData.getId(), new ArrayList<Byte>(activityNewData.getId()));
                        }
                    }
                }
            }
        }

        Message message = new Message(MessageCommand.GAME_ACTIVITY_NEW_MESSAGE);
        message.setByte(activityMap.size());
        for (Entry<Byte, List<Byte>> entry : activityMap.entrySet()) {
            message.setByte(entry.getKey());
            message.setByte(entry.getValue().size());
            for (byte id : entry.getValue()) {
                message.setByte(id);
                IActivityInfo info = EActivityNewType.getType(id).getActivityInfo();
                info.getMessage(message, entry.getKey(), role);
            }
        }
        return message;
    }

    public static String getKey(byte groupId) {
        if (activityOpenMap.containsKey(groupId)) {
            return DateUtil.formatShortDate(activityOpenMap.get(groupId).getStartTime()) + groupId;
        } else {
            return "";
        }
    }
}