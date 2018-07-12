package com.rd.game.manager;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.rd.bean.mail.Mail;
import com.rd.bean.pay.OrderData;
import com.rd.common.MailService;
import com.rd.dao.PayDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.model.PayModel;
import com.rd.model.data.PayAdditionModelData;
import com.rd.model.data.PayModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class PayManager {
    private static final Logger logger = Logger.getLogger(PayManager.class);
    private GameRole role;
    /**
     * 所有订单 uid- order
     **/
    private Map<String, OrderData> _orders;

    //******************************以下为计算值和辅助数据**************************************//
    /**
     * 用于支付的订单
     * 过滤括月卡等 diamond=0 的订单View
     */
    private Set<String> _ordersInPay;
    private TreeMultimap<Integer, String> _rmb2Orders;
    /**
     * 首冲订单
     */
    private String firstOrder;
    //********************************************************************************************//
    private PayOrderPredicate payOrderPredicate = new PayOrderPredicate();

    public PayManager(GameRole role) {
        this.role = role;
    }

    private class PayOrderPredicate implements Predicate<Map.Entry<String, OrderData>> {
        @Override
        public boolean apply(Map.Entry<String, OrderData> entry) {
            return this.apply(entry.getValue());
        }

        public boolean apply(OrderData orderData) {
            return orderData.isCountable();
        }
    }

    private void init() {
        _orders = role.getDbManager().payDao.getOrders(role.getPlayerId());
        // update view
        _ordersInPay = Sets.newHashSet(Maps.filterEntries(_orders, payOrderPredicate).keySet());
        _rmb2Orders = TreeMultimap.create();
        for (OrderData orderData : _orders.values()) {
            _rmb2Orders.put(orderData.getAmount(), orderData.getUID());
        }
    }

    private Map<String, OrderData> getOrders() {
        if (null == _orders) {
            init();
        }
        return _orders;
    }

    private Set<String> getOrdersInPay() {
        if (null == _ordersInPay) {
            init();
        }
        return _ordersInPay;
    }

    private TreeMultimap<Integer, String> getRmb2Orders() {
        if (null == _rmb2Orders) {
            init();
        }
        return _rmb2Orders;
    }

    /**
     * 获取每日所有订单
     *
     * @return
     */
    public Map<String, Integer> getDailyOrderByAmount() {
        Map<String, Integer> list = new LinkedHashMap<>();
        long ts = System.currentTimeMillis();
        List<OrderData> orders = new ArrayList<>();
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (DateUtil.isSameDay(orderData.getCreateTime(), ts)) {
                orders.add(orderData);
            }
        }
        Collections.sort(orders, new Comparator<OrderData>() {

            @Override
            public int compare(OrderData o1, OrderData o2) {
                if (o1.getCreateTime() < o2.getCreateTime()) return -1;
                if (o1.getCreateTime() > o2.getCreateTime()) return 1;
                return 0;
            }
        });
        for (OrderData orderData : orders) {
            list.put(orderData.getUID(), orderData.getAmount() * 100);
        }
        return list;
    }

    /**
     * 获取玩家所有订单
     *
     * @return
     */
    public Map<String, Integer> getAllOrders(long fromTime, long toTime) {
        Map<String, Integer> list = new HashMap<>();
        for (OrderData od : getOrders().values()) {
            if (od.getCreateTime() >= fromTime && od.getCreateTime() <= toTime) {
                list.put(od.getUID(), od.getDiamond());
            }
        }
        return list;
    }

    /**
     * 获取所有已充值元宝
     *
     * @return
     */
    public int getAllDiamond() {
        int sum = 0;
        for (String uid : getOrdersInPay()) {
            OrderData od = getOrder(uid);
            sum += od.getDiamond();
        }
        return sum;
    }

    /**
     * 添加订单
     *
     * @param orderData
     */
    public void createOrder(OrderData orderData) {
        // 保存充值订单
        String formatData = DateUtil.formatDateTime(orderData.getCreateTime());
        // 系统调用 new dao
        int result = new PayDao().createPay(orderData, formatData);
        Preconditions.checkArgument(result >= 0, "PayManager.createOrder() failed. Cannot create pay.");

        // DB操作成功再更新内存 模拟读
        String uid = orderData.getUID();
        getOrders().put(uid, orderData);
        // update view
        if (payOrderPredicate.apply(orderData)) {
            if (getOrdersInPay().isEmpty()) {
                firstOrder = uid;
            }
            getOrdersInPay().add(uid);
        }
        getRmb2Orders().put(orderData.getAmount(), uid);
    }

    /**
     * 检查订单是否存在
     *
     * @param orderData
     * @return
     */
    public boolean isOrderExisted(OrderData orderData) {
        return getOrders().containsKey(orderData.getUID());
    }

    /**
     * 是否首冲
     *
     * @return
     */
    public boolean isFirstPay() {
        return getPayCount() == 1;
    }

    /**
     * 是否进行过首冲
     *
     * @return
     */
    public boolean hasFirstPay() {
        return getPayCount() >= 1;
    }

    /**
     * 获取所有充值的次数
     *
     * @param
     * @return
     */
    public int getPayCount() {
        return getOrdersInPay().size();
    }

    /**
     * 获取指定金额的充值次数
     *
     * @param rmb
     * @return
     */
    public int getPayCount(int rmb) {
        if (!getRmb2Orders().containsKey(rmb)) {
            return 0;
        }
        return getRmb2Orders().get(rmb).size();
    }

    /**
     * 获取每档充值的历史次数
     *
     * @return
     */
    public Map<Integer, Integer> getPayCounts() {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer rmb : getRmb2Orders().keySet()) {
            counts.put(rmb, getPayCount(rmb));
        }
        return counts;
    }

    private OrderData getOrder(String uid) {
        return getOrders().get(uid);
    }

    /**
     * 获取第一次的充值订单
     *
     * @return
     */
    public OrderData getFirstOrderInPay() {
        if (firstOrder == null) {
            return null;
        }
        return getOrders().get(firstOrder);
    }

    /**
     * 附加奖励处理
     * TODO 放到上线处理
     *
     * @param payModelData
     */
    public void handleAdditions(PayModelData payModelData) {
        if (payModelData.getAdditions().isEmpty()) {
            return;
        }
        for (Integer addition : payModelData.getAdditions()) {
            PayAdditionModelData additionModelData = PayModel.getAdditionModel(addition);
            if (additionModelData == null) {
                logger.error("玩家 playerId=" + role.getPlayer().getId() + "," + payModelData.getRmb() + "元附加奖励" + addition + "检查失败");
                continue;
            }
            if (!checkValid(payModelData.getRmb(), additionModelData)) {
                continue;
            }
            Mail mail = MailService.createMail(additionModelData.getTitle(), additionModelData.getContent(),
                    EGoodsChangeType.PAY_REWARD_ADD, additionModelData.getRewardList());
            MailService.sendPaymentSystemMail(role.getPlayer().getId(), mail);
        }
    }

    /**
     * 检查附加奖励条件
     *
     * @param rmb
     * @param modelData
     * @return
     */
    private boolean checkValid(int rmb, PayAdditionModelData modelData) {
        int times = 0;
        // 首冲
        switch (modelData.getCountType()) {
            case FirstPay:
                if (getPayCount() == 1 && getFirstOrderInPay().getAmount() == rmb) {
                    times = 1;
                }
                break;
            case OtherPay:
                times = getPayCount(rmb);
                if (modelData.getTimes() == 1) {
                    // 首冲与同档位其他首次充值互斥 没做配置 写在这
                    OrderData firstOrder = getFirstOrderInPay();
                    if (firstOrder != null && firstOrder.getAmount() == rmb) {
                        times = 0;
                    }
                }
                break;
            default:
                break;
        }
        return times == modelData.getTimes();
    }

    /**
     * 获取今日充值累计金额
     *
     * @return
     */
    public int getTodayRmbInPay() {
        long ts = System.currentTimeMillis();
        int sum = 0;
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (DateUtil.isSameDay(orderData.getCreateTime(), ts)) {
                sum += orderData.getAmount();
            }
        }
        return sum;
    }

    /**
     * 获取今日充值累计代币
     *
     * @return
     */
    public int getTodayDiamondInPay() {
        long ts = System.currentTimeMillis();
        int sum = 0;
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (DateUtil.isSameDay(orderData.getCreateTime(), ts)) {
                sum += orderData.getDiamond();
            }
        }
        return sum;
    }

    /**
     * 获取指定时间内的充值累计金额
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public int getRmbInPay(String fromTime, String toTime) {
        return getRmbInPay(DateUtil.parseDataTime(fromTime).getTime(), DateUtil.parseDataTime(toTime).getTime());
    }


    /**
     * 获取指定时间内的充值累计金额
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public int getRmbInPay(long fromTime, long toTime) {
        int sum = 0;
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (orderData.getCreateTime() >= fromTime && orderData.getCreateTime() <= toTime) {
                sum += orderData.getAmount();
            }
        }
        return sum;
    }

    /**
     * 获取指定时间内的充值累计代币
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public int getDiamondInPay(String fromTime, String toTime) {
        return getDiamondInPay(DateUtil.parseDataTime(fromTime).getTime(), DateUtil.parseDataTime(toTime).getTime());
    }


    /**
     * 获取指定时间内的充值累计代币
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public int getDiamondInPay(long fromTime, long toTime) {
        int sum = 0;
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (orderData.getCreateTime() >= fromTime && orderData.getCreateTime() <= toTime) {
                sum += orderData.getDiamond();
            }
        }
        return sum;
    }

    /**
     * 充值记录数据
     */
    public Message getPayRecordMsg() {
        Map<Integer, Integer> map = getPayCounts();
        Message msg = new Message(MessageCommand.GAME_PAY_MESSAGE);
        msg.setByte(map.size());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            msg.setInt(entry.getKey());
        }
        return msg;
    }

    /**
     * 获取时间段内充值大于指定金额的天数
     *
     * @param rmb
     * @param startTime
     * @param endTime
     * @return
     */
    public byte getPayGreaterDays(int rmb, long startTime, long endTime) {
        Set<Integer> days = new HashSet<>();
        for (Collection<String> orders : getRmb2Orders().asMap().tailMap(rmb, true).values()) {
            for (String uid : orders) {
                OrderData orderData = getOrder(uid);
                if (orderData.getCreateTime() < startTime || orderData.getCreateTime() > endTime) {
                    continue;
                }
                int day = DateUtil.getDistanceDay(startTime, orderData.getCreateTime());
                days.add(day);
            }
        }
        return (byte) days.size();
    }

    /**
     * 获取时间段内充值大于指定金额的天数
     *
     * @param rmb
     * @param startTime
     * @param endTime
     * @return
     */
    public byte getPayConGreaterDays(int rmb, long startTime, long endTime) {
        List<String> orderList = new ArrayList<>();
        for (String uid : getOrdersInPay()) {
            OrderData orderData = getOrder(uid);
            if (orderData.getCreateTime() < startTime || orderData.getCreateTime() > endTime) {
                continue;
            }
            orderList.add(uid);
        }
        int day = DateUtil.getDistanceDay(startTime, endTime);
        byte days = 0;
        for (int i = 0; i <= day; i++) {
            int sum = 0;
            for (String uid : orderList) {
                OrderData orderData = getOrder(uid);
                long createTime = orderData.getCreateTime();
                int count = orderData.getDiamond();
                if (DateUtil.isSameDay(createTime, startTime)) {
                    sum += count;
                }
            }
            if (sum >= rmb * 100) {
                days++;
            }
            sum = 0;
            startTime += 24 * 60 * 60 * 1000;
        }
        return days;
    }

    /**
     * 获取充值天数
     *
     * @return
     */
    public int getPayDays() {
        Set<Integer> days = new HashSet<>();
        for (OrderData orderData : getOrders().values()) {
            if (!payOrderPredicate.apply(orderData)) {
                continue;
            }
            int day = DateUtil.getDistanceDay(0, orderData.getCreateTime());
            days.add(day);
        }
        return days.size();
    }

}
