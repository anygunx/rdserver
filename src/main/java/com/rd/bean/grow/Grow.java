package com.rd.bean.grow;

import com.rd.enumeration.EGrow;
import com.rd.net.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月5日上午11:12:56
 */
public class Grow {

    private Map<Short, GrowSeed> map = new HashMap<>();

    //出战
    private short[] go = {};

    private GrowSuit[] suit;

    public Grow() {

    }

    public Grow(EGrow type) {
        switch (type) {
            case PET:
                go = new short[3];
                suit = new GrowSuit[2];
                suit[0] = new GrowSuit(4);
                suit[1] = new GrowSuit(4);
                break;
            case MATE:
                go = new short[2];
                suit = new GrowSuit[2];
                suit[0] = new GrowSuit(4);
                suit[1] = new GrowSuit(4);
                break;
            case FAIRY:
                suit = new GrowSuit[4];
                suit[0] = new GrowSuit(1);
                suit[1] = new GrowSuit(1);
                suit[2] = new GrowSuit(1);
                suit[3] = new GrowSuit(1);
                break;
            case GODDESS:
                go = new short[2];
                suit = new GrowSuit[2];
                suit[0] = new GrowSuit(4);
                suit[1] = new GrowSuit(4);
                break;
            case ROLE:
                go = new short[2];
                suit = new GrowSuit[2];
                suit[0] = new GrowSuit(4);
                suit[1] = new GrowSuit(4);
                break;
        }
    }

    public void getMessageSeed(Message message) {
        message.setByte(map.size());
        for (GrowSeed gs : map.values()) {
            gs.getMessage(message);
        }
    }

    public void getMessageGo(Message message) {
        message.setByte(go.length);
        for (short id : go) {
            message.setShort(id);
        }
    }

    public void getMessage(Message message) {
        getMessageSeed(message);
        getMessageGo(message);

        for (GrowSuit suit : suit) {
            suit.getMessage(message);
        }
    }

    public Map<Short, GrowSeed> getMap() {
        return map;
    }

    public void setMap(Map<Short, GrowSeed> map) {
        this.map = map;
    }

    public short[] getGo() {
        return go;
    }

    public void setGo(short[] go) {
        this.go = go;
    }

    public GrowSuit[] getSuit() {
        return suit;
    }

    public void setSuit(GrowSuit[] suit) {
        this.suit = suit;
    }
}
