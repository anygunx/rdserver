package com.rd.activity.data;


import com.rd.model.data.DialModelData;
import org.w3c.dom.Element;

/**
 * 转盘数据
 *
 * @author Created by U-Demon on 2016年12月27日 下午2:31:54
 * @version 1.0.0
 */
public class DialLogicData extends BaseActivityLogicData {
    private DialModelData modelData;

    @Override
    public String getKey() {
        return String.valueOf(modelData.getId());
    }

    @Override
    public void loadData(Element root) {
        modelData = DialModelData.create(root);
    }

    public int getId() {
        return modelData.getId();
    }

    public DialModelData getModelData() {
        return modelData;
    }

}
