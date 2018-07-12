package com.rd.bean.player;

import com.rd.bean.rank.PlayerRank;
import com.rd.define.EAttrType;
import com.rd.net.message.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 战斗玩家对象
 *
 * @author U-Demon Created on 2017年5月17日 下午1:08:17
 * @version 1.0.0
 */
public class BattlePlayer extends SimplePlayer {

    //外形数据
    private AppearPlayer ap = null;

    //角色属性
    private List<AttrCharacter> acs = null;

    //时间搓
    private long validTime = 0;

    public BattlePlayer() {

    }

    public BattlePlayer(Player player) {
        super.init(player);
        encodeName();

        this.ap = new AppearPlayer();
        this.ap.init(player, -1);

        this.acs = new ArrayList<>();
//		for (Character cha : player.getCharacterList()) {
//			AttrCharacter ac = new AttrCharacter();
//			if (cha.getFighting() == 0) {				
//				cha.updateFighting(player, new int[EAttrType.ATTR_SIZE], 0);
//			}
//			ac.setOcc(cha.getOccupation());
//			ac.setFighting(cha.getFighting());
//			ac.setAttribute(cha.getAttribute());
//			this.acs.add(ac);
//		}
    }

    public void getMsg(Message msg) {
        this.getBaseSimpleMessage(msg);
        this.ap.getMessage(msg);
        if (this.acs == null)
            msg.setByte(0);
        else {
            msg.setByte(this.acs.size());
            for (AttrCharacter ac : this.acs) {
                ac.getMessage(msg);
            }
        }
    }

    public void init(BattlePlayer bp, PlayerRank pr) {
        float rate = pr.fighting * 1.f / bp.fighting;
        this.setId(pr.id);
        this.setName(pr.name);
        this.setHead(pr.head);
        this.setRein(pr.rein);
        this.setLevel(pr.level);
        this.setVip(pr.vip);
        this.setFighting(pr.fighting);
        this.ap = bp.ap;
        this.validTime = bp.validTime;
        this.acs = new ArrayList<>();
        for (AttrCharacter ac : bp.acs) {
            AttrCharacter acCopy = new AttrCharacter();
            acCopy.setFighting((long) (ac.getFighting() * rate));
            acCopy.setOcc(ac.getOcc());
            int[] attrs = new int[EAttrType.ATTR_SIZE];
            for (int i = 0; i < ac.getAttribute().length; i++) {
                attrs[i] = (int) (ac.getAttribute()[i] * rate);
            }
            acCopy.setAttribute(attrs);
            this.acs.add(acCopy);
        }
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

    public long getValidTime() {
        return validTime;
    }

    public void setValidTime(long validTime) {
        this.validTime = validTime;
    }

    public AppearPlayer getAp() {
        return ap;
    }

    public void setAp(AppearPlayer ap) {
        this.ap = ap;
    }

    public List<AttrCharacter> getAcs() {
        return acs;
    }

    public void setAcs(List<AttrCharacter> acs) {
        this.acs = acs;
    }

}
