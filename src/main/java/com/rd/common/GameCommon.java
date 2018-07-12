package com.rd.common;

import com.rd.bean.comm.IRandomData;
import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.PlayerDao;
import com.rd.define.EAttrType;
import com.rd.define.FightDefine;
import com.rd.define.GameDefine;
import com.rd.define.TextDefine;
import com.rd.enumeration.EAttr;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.TitleModel;
import com.rd.model.data.TitleModelData;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Created by U-Demon on 2016年12月6日 下午2:05:43
 * @version 1.0.0
 */
public class GameCommon {

    private static final Logger logger = Logger.getLogger(GameCommon.class);

    public static final byte True = 1;
    public static final byte False = 0;

    private GameCommon() {

    }

    /**
     * id生成器
     **/
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    private static final int OFFSET = 1000000;
    /**
     * 百分比基数
     **/
    public static final float PERCENT_DIVIDEND = 10000.0f;

    /**
     * 生成19位游戏世界唯一ID
     * 范围 0~9223372036854775807 可使用至2262-04-12 07:47:16
     * 消息ID生成规则: 3位 serverId + 10位当前时间戳（s） +  6位序列号(0~999,999)
     * 依赖服务器时间，允许每秒999,999请求以下，即可以满足正常需求
     *
     * @return
     */
    public static long generateId() {
        short serverId = GameDefine.getServerId();
        long currentTime = System.currentTimeMillis();

        String source = String.format("%03d", serverId) + (currentTime / DateUtil.SECOND) + String.format("%06d", idGenerator.incrementAndGet() % OFFSET);
        return Long.parseLong(source);
    }

