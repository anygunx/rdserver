package com.rd.game;

import com.rd.bean.player.NBiaoche;
import com.rd.bean.player.NHuSongPlayer;
import com.rd.bean.player.Player;
import com.rd.dao.NBiaoCheDao;
import com.rd.dao.PlayerDao;
import com.rd.define.GameDefine;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NGameBiaocCheManager {
    //车队
    private Map<Integer, NHuSongPlayer> robPlayers = new HashMap<>();
    private List<NHuSongPlayer> biaocheList = new ArrayList<NHuSongPlayer>();
    private static final Logger logger = Logger.getLogger(NGameFactionManager.class.getName());
    private NBiaoCheDao _dao = new NBiaoCheDao();
    private PlayerDao playerDao = new PlayerDao();
    private static NGameBiaocCheManager biaocCheManager = new NGameBiaocCheManager();

    public static NGameBiaocCheManager getInstance() {
        return biaocCheManager;
    }

    /****
     *
     */
    public void init() {
        List<NBiaoche> list = _dao.getNBiaocheList();
        for (NBiaoche nBiaoche : list) {
            if (nBiaoche.getShengYuTime() < 1) {
                continue;
            }
            Player player = playerDao.getPlayer(nBiaoche.getPlayerId());
            if (player == null) {
                continue;
            }
            NHuSongPlayer husong = new NHuSongPlayer(player);
            husong.setnBiaoche(nBiaoche);
            addBiaoche(husong);
        }

    }

    /**
     * 添加玩家押镖车
     */
    public int addBiaoche(NHuSongPlayer nHuSongPlayer) {
        if (robPlayers.containsKey(nHuSongPlayer.getId())) {
            return GameDefine.FALSE;
        }
        robPlayers.put(nHuSongPlayer.getId(), nHuSongPlayer);
        biaocheList.add(nHuSongPlayer);
        return GameDefine.TRUE;
    }

    /**
     * 根据玩家的角色id 获取对应的镖车
     */
    public NHuSongPlayer getNBiaocheById(int id) {
        return robPlayers.get(id);

    }

    public List<NHuSongPlayer> getbiaocheList() {
        List<NHuSongPlayer> list = new ArrayList<>();
        list.addAll(biaocheList);
        for (NHuSongPlayer nHuSongPlayer : list) {
            if (nHuSongPlayer.getnBiaoche().getShengYuTime() < 1) {
                biaocheList.remove(nHuSongPlayer);
                robPlayers.remove(nHuSongPlayer.getId());
            }
        }
        return biaocheList;
    }


    public void remove(NHuSongPlayer huSongPlayer) {
//		robPlayers.remove(huSongPlayer.getId());
//		biaocheList.remove(huSongPlayer);
    }


}
