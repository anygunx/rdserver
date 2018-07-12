package com.rd.bean.card;

import com.google.common.reflect.TypeToken;
import com.rd.define.EAttrType;
import com.rd.model.CardModel;
import com.rd.model.data.CardModelData;
import com.rd.model.data.CardSuitLvModelData;
import com.rd.model.data.CardSuitModelData;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerCardBook {
    private static final Logger logger = Logger.getLogger(PlayerCardBook.class);
    /**
     * 卡牌数据
     * dbdata
     */
    private Map<Short, PlayerCardData> cards = new HashMap<>();
    // ------------------------------------  以下计算值 ---------------------------------------------//
    /**
     * 卡牌套装
     **/
    private Map<Short, PlayerCardSuit> suits = new HashMap<>();
    /**
     * 属性加成
     */
    private int[] attr;

    public void init(String json) {
        Set<PlayerCardData> list = StringUtil.isEmpty(json) ?
                Collections.emptySet() :
                StringUtil.gson2set(json, new TypeToken<Set<PlayerCardData>>() {
                });
        Map<Short, PlayerCardData> map = new HashMap<>();
        for (PlayerCardData card : list) {
            map.put(card.getId(), card);
        }
        cards = map;
        updateSuit();
        updateAttr();
    }

    public String getJson() {
        return StringUtil.obj2Gson(cards.values());
    }

    /**
     * 更新所有套装
     */
    private void updateSuit() {
        // 配合客户端显示 套装分页
        for (Short suitId : CardModel.getSuitsId()) {
            PlayerCardSuit suit = new PlayerCardSuit(suitId);
            updateSuit(suit);
            suits.put(suitId, suit);
        }
    }

    public PlayerCardSuit getSuit(short suitId) {
        return suits.get(suitId);
    }

    public Map<Short, PlayerCardData> getCards() {
        return cards;
    }

    private void updateSuit(PlayerCardSuit suit) {
        short suitId = suit.getId();
        CardSuitModelData modelData = CardModel.getSuit(suitId);
        if (modelData == null) {
            logger.error("PlayerCardBook.updateSuit() 卡牌套装 suitId=" + suit.getId() + ".");
            return;
        }
        Map<Byte, CardSuitLvModelData> suitLvMap = CardModel.getSuitLvMap(suitId);
        for (Byte lv = (byte) (suit.getLv() + 1); lv <= suitLvMap.size(); lv++) {
            CardSuitLvModelData lvData = CardModel.getSuitLvData(suitId, lv);
            if (lvData == null) {
                logger.error("PlayerCardBook.updateSuit() 卡牌套装等级 suitId=" + suitId + ",lv=" + lv + " 数据错误.");
                break;
            }
            if (!isMatch(modelData.getFormula(), lvData.getCardLv())) {
                break;
            }
            suit.setLv(lv);
        }
    }


//    /**
//     * 更新套装等级
//     * @param suit
//     */
//    private void updateSuit(PlayerCardSuit suit) {
//        Map<Byte, CardSuitLvModelData> suitLvMap = CardModel.getSuitLvMap(suit.getId());
//        for (Byte lv = (byte) (suit.getLv() + 1); lv <= suitLvMap.size(); lv++){
//            if(!isMatch(suit.getId(), lv)){
//                break;
//            }
//            suit.setLv(lv);
//        }
//    }
//
//    /**
//     * 将卡牌数据封装到指定套装
//     * @param suitModelData
//     * @return
//     */
//    private PlayerCardSuit createSuitPage(CardSuitModelData suitModelData){
//        short suitId = suitModelData.getId();
//        PlayerCardSuit pageMax = new PlayerCardSuit(suitId);
//        if (!cards.keySet().containsAll(suitModelData.getFormula())){
//            // 配方不齐
//            return pageMax;
//        }
//        Map<Byte, CardSuitLvModelData> suitLvMap = CardModel.getSuitLvMap(suitId);
//        for (byte lv = 1; lv <= suitLvMap.size(); lv++){
//            CardSuitLvModelData lvModelData = CardModel.getSuitLvData(suitId, lv);
//            if (lvModelData == null){
//                logger.error("PlayerCardBook.createSuitPage() 卡牌套装 suitId="+suitId + ",lv=" + lv + " 数据错误.");
//                continue;
//            }
//            PlayerCardSuit page = createSuitPage(lvModelData, lv);
//            if (page != null){
//                if (page.getLv() > pageMax.getLv()){
//                    pageMax = page;
//                }
//            }else{
//                // 此处要求高阶套装卡牌等级一定全部大于低阶套装
//                break;
//            }
//        }
//        return pageMax;
//    }
//
//    /**
//     * 将玩家卡牌数据封装到指定等级的套装
//     * @param suitModelData
//     * @param lv
//     * @return
//     */
//    private PlayerCardSuit createSuitPage(CardSuitLvModelData suitModelData, byte lv){
//        Map<Short, Integer> cards = getCardsInSuit(suitModelData.getId(), lv);
//        if (cards.size() < suitModelData.getFormula().size()){
//            // 不完全满足要求
//            return null;
//        }
//        return new PlayerCardSuit(suitModelData.getId(), lv, cards);
//    }

    /**
     * 检查当前卡牌是否与配方等级匹配
     *
     * @param formula
     * @param lv
     * @return
     */
    private boolean isMatch(Set<Short> formula, short lv) {
        if (!cards.keySet().containsAll(formula)) {
            return false;
        }
        for (Short cardId : formula) {
            PlayerCardData card = getCard(cardId);
            if (card == null) {
                return false;
            }
            if (card.getLv() < lv) {
                return false;
            }
        }
        return true;
    }

    public PlayerCardData getCard(Short cardId) {
        return cards.get(cardId);
    }

    public void addCard(PlayerCardData data) {
        cards.put(data.getId(), data);
        short suitId = CardModel.getSuitFromCard(data.getId());
        PlayerCardSuit suit = getSuit(suitId);
        //刷新等级
        updateSuit(suit);
        //更新缓存的属性
        updateAttr();
    }
//
//    /**
//     * 获取满足套装要求的当前卡牌
//     * @param suitId
//     * @param lv
//     * @return
//     */
//    private Map<Short, Integer> getCardsInSuit(short suitId, byte lv){
//        CardSuitLvModelData modelData = CardModel.getSuitLvData(suitId, lv);
//        if (modelData == null){
//            logger.error("PlayerCardBook.getCardsInSuit() 卡牌套装 suitId="+suitId + ",lv=" + lv + " 数据错误.");
//            return null;
//        }
//        Map<Short, Integer> cardBook = getCards();
//        Map<Short, Integer> cards = new HashMap<>();
//        for (Integer card: modelData.getFormula()){
//            Map.Entry<Short, Integer> cardEntry = getMatchCard(cardBook, card);
//            if (cardEntry != null) {
//                cards.put(cardEntry.getKey(), cardEntry.getValue());
//            }
//        }
//        return cards;
//    }
//
//
//    private Map.Entry<Short, Integer> getMatchCard(short id, short lv) {
//        CardModelData cardModelData = CardModel.getCard(id, lv);
//        if (cardModelData == null){
//            logger.error("PlayerCardBook.getMatchCard() 卡牌数据 cardId="+ id + ",lv=" + lv + "数据错误");
//            return null;
//        }
//        PlayerCardData cardData = getCard(id);
//        if (cardData == null){
//            return null;
//        }
//        CardModelData currentData = CardModel.getCard(cardId);
//        if (currentData.getLv() < cardModelData.getLv()){
//            return null;
//        }
//        return new AbstractMap.SimpleEntry<>(cardType, cardId);
//    }

    public Map<Short, PlayerCardSuit> getSuits() {
        return suits;
    }

    /**
     * 更新属性
     */
    public void updateAttr() {
        int[] attr = new int[EAttrType.ATTR_SIZE];
        // 套装加成
        Map<Short, Double> addCard = new HashMap<>();
        for (PlayerCardSuit suit : getSuits().values()) {
            if (suit.getLv() == 0) {
                continue;
            }
            CardSuitLvModelData suitModelData = CardModel.getSuitLvData(suit.getId(), suit.getLv());
            if (suitModelData == null) {
                logger.error("PlayerCardBook.updateAttr() 卡牌套装 suitId=" + suit.getId() + ",lv=" + suit.getLv() + " 数据错误.");
                continue;
            }
            CardSuitModelData cardSuitModelData = CardModel.getSuit(suit.getId());
            if (cardSuitModelData == null) {
                logger.error("PlayerCardBook.updateAttr() 卡牌列表 suitId=" + suit.getId() + " 数据错误.");
                continue;
            }
            for (Short cid : cardSuitModelData.getFormula()) {
                addCard.put(cid, suitModelData.getCardAddition());
            }
            for (int i = 0; i < attr.length; ++i) {
                // 玩家属性加成
                attr[i] += suitModelData.getAttr()[i];
            }
        }

        // 卡牌加成
        for (PlayerCardData cardData : cards.values()) {
            CardModelData cardModelData = CardModel.getCard(cardData.getId());
            if (cardModelData == null && cardData.getLv() > -1) {
                logger.error("PlayerCardBook.updateAttr() 找不到卡牌数据 cardId=" + cardData.getId() + ", lv=" + cardData.getLv() + ".");
                continue;
            }
            int[] cardAttr = new int[EAttrType.ATTR_SIZE];
            if (cardData.getId() < 36) {
                switch (cardModelData.getPos()) {
                    case 1:
                        cardAttr[EAttrType.ATTACK.getId()] += (int) (CardModel.getAttack()[cardData.getLv()] * (0.5D / 2.1D));
                        break;
                    case 2:
                        cardAttr[EAttrType.PHYDEF.getId()] += (int) (CardModel.getPhyDef()[cardData.getLv()] * 0.6D);
                        cardAttr[EAttrType.MAGICDEF.getId()] += (int) (CardModel.getMagicDef()[cardData.getLv()] * 0.6D);
                        break;
                    case 3:
                        cardAttr[EAttrType.HP.getId()] += (int) (CardModel.getHp()[cardData.getLv()] * 0.7D);
                        break;
                    case 4:
                        cardAttr[EAttrType.ATTACK.getId()] += (int) (CardModel.getAttack()[cardData.getLv()] * (0.8D / 2.1D));
                        cardAttr[EAttrType.PHYDEF.getId()] += (int) (CardModel.getPhyDef()[cardData.getLv()] * 0.2D);
                        cardAttr[EAttrType.MAGICDEF.getId()] += (int) (CardModel.getMagicDef()[cardData.getLv()] * 0.2D);
                        break;
                    case 5:
                        cardAttr[EAttrType.HP.getId()] += (int) (CardModel.getHp()[cardData.getLv()] * 0.3D);
                        cardAttr[EAttrType.ATTACK.getId()] += (int) (CardModel.getAttack()[cardData.getLv()] * (0.8D / 2.1D));
                        cardAttr[EAttrType.PHYDEF.getId()] += (int) (CardModel.getPhyDef()[cardData.getLv()] * 0.2D);
                        cardAttr[EAttrType.MAGICDEF.getId()] += (int) (CardModel.getMagicDef()[cardData.getLv()] * 0.2D);
                        break;
                }
            } else {
                cardAttr[EAttrType.HP.getId()] += (int) (CardModel.getHp()[cardData.getLv()] * 0.3D);
                cardAttr[EAttrType.ATTACK.getId()] += (int) (CardModel.getAttack()[cardData.getLv()] * (0.8D / 2.1D));
                cardAttr[EAttrType.PHYDEF.getId()] += (int) (CardModel.getPhyDef()[cardData.getLv()] * 0.2D);
                cardAttr[EAttrType.MAGICDEF.getId()] += (int) (CardModel.getMagicDef()[cardData.getLv()] * 0.2D);
            }
            if (addCard.containsKey(cardData.getId())) {
                //卡牌属性加成
                for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                    attr[i] += cardAttr[i] * (1 + addCard.get(cardData.getId()));
                }
            } else {
                for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                    attr[i] += cardAttr[i];
                }
            }
        }

        this.attr = attr;
    }

    public int[] getAttr() {
        return attr;
    }
}
