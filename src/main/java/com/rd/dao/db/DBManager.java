package com.rd.dao.db;

import com.rd.dao.*;

public class DBManager {

    public PlayerDao playerDao;
    public DungeonDao dungeonDao;
    public LadderDao ladderDao;
    public MailDao mailDao;
    public EscortDao escortDao;
    public GangDao gangDao;
    public PvpDao pvpDao;
    public RelationshipDao relationshipDao;
    public ChatDao chatDao;
    public CrossDao crossDao;
    public PayDao payDao;
    public CopyDao copyDao;
    public NShopDao nShopDao;
    public NFactionDao nFactionDao;
    public NBiaoCheDao nBiaoCheDao;

    public DBManager() {
        this.playerDao = new PlayerDao();
        this.dungeonDao = new DungeonDao();
        this.ladderDao = new LadderDao();
        this.mailDao = new MailDao();
        this.escortDao = new EscortDao();
        this.gangDao = new GangDao();
        this.pvpDao = new PvpDao();
        this.relationshipDao = new RelationshipDao();
        this.chatDao = new ChatDao();
        this.crossDao = new CrossDao();
        this.payDao = new PayDao();
        this.copyDao = new CopyDao();
        this.nShopDao = new NShopDao();
        this.nFactionDao = new NFactionDao();
        this.nBiaoCheDao = new NBiaoCheDao();
    }
}
