package com.rd.bean.player;

import com.rd.define.TextDefine;
import com.rd.net.message.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 竞技场
 * Created by XingYun on 2016/5/19.
 */
public class PlayerArenaRecord {
    private final byte type;
    private final long timestamp;
    private final String name;
    private final byte result;
    private final int changeRank;

    public PlayerArenaRecord(byte type, long timestamp, String name, byte result, int changeRank) {
        this.type = type;
        this.timestamp = timestamp;
        this.name = name;
        this.result = result;
        this.changeRank = changeRank;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append(",")
                .append(timestamp).append(",")
                .append(name).append(",")
                .append(result).append(",")
                .append(changeRank);
        return builder.toString();
    }

    public static PlayerArenaRecord createWithString(String str) {
        String[] params = str.split(",");
        PlayerArenaRecord record = new PlayerArenaRecord(
                Byte.valueOf(params[0]),
                Long.valueOf(params[1]),
                params[2],
                Byte.valueOf(params[3]),
                Integer.valueOf(params[4]));
        return record;
    }

    public byte getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public byte getResult() {
        return result;
    }

    public int getChangeRank() {
        return changeRank;
    }

    public void getMessage(Message message) {
        message.setByte(type);              //        type（n）	byte	战斗类型：0-被攻击，1-攻击
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(timestamp);      //        time（n）	long	战斗时间
        SimpleDateFormat format = new SimpleDateFormat("MM" + TextDefine.MONTH + "dd HH:mm");
        message.setString(format.format(c1.getTime()));
        message.setString(name);            //        name（n）	String	对方名称
        message.setByte(result);            //        result（n）	byte	战斗结果：0-失败，1-胜利
        message.setInt(changeRank);
    }

}
