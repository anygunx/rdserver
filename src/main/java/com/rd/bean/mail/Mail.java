package com.rd.bean.mail;

import com.alibaba.fastjson.JSON;
import com.rd.bean.drop.DropData;
import com.rd.define.GameDefine;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件实体
 *
 * @author Created by U-Demon on 2016年11月7日 下午2:14:23
 * @version 1.0.0
 */
public class Mail {

    //邮件ID
    private int id;

    //发送时间
    private String sendTime;

    //标题
    private String title;

    //内容
    private String content;

    //邮件状态
    private byte state;

    //附件
    private List<DropData> atta = new ArrayList<>();

    //邮件类型
    private short type;

    public void initState() {
        //发送时间
        this.sendTime = DateUtil.formatDateTime(System.currentTimeMillis());
        //邮件状态
        if (atta.size() > 0)
            state = GameDefine.MAIL_STATE_UNREWARD + GameDefine.MAIL_STATE_UNREAD;
        else
            state = GameDefine.MAIL_STATE_REWARDED + GameDefine.MAIL_STATE_UNREAD;
    }

    /**
     * 读取邮件后修改状态
     */
    public void read() {
        if (state % 10 == GameDefine.MAIL_STATE_UNREAD)
            state += (GameDefine.MAIL_STATE_READED - GameDefine.MAIL_STATE_UNREAD);
    }

    /**
     * 领取邮件后修改状态
     */
    public void reward() {
        state = GameDefine.MAIL_STATE_REWARDED + GameDefine.MAIL_STATE_READED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = DateUtil.formatDateTime(sendTime);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public List<DropData> getAtta() {
        return atta;
    }

    public void setAtta(List<DropData> atta) {
        this.atta = atta;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getAttrJson() {
        return JSON.toJSONString(atta);
    }

    public void setAtta(String data) {
        if (!StringUtil.isEmpty(data)) {
            this.atta = JSON.parseArray(data, DropData.class);
        }
    }

    public void getMessage(Message message) {
        message.setInt(id);
        message.setString(sendTime);
        message.setString(title);
        message.setString(content);
        message.setByte(state);
        message.setByte(atta.size());
        for (DropData data : atta) {
            data.getMessage(message);
        }
    }

    public Mail clone() {
        Mail mail = new Mail();
        mail.id = this.id;
        mail.sendTime = this.sendTime;
        mail.title = this.title;
        mail.content = this.content;
        mail.state = this.state;
        mail.type = this.type;
        mail.atta = this.atta;
        return mail;
    }

}