    /**
     * 获取随机数据
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends IRandomData> T getRandomData(List<T> list) {
        int random = (int) (Math.random() * FightDefine.RANDOM_BASE);
        int rate = 0;
        for (T data : list) {
            if (data.getWeight() < 1) {
                continue;
            }
            rate += data.getWeight();
            if (rate > random) {
                return data;
            }
        }
        return null;
    }

    /**
     * 获取随机索引
     *
     * @param percentArray 概率数组
     * @return
     */
    public static int getRandomIndex(int... percentArray) {
        int random = (int) (Math.random() * FightDefine.RANDOM_BASE);
        int rate = 0;
        for (int i = 0; i < percentArray.length; ++i) {
            if (percentArray[i] < 1) {
                continue;
            }
            rate += percentArray[i];
            if (rate > random) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取随机索引
     *
     * @param percentArray 概率数组
     * @return
     */
    public static int getRandomIndex(short... percentArray) {
        int random = (int) (Math.random() * FightDefine.RANDOM_BASE);
        int rate = 0;
        for (int i = 0; i < percentArray.length; ++i) {
            if (percentArray[i] < 1) {
                continue;
            }
            rate += percentArray[i];
            if (rate > random) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取随机索引
     *
     * @param percentArray 概率数组
     * @return
     */
    public static byte getRandomIndex(byte[] percentArray) {
        int random = (int) (Math.random() * FightDefine.RANDOM_HUNDRED_BASE);
        int rate = 0;
        for (byte i = 0; i < percentArray.length; ++i) {
            if (percentArray[i] < 1) {
                continue;
            }
            rate += percentArray[i];
            if (rate > random) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 得到掉落装备
     *
     * @param goodsId
     * @param quality
     * @return
     */
    public static DropData getDropMasterEquip(short goodsId, byte quality) {
        DropData data = new DropData();
        data.setT(EGoodsType.EQUIP.getId());
        data.setG(goodsId);
        data.setQ(quality);
        data.setN(1);
        return data;
    }

    public static byte getBlessValue() {
        return (byte) (Math.random() * 3 + 9);
    }

    public static boolean isWinPercent(int value) {
        if (value > Math.random() * FightDefine.RANDOM_HUNDRED_BASE) {
            return true;
        } else {
            return false;
        }
    }

    public static DropData parseDropData(String str) {
        if (str == null || str.isEmpty() || str.equals("0") || str.equals("0,0,0")) {
            return null;
        }

        String[] data = str.split(",");
        DropData dropData = new DropData();
        dropData.setT(Byte.parseByte(data[0]));
        dropData.setG(Short.parseShort(data[1]));
        if (EGoodsType.EQUIP.getId() == dropData.getT()) {
            dropData.setQ(Byte.parseByte(data[2]));
            dropData.setN(1);
        } else {
            dropData.setQ((byte) 0);
            dropData.setN(Integer.parseInt(data[2]));
        }
        return dropData;
    }

    public static List<DropData> parseDropDataList(String str) {
        if (str == null || str.isEmpty() || str.equals("0")) {
            return Collections.emptyList();
        }

        List<DropData> dropList = new ArrayList<>();
        if (!StringUtil.isEmpty(str) && str.length() > 2) {
            String[] dropStr = str.split("#");
            for (String dataStr : dropStr) {
                String[] data = dataStr.split(",");
                if (EGoodsType.SHOW.getId() == Byte.parseByte(data[0].trim())) {
                    continue;
                }
                DropData dropData = new DropData();
                dropData.setT(Byte.parseByte(data[0]));
                dropData.setG(Short.parseShort(data[1]));
                if (EGoodsType.EQUIP.getId() == dropData.getT()) {
                    dropData.setQ(Byte.parseByte(data[2]));
                    dropData.setN(1);
                } else {
                    dropData.setQ((byte) 0);
                    dropData.setN(Integer.parseInt(data[2]));
                }
                dropList.add(dropData);
            }
        }
        return dropList;
    }


    /**
     * 得到随机装备位
     */
    public static byte getRandomEquipPosition() {
        return (byte) (Math.random() * 6);
    }

    /**
     * 得到随机职业
     */
    public static byte getRandomEquipOccupation() {
        return (byte) (Math.random() * 3);
    }

    /**
     * 得到掉落装备等级
     *
     * @param level
     * @return
     */
    public static short getDropEquipLevel(short level) {
        if (level < 10) {
            return 1;
        } else {
            return (short) (level - (level % 10));
        }
    }

    /**
     * 战斗力计算
     */
    public static int calculationFighting(int[] attr) {
        int fighting = 0;
        for (EAttr type : EAttr.values()) {
            fighting += attr[type.ordinal()] * type.getFactor();
        }
        return fighting;
    }

    /**
     * 战斗力装备计算(不带浮动)
     */
    public static int calculationEquipFighting(int[] attr, double quality) {
        int fighting = 0;
        for (EAttrType type : EAttrType.values()) {
            fighting += attr[type.getId()] * type.getFactor() * quality;
        }
        return fighting;
    }

    /**
     * 计算离线经验双倍消耗
     *
     * @param time
     * @param lv
     * @return
     */
    public static int calculationOfflineCost(int exp, short lv) {
        return (int) Math.ceil(exp / PERCENT_DIVIDEND);
    }

    /**
     * 获取随机头像
     *
     * @return
     */
    public static byte getRandomHead() {
        return (byte) GameUtil.getRangedRandom(0, 5);
    }

    public static String getRandomName() {
        return getRandomName(GameDefine.getServerId());
    }

    public static String getRandomName(int serverId) {
        Random random = new Random();
        int index_1 = random.nextInt(TextDefine.SURNAMES.length);
        String[] names = null;
        names = TextDefine.MALENAMES;
        int index_2 = random.nextInt(names.length);
        return getFormatName(TextDefine.SURNAMES[index_1] + names[index_2], (short) serverId);
    }

    public static String getFormatName(String s, short serverId) {
        return getServerPrefix(serverId) + s;
    }

    public static String getServerPrefix(short serverId) {
        return "S" + serverId + ".";
    }

    public static boolean checkReservedWord(String name) {
        if (StringUtil.checkContain(name, TextDefine.RESERVED_WORDS)) {
            return false;
        }
        return true;
    }

    /**
     * 得到随机灵器部位
     */
    public static byte getRandomArtifactPosition() {
        return (byte) (Math.random() * 3);
    }

    /**
     * 得到随机灵器类型
     */
    public static byte getRandomArtifactType() {
        return (byte) (Math.random() * 5);
    }

    /**
     * 得到随机索引
     *
     * @param 长度
     */
    public static int getRandomIndex(int length) {
        return (int) (Math.random() * length);
    }

    /**
     * @param value
     * @param index
     * @return boolean
     */
    public static boolean getBit2BooleanValue(int value, int index) {
        if (index < 0 || index >= Integer.SIZE) {
            throw new IllegalArgumentException();
        }
        return ((value >> index) & 0x01) == True;
    }

    /**
     * @param value
     * @param fromIndex
     * @param toIndex
     * @return int
     */
    public static int setSubValue(int value, int subValue, int fromIndex, int toIndex) {
        if ((fromIndex < 0 || fromIndex >= Integer.SIZE) ||
                (toIndex < 0 || toIndex >= Integer.SIZE) ||
                (fromIndex > toIndex)) {
            throw new IllegalArgumentException();
        }
        int bits = toIndex - fromIndex + 1;
        if (subValue >= (2 << bits)) {
            throw new IllegalArgumentException();
        }
        // clear
        value &= ~((0xffffffff >>> (Integer.SIZE - bits)) << fromIndex);
        return value | (subValue << fromIndex);
    }

    public static byte[] parseByteValueStruct(String str) {
        String[] arrayStr = str.split("#");
        byte[] value = new byte[arrayStr.length];
        for (int i = 0; i < arrayStr.length; ++i) {
            String[] strData = arrayStr[i].split(",");
            value[i] = Byte.parseByte(strData[0]);
        }
        return value;
    }

    public static short[] parseShortChanceStruct(String str) {
        String[] arrayStr = str.split("#");
        short[] chance = new short[arrayStr.length];
        for (int i = 0; i < arrayStr.length; ++i) {
            String[] strData = arrayStr[i].split(",");
            chance[i] = Short.parseShort(strData[1]);
        }
        return chance;
    }

    public static short[] parseShortChance(String str) {
        String[] arrayStr = str.split("#");
        short[] chance = new short[arrayStr.length];
        for (int i = 0; i < arrayStr.length; ++i) {
            chance[i] = Short.parseShort(arrayStr[i]);
        }
        return chance;
    }

    public static int[] paraseSumAttr(int[] attr, String str) {
        int[] attribute = new int[EAttrType.ATTR_SIZE];
        String[] array = str.split("#");
        for (int i = 0; i < array.length; ++i) {
            String[] t = array[i].split(",");
            attribute[Integer.parseInt(t[0])] = Integer.parseInt(t[1]);
        }
        for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
            attribute[i] += attr[i];
        }
        return attribute;
    }

    public static void grantTitle(int playerId, int titleId) {
        short id = (short) titleId;
        TitleModelData model = TitleModel.getTitle(id);
        if (model == null)
            return;
        IGameRole gr = GameWorld.getPtr().getGameRole(playerId);
        if (gr == null)
            return;
        Player player = gr.getPlayer();
        if (player == null)
            return;
        long curr = System.currentTimeMillis();
        try {
            //永久称号
            if (model.getTime() == -1) {
                player.getTitle().put(id, -1L);
            }
            //限时称号
            else {
                Long endTime = player.getTitle().get(id);
                if (endTime == null || endTime < curr)
                    endTime = curr;
                endTime += model.getTime() * DateUtil.SECOND;
                if (endTime % 100000 > 45000)
                    endTime -= 30000;
                player.getTitle().put(id, endTime);
            }
            new PlayerDao().updateTitle(player);
            //在线玩家退送消息
            if (gr.isOnline()) {
                GameRole role = (GameRole) gr;
                role.putMessageQueue(role.getTitleManager().getTitleInfoMsg(id));
            }
        } catch (Exception e) {
            logger.error("授予:" + playerId + ",称号:" + id + "时发生异常", e);
        }
    }

    public static byte[] parseByteArray(String str) {
        String[] array = str.split(",");
        byte[] s = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            s[i] = Byte.parseByte(array[i]);
        }
        return s;
    }

    public static String toString(byte[] data) {
        if (data == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(data[i]);
        }
        return builder.toString();
    }
}
