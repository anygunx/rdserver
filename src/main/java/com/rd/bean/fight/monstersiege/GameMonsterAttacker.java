package com.rd.bean.fight.monstersiege;

import com.google.gson.annotations.SerializedName;
import com.rd.net.message.Message;

import java.util.Comparator;

/**
 * 怪物攻击者
 */
public class GameMonsterAttacker implements IGameMonsterAttacker {
    private int id;
    @SerializedName("n")
    private String name;
    @SerializedName("h")
    private byte head;
    @SerializedName("v")
    private int value;

    public static final Comparator<GameMonsterAttacker> comparator = (o1, o2) -> Integer.valueOf(o1.getValue()).compareTo(o2.getValue());


    public GameMonsterAttacker() {
    }

    public GameMonsterAttacker(int id, String name, byte head, int value) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addValue(int addValue) {
        this.value += addValue;
    }

    public void getMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setByte(head);
        message.setInt(value);
    }

//    @Override
//    public int hashCode(){
//        return id;
//    }
//
//    @Override
//    public boolean equals(Object o){
//        if (o == null){
//            return false;
//        }
//        if (!(o instanceof GameMonsterAttacker)){
//            return false;
//        }
//        GameMonsterAttacker other = (GameMonsterAttacker) o;
//        return this.hashCode() == other.hashCode();
//    }

}
