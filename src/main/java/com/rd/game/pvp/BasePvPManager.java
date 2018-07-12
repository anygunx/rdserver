package com.rd.game.pvp;

/**
 * PVP管理器的基础类，每个PVP服务器一个manager
 *
 * @author U-Demon Created on 2017年5月9日 下午4:39:36
 * @version 1.0.0
 */
public abstract class BasePvPManager {

    protected int pvpId;

    public BasePvPManager() {

    }

    public void setPvpId(int pvpId) {
        this.pvpId = pvpId;
    }

    /**
     * 初始化的一些操作
     */
    public void init() {

    }

}
