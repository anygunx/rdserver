package com.rd.model;

import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 敏感字
 *
 * @author Created by U-Demon on 2016年11月8日 下午5:01:59
 * @version 1.0.0
 */
public class WordSensitiveModel {

    private static Logger logger = Logger.getLogger(WordSensitiveModel.class);

    private static final String RES_SENSITIVE_WORD = "sensitive/sensitive.txt";
    @SuppressWarnings("rawtypes")
    public volatile static Map<Character, Map> sensitiveWordMap;
    private static WordSensitiveFilter filter = new WordSensitiveFilter();
    public static final int BUFFER_SIZE = 2 * 1024 * 1024;//设置缓存为2mb;

    public static void loadData(String path) {
        loadSensitiveWords(path);
    }

    private static void loadSensitiveWords(String path) {
        final File file = new File(path, RES_SENSITIVE_WORD);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                FileInputStream finStream = null;
                InputStreamReader inReader = null;
                BufferedReader bufferReader = null;
                String line = null;
                try {
                    Set<String> keyWordSet = new HashSet<>();
                    finStream = new FileInputStream(file);
                    inReader = new InputStreamReader(finStream, "UTF-8");
                    bufferReader = new BufferedReader(inReader, BUFFER_SIZE); //缓存
                    while ((line = bufferReader.readLine()) != null) {
                        String lineStr = StringUtil.trimFirstAndLastChar(line, ' ');
                        if (StringUtil.isEmpty(lineStr)) {
                            continue;
                        }
                        String[] sensitiveArray = lineStr.split("\t");
                        for (String sensitive : sensitiveArray) {
                            if (sensitive.length() == 0) {
                                continue;
                            }
                            keyWordSet.add(sensitive);
                        }
                    }
                    sensitiveWordMap = createSensitiveMap(keyWordSet);
                } catch (Exception e) {
                    logger.error("加载敏感字库数据出错...", e);
                    logger.error(line);
                } finally {
                    try {
                        if (finStream != null) {
                            finStream.close();
                        }
                        if (inReader != null) {
                            inReader.close();
                        }
                        if (bufferReader != null) {
                            bufferReader.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public String toString() {
                return "sensitive";
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    /**
     * 结束标志 一个敏感词中不包含的字符即可
     */
    public static final Character END_FLAG = '+';

    /**
     * 词库初始化
     *
     * @param keyWordSet 敏感词集合
     * @return 构建的DFA算法模型：
     * 中 = {
     * 国 = {
     * 人 = {
     * $={}
     * 民 = {
     * $={}
     * }
     * }
     * 男  = {
     * 人 = {
     * $={}
     * }
     * }
     * }
     * }
     * 五 = {
     * 星 = {
     * 红 = {
     * 旗 = {
     * $={}
     * }
     * }
     * }
     * }
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map<Character, Map> createSensitiveMap(Set<String> keyWordSet) {
        Map<Character, Map> sensitiveWordMap = new HashMap(keyWordSet.size());
        for (String key : keyWordSet) {
            Map<Character, Map> currentMap = sensitiveWordMap; // root
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);
                Map<Character, Map> wordMap = currentMap.get(keyChar);
                if (wordMap == null) {
                    wordMap = new HashMap<>();
                    currentMap.put(keyChar, wordMap);
                }
                if (i == key.length() - 1) {
                    wordMap.put(END_FLAG, Collections.emptyMap());
                }
                currentMap = wordMap;
            }
        }
        return sensitiveWordMap;
    }

    public static String replaceSensitive(String string) {
        return filter.replaceSensitiveWord(string, WordSensitiveFilter.MATCH_TYPE_MAX, "*");
    }

    public static boolean checkSensitive(String string) {
        return !filter.isContainSensitiveWord(string, WordSensitiveFilter.MATCH_TYPE_MIN);
    }

    static class WordSensitiveFilter {
        public static final int MATCH_TYPE_MIN = 1;      //最小匹配
        public static final int MATCH_TYPE_MAX = 2;      //最大匹配

        /**
         * 是否包含敏感字符
         *
         * @param matchType
         * @return
         */
        public boolean isContainSensitiveWord(String txt, int matchType) {
            boolean flag = false;
            for (int i = 0; i < txt.length(); i++) {
                int matchFlag = this.checkSensitiveWord(txt, i, matchType);
                if (matchFlag > 0) {
                    flag = true;
                }
            }
            return flag;
        }

        /**
         * 获取文字中的敏感词
         *
         * @param string    文字
         * @param matchType
         * @return
         */
        public Set<String> getSensitiveWord(String string, int matchType) {
            Set<String> sensitiveWordList = new HashSet<>();
            for (int i = 0; i < string.length(); i++) {
                int length = checkSensitiveWord(string, i, matchType);
                if (length > 0) {
                    sensitiveWordList.add(string.substring(i, i + length));
                    i = i + length - 1;
                }
            }
            return sensitiveWordList;
        }

        /**
         * 替换敏感字字符
         *
         * @param txt
         * @param matchType
         * @param replaceChar
         * @return
         */
        public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
            String resultTxt = txt;
            Set<String> set = getSensitiveWord(txt, matchType);
            for (String word : set) {
                String replaceString = getReplaceChars(replaceChar, word.length());
                resultTxt = resultTxt.replaceAll(word, replaceString);
            }
            return resultTxt;
        }

        /**
         * 获取替换字符串
         *
         * @param replaceChar
         * @param length
         * @return
         */
        private String getReplaceChars(String replaceChar, int length) {
            String resultReplace = replaceChar;
            for (int i = 1; i < length; i++) {
                resultReplace += replaceChar;
            }

            return resultReplace;
        }

        /**
         * 检查串中是否包含敏感字符
         *
         * @return 敏感词长度
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public int checkSensitiveWord(String txt, int beginIndex, int matchType) {
            boolean flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
            int matchFlag = 0;     //匹配标识数默认为0
            Map<Character, Map> currentMap = sensitiveWordMap;
            for (int i = beginIndex; i < txt.length(); i++) {
                char word = txt.charAt(i);
                currentMap = currentMap.get(word);
                if (currentMap != null) {
                    matchFlag++;
                    if (currentMap.containsKey(END_FLAG)) {
                        flag = true;
                        if (MATCH_TYPE_MIN == matchType) {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            if (matchFlag < 1 || !flag) {        //长度大于等于1为词
                matchFlag = 0;
            }
            return matchFlag;
        }
    }

//    public static void main(String[] args) {
//        Set<String> keySet = new HashSet<>();
//        keySet.add("法轮功");
//        keySet.add("法轮");
//        keySet.add("shit");
//        keySet.add("shitty");
//        keySet.add("三级片");
//        keySet.add("傻逼");
//        keySet.add("傻逼儿子");
//
//        GameSensitiveWords gameSensitiveWords = new GameSensitiveWords();
//        sensitiveWordMap = gameSensitiveWords.createSensitiveMap(keySet);
//
//        System.out.println("敏感词的数量：" + sensitiveWordMap.size());
//        String string = "太多的伤感情怀也许只局限于饲养基地 shit,荧幕中的情节，shitty主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
//                + "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，法轮"
//                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着傻逼儿子呵呵。";
////        String string = "S3.臭傻逼";
//        System.out.println("待检测语句字数：" + string.length());
//        long beginTime = System.currentTimeMillis();
//        Set<String> set = filter.getSensitiveWord(string, SensitiveWordFilter.MATCH_TYPE_MIN);
//        long endTime = System.currentTimeMillis();
//        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
//        System.out.println("总共消耗时间为：" + (endTime - beginTime));
//        System.out.println(filter.replaceSensitiveWord(string, SensitiveWordFilter.MATCH_TYPE_MAX, "*"));
//    }

}
