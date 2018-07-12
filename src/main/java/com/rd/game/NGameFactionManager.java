package com.rd.game;

import com.rd.bean.faction.NFaction;
import com.rd.bean.faction.NFactionMember;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.dao.NFactionDao;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.define.NFactionDefine;
import com.rd.game.event.ISimplePlayerListener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NGameFactionManager implements ISimplePlayerListener {

    private static final Logger logger = Logger.getLogger(NGameFactionManager.class.getName());

    private static NGameFactionManager factionManager = new NGameFactionManager();

    public static NGameFactionManager getInstance() {
        return factionManager;
    }

    //private GangDao gangDao = new GangDao();
    private NFactionDao nFactionDao = new NFactionDao();
    private AtomicInteger idGenerator;
    private Map<Integer, NFaction> gangMap = new HashMap<Integer, NFaction>();
    private List<NFaction> gangList = new ArrayList<>();

    /****
     * 创建帮会
     */
    public synchronized short createFaction(GameRole gameRole, String name, short level) {
        if (!checkGangName(name)) {
            return ErrorDefine.ERROR_NAME_DUPLICATE;
        }

        int gangId = generateGangId();
        NFaction gang = new NFaction(gangId, gameRole.getPlayer(), name, level);

        NFactionMember member = new NFactionMember(gameRole.getPlayer(), gangId, NFactionDefine.GANG_POSITION_PRESIDENT, gameRole.getDungeonManager().getDungeonGangPass());
        if (nFactionDao.createGang(gang, member)) {
            gameRole.getPlayer().setFaction(gang);
            gang.getMemberMap().put(member.getPlayerId(), member);
            gangMap.put(gang.getId(), gang);
            gangList.add(gang);
        } else {
            return ErrorDefine.ERROR_OPERATION_FAILED;
        }
        return GameDefine.NONE;
    }


    public synchronized short changeName(String name, NFaction faction) {
        if (!checkGangName(name)) {
            return ErrorDefine.ERROR_NAME_DUPLICATE;
        }
        faction.setName(name);
        return GameDefine.NONE;
    }

    private int generateGangId() {
        return idGenerator.incrementAndGet();
    }


    public boolean checkGangName(String name) {
        for (NFaction gang : this.gangMap.values()) {
            if (gang.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public synchronized void removeGang(NFaction faction) {
        gangMap.remove(faction.getId());
        gangList.remove(faction);
    }

    public void init() {
        int serverId = GameDefine.getServerId();
        int idLow = GameDefine.getIdLow(serverId);
        int idHigh = GameDefine.getIdHigh(serverId);
        int maxId = nFactionDao.getFactionMaxId(idLow, idHigh);
        this.idGenerator = new AtomicInteger(maxId == 0 ? idLow : maxId);
        logger.info("GameGangManager.initIdGenerator() generator=" + idGenerator.get());
        this.loadGang();
        GameWorld.getPtr().addSimplePlayerListener(this);
    }


    public void setPlayerGang(Player player) {
        for (NFaction faction : gangMap.values()) {
            if (faction.getMemberMap().containsKey(player.getId())) {
                player.setFaction(faction);
                return;
            }
        }
    }


    public void loadGang() {
//		gangMap = new ConcurrentHashMap<>(nFactionDao.getAllGang());
//		gangList = new ArrayList<>();
//        for (NFaction gang: gangMap.values()){
//            ConcurrentHashMap<Integer, NFactionMember> members = new ConcurrentHashMap<>(nFactionDao.getGangMembers(gang.getId()));
//            gang.setMemberMap(members);
//            gangList.add(gang);
//        }
        // this.sortGang();

    }

    public NFaction getFaction(int gangId) {
        return gangMap.get(gangId);
    }

    public List<NFaction> getFactionList() {
        return gangList;
    }

    @Override
    public void updateSingleHandler(SimplePlayer simplePlayer) {
        // TODO Auto-generated method stub

    }
}
