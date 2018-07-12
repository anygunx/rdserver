package com.rd.bean.auction;

import com.rd.bean.mail.Mail;
import com.rd.common.MailService;
import com.rd.define.EAuction;
import com.rd.define.EGoodsChangeType;
import com.rd.define.TextDefine;
import com.rd.model.AuctionModel;
import com.rd.model.data.AuctionItemModelData;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;

/**
 * 世界拍卖行
 * Created by XingYun on 2017/10/25.
 */
public class WorldAuction extends BaseAuction {
    public WorldAuction() {
        super(EAuction.WorldAuction.id);
    }

    @Override
    protected float getTax() {
        return AuctionModel.getTaxWorld();
    }

    @Override
    protected void onLoss(AuctionItemData item) {
        switch (item.getType()) {
            // TODO 可不可以根据Owner来判定是否返还 这样与类型无关好扩展
            // 流拍的层级关系 须要整理
            case Gang:
                // 流拍回收
                break;
            case Personal:
                // 流拍返还
                return2seller(item);
                break;

        }
    }

    private void return2seller(AuctionItemData item) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOGIC, new Task() {
            @Override
            public void run() {
                try {
                    // 须要保证在createItem时的Onwer只有一个
                    int owner = item.getOwners().iterator().next();
                    // 竞价人获得物品
                    AuctionItemModelData modelData = AuctionModel.getData(item.getModelId());
                    Mail mail = MailService.createMail(TextDefine.AUCTION_LOSS_TITLE, TextDefine.AUCTION_LOSS_CONTENT, EGoodsChangeType.AUCTION_LOSS_ADD, modelData.getGoods());
                    MailService.sendSystemMail(owner, mail);
                } catch (Exception e) {
                    logger.error("流拍返还错误：" + item);
                    logger.error(e.getMessage());
                }
            }

            @Override
            public String name() {
                return "auctionLossTask";
            }
        });
    }
}
