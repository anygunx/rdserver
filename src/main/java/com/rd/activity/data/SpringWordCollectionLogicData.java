package com.rd.activity.data;

import com.rd.model.data.ExchangeModelData;
import org.w3c.dom.Element;

/**
 * 新春集字
 * Created by XingYun on 2017/1/18.
 */
public class SpringWordCollectionLogicData extends BaseActivityLogicData {
    private ExchangeModelData modelData;

    public ExchangeModelData getModelData() {
        return modelData;
    }

    @Override
    public String getKey() {
        return String.valueOf(modelData.getId());
    }

    @Override
    public void loadData(Element root) {
        modelData = ExchangeModelData.create(root);
    }
}
