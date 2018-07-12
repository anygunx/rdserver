package com.rd.game.manager;

import com.rd.bean.card.PlayerCardBook;
import com.rd.bean.card.PlayerCardData;
import com.rd.bean.card.PlayerCardSuit;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.CardModel;
import com.rd.model.ConstantModel;
import com.rd.model.data.CardModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.EnumSet;

public class CardManager {
    private static final Logger logger = Logger.getLogger(CardManager.class);
    private GameRole gameRole;
    private PlayerCardBook book;

    public CardManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.book = gameRole.getPlayer().getCardBook();
    }

//    public void processCardGetInfo(Message request){
//        Message message = getCardBookMessage();
//        message.setChannel(request.getChannel());
//        gameRole.sendMessage(message);
//    }

    public Message getCardBookMessage() {
        Message message = new Message(MessageCommand.CARD_GET_INFO_MESSAGE);
        // 卡牌数据
        message.setByte(book.getCards().size());
        for (PlayerCardData card : book.getCards().values()) {
            card.getMessage(message);
        }
        // 套装数据
        message.setByte(book.getSuits().size());
        for (PlayerCardSuit suit : book.getSuits().values()) {
            suit.getMessage(message);
        }
        return message;
    }

    public void processCardLevelUp(Message request) {
        short cardId = request.readShort();
        PlayerCardData currentData = book.getCard(cardId);
        if (currentData == null) {
            currentData = new PlayerCardData(cardId, (short) -1);
        }
        short nextLv = (short) (currentData.getLv() + 1);
        CardModelData modelData = CardModel.getCard(cardId);
        if (modelData == null || nextLv > ConstantModel.CARD_MAX_LEVEL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(modelData.getCost(), EGoodsChangeType.CARD_LVUP_CONSUME, saves)) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        currentData.setLv(nextLv);
        book.addCard(currentData);
        saves.add(EPlayerSaveType.CARD_BOOK);

        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.CARD_LEVEL_UP, 1, saves));

        gameRole.savePlayer(saves);

        short suitId = CardModel.getSuitFromCard(cardId);
        PlayerCardSuit suitData = book.getSuit(suitId);

        Message message = new Message(MessageCommand.CARD_LEVEL_UP_MESSAGE, request.getChannel());
        currentData.getMessage(message);
        suitData.getMessage(message);
        gameRole.sendMessage(message);
    }


}
