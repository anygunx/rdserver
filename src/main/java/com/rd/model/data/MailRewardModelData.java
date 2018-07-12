package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 邮件奖励数据
 *
 * @author lwq
 */
public class MailRewardModelData {

    private final short id;
    private final String title;
    private final String content;
    private final List<DropData> dropDatas;

    public short getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<DropData> getDropDatas() {
        return dropDatas;
    }

    public MailRewardModelData(short id, String title, String content, List<DropData> dropDatas) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.dropDatas = dropDatas;
    }


}
