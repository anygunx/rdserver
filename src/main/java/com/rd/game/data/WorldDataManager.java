package com.rd.game.data;

import com.alibaba.fastjson.JSON;
import com.rd.bean.drop.DropData;
import com.rd.dao.WorldDao;
import com.rd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 世界数据管理
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月12日下午4:36:21
 */
public class WorldDataManager {

    private List<TownSoulTurntableRecord> townSoulTurntableRecordList;

    private WorldDao worldDao;

    public WorldDataManager() {
        this.worldDao = new WorldDao();
    }

    public void init() {
        String record = this.worldDao.getTownSoulTurntableRecord();
        if (StringUtil.isEmpty(record)) {
            townSoulTurntableRecordList = new ArrayList<>();
        } else {
            townSoulTurntableRecordList = JSON.parseArray(record, TownSoulTurntableRecord.class);
        }
    }

    public List<TownSoulTurntableRecord> getTownSoulTurntableRecordList() {
        return this.townSoulTurntableRecordList;
    }

    private String getTownSoulTurntableRecordListJson() {
        return JSON.toJSONString(this.townSoulTurntableRecordList);
    }

    public void addRecord(String name, DropData reward) {
        townSoulTurntableRecordList.add(new TownSoulTurntableRecord(name, reward));
        if (townSoulTurntableRecordList.size() > 3) {
            townSoulTurntableRecordList.remove(0);
        }
    }

    public void saveTownSoulTurntableRecord() {
        worldDao.updateTownsoulturntablerecord(getTownSoulTurntableRecordListJson());
    }
}
