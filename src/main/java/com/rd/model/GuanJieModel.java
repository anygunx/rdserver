package com.rd.model;

import com.rd.define.EAttrType;
import com.rd.model.data.GuanJieData;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

/**
 * @author lwq
 */
public class GuanJieModel {

    private static Logger log = Logger.getLogger(GuanJieModel.class.getName());

    private static Map<Byte, GuanJieData> guanJieDataMap;

    private static Map<Integer, GuanJieData> guanJieNeedMap;

    public static void loadData(String Path) {
        loadGuanJie(Path);
    }

    private static void loadGuanJie(String Path) {

        final File file = new File(Path + "gamedata/guanzhi.xml");

        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Byte, GuanJieData> tempMap = new HashMap<>();
                    Map<Integer, GuanJieData> tempNeedMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "data");
                    for (int i = 0; i < elements.length; i++) {
                        GuanJieData guanJieData = new GuanJieData();
                        guanJieData.setId(Byte.parseByte(XmlUtils.getAttribute(elements[i], "id")));
                        guanJieData.setLevel(Byte.parseByte(XmlUtils.getAttribute(elements[i], "level")));
                        guanJieData.setAttr(EAttrType.getAttr(elements[i]));
                        guanJieData.setBossinch(Integer.parseInt(XmlUtils.getAttribute(elements[i], "bossinch")));
                        guanJieData.setNeed(Integer.parseInt(XmlUtils.getAttribute(elements[i], "need")));
                        guanJieData.setIncome(Integer.parseInt(XmlUtils.getAttribute(elements[i], "income")));
                        tempMap.put(guanJieData.getId(), guanJieData);
                        tempNeedMap.put(guanJieData.getNeed(), guanJieData);
                    }
                    guanJieDataMap = tempMap;
                    guanJieNeedMap = tempNeedMap;
                } catch (Exception e) {
                    log.error("加载官阶模型数据出错...");
                    e.printStackTrace();
                }

            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return "officer";
            }


        };

        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);

    }

    public static Map<Byte, GuanJieData> getGuanJieDataMap() {
        return guanJieDataMap;
    }

    public static GuanJieData getGuanJieData(byte id) {
        return guanJieDataMap.get(id);
    }

    public static GuanJieData getGuanJieData(Integer need) {
        return guanJieNeedMap.get(need);
    }

    public static GuanJieData getData(int weiWang) {

        Set<Integer> needSet = guanJieNeedMap.keySet();

        if (needSet.contains(weiWang)) {

            return guanJieNeedMap.get(weiWang);

        } else {

            List<Integer> needList = new ArrayList<>();
            needList.add(weiWang);

            for (Integer need : needSet) {
                needList.add(need);
            }
            Collections.sort(needList);

            int index = needList.indexOf(weiWang);

            if (index == 0) {

                return guanJieNeedMap.get(0);

            } else {

                return guanJieNeedMap.get(needList.get(index - 1));
            }

        }

    }
}
