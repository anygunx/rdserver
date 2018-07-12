package com.rd.bean.player;

import com.rd.net.message.Message;

public class AppearCharacter {

    public byte occ = 0;

    //翅膀外形
    public short wingShow = 0;
    //坐骑外形
    public short mountStage = 0;
    public byte mountS = 0;
    //武器外形
    public short weapon = 0;
    public byte weaponQ = 0;
    public byte weaponS = 0;
    //衣服外形
    public short clothes = 0;
    public byte clothesQ = 0;
    public byte clothesS = 0;
    //称号
    public short title = 0;

    public AppearCharacter() {

    }

    AppearCharacter(Character cha) {
//		this.occ = cha.getOccupation();
//		this.wingShow = cha.getWingShow();
//		this.mountStage = cha.getMountStage();
//		//武器外形
//		Equip equip = cha.getEquipList().get(EquipDefine.EQUIP_TYPE_WEAPON);
//		if (equip != null) {
//			this.weapon = equip.getG();
//			this.weaponQ = equip.getQ();
//		}
//		this.weaponS = cha.getWeaponShow();
//		//衣服外形
//		equip = cha.getEquipList().get(EquipDefine.EQUIP_TYPE_CLOTHES);
//		if (equip != null) {
//			this.clothes = equip.getG();
//			this.clothesQ = equip.getQ();
//		}
//		this.clothesS = cha.getArmorShow();
//		this.mountS = cha.getMountShow();
//		//称号
//		this.title = cha.getTitle();
    }

    public void getMsg(Message msg) {
        msg.setByte(wingShow);
        msg.setByte(mountStage);
        msg.setShort(weapon);
        msg.setByte(weaponQ);
        msg.setShort(clothes);
        msg.setByte(clothesQ);
        msg.setByte(title);
        msg.setByte(occ);
        msg.setByte(weaponS);
        msg.setByte(clothesS);
        msg.setByte(mountS);
    }

}
