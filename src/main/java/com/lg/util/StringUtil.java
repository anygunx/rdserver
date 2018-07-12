package com.lg.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

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
     * �ַ����еĿո�ᱻȥ��
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
     * �ַ����еĿո�ᱻȥ��
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

    /**
     * ��һ��Integer�б��������ֵ��sep�ָ�����������,ȥ������һ��sep,���ظ��ַ���
     *
     * @param list
     * @param sep  �ָ���
     * @return
     */
    public static String getString(List<Integer> list, String sep) {
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

    public static String toWrapString(Object obj, String content) {
        if (obj == null) {
            return "null";
        } else {
            return obj.getClass().getName() + "@" + obj.hashCode() + "[\r\n" + content + "\r\n]";
        }
    }

    // ��1,2,3��{1,2,3}��ʽ���ַ���ת��ΪJDK��bitset
    // �����������Ƿ���{}�����������Ƿ��пո��Ƿ�Ϸ�����
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
            return str.replaceAll("'", "��").replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
        }
        return "";
    }

    /**
     * �ж������ַ����Ƿ����
     *
     * @param s1
     * @param s2
     * @return true,�ַ������;false,�ַ��������
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 != null) {
            return s1.equals(s2);
        }
        if (s2 != null) {
            return false;
        }
        // �����ַ�������null
        return true;
    }

    /**
     * �ж��ַ����Ƿ�ʱ����
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
     * �ж�һ�仰�Ƿ��Ǻ���
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
     * �жϵ����ַ��Ƿ��Ǻ���
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
     * �ж��ַ��Ƿ�����ĸ(������Сд)��������
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
     * �����ַ����к��ֵ�����
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
     * �����ַ�������ĸ�����ֵĸ�����������ĸ������Сд
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
     * �ж��ַ����Ƿ�Ϊ��
     *
     * @param str
     * @return true,�ַ����ǿյ�;false,�ַ������ǿյ�
     */
    public static boolean isEmpty(String str) {
        if (str == null || (str.trim().length() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * ���ַ�������ĸ��д
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
     * ���ַ�����С����()��ֳ�����
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
     * �ַ���ƴ��
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
     * �滻�ַ���
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
     * �滻�ַ���
     */
    public static final String replaceAllMultParams(String str, String... params) {
        for (int i = 0; i < params.length; i++) {
            str = replaceAllwithKey(str, "$" + i, params[i]);
        }
        return str;
    }

    public static String toFirstUpper(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String first = str.substring(0, 1);
        return str.replaceFirst(first, first.toUpperCase());
    }

    /**
     * ȥ���ַ�����β���ֵ�ĳ���ַ�.
     *
     * @param source  Դ�ַ���.
     * @param element ��Ҫȥ�����ַ�.
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
}
