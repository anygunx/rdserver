package com.rd.dao.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBOperator {

    static Logger log = Logger.getLogger(DBOperator.class.getName());

    private Connection con = null;
    private PreparedStatement ps = null;

    /**
     * 执行查询语句
     */
    public ResultSet executeQuery(String sql) {
        ResultSet set = null;
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql);
            set = ps.executeQuery();
        } catch (Exception e) {
            //Logger.error(LoggerSystem.DB, e, "Execute Statement Error...", "sql:" + sql);
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        }
        return set;
    }

    /**
     * 执行多条SQL语句
     */
    public void executeMultiSQL(String sql) {
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql);
            ps.execute();
        } catch (Exception e) {
            //Logger.error(LoggerSystem.DB, e, "Execute Statement Error...", "sql:" + sql);
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 执行SQL语句
     **/
    public int executeSql(String sql) {
        int id = 0;
        PreparedStatement ps = null;
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
            id = -1;
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        } finally {
            executeClose();
        }
        return id;
    }

    /**
     * 执行SQL语句
     **/
    public List<Integer> executeMultiSql(String sql) {
        List<Integer> idList = null;
        PreparedStatement ps = null;
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            idList = new ArrayList<>();
            while (rs.next()) {
                idList.add(rs.getInt(1));
            }
        } catch (Exception e) {
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        } finally {
            executeClose();
        }
        return idList;
    }

    /**
     * 执行SQL语句
     */
    public int executeSql(String sql, byte[] bs) {
        int id = 0;
        PreparedStatement ps = null;
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            if (bs != null) {
                ps.setBytes(1, bs);
            }
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        } finally {
            executeClose();
        }
        return id;
    }

    /**
     * 是否数据库连接
     */
    public void executeClose() {
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (Exception e) {
            log.error("Close MySQL Database Connection Error...", e);
            log.error(e.getMessage(), e);
            //Logger.error(LoggerSystem.DB, e, "Close MySQL Database Connection Error...");
        }
    }

    public void executeClosePs() {
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
        } catch (Exception e) {
            log.error("Close MySQL Database ps Error...", e);
            log.error(e.getMessage(), e);
        }
    }

//	/**
//	 * 执行SQL语句
//	 * **/
//	public boolean executeSqlsWithRollback(String... sqls) {
//		String sql = null;
//		try {
//			if (con == null) {
//				con = ProxoolDB.getConnection();
//			}
//			con.setAutoCommit(false);
//			for (int i = 0; i < sqls.length; i ++) {
//				ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
//				ps.execute();
//			}
//			con.commit();
//			return true;
//		} catch (Exception e) {
//			if (con != null) {
//				try {
//					con.rollback();
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			log.error("Execute Statement Error... sql:" + sql, e);
//			log.error(e.getMessage(), e);
//		} finally {
//			executeClose();
//		}
//		return false;
//	}
//
    /////////////////////////////////////////////////////////////////////

    public int executeSql(String sql, boolean releaseConnection) {
        int id = 0;
        PreparedStatement ps = null;
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
            //Logger.error(LoggerSystem.DB, e, "Execute Statement Error...", "sql:" + sql);
        }
        return id;
    }

    /**
     * 获得PreparedStatement
     **/
    public PreparedStatement getPrepareStatement(String sql) {
        try {
            if (con == null) {
                con = ProxoolDB.getConnection();
            }
            ps = con.prepareStatement(sql);
        } catch (Exception e) {
            log.error("Execute Statement Error... sql:" + sql, e);
            log.error(e.getMessage(), e);
        }
        return ps;
    }


}
