package com.rd.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rd.bean.drop.DropData;
import com.rd.bean.player.ArenaChallenge;
import com.rd.bean.rank.PlayerRank;
import com.rd.common.goods.EGoodsType;
import com.rd.util.DiceUtil.Ele;
import org.w3c.dom.Element;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final String COMMA = ",";
    public static final String SEMIC = ";";

    public static String trim(String str) {
        if (str == null) {
            str = "";
        } else {
            str = str.trim();
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.charAt(0) == '"') {
            str = str.substring(1);
        }
        if (str.charAt(str.length() - 1) == '"') {
            str = str.substring(0, str.length() - 1);
        }
        str = str.replaceAll(" ", "");
        return str;
    }

    public static String[] getStringList(String str) {
        str = trim(str);
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        String sep = ",";
        if (str.indexOf(':') >= 0) {
            sep = ":";
        }
        return str.split(sep);
    }

    public static String[] getStringList(String str, String sep) {
        str = trim(str);
        return str.split(sep);
    }

    /**
     * 字符串中的空格会被去掉
     *
     * @param str
     * @param sep
     * @return
     */
    public static int[] getIntArray(String str, String sep) {
        if (isEmpty(str)) {
            return new int[0];
        }
        String[] prop = getStringList(str, sep);
        List<Integer> tmp = new ArrayList<Integer>();
        for (int i = 0; i < prop.length; i++) {
            try {
                int r = Integer.parseInt(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        int[] ints = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            ints[i] = tmp.get(i);
        }
        return ints;
    }

    /**
     * 字符串中的空格会被去掉
     *
     * @param str
     * @param sep
     * @return
     */
    public static byte[] getByteArray(String str, String sep) {
        String[] prop = getStringList(str, sep);
        List<Byte> tmp = new ArrayList<Byte>();
        for (int i = 0; i < prop.length; i++) {
            try {
                byte r = Byte.parseByte(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        byte[] bs = new byte[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            bs[i] = tmp.get(i);
        }
        return bs;
    }

    public static List<Integer> getIntList(String str, String sep) {
        List<Integer> tmp = new ArrayList<Integer>();
        if (str == null || str.trim().equals("")) {
            return tmp;
        }
        String[] prop = getStringList(str, sep);
        for (int i = 0; i < prop.length; i++) {
            try {
                int r = Integer.parseInt(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public static Set<Integer> getIntSet(String additions, String sep) {
        Set<Integer> set = new HashSet<>();
        int[] array = getIntArray(additions, sep);
        if (array == null) {
            return set;
        }
        for (Integer i : array) {
            set.add(i);
        }
        return set;
    }


    public static List<Short> getShortList(String str, String sep) {
        List<Short> tmp = new ArrayList<Short>();
        if (str == null || str.trim().equals("")) {
            return tmp;
        }
        String[] prop = getStringList(str, sep);
        for (int i = 0; i < prop.length; i++) {
            try {
                short r = Short.parseShort(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public static Set<Short> getShortSet(String str, String sep) {
        Set<Short> tmp = new HashSet<>();
        if (str == null || str.trim().equals("")) {
            return tmp;
        }
        String[] prop = getStringList(str, sep);
        for (int i = 0; i < prop.length; i++) {
            try {
                short r = Short.parseShort(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public static Set<Byte> getByteSet(String str, String sep) {
        Set<Byte> tmp = new HashSet<>();
        if (str == null || str.trim().equals("")) {
            return tmp;
        }
        String[] prop = getStringList(str, sep);
        for (int i = 0; i < prop.length; i++) {
            try {
                byte r = Byte.parseByte(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    /**
     * 将一个Integer列表里面的数值用sep分隔符连接起来,去掉最后的一个sep,返回该字符串
     *
     * @param list
     * @param sep  分隔符
     * @return
     */
    public static <T> String getString(Collection<T> list, String sep) {
        StringBuilder sb = new StringBuilder();
        for (T var : list) {
            sb.append(var).append(sep);
        }
        String result = sb.toString();
        if (result.endsWith(sep)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String getString(int[] list, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int var : list) {
            sb.append(var).append(sep);
        }
        String result = sb.toString();
        if (result.endsWith(sep)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static List<Float> getFloatList(String str, String sep) {
        List<Float> tmp = new ArrayList<Float>();
        if (str == null || str.trim().equals("")) {
            return tmp;
        }
        String[] prop = getStringList(str, sep);
        for (int i = 0; i < prop.length; i++) {
            try {
                float r = Float.parseFloat(prop[i]);
                tmp.add(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    public static String join(String[] strs, String sep) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            buffer.append(sep).append(strs[i]);
        }
        return buffer.toString();
    }

    public static double[] getDoubleList(String str) {
        String[] prop = getStringList(str);
        double[] ds = new double[prop.length];
        for (int i = 0; i < ds.length; i++) {
            ds[i] = Double.parseDouble(prop[i]);
        }
        return ds;
    }

    public static List<String> getListBySplit(String str, String split) {
        List<String> list = new ArrayList<String>();
        if (str == null || str.trim().equalsIgnoreCase(""))
            return null;
        String[] strs = str.split(split);
        for (String temp : strs) {
            if (temp != null && !temp.trim().equalsIgnoreCase("")) {
                list.add(temp.trim());
            }
        }
        return list;
    }

    public static int[] getIntList(String str) {
        String[] prop = getStringList(str);
        List<Integer> tmp = new ArrayList<Integer>();
        for (int i = 0; i < prop.length; i++) {
            try {
                String sInt = prop[i].trim();
                if (sInt.length() < 20) {
                    int r = Integer.parseInt(prop[i].trim());
                    tmp.add(r);
                }
            } catch (Exception e) {
            }
        }
        int[] ints = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            ints[i] = tmp.get(i);
        }
        return ints;

    }

    public static short[] getShortList(String str) {
        String[] prop = getStringList(str);
        List<Short> tmp = new ArrayList<Short>();
        for (int i = 0; i < prop.length; i++) {
            try {
                String sInt = prop[i].trim();
                if (sInt.length() < 20) {
                    short r = Short.parseShort(prop[i].trim());
                    tmp.add(r);
                }
            } catch (Exception e) {
            }
        }
        short[] ints = new short[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            ints[i] = tmp.get(i);
        }
        return ints;

    }


    public static String toWrapString(Object obj, String content) {
        if (obj == null) {
            return "null";
        } else {
            return obj.getClass().getName() + "@" + obj.hashCode() + "[\r\n" + content + "\r\n]";
        }
    }

    // 将1,2,3和{1,2,3}格式的字符串转化为JDK的bitset
    // 考虑了两边是否有{}，数字两边是否有空格，是否合法数字
    public static BitSet bitSetFromString(String str) {
        if (str == null) {
            return new BitSet();
        }
        if (str.startsWith("{")) {
            str = str.substring(1);
        }
        if (str.endsWith("}")) {
            str = str.substring(0, str.length() - 1);
        }
        int[] ints = getIntList(str);
        BitSet bs = new BitSet();
        for (int i : ints) {
            bs.set(i);
        }
        return bs;
    }

    public static boolean hasExcludeChar(String str) {
        if (str != null) {
            char[] chs = str.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                if (Character.getType(chs[i]) == Character.PRIVATE_USE) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String replaceSql(String str) {
        if (str != null) {
            return str.replaceAll("'", "’").replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
        }
        return "";
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param s1
     * @param s2
     * @return true, 字符串相等;false,字符串不相等
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 != null) {
            return s1.equals(s2);
        }
        if (s2 != null) {
            return false;
        }
        // 两个字符串都是null
        return true;
    }

    /**
     * 判断字符串是否时数字
     *
     * @param text
     * @return
     */
    public static boolean isDigit(String text) {
        String reg = "[-]*[\\d]+[\\.\\d+]*";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(text);
        return mat.matches();
    }

    /**
     * 判断一句话是否是汉语
     *
     * @param text
     * @return
     */
    public static boolean isChiness(String text) {
        String reg = "[\\w]*[\\u4e00-\\u9fa5]+[\\w]*";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(text);
        boolean result = mat.matches();
        return result;
    }

    /**
     * 判断单个字符是否是汉语
     *
     * @param cha
     * @return
     */
    public static boolean isChineseChar(char cha) {
        String reg = "[\\u4e00-\\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        String text = Character.toString(cha);
        Matcher mat = pat.matcher(text);
        boolean result = mat.matches();
        return result;
    }

    /**
     * 判断字符是否是字母(包括大小写)或者数字
     *
     * @param cha
     * @return
     */
    public static boolean isLetterAndDigit(String cha) {
        String reg = "[\\w]+";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(cha);
        boolean result = mat.matches();
        return result;
    }

    /**
     * 返回字符串中汉字的数量
     *
     * @param test
     * @return
     */
    public static int getChineseCount(String test) {
        int count = 0;
        boolean tempResult = false;
        for (int i = 0; i < test.length(); i++) {
            char cha = test.charAt(i);
            tempResult = isChineseChar(cha);
            if (tempResult) {
                count++;
            }
        }
        return count;
    }

    /**
     * 返回字符串中字母和数字的个数，其中字母包括大小写
     *
     * @param text
     * @return
     */
    public static int getLetterAndDigitCount(String text) {
        int count = 0;
        boolean tempResult = false;
        for (int i = 0; i < text.length(); i++) {
            tempResult = isLetterAndDigit(text);
            if (tempResult) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return true, 字符串是空的;false,字符串不是空的
     */
    public static boolean isEmpty(String str) {
        if (str == null || (str.trim().length() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * 将字符串首字母大写
     *
     * @param s
     * @return
     */
    public static String upperCaseFirstCharOnly(String s) {
        if (s == null || s.length() < 1) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * 将字符串按小括号()拆分成数组
     *
     * @param src
     * @return
     */
    public static String[] bracketToArray(String src) {
        if (StringUtil.isEmpty(src)) {
            throw new IllegalArgumentException("source string is null or empty");
        }
        List<String> strList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("(\\()(.*?)(\\))");
        Matcher matcher = pattern.matcher(src);
        while (matcher.find()) {
            strList.add(matcher.group().replaceAll("\\(|\\)", ""));
        }
        if (strList.size() == 0) {
            throw new IllegalArgumentException("source string's format is not suitable");
        }
        return strList.toArray(new String[strList.size()]);
    }

    public static boolean isAllNumeric(String str) {
        if (str == null) {
            return false;
        }
        String format = "^-?\\d+$";
        Pattern p = Pattern.compile(format, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 字符串拼接
     */
    public static String assemble(char sep, Object... objects) {
        if (objects == null) {
            return "";
        }
        if (objects.length == 1) {
            return objects[0].toString();
        }
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            if (object == null) {
                continue;
            }
            sb.append(object.toString()).append(sep);
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 替换字符串
     */
    public static final String replaceAllwithKey(String str, String key, String replacement) {
        if (str != null && key != null && replacement != null && !str.equals("") && !key.equals("")) {
            StringBuilder strbuf = new StringBuilder();
            int begin = 0;
            int slen = str.length();
            int npos = 0;
            int klen = key.length();
            for (; begin < slen && (npos = str.indexOf(key, begin)) >= begin; begin = npos + klen) {
                strbuf.append(str.substring(begin, npos)).append(replacement);
            }
            if (begin == 0) {
                return str;
            }
            if (begin < slen) {
                strbuf.append(str.substring(begin));
            }
            return strbuf.toString();
        } else {
            return str;
        }
    }

    /**
     * 替换字符串
     */
    public static final String replaceAllMultParams(String str, String... params) {
        for (int i = 0; i < params.length; i++) {
            str = replaceAllwithKey(str, "$" + i, params[i]);
        }
        return str;
    }

    public static final String replaceParams(String str, String... params) {
        if (params == null)
            return str;
        String content = str;
        for (int i = 0; i < params.length; i++) {
            String s = "{" + i + "}";
            if (content.indexOf(s) == -1)
                continue;
            content = content.replace(s, params[i]);
        }
        return content;
    }

    public static String toFirstUpper(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String first = str.substring(0, 1);
        return str.replaceFirst(first, first.toUpperCase());
    }

    /**
     * 去除字符串首尾出现的某个字符.
     *
     * @param source  源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    public static String trimFirstAndLastChar(String source, char element) {
        if (source.length() == 0) {
            return source;
        }
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        do {
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }

    public static boolean checkContain(String str, String... words) {
        for (String word : words) {
            if (str.indexOf(word) != -1) {
                return true;
            }
        }
        return false;
    }

    public static String toJson(Object obj) {
        if (obj == null)
            return "{}";
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            sb.append(fieldToJson(field, obj));
            sb.append(",");
        }
        for (Field field : clazz.getSuperclass().getDeclaredFields()) {
            sb.append(fieldToJson(field, obj));
            sb.append(",");
        }
        String json = sb.substring(0, sb.length() - 1);
        return "{" + json + "}";
    }

    private static String fieldToJson(Field field, Object obj) {
        String fieldAttrStr = field.toGenericString();
        String[] fieldAttrs = fieldAttrStr.split(" ");
        String fieldClass = fieldAttrs[1];
        field.setAccessible(true);
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(field.getName()).append("\"");
        sb.append(":");
        try {
            if (fieldClass.equals("java.lang.String"))
                sb.append("\"").append(field.get(obj)).append("\"");
            else
                sb.append("'").append(field.get(obj)).append("'");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String obj2Gson(Object obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T gson2Obj(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static List<PlayerRank> gson2ListPR(String json) {
        return new Gson().fromJson(json, new TypeToken<List<PlayerRank>>() {
            private static final long serialVersionUID = 1L;
        }.getType());
    }

    public static List<ArenaChallenge> gson2ListAC(String json) {
        return new Gson().fromJson(json, new TypeToken<List<ArenaChallenge>>() {
            private static final long serialVersionUID = 1L;
        }.getType());
    }

    public static <T> List<T> gson2List(String json, TypeToken<List<T>> typeToken) {
        if (StringUtil.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(json, typeToken.getType());
        }
    }

    public static <K, V> Map<K, V> gson2Map(String json, TypeToken<Map<K, V>> typeToken) {
        if (StringUtil.isEmpty(json)) {
            return new HashMap<>();
        } else {
            return new Gson().fromJson(json, typeToken.getType());
        }
    }

    public static <T> Set<T> gson2set(String json, TypeToken<Set<T>> typeToken) {
        if (StringUtil.isEmpty(json)) {
            return new HashSet<>();
        } else {
            return new Gson().fromJson(json, typeToken.getType());
        }
    }


    public static List<DropData> getRewardDropList(String str) {
        List<DropData> dropList = new ArrayList<>();
        if (StringUtil.isEmpty(str) || str.length() < 5) {
            return dropList;
        }
        String[] goodsStrArr = null;
        if (str.contains(";"))
            goodsStrArr = str.split(";");
        else
            goodsStrArr = str.split("#");
        for (int i = 0; i < goodsStrArr.length; ++i) {
            DropData dropData = getRewardDropData(goodsStrArr[i]);
            if (dropData != null) {
                dropList.add(dropData);
            }
        }
        return dropList;
    }

    public static DropData getRewardDropData(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        String[] data = str.split(",");
        if (EGoodsType.SHOW.getId() == Integer.parseInt(data[0])) {
            return null;
        }
        if (data.length == 1) {
            DropData dropData = new DropData();
            dropData.setN(Integer.parseInt(data[0]));
            return dropData;
        }
        if (data.length == 3) {
            DropData dropData = new DropData();
            dropData.setT(Byte.parseByte(data[0]));
            dropData.setG(Short.parseShort(data[1]));
            dropData.setN(Integer.parseInt(data[2]));
            return dropData;
        }
        if (data.length == 4) {
            DropData dropData = new DropData();
            dropData.setT(Byte.parseByte(data[0]));
            dropData.setG(Short.parseShort(data[1]));
            dropData.setN(Integer.parseInt(data[2]));
            dropData.setQ(Byte.parseByte(data[3]));
            return dropData;
        }
        return null;
    }

    public static List<Integer> getGailvs(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        String[] data = str.split(",");
        List<Integer> list = new ArrayList<>();

        if (data.length == 8) {
            for (int i = 0; i < 8; i++) {
                list.add(Integer.parseInt(data[i]));
            }
            return list;
        }
        return null;
    }

    public static List<Float> getBeilvs(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        String[] data = str.split(",");
        List<Float> list = new ArrayList<>();

        if (data.length == 8) {
            for (int i = 0; i < 8; i++) {
                list.add(Float.parseFloat(data[i]));
            }
            return list;
        }
        return null;
    }

    public static List<Ele> getEles(Element[] elements) {
        if (elements == null || elements.length == 0) return null;
        List<Ele> eles = new ArrayList<>();
        for (Element element : elements) {
            int id = Integer.parseInt(element.getAttribute("id"));
            int chance = Integer.parseInt(element.getAttribute("diaoluo"));
            Ele ele = new Ele(id, chance);
            eles.add(ele);
        }
        return eles;
    }

    public static List<DropData> getDropDataSum(List<DropData> list) {
        //tmpList 深拷贝
        Gson gson = new Gson();
        String json = gson.toJson(list);
        List<DropData> tmpList = gson.fromJson(json, new TypeToken<List<DropData>>() {
        }.getType());
        List<DropData> dropDataList = new ArrayList<>();
        for (DropData dd : tmpList) {
            boolean flag = true;
            if (dropDataList.isEmpty()) {
                dropDataList.add(dd);
            } else {
                int len = dropDataList.size();
                for (int i = 0; i < len; i++) {
                    DropData data = dropDataList.get(i);
                    if (data.getT() == dd.getT() && data.getG() == dd.getG()) {
                        data.setN(data.getN() + dd.getN());
                        dropDataList.set(i, data);
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    dropDataList.add(dd);
                }
            }
        }
        return dropDataList;
    }

    public static List<Short> getIds(String str) {
        List<Short> list = new ArrayList<>();
        if (str == null || "".equals(str)) return list;
        String[] data = str.trim().split(",");
        for (String string : data) {
            list.add(Short.valueOf(string));
        }
        return list;
    }

    public static List<Integer> getIds2Int(String str) {
        List<Integer> list = new ArrayList<>();
        if (str == null || "".equals(str)) return list;
        String[] data = str.trim().split(",");
        for (String string : data) {
            list.add(Integer.valueOf(string));
        }
        return list;
    }
}
