package com.rd.define;

/**
 * 天梯常量
 *
 * @author Created by U-Demon on 2016年11月2日 上午11:13:37
 * @version 1.0.0
 */
public class LadderDefine {

    //-=-=-=-=-=-=-=-=-=-=-=-=天梯竞技场参数-=-=-=-=-=-=-=-=-=-=-=-=//

    //连续赢几场(不包含)之后算连胜
    public static final int LADDER_CONWIN_NUM = 2;
    //连胜奖励星级数量
    public static final int LADDER_CONWIN_STAR = 2;

    //天梯掉线，不计入场次开关
    public static final boolean LADDER_DISCONNECT = false;

    public static class LadderRGS {
        public final int rank;
        public final int grade;
        public final int star;

        public LadderRGS(int rank, int grade, int star) {
            this.rank = rank;
            this.grade = grade;
            this.star = star;
        }

        @Override
        public String toString() {
            return "LadderRGS [rank=" + rank + ", grade=" + grade + ", star=" + star + "]";
        }
    }

}
