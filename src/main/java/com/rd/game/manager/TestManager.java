package com.rd.game.manager;

import com.rd.bean.player.Player;
import com.rd.define.EAttrType;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * <p>Title: 测试管理</p>
 * <p>Description: 开发中测试用类，发布版中屏蔽</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年11月28日 上午11:26:24
 */
public class TestManager {

    private static final Logger logger = Logger.getLogger(TestManager.class);

    private GameRole gameRole;
    private Player player;

    public TestManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void processTestFighting(Message request) {
        if (false) {
            //if(!GameDefine.ISPUBLISH){
            int[] attr = new int[EAttrType.ATTR_SIZE];
            for (int i = 0; i < attr.length; ++i) {
                attr[i] = request.readInt();
            }
            int fighting = request.readInt();

            for (int i = 0; i < attr.length; ++i) {
                //System.out.println(EAttrType.getType(i).getDesc()+": "+attr[i]+"  "+player.getAttr()[i]);
            }
            System.out.println("战斗力" + ": " + fighting + "  " + player.getFighting());
            if (Math.abs(fighting - player.getFighting()) < 100) {
                System.out.println("Good~");
                gameRole.sendTick(request);
            } else {
                System.out.println("Bad!");
                Message message = new Message(MessageCommand.TEST_FIGHTING_MESSAGE, request.getChannel());
                message.setString("战斗力校验失败！！");
                gameRole.sendMessage(message);
            }
        } else {
            gameRole.sendTick(request);
        }
    }

    public void processLogPrint(Message request) {
        String account = request.readString();
        logger.info("LOGPRINT=" + account);
    }

    public static void main(String[] args) {

    }
}
