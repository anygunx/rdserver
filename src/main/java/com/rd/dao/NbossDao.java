package com.rd.dao;

import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

public class NbossDao {
    private static Logger logger = Logger.getLogger(NbossDao.class);

    private static final NbossDao _instance = new NbossDao();

    public static NbossDao getInstance() {
        return _instance;
    }

    private DBOperator db = new DBOperator();


//    public  List<E> getRelations(int playerId) {
//    	LinkedHashMap<Integer, NRelatedPlayer> relation = new LinkedHashMap<>();
//        ResultSet rs = db.executeQuery("select gz_playerId,youqing_value,updatetime from guanzhu where playerId=" + playerId);
//        
//        try {
//        	
//            while(rs != null && rs.next())
//            {
//                int gz_playerId = rs.getInt(1);
//                int youqing_value=rs.getInt(2);
//                long updatetime=rs.getLong(3);
//                NRelatedPlayer pr=new NRelatedPlayer();
//                pr.init(gz_playerId,youqing_value,updatetime);
//               // List<Integer> relatedIdList = StringUtil.getIntList(rs.getString(2), SEPARATOR);
//                
//                relation.put(gz_playerId, pr); 
//               // resultMap.put(type, relation == null? new LinkedHashMap<>(): relation);
//            }
//            if(!relation.isEmpty()) {
//            	getRelation(relation);
//            }
//            return relation; 
//             
//        } catch (SQLException e) {
//            logger.error(e.getMessage(), e);
//        } finally {
//            db.executeClose();
//        }
//        return relation;
//    }
//

}
