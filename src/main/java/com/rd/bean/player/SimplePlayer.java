package com.rd.bean.player;

import com.rd.define.GameDefine;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class SimplePlayer implements ISimplePlayer {
    /**
     * id
     **/
    protected int id = 0;
    /**
     * 名字
     **/
    protected String name = "";
    /**
     * 头像
     **/
    protected byte head = 0;
    /**
     * 转生
     **/
    protected short rein = 0;
    /**
     * 等级
     **/
    protected short level = 1;
    /**
     * vip经验
     **/
    protected int vip = 0;
    /**
     * 战斗力
     **/
    protected long fighting;

    //所在服ID
    protected short serverId;

    public short getServerId() {
        return serverId;
    }

    public void setServerId(short serverId) {
        this.serverId = serverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    /**
     * 获取玩家等级
     * 包含转生数据
     *
     * @return
     */
    public int getLevelWithRein() {
        return level < GameDefine.REIN_LV ? level : rein * GameDefine.REIN_LV_STEP + GameDefine.REIN_LV;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public long getFighting() {
        return fighting;
    }

    public void setFighting(long fighting) {
        this.fighting = fighting;
    }

    public short getRein() {
        return rein;
    }

    public void setRein(int rein) {
        this.rein = (short) rein;
    }

    public void addRein() {
        this.rein++;
    }

    public void getSimpleMessage(Message message) {
        getBaseSimpleMessage(message);
        message.setInt(vip);
        message.setLong(fighting);
    }

    public void getBaseSimpleMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setByte(head);
        message.setShort(level);
    }


    public void getSimpleMessageNew(Message message) {
        getBaseSimpleMessageNew(message);
        message.setByte(vip);

    }

    public void getBaseSimpleMessageNew(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setInt((int) fighting);
        message.setByte(head);
        message.setShort(level);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(StringUtil.COMMA)
                .append(name).append(StringUtil.COMMA)
                .append(rein).append(StringUtil.COMMA)
                .append(level).append(StringUtil.COMMA)
                .append(vip).append(StringUtil.COMMA)
                .append(fighting);
        return sb.toString();
    }

    public static SimplePlayer createWithString(String str) {
        String[] array = str.split(",");
        SimplePlayer simplePlayer = new SimplePlayer();
        simplePlayer.initWithString(array);
        return simplePlayer;
    }

    public void initWithString(String[] params) {
        setId(Integer.valueOf(params[0]));
        setName(params[1]);
        setRein(0);
        setLevel(Short.valueOf(params[2]));
        setVip(Integer.valueOf(params[3]));
        setFighting(Integer.valueOf(params[4]));
    }

    public void init(Player player) {
        setId(player.getId());
        setName(player.getName());
        setRein(player.getRein());
        setLevel(player.getLevel());
        setVip(player.getVip());
        if (player.getFighting() == 0)
            player.updateFighting();
        setFighting(player.getFighting());
        setServerId(player.getServerId());
    }

    public boolean lvValidate(int needLv) {
        return needLv <= GameDefine.REIN_LV ?
                level >= needLv :
                rein >= needLv / 10 - GameDefine.REIN_LV / 10;
    }

    /**
     * 获取包含转生的整数等级
     *
     * @return
     */
    public short getLvConvert() {
        if (rein > 0)
            return (short) (rein * 10 + GameDefine.REIN_LV);
        if (level >= GameDefine.REIN_LV)
            return GameDefine.REIN_LV;
        int lv = level / 10 * 10;
        if (lv < 1)
            lv = 1;
        return (short) lv;
    }

    public void encodeName() {
        try {
            this.name = URLEncoder.encode(this.name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void decodeName() {
        try {
            this.name = URLDecoder.decode(this.name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到战斗外形消息
     *
     * @param message
     */
    public void getFightAppearMessage(Message message) {

    }
}
